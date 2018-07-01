package sparkstreaming;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import bean.Log;

public class SparkKafka10 {

	public static void main(String[] argv) throws Exception {

		// Configure Spark to connect to Kafka
		// Map<String, Object> kafkaParams = new
		// KafkaConfig().sparkKafkaConnectionConfiguration();

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

		// Configure Spark to listen messages in topic test
		Collection<String> topics = Arrays.asList("customer");

		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("SparkKafkaDataStreaming");

		// Read messages in batch of 30 seconds
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(30));

		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

		// Read value of each message from Kafka and return it
		// //ConsumerRecord<String,
		// String>
		JavaDStream<String> lines = stream.map((kafkaRecord) -> kafkaRecord.value());

		// Break every message into words and return list of words
		JavaDStream<Tuple2<String, Log>> streamarray = lines.flatMap((String l) -> {
			String words[] = l.split("\t");
			Set<Tuple2<String, Log>> ls = new HashSet<Tuple2<String, Log>>();
			ls.add(new Tuple2<>("alldata", new Log(words[0], words[1], words[2], words[3], words[4])));
			ls.add(new Tuple2<>("usercountperpage", new Log(words[1], words[2])));
			return ls.iterator();
		});

		JavaDStream<Tuple2<String, Log>> alldatastream = streamarray.filter(f -> {
			if (f._1.equals("alldata"))
				return true;
			else
				return false;
		});
		alldatastream.print();
		try {
			new SparkStreamRDDIteration().allDataIteration(alldatastream);
		} catch (SQLException | ClassNotFoundException classexception) {
			classexception.printStackTrace();
		}

		// second RDD
		JavaDStream<Tuple2<String, Log>> usercountperpagestream = streamarray.filter(f -> {
			if (f._1.equals("usercountperpage"))
				return true;
			else
				return false;
		});

		JavaPairDStream<Tuple2<String, Log>, Integer> pageusermap = usercountperpagestream.mapToPair(f -> {
			return new Tuple2<Tuple2<String, Log>, Integer>(f, 1);
		});

		JavaPairDStream<Tuple2<String, Log>, Integer> pageusermapcount = pageusermap
				.reduceByKey((Integer first, Integer second) -> {
					return first + second;
				});

		// try {
		// new
		// SparkStreamRDDIteration().allUserPageCountIteration(pageusermapcount);
		// } catch (SQLException | ClassNotFoundException classexception) {
		// classexception.printStackTrace();
		// }

		jssc.start();
		jssc.awaitTermination();
	}
}