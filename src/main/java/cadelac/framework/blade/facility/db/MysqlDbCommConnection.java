package cadelac.framework.blade.facility.db;

public class MysqlDbCommConnection extends DbCommConnection {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			handleErrors(e);
		}
	}

	public MysqlDbCommConnection(final String name_) {
		super(name_);
	}

	protected String createConnectString() {
		final DbCommUrl dbCommUrl = (DbCommUrl) this.getCommUrl();
		final DbCommCredentials credentials = (DbCommCredentials) this.getCommCredentials();
		return "jdbc:mysql://" + dbCommUrl.getDbServer() + "/" + dbCommUrl.getDbName() + "?user=" + credentials.getAccountName() + "&password=" + credentials.getPassword();
	}
}
