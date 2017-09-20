package cadelac.framework.blade.facility.db;

public class SqliteDbCommConnection extends DbCommConnection {

	static {
		try {
			Class.forName("org.sqlite.JDBC").newInstance();
		} catch (Exception e) {
			handleErrors(e);
		}
	}

	public SqliteDbCommConnection(final String name_) {
		super(name_);
	}

	protected String createConnectString() {
		DbCommUrl dbCommUrl = (DbCommUrl) this.getCommUrl();
		return "jdbc:sqlite:" + dbCommUrl.getDbName();
	}
}
