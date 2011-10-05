/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.aol.webservice_base.persistence.database.IDatabaseHelper;

/**
 * @author human
 *
 */
public class JDBCConnectionManager implements IDBConnectionManager {

    protected static final Logger logger = Logger.getLogger(JDBCConnectionManager.class);
    protected boolean shuttingDown = false;
    // config
    protected IDatabaseHelper dbHelper;
    protected int minConnections;
    protected int maxConnections;
    protected Long expireConnectionMs; // expire connections based on creation time
    protected Long staleConnectionMs;  // expire connections that aren't used for certain period of time
    protected int isValidTimeout = 1;     // timeout to use for isValid calls (in seconds)
    protected Connections connections;

    public JDBCConnectionManager() {
    }

    public void init() throws SQLException {
        connections = new Connections(this);

        Thread threadConnectionsCreator = new BackgroundCreateConnections();
        threadConnectionsCreator.start();
    }

    public void terminate() {
        shuttingDown = true;
        connections.destroyAll();
    }

    protected class BackgroundCreateConnections extends Thread {

        public void run() {
            // create the minimum number of exceptions
            for (int i = 0; i < minConnections; i++) {
                Connection connection = createConnection();
                if (connection != null) {
                    connections.addAvailable(connection, new ConnectionInfo(connection));
                } else {
                    // problem connecting, so lets not build up minConnections
                    break;
                }
            }
        }
    }

    public void failConnection(Connection connection) {
        if (!shuttingDown) {
            connections.failConnection(connection);
        }
    }

    public boolean checkConnection(Connection connection) {
        return !connections.isFailedConnection(connection);
    }

    public void returnConnection(Connection connection) {
        if (!shuttingDown) {
            connections.returnConnection(connection);
        }
    }

    public Connection getConnection(Integer bucket) throws PersistenceException {
        if (connections == null) {
            throw new Error("Connection manager must be initialized before use");
        }

        if (shuttingDown) {
            return null;
        }

        Connection connection = connections.obtainConnection();
        int currentConnections = connections.getCount();
        if ((connection == null) && (currentConnections < maxConnections)) {
            connection = createConnection();
            if (connection == null) {
                throw new PersistenceException("Could not create db connection: " + currentConnections + " of " + maxConnections);
            }
            ConnectionInfo info = new ConnectionInfo(connection);
            connections.addInUse(connection, info);
        }

        if (connection == null) {
            throw new PersistenceException("Could not obtain / get connection: " + currentConnections + " of " + maxConnections);
        }

        return connection;
    }

