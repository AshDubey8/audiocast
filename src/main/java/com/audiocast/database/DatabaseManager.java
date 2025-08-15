package com.audiocast.database;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./data/audiocast;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static final int MAX_CONNECTIONS = 3;
    
    private static DatabaseManager instance;
    private DataSource dataSource;
    
    private DatabaseManager() {
        initializeConnectionPool();
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeConnectionPool() {
        dataSource = JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);
        ((JdbcConnectionPool) dataSource).setMaxConnections(MAX_CONNECTIONS);
        System.out.println("Connection pool initialized with " + MAX_CONNECTIONS + " connections");
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    private void initializeDatabase() {
        try (Connection conn = getConnection()) {
            executeSQLScript(conn, "/com/audiocast/database/schema.sql");
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void executeSQLScript(Connection conn, String scriptPath) {
        try (InputStream is = getClass().getResourceAsStream(scriptPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             Statement stmt = conn.createStatement()) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                sql.append(line).append(" ");
                
                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        if (dataSource instanceof JdbcConnectionPool) {
            ((JdbcConnectionPool) dataSource).dispose();
        }
    }
}
