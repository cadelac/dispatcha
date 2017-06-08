package cadelac.framework.blade.facility.db.connection;

import cadelac.lib.primitive.comm.CommCredentials;
import cadelac.lib.primitive.db.DbCommUrl;

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
		DbCommUrl dbCommUrl = (DbCommUrl) this.getCommUrl();
		CommCredentials credentials = (CommCredentials) this.getCommCredentials();
		return "jdbc:mysql://" + dbCommUrl.getDbServer() + "/" + dbCommUrl.getDbName() + "?user=" + credentials.getAccountName() + "&password=" + credentials.getPassword();
	}
}
