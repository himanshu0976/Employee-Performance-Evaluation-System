package com.eps.util;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class H2FallbackTest {

    @Test
    public void testH2DatabaseInitialization() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:eps_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
            assertNotNull(conn);

            // Read schema.sql from database/schema.sql
            java.nio.file.Path schemaPath = java.nio.file.Paths.get("database", "schema.sql");
            String schemaSql = java.nio.file.Files.readString(schemaPath, StandardCharsets.UTF_8);

            // Clean MySQL-specific commands that H2 doesn't need
            schemaSql = schemaSql.replace("CREATE DATABASE IF NOT EXISTS eps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;", "")
                                 .replace("USE eps_db;", "")
                                 .replace("SET FOREIGN_KEY_CHECKS = 0;", "SET REFERENTIAL_INTEGRITY FALSE;")
                                 .replace("SET FOREIGN_KEY_CHECKS = 1;", "SET REFERENTIAL_INTEGRITY TRUE;");

            executeSqlScript(conn, schemaSql);

            // Read sample-data.sql
            java.nio.file.Path dataPath = java.nio.file.Paths.get("database", "sample-data.sql");
            String dataSql = java.nio.file.Files.readString(dataPath, StandardCharsets.UTF_8);
            dataSql = dataSql.replace("USE eps_db;", "");

            executeSqlScript(conn, dataSql);

            // Verify tables and records
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                    assertTrue(rs.next());
                    assertEquals(13, rs.getInt(1)); // 1 admin + 2 managers + 10 employees
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM departments")) {
                    assertTrue(rs.next());
                    assertEquals(3, rs.getInt(1));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evaluation_criteria")) {
                    assertTrue(rs.next());
                    assertEquals(6, rs.getInt(1));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evaluation_cycles")) {
                    assertTrue(rs.next());
                    assertEquals(2, rs.getInt(1));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evaluations")) {
                    assertTrue(rs.next());
                    assertTrue(rs.getInt(1) >= 10);
                }
            }
        }
    }

    private void executeSqlScript(Connection conn, String script) throws Exception {
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
}
