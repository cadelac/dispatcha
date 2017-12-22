package cadelac.framework.blade.facility.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyedInsertQuery {
	
	public static int insert(
			final Fabricator<DbCommConnection,PreparedStatement> provider_
			, DbCommConnection dbCommConnection_) 
					throws Exception {
		
		final PreparedStatement ps = provider_.fabricate(dbCommConnection_);
		if (ps.executeUpdate() != 1) {
			ps.close();
			throw new SQLException("insert failure");
		}
		
		int rowId = 0;
		ResultSet rs = ps.getGeneratedKeys();
		if (rs.next()) {
			rowId = rs.getInt(1);
		}
        
        ps.close();
        rs.close();
		return rowId;
	}
	
}
