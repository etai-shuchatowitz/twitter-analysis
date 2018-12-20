import client.AggregateDao;
import client.TweetDao;
import model.AggregatedTweets;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class MainWriteAggregatesToDynamo {

    public static void main(String[] args) {

        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String aggTableName = "etai-50000-aggregate";
        String tweetTableName = "etai-twitter-50000-sentiment";

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String beginDate = "2018-10-15T00:00:00Z";
        String endDate = "2018-11-28T00:00:00Z";
        TemporalAccessor beginTemporalAccessor = timeFormatter.parse(beginDate);
        TemporalAccessor endTemporalAccessor = timeFormatter.parse(endDate);
        Instant beginTime = Instant.from(beginTemporalAccessor);
        Instant endTime = Instant.from(endTemporalAccessor);

        AggregateDao aggregateDao = new AggregateDao(username, password, aggTableName);
        TweetDao tweetDao = new TweetDao(username, password, tweetTableName);

        Instant timeTally = beginTime;

        while(timeTally.isBefore(endTime)) {
            String timestamp = timeTally.toString();
            AggregatedTweets aggregatedTweets = tweetDao.getAllTweetsWithTimestamp(timestamp);
            aggregateDao.putItem(aggregatedTweets);
            System.out.println(aggregatedTweets);
            timeTally = timeTally.plus(10, ChronoUnit.MINUTES);
        }
    }
}
