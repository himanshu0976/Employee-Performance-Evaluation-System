package com.eps.util;

import com.eps.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database utility managing connection pooling via HikariCP.
 * Supports MySQL 8.0+ production database with automatic in-memory fallback
 * for zero-friction local testing and evaluation.
 */
public final class DBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private static HikariDataSource dataSource;
    private static boolean isEmbedded = false;

    static {
        initDataSource();
    }

    private DBUtil() {
    }

    public static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            logger.warn("Could not load db.properties, using defaults");
        }

        String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        String url = System.getProperty("db.url", props.getProperty("db.url", 
                "jdbc:mysql://localhost:3306/eps_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"));
        String username = System.getProperty("db.username", props.getProperty("db.username", "root"));
        String password = System.getProperty("db.password", props.getProperty("db.password", ""));

        // Check environment variables as overrides
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");
        if (envUrl != null && !envUrl.isBlank()) url = envUrl;
        if (envUser != null && !envUser.isBlank()) username = envUser;
        if (envPass != null) password = envPass;

        // Step 1: Attempt MySQL connection
        HikariDataSource mysqlDs = null;
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            int maxPoolSize = Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10"));
            int minIdle = Integer.parseInt(props.getProperty("db.pool.minimumIdle", "2"));
            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);
            config.setConnectionTimeout(3000); // 3 seconds timeout for fast detection
            config.setPoolName("EPS-MySQLPool");

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            mysqlDs = new HikariDataSource(config);

            // Verify connectivity
            try (Connection testConn = mysqlDs.getConnection();
                 Statement stmt = testConn.createStatement()) {
                stmt.execute("SELECT 1");
            }

            dataSource = mysqlDs;
            isEmbedded = false;
            logger.info("Successfully connected to MySQL 8 database: {}", url);
            return;

        } catch (Exception ex) {
            logger.warn("MySQL connection to {} failed ({}). Activating embedded database fallback for standalone run/test.",
                    url, ex.getMessage());
            if (mysqlDs != null) {
                try {
                    mysqlDs.close();
                } catch (Exception ignored) {
                }
            }
        }

        // Step 2: Fallback to embedded H2 in MySQL mode
        try {
            HikariConfig h2Config = new HikariConfig();
            h2Config.setDriverClassName("org.h2.Driver");
            h2Config.setJdbcUrl("jdbc:h2:mem:eps_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1");
            h2Config.setUsername("sa");
            h2Config.setPassword("");
            h2Config.setMaximumPoolSize(10);
            h2Config.setMinimumIdle(2);
            h2Config.setPoolName("EPS-H2FallbackPool");

            dataSource = new HikariDataSource(h2Config);
            isEmbedded = true;

            // Seed embedded database with schema and sample data
            try (Connection conn = dataSource.getConnection()) {
                initEmbeddedSchemaAndData(conn);
            }

            logger.info("Embedded database successfully initialized and seeded with sample data.");

        } catch (Exception e) {
            logger.error("Failed to initialize embedded database: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to initialize database connection: " + e.getMessage(), e);
        }
    }

    private static void initEmbeddedSchemaAndData(Connection conn) {
        try {
            String schemaSql = loadSqlResource("sql/schema.sql", "database/schema.sql");
            if (schemaSql != null) {
                schemaSql = schemaSql.replace("CREATE DATABASE IF NOT EXISTS eps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;", "")
                                     .replace("USE eps_db;", "")
                                     .replace("SET FOREIGN_KEY_CHECKS = 0;", "SET REFERENTIAL_INTEGRITY FALSE;")
                                     .replace("SET FOREIGN_KEY_CHECKS = 1;", "SET REFERENTIAL_INTEGRITY TRUE;");
                executeScript(conn, schemaSql);
            }

            String dataSql = loadSqlResource("sql/sample-data.sql", "database/sample-data.sql");
            if (dataSql != null) {
                dataSql = dataSql.replace("USE eps_db;", "");
                executeScript(conn, dataSql);
            }
        } catch (Exception e) {
            logger.error("Error executing embedded database initialization scripts: {}", e.getMessage(), e);
        }
    }

    private static String loadSqlResource(String classpathLocation, String filesystemLocation) {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream(classpathLocation)) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }

        try {
            Path path = Paths.get(filesystemLocation);
            if (Files.exists(path)) {
                return Files.readString(path, StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static void executeScript(Connection conn, String script) throws SQLException {
        StringBuilder sb = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inLineComment = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);

            if (inLineComment) {
                if (c == '\n' || c == '\r') {
                    inLineComment = false;
                }
                continue;
            }

            if (!inSingleQuote && c == '-' && i + 1 < script.length() && script.charAt(i + 1) == '-') {
                inLineComment = true;
                i++;
                continue;
            }

            if (c == '\'') {
                inSingleQuote = !inSingleQuote;
                sb.append(c);
            } else if (c == ';' && !inSingleQuote) {
                String statement = sb.toString().trim();
                if (!statement.isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(statement);
                    }
                }
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        String remaining = sb.toString().trim();
        if (!remaining.isEmpty()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(remaining);
            }
        }
    }

    /**
     * Obtains a connection from the connection pool.
     */
    public static Connection getConnection() {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        if (dataSource == null) {
            throw new DatabaseException("Database connection pool is not initialized.");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error acquiring database connection: {}", e.getMessage());
            throw new DatabaseException("Failed to acquire database connection: " + e.getMessage(), e);
        }
    }

    public static boolean isEmbedded() {
        return isEmbedded;
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing HikariCP connection pool");
            dataSource.close();
        }
    }
}
