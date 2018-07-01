package sparkstreaming;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import bean.Log;
import scala.Tuple2;
import sqlconnect.ConnectionString;

public class SparkDataInsert {
	

	public static void insertAllDataBatch(Iterator<Tuple2<String, Log>> rdd) {
		Connection con = ConnectionString.getConnection();
		try {
			String query = "insert into AllData(hittime,ipaddress,page,responsecode,responsetime) values(?,?,?,?,?)";
			con.setAutoCommit(false);
			PreparedStatement preparedStmt = con.prepareStatement(query);

			while (rdd.hasNext()) {
				Tuple2<String, Log> tuple = rdd.next();
				Timestamp timestamp = convertToTimeStamp(tuple._2.getTimestamp());
				preparedStmt.setTimestamp(1, timestamp);// zw(1, new java.sql.DateTime(date.getTime()));
				preparedStmt.setString(2, tuple._2.getIpaddress());
				preparedStmt.setString(3, tuple._2.getPage());
				preparedStmt.setString(4, tuple._2.getResponsecode());
				preparedStmt.setString(5, tuple._2.getResponsetime());
				preparedStmt.addBatch();
			}
			preparedStmt.executeBatch();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void insertPageUserCountBatch(Iterator<Tuple2<Tuple2<String,Log>,Integer>> pageusercountiterator) {
		Connection con = ConnectionString.getConnection();
		try {
			String query = "insert into PageUserCount(page,ipaddress,count) values(?,?,?)";
			con.setAutoCommit(false);
			PreparedStatement preparedStmt = con.prepareStatement(query);
			while (pageusercountiterator.hasNext()) {
				Tuple2<Tuple2<String,Log>,Integer> tuple = pageusercountiterator.next();
				
				preparedStmt.setString(1, tuple._1._2.getPage());
				preparedStmt.setString(2, tuple._1._2.getIpaddress());
				preparedStmt.setInt(3, tuple._2);
				preparedStmt.addBatch();
			}
			preparedStmt.executeBatch();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public static void main(String args[]) throws ClassNotFoundException,
	// SQLException {
	// new SparkStreamRDDIteration().insertActiveUserCount("2012-06-20
	// 16:00:47","192.0.16.26",5);
	// }

	public static Timestamp convertToTimeStamp(String stringtime) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = format.parse(stringtime);
		Timestamp timestamp = new java.sql.Timestamp(date.getTime());
		return timestamp;
	}
}
