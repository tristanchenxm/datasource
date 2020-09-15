package nameless.common.datasource;

import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MasterSlaveDualConnectionDataSource implements DataSource {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MasterSlaveDualConnectionDataSource.class);
    private static final ThreadLocal<Boolean> readOnly = new ThreadLocal<>();

    private DataSource masterDataSource;
    private DataSource slaveDataSource;

    public MasterSlaveDualConnectionDataSource(DataSource masterDataSource, DataSource slaveDataSource) {
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;
    }

    private DataSource determineDataSource() {
        DataSource ds = readOnly.get() == null ? masterDataSource : slaveDataSource;
        if (log.isDebugEnabled()) {
            log.debug("use datasource {}", ds.toString());
        }
        return ds;
    }
    public static void readOnly() {
        if (!TransactionSynchronizationManager.isActualTransactionActive() && readOnly.get() == null) {
            readOnly.set(Boolean.TRUE);
        }
    }

    public static void clearContext() {
        readOnly.remove();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return determineDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return determineDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return determineDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        masterDataSource.setLogWriter(out);
        slaveDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        masterDataSource.setLoginTimeout(seconds);
        slaveDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return determineDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return determineDataSource().getParentLogger();
    }
}
