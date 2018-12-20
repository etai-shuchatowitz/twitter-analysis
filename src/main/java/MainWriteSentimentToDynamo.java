import client.S3Connection;
import client.TweetDao;
import com.amazonaws.services.s3.model.S3Object;
import model.AggregatedTweets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TweetSentimentService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainWriteSentimentToDynamo {

    private static final Logger LOG = LogManager.getLogger(MainWriteSentimentToDynamo.class);

    public static void main(String[] args) throws IOException {

        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String bucketName = "etai-twitter-data";
        String tableName = "etai-twitter-50000-sentiment";

        TweetSentimentService tweetSentimentService = new TweetSentimentService();

        S3Connection s3Connection = new S3Connection(username, password, bucketName);
        TweetDao tweetDao = new TweetDao(username, password, tableName);

        String keyDate = args[0];

        /**
         * Per thread:
         * 1. Download a file
         * 2. Do sentiment on the file -> output Map<String, List<Float>>
         * 3. Upload to Dynamo: (a) sentiment (b) number of tweets (c) timestamp
         *
         * TODO: (1) compare with Bitcoin price (2) Get Sentiment on news (3) outline paper doing process, AWS, the lambda batch processes.
         *
         */

        List<String> keys = s3Connection.getKeys();

        ThreadPoolExecutor threads = new ThreadPoolExecutor(40, 40, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        threads.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        for (String key : keys) {
//            if(key.contains(keyDate)) {
                threads.submit(new Thread(() -> {
                    try {
                        S3Object s3Object = s3Connection.getObject(key);
                        Map<String, List<Float>> listOfTweetsWithSentiment = tweetSentimentService.getTweetsFromJsonFile(s3Object.getObjectContent());
                        List<AggregatedTweets> aggregatedTweets = tweetSentimentService.createAggregatedTweetsFromMap(listOfTweetsWithSentiment, key);

                        for (AggregatedTweets aggregatedTweet : aggregatedTweets) {
                            System.out.println(aggregatedTweet);
                            tweetDao.putItem(aggregatedTweet);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }));
//            }
        }

        threads.shutdown();

    }

}
