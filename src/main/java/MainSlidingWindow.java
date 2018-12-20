import client.TweetDao;
import model.AggregatedTweets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class MainSlidingWindow {

    public static void main(String[] args) throws FileNotFoundException {

        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String tweetTableName = "etai-twitter-by-hour";

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String beginDate = "2018-10-28T00:00:00Z";
        String endDate = "2018-11-28T00:00:00Z";
        TemporalAccessor beginTemporalAccessor = timeFormatter.parse(beginDate);
        TemporalAccessor endTemporalAccessor = timeFormatter.parse(endDate);
        Instant beginTime = Instant.from(beginTemporalAccessor);
        Instant endTime = Instant.from(endTemporalAccessor);

        TweetDao tweetDao = new TweetDao(username, password, tweetTableName);

        Instant timeTally = beginTime;

        while(timeTally.isBefore(endTime)) {
            PrintWriter pw = new PrintWriter(new File("src/main/resources/sliding_window_csvs/" + timeTally + ".csv"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Date");
            stringBuilder.append(",");
            stringBuilder.append("Sentiment");
            stringBuilder.append(",");
            stringBuilder.append("Close");
            stringBuilder.append(",");
            stringBuilder.append("numTweets");
            stringBuilder.append("\n");
            Instant innerTally = timeTally;
            for(int i = -24; i < 24; i++) {
                System.out.println(innerTally);
                AggregatedTweets aggregatedTweets = tweetDao.getAllTweetsWithTimestamp(innerTally.toString());
                float sentiment = aggregatedTweets.getSentimentScore();
                float price = aggregatedTweets.getBitcoinClosingPrice();
                stringBuilder.append(innerTally.toString());
                stringBuilder.append(",");
                stringBuilder.append(sentiment);
                stringBuilder.append(",");
                stringBuilder.append(price);
                stringBuilder.append(",");
                stringBuilder.append(aggregatedTweets.getNumberOfTweets());
                stringBuilder.append("\n");
                innerTally = timeTally.plus(i, ChronoUnit.HOURS);
            }
            pw.write(stringBuilder.toString());
            pw.close();
            timeTally = timeTally.plus(1, ChronoUnit.HOURS);
        }
    }

}
