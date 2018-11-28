import client.S3Connection;
import service.TweetSentimentService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {

        String username = null;
        String password = null;
        String bucketName = null;

        final Map<String, List<Float>> averageSentimentPerHour = new HashMap<>();
        TweetSentimentService tweetSentimentService = new TweetSentimentService();

//        S3Connection s3Connection = new S3Connection(username, password, bucketName);

//        String outputFile = "sentiments.txt";

//        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));

        /**
         * Per thread:
         * 1. Download a file
         * 2. Do sentiment on the file -> output Map<String, List<Float>>
         * 3. Upload to Dynamo: (a) sentiment (b) number of tweets (c) timestamp
         *
         * TODO: (1) compare with Bitcoin price (2) Get Sentiment on news (3) outline paper doing process, AWS, the lambda batch processes.
         *
         */

        List<String> keys = new ArrayList<>();


//        List<String> keys = s3Connection.getKeys();

        ThreadPoolExecutor threads = new ThreadPoolExecutor(100, 100, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        threads.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        for (String key : keys) {

            threads.submit(new Thread(() -> {
                try {
                    System.out.println(key);
//                    PinpointConnection.uploadEndpointToPinpoint(subscribers, appId, validTopics);

                } catch (Exception e) {
                    System.out.println("Error connecting to Dyanmo");
                }
            }));


        }

//        for (int i = initialTime; i < finalTime; i++) {
//
//            String path = "src/main/resources/11/14/" + String.format("%02d", i );
//
//            Files.walk(Paths.get(path))
//                    .filter(Files::isRegularFile).parallel()
//                    .forEach(file -> {
//                        tweetSentimentService.getTweetsFromJsonFile(file.toFile(), averageSentimentPerHour);
//                        System.out.println("Doing file " + file.toString());
//
//                    });
//
//            for (Map.Entry<String, List<Float>> entry : averageSentimentPerHour.entrySet()) {
//                Double average = entry.getValue().stream().mapToDouble(val -> val).average().orElse(0.0);
//                String conclusion = entry.getKey() + ": " + average;
//                writer.append(conclusion);
//            }
//        }

//        writer.close();
        threads.shutdown();

    }
}
