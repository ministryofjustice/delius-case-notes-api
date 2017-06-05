package uk.gov.justice.digital.noms.delius.service;

import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.driver.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Service
public class ListenerThing {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataSource dataSource;

    @Autowired
    public ListenerThing(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;

        doThings();
    }

    private void doThings() throws SQLException {
        Connection connection = dataSource.getConnection();

        if (connection.isWrapperFor(OracleConnection.class)) {
            OracleConnection oracleConnection = connection.unwrap(OracleConnection.class);

            Properties prop = new Properties();

            prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
            prop.setProperty(OracleConnection.DCN_IGNORE_INSERTOP, "false");
            prop.setProperty(OracleConnection.DCN_IGNORE_UPDATEOP, "false");
            prop.setProperty(OracleConnection.NTF_QOS_PURGE_ON_NTFN, "false");
            prop.setProperty(OracleConnection.DCN_BEST_EFFORT, "true");

            DatabaseChangeRegistration dcr = oracleConnection.registerDatabaseChangeNotification(prop);

            dcr.addListener(new MyListener());

            Statement stmt = oracleConnection.createStatement();

            ((OracleStatement) stmt).setDatabaseChangeRegistration(dcr);

            ResultSet rs = stmt.executeQuery("select CONTACT_ID from CONTACT where CONTACT_ID = '1'");

            String[] tableNames = dcr.getTables();

            logger.info("registered tables: {}" , (Object[])tableNames);

            rs.close();
            stmt.close();
            connection.close();
        } else {
            logger.error("Not using an Oracle data source...");
        }
    }


    class MyListener implements DatabaseChangeListener {

        public void onDatabaseChangeNotification(DatabaseChangeEvent e) {
            System.out.println("got an event! Class is '" + e.getClass() + "' " + e.toString());
        }
    }
}
