package cadelac.framework.blade.facility.db.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cadelac.framework.blade.facility.db.DbCommInitParams;
import cadelac.lib.primitive.comm.AbstractCommConnection;
import cadelac.lib.primitive.comm.CommCredentials;
import cadelac.lib.primitive.comm.CommUrl;
import cadelac.lib.primitive.db.DbCommUrl;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.FrameworkException;

public abstract class DbCommConnection extends AbstractCommConnection {

	public DbCommConnection(final String name) {
		super(name);
		this._connection = null;
		this._isInitialized = false;
	}

	@Override
	public void disconnect() throws FrameworkException, IOException  {
		if (this._connection != null) {
			try {
				this._connection.close();
				setIsConnected(false);
				logger.info("Disconnected from DB [" + _url.getDbName() + "]");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getIsInitialized() {
		return this._isInitialized;
	}

	public void init(final DbCommInitParams params_) throws FrameworkException {
		if (params_ == null)
			throw new ArgumentException(String.format("Cannot initialize [%s]; argument [%s] is null", DbCommConnection.class.getSimpleName(), DbCommInitParams.class.getSimpleName()));
		
		if (getId() == null || getId().isEmpty())
			throw new ArgumentException("Cannot initialize DbCommConnection; name is null or empty");
		
		if (getIsInitialized())
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; it has already been initialized");
		
		// no checking required. DbCommInitParams is guaranteed to contain a DbCommUrl
		this._url = (DbCommUrl) params_.getCommUrl();
		if (_url == null)
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; CommUrl is null");

		if (!_url.isValid())
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; CommUrl [" + this._url.toString() + "] is malformed");

		this._credentials = params_.getCommCredentials();		
		setIsInitialized(true);	
	}
	
	
	public void connect() throws SQLException, FrameworkException {		
		final String connect_string = createConnectString();
		_connection = DriverManager.getConnection(connect_string);
		if (_connection != null) {
			setIsConnected(true);
			logger.info("Connected to DB [" + _url.getDbName() + "]");
		}
	}
	
	public Connection getConnection() {
		return _connection;
	}
	
	
	protected void setIsInitialized(boolean isInitialized_) {
		this._isInitialized = isInitialized_;
	}
	
	protected CommUrl getCommUrl() {
		return this._url;
	}
	
	protected CommCredentials getCommCredentials() {
		return this._credentials;
	}
	
	// execute() method should be defined on the operation and not on the connection!!!!
//	public DbResult execute(DbOperation operation_, DbStatementParam params_) throws SQLException, InstantiationException, IllegalAccessException {
//		return (_connection != null) ? operation_.execute(_connection, params_) : null;
//	}
	
	abstract protected String createConnectString();

	
    protected static void handleErrors(Exception ex_) {
        System.out.println("Exception: " + ex_.getMessage());
    } 
    
    private static final Logger logger = Logger.getLogger(DbCommConnection.class);
    
    private DbCommUrl _url;
    private CommCredentials _credentials;
	private Connection _connection;
	private boolean _isInitialized;
}



/*   
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
   
   public static void loadAllSymbols() {
       loadSymbolsUsingQuery("SELECT id, symbol FROM symbols");
   }
   
   public static void loadSymbolsUsingQuery(String query_) {
       if ( _connection != null ) {
           Statement s = null;
           ResultSet rs = null;
           
           try {
               s = _connection.createStatement();
               s.executeQuery(query_);
               rs = s.getResultSet();
               int count=0;
               while (rs.next())
               {
                   int id = rs.getInt("id");
                   String symbol = rs.getString("symbol");
                   MiddleWareContract  contract = new MiddleWareContract(symbol, id);
                   System.out.println("Loaded symbol [" + symbol + "]: id=" + id );
                   ++count;
               }
               System.out.println("Loaded " + count + " symbols.");
           } catch (SQLException except_) {
               handleErrors(except_);
           } finally {
               if ( rs != null ) {
                   try {
                       rs.close ();
                       s.close ();
                   } catch (SQLException ex) {
                       //
                   }
               }
           }
       }   
   }
   
   public static ArrayList loadPosition() {
       ArrayList positions = new ArrayList();
       
       if ( _connection != null ) {
           Statement s = null;
           ResultSet rs = null;
           
           try {
               Date dateToday = new Date();
               DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               String formattedDate = dateFormat.format(dateToday);
               
               s = _connection.createStatement();
               s.executeQuery("SELECT id, symbol, quantity, avg_amount FROM positions WHERE asof_date='" + 
                       formattedDate + "' ORDER BY symbol");
               rs = s.getResultSet();
               
               
               int count=0;
               while (rs.next()) {
                   int id = rs.getInt("id");
                   String symbol = rs.getString("symbol");
                   int quantity = rs.getInt("quantity");
                   float amount = rs.getFloat("avg_amount");
                   positions.add( new Instrument(symbol, quantity, amount) );
                   
                   System.out.println("Position: " + id + ", " + symbol + ", " + quantity + ", " + amount);
                   ++count;
               }
               System.out.println("Loaded " + count + " positions.");
               
           } catch (SQLException except_) {
               handleErrors(except_);
           } catch ( MiddleWare.AppException except_ ) {
           // ignore
           }
           finally {
               if ( rs != null ) {
                   try {
                       rs.close ();
                       s.close ();
                   } catch (SQLException ex) {
                       //
                   }
               }
           }
       }
       return positions;
   }  
   
   public static void write(MiddleWareOrder order_) {
       if ( _connection != null ) {
           Statement s = null;
           try {
               
               Date dateToday = new Date();
               DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
               
               s = _connection.createStatement();
               s.executeUpdate("insert into orders values (null, " +
                       order_.getOrderId() + ", " +
                       order_.getInstrument().getSymbol().getSymbolId() + ", '" +
                       order_.getSide().getTextForOrders() + "', " +
                       order_.getResidualQuantity() + ", " +
                       order_.getLimitPrice() + ", '" +
                       order_.getStatus().getText() + "', '" +
                       dateFormat.format(dateToday) + "', '" +
                       timeFormat.format(dateToday) + "', " +
                       _accountId + ");", Statement.RETURN_GENERATED_KEYS);
               
               ResultSet rs = s.getGeneratedKeys();
               if (rs.next()) {
                   order_.setDBOrderId(rs.getInt(1));
                   System.out.println("DBOrderId=" + order_.getDBOrderId());
               } 
               
//               s = _connection.prepareStatement (
//                       "INSERT INTO orders VALUES ( null, ?, ?, ?, ?, ?, ?, ?, ? )" );
//               s.setInt(1, order_.getOrderId());
//               s.setInt(2, order_.getInstrument().getSymbol().getSymbolId());
//               s.setString(3, order_.getSide().getTextForOrders());
//               s.setInt(4, order_.getResidualQuantity());
//               s.setDouble(5, order_.getLimitPrice());
//               s.setString(6, order_.getStatus().getText());
//               s.setString(7, dateFormat.format(dateToday));
//               s.setString(8, timeFormat.format(dateToday));
//               s.executeUpdate();
               
           } catch (SQLException except_) {
               handleErrors(except_);
           } finally {
               try {
                   s.close();
               } catch (SQLException ex) {
                   //
               }
           }
       }
   }
   public static void write(MiddleWareExecution execution_, MiddleWareOrder order_) {
       if ( _connection != null ) {
           Statement s = null;
           
           try {
               
               Date dateToday = new Date();
               DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
               
               s = _connection.createStatement();
               s.executeUpdate("INSERT INTO executions VALUES (null, " +
                       order_.getDBOrderId()           + ", " +
                       execution_.getQuantity()        + ", " +
                       execution_.getPrice()           + ", '" +
                       dateFormat.format(dateToday)    + "', '" +
                       timeFormat.format(dateToday)    + "')");
           } catch (SQLException except_) {
               handleErrors(except_);
           } finally {
               try {
                   s.close();
               } catch (SQLException ex) {
                   //
               }
           }
       }
   }
   public static void updateStatus(MiddleWareOrder order_) {
       if ( _connection != null ) {
           Statement s = null;
           try {                
               s = _connection.createStatement();
               s.executeUpdate("UPDATE orders SET order_status='" +
                       order_.getStatus().getText() + "' where id=" +
                       order_.getDBOrderId() );
           } catch (SQLException except_) {
               handleErrors(except_);
           } finally {
               try {
                   s.close();
               } catch (SQLException ex) {
                   //
               }
           }
       }
   }
*/
