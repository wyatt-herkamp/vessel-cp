package dev.tuxjsql.vesselcp;

import dev.tuxjsql.core.connection.ConnectionProvider;
import dev.tuxjsql.core.connection.ConnectionSettings;
import pw.rayz.vessel.datasource.Database;
import pw.rayz.vessel.datasource.profiles.implementations.BasicProfile;
import pw.rayz.vessel.pool.Pool;

import java.sql.Connection;
import java.util.Properties;

public class VesselConnectionProvider implements ConnectionProvider {
    private Pool pool;

    @Override
    public Connection getConnection() {
        return new VesselConnection(pool.retrieveConnection());
    }

    @Override
    public void close() {
        pool.clearPool();
    }

    @Override
    public void returnConnection(Connection connection) {
        pool.offerConnection(connection);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String name() {
        return "Vessel-CP";
    }

    @Override
    public void setup(ConnectionSettings connectionSettings, Properties properties) {
        Database database = new Database(connectionSettings.getUrl(), connectionSettings.getDriver());
        BasicProfile profile = new BasicProfile();
        profile.mergeConnectionProperties(properties);
        profile.setPoolCapacity(Integer.parseInt(properties.getProperty("pool.size", "5")));

        pool = new Pool(database, profile);
        this.pool.populatePool();
    }
}
