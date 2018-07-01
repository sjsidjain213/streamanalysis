package sparkstreaming;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import bean.Log;
import scala.Tuple2;

public class SparkStreamRDDIteration implements Serializable {

	public void allDataIteration(JavaDStream<Tuple2<String, Log>> alldatastream)
			throws ClassNotFoundException, SQLException {
		alldatastream.foreachRDD(f -> {
			List<Tuple2<String, Log>> alldatalist = f.collect();
			Iterator<Tuple2<String, Log>> alldataiterator = alldatalist.iterator();
			SparkDataInsert.insertAllDataBatch(alldataiterator);
		});
	}

	public void allUserPageCountIteration(JavaPairDStream<Tuple2<String, Log>, Integer> pageusermapcount)
			throws ClassNotFoundException, SQLException {
		pageusermapcount.foreachRDD(f -> {
			List<Tuple2<Tuple2<String, Log>, Integer>> pageusercountlist = f.collect();
			Iterator<Tuple2<Tuple2<String, Log>, Integer>> pageusercountiterator = pageusercountlist.iterator();
			SparkDataInsert.insertPageUserCountBatch(pageusercountiterator);
		});

	}
}
