package com.eps;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Standalone Embedded Apache Tomcat 10+ server runner.
 * Allows running the complete Jakarta EE 10 Employee Performance Evaluation System (EPS)
 * directly from the command line without requiring a separate Tomcat installation.
 */
public class ServerRunner {
    private static final Logger logger = LoggerFactory.getLogger(ServerRunner.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "/eps";

    private Tomcat tomcat;
    private int port;

    public ServerRunner() {
        this(resolvePort());
    }

    public ServerRunner(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = resolvePort();
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        ServerRunner runner = new ServerRunner(port);
        try {
            runner.start();
            runner.printBanner();
            runner.await();
        } catch (Exception e) {
            logger.error("Failed to start embedded Tomcat server: {}", e.getMessage(), e);
            System.err.println("Error starting server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(port);

        // Configure base working directory
        Path workDir = Path.of("target", "tomcat-work");
        if (!Files.exists(workDir)) {
            Files.createDirectories(workDir);
        }
        tomcat.setBaseDir(workDir.toAbsolutePath().toString());

        // Bind default HTTP connector
        tomcat.getConnector();

        // Configure webapp directory
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            throw new IllegalStateException("Webapp directory not found at: " + webappDir.getAbsolutePath());
        }

        // Deploy webapp under /eps
        Context ctx = tomcat.addWebapp(CONTEXT_PATH, webappDir.getAbsolutePath());
        ctx.setParentClassLoader(ServerRunner.class.getClassLoader());

        // Mount target/classes into /WEB-INF/classes so servlets and filters are scanned
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        // Add root redirect to /eps for convenience when accessing http://localhost:8080/
        Context rootCtx = tomcat.addContext("", new File(System.getProperty("java.io.tmpdir")).getAbsolutePath());
        Tomcat.addServlet(rootCtx, "RootRedirectServlet", new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
                    throws IOException {
                resp.sendRedirect(CONTEXT_PATH + "/");
            }
        });
        rootCtx.addServletMappingDecoded("", "RootRedirectServlet");

        tomcat.start();
        logger.info("Tomcat 10 embedded server started on port {}", port);
    }

    public void await() {
        if (tomcat != null) {
            tomcat.getServer().await();
        }
    }

    public void stop() {
        if (tomcat != null) {
            try {
                tomcat.stop();
                tomcat.destroy();
                logger.info("Embedded Tomcat server stopped.");
            } catch (Exception e) {
                logger.warn("Error stopping Tomcat: {}", e.getMessage());
            }
        }
    }

    public int getPort() {
        return port;
    }

    private void printBanner() {
        System.out.println("===============================================================================");
        System.out.println("       EMPLOYEE PERFORMANCE EVALUATION SYSTEM (EPS) - TOMCAT 10+ SERVER       ");
        System.out.println("===============================================================================");
        System.out.println(" Server URL:       http://localhost:" + port + "/eps/");
        System.out.println(" Root Shortcut:    http://localhost:" + port + "/");
        System.out.println(" Status:           ONLINE & READY");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println(" DEMO CREDENTIALS:");
        System.out.println("   [Admin]    admin    / Admin@123    -> http://localhost:" + port + "/eps/admin/dashboard");
        System.out.println("   [Manager]  manager1 / Manager@123  -> http://localhost:" + port + "/eps/manager/dashboard");
        System.out.println("   [Manager]  manager2 / Manager@123  -> http://localhost:" + port + "/eps/manager/dashboard");
        System.out.println("   [Employee] emp1     / Employee@123 -> http://localhost:" + port + "/eps/employee/dashboard");
        System.out.println("===============================================================================");
    }

    private static int resolvePort() {
        String portProp = System.getProperty("port");
        if (portProp != null && !portProp.isBlank()) {
            try {
                return Integer.parseInt(portProp);
            } catch (NumberFormatException ignored) {
            }
        }
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            try {
                return Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_PORT;
    }
}
