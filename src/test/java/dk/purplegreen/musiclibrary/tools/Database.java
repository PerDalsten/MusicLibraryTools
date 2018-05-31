package dk.purplegreen.musiclibrary.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import org.junit.rules.ExternalResource;

public class Database extends ExternalResource {

	static {
		System.setProperty("derby.stream.error.file", "logs/derby.log");

		// The MySQL driver seems to have issues with parsing the ;drop=true,
		// so making sure that only the Derby embedded driver is available
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (!driver.getClass().getName().equals("org.apache.derby.jdbc.AutoloadedDriver")) {
				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final String DB_URL = "jdbc:derby:memory:musiclibrarytestdb";

	public String getConnectionURL() {
		return DB_URL + ";user=musiclibrary;password=musiclibrary";
	}

	@Override
	protected void before() throws Throwable {

		try (Connection con = DriverManager.getConnection(DB_URL + ";create=true")) {

			// Create schema
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(FileTest.class.getResourceAsStream("/musiclibrary-derby.sql")))) {

				StringBuilder cmd = new StringBuilder();

				for (String line; (line = reader.readLine()) != null;) {

					line = line.trim();

					if (line.isEmpty())
						continue;

					if (line.startsWith("--"))
						continue;

					if (line.endsWith(";")) {
						cmd.append(line.substring(0, line.length() - 1));

						try (Statement stmt = con.createStatement()) {
							stmt.execute(cmd.toString());
						}
						cmd = new StringBuilder();
					} else
						cmd.append(line);
				}
			}

			// Create test data
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(FileTest.class.getResourceAsStream("/testdata.sql")))) {

				for (String line; (line = reader.readLine()) != null;) {

					String cmd = line.trim();

					if (cmd.isEmpty())
						continue;

					if (cmd.startsWith("--"))
						continue;

					try (Statement stmt = con.createStatement()) {
						stmt.execute(cmd);
					}
				}
			}
		}
	}

	@Override
	protected void after() {
		try {
			DriverManager.getConnection(DB_URL + ";drop=true");
		} catch (SQLException e) {
			if (!("08006".equals(e.getSQLState()))) {
				e.printStackTrace();
			}
		}
	}
}
