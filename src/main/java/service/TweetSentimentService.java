package service;

import com.google.gson.Gson;
import model.Tweet;
import sentiment.SentimentAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TweetSentimentService {

    private static final Gson gson = new Gson();
    private static final SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

    public Map<String, List<Float>> getTweetsFromJsonFile(File file, Map<String, List<Float>> averageSentimentPerHour) {

        ThreadPoolExecutor lineThread = new ThreadPoolExecutor(100, 100, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        lineThread.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            int tally = 0;
            while ((line = br.readLine()) != null) {
                Tweet tweet = gson.fromJson(line, Tweet.class);
                if(tweet.getUser() != null) {
                    if (tweet.getUser().getFollowersCount() > 3000) {
                        float sentiment = SentimentAnalyzer.findSentiment(tweet.getText());
                        tweet.setSentiment(sentiment);
                        long timestamp = tweet.getTimestamp();
                        Instant instant = Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.MINUTES);
                        long numberOfMinutesToOffset = (instant.getEpochSecond() % 600) / 60;
                        Instant offSetInstant = instant.minus(numberOfMinutesToOffset, ChronoUnit.MINUTES);
                        System.out.println(offSetInstant);

                        List<Float> sentiments;
                        if (averageSentimentPerHour.get(offSetInstant.toString()) == null) {
                            sentiments = new ArrayList<>();
                        } else {
                            sentiments = averageSentimentPerHour.get(offSetInstant.toString());
                        }
                        sentiments.add(sentiment);
                        averageSentimentPerHour.put(offSetInstant.toString(), sentiments);

                        tally++;
                    }
                }
            }

            System.out.println("Final tally is: " + tally);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return averageSentimentPerHour;
    }
}