    protected Connection createConnection() {
        if (!shuttingDown) {
            try {
                DataSource ds = dbHelper.getDataSource();
                return ds.getConnection();
            } catch (Exception e) {
                String dbInfo = (dbHelper == null) ? "<<unknown>>" : dbHelper.getDbInfo();
                logger.warn("Problem obtaining db connection: " + e.getClass().getSimpleName() + ":" + e.getMessage() + " " + dbInfo);
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.aol.webservice_base.persistence.IDBConnectionManager#bucketizerRequired()
     */
    public boolean isBucketizerRequired() {
        return false;
    }

    public Integer bucketize(Object bucketItem) throws PersistenceException {
        return null;
    }

    public boolean isDuplicateException(Connection connection, SQLException sqlE) {
        return dbHelper.isDuplicateException(sqlE);
    }

    public boolean isCodingErrorException(Connection connection, SQLException sqlE) {
        return dbHelper.isCodingErrorException(sqlE);
    }

    public void setDbHelper(IDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setExpireConnectionMs(Long expireConnectionMs) {
        this.expireConnectionMs = expireConnectionMs;
    }

    public void setStaleConnectionMs(Long staleConnectionMs) {
        this.staleConnectionMs = staleConnectionMs;
    }
    
    public void setIsValidTimeout(int isValidTimeout) {
   	 this.isValidTimeout = isValidTimeout;
    }

    // state
    protected class Connections {

        protected JDBCConnectionManager manager;
        protected AtomicInteger count = new AtomicInteger();
        protected Map<Connection, ConnectionInfo> available = Collections.synchronizedMap(new LinkedHashMap<Connection, ConnectionInfo>());
        protected Map<Connection, ConnectionInfo> inUse = Collections.synchronizedMap(new LinkedHashMap<Connection, ConnectionInfo>());

        protected Connections(JDBCConnectionManager manager) {
            this.manager = manager;
        }

        protected int getCount() {
            return count.get();
        }

        /*
         * Add connection to available queue - done at init time
         */
        protected void addAvailable(Connection connection, ConnectionInfo info) {
            available.put(connection, info);
            count.incrementAndGet();
        }

        /*
         * Add connection to inUse, since it's going to be distributed anyhow
         */
        protected void addInUse(Connection connection, ConnectionInfo info) {
            inUse.put(connection, info);
            count.incrementAndGet();
        }

        protected Connection obtainConnection() {
            ConnectionInfo info = null;
            synchronized (available) {
                Iterator<Connection> keyIter = available.keySet().iterator();
                if (keyIter.hasNext()) {
                    info = available.remove(keyIter.next());
                }
            }

            // if we are giving away a connection, ensure we track it
            if (info != null) {
                // TBD is this the right way to handle closed connections
                // and is the 1 second timeout for isValid() Ok or
                // should we config
                
                boolean usable = true;
                try {
                    if (info.isExpired(manager) || !info.connection.isValid(isValidTimeout)) {
                        usable = false;
                    }
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(JDBCConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                    usable = false;
                }
                if (!usable) {
                    destroyConnection(info.connection);
                    // destroying this connection won't decrement the count (not in list), so we do it here.
                    count.decrementAndGet();
                } else {
                    inUse.put(info.connection, info);
                    return info.connection;
                }
            }

            // got nothing
            return null;
        }

        protected boolean isFailedConnection(Connection connection) {
            ConnectionInfo info = inUse.get(connection);
            if (info != null) {
                return info.isFailed();
            } else {
                throw new RuntimeException("Attempt to check a connection that is not in use.");
            }
        }

        protected void failConnection(Connection connection) {
            ConnectionInfo info = inUse.get(connection);
            if (info != null) {
                info.setFailed(true);
            } else {
                throw new RuntimeException("Attempt to fail a connection that is not in use.");
            }
        }

        protected void returnConnection(Connection connection) {
            ConnectionInfo info = inUse.remove(connection);

            // only return connections to available that were "inUse"
            if ((info != null) && !info.isFailed()) {
                info.updateLastAccess();
                available.put(info.connection, info);
            } else {
                destroyConnection(connection);
            }
        }

        /**
         * Destroy connection.  Decrement counts if we were tracking this connection
         *
         * @param connection the connection
         */
        protected void destroyConnection(Connection connection) {
            try {
                connection.close();
            } catch (SQLException e) {
            }

            ConnectionInfo removed = null;
            removed = available.remove(connection);

            if (removed == null) {
                removed = inUse.remove(connection);
            }

            if (removed != null) {
                count.decrementAndGet();
            }
        }

        protected synchronized void destroyAll() {
            Set<Connection> connections = available.keySet();
            available = Collections.EMPTY_MAP;
            connections.addAll(inUse.keySet());
            inUse = Collections.EMPTY_MAP;

            for (Connection conn : connections) {
                destroyConnection(conn);
            }
        }
    }

    protected static class ConnectionInfo {

        protected Connection connection;
        protected long creationTime;
        protected long lastAccessTime;
        protected boolean failed;

        public ConnectionInfo(Connection connection) {
            this.connection = connection;
            this.creationTime = System.currentTimeMillis();
            updateLastAccess();
            this.failed = false;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public void updateLastAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired(JDBCConnectionManager connectionMgr) {
            // expire
            if ((connectionMgr.expireConnectionMs != null) && (connectionMgr.expireConnectionMs >= 0) && (creationTime + connectionMgr.expireConnectionMs <= System.currentTimeMillis())) {
                return true;
            }
            if ((connectionMgr.staleConnectionMs != null) && (connectionMgr.staleConnectionMs >= 0) && (lastAccessTime + connectionMgr.staleConnectionMs <= System.currentTimeMillis())) {
                return true;
            }
            return false;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }
    }
}
