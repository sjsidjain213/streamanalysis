package sqlconnect;
import java.sql.*;  
public class connectorclass {

	public static void main(String args[]) {
	try{  
		Class.forName("com.mysql.jdbc.Driver");  
		Connection con=DriverManager.getConnection(  
		"jdbc:mysql://54.168.9.108/test","root","root");  
		//here sonoo is database name, root is username and password  
		Statement stmt=con.createStatement();  
//		String query = "insert into testtable(name) values(?)";
//		   PreparedStatement preparedStmt = con.prepareStatement(query);
//		      preparedStmt.setString (1, "Barney");
//		      preparedStmt.execute();  
		
		
		System.out.println("end");
		con.close();  
		}catch(Exception e){ System.out.println(e);}  
		} 

}
