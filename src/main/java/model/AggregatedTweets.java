package model;

public class AggregatedTweets {

    private String timestamp;
    private String filename;
    private float sentimentScore;
    private int numberOfTweets;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public float getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(float sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public int getNumberOfTweets() {
        return numberOfTweets;
    }

    public void setNumberOfTweets(int numberOfTweets) {
        this.numberOfTweets = numberOfTweets;
    }
}
