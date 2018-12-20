package model;

public class AggregatedTweets {

    private String timestamp;
    private String filename;
    private float sentimentScore;
    private int numberOfTweets;
    private float bitcoinClosingPrice;

    public float getBitcoinClosingPrice() {
        return bitcoinClosingPrice;
    }

    public void setBitcoinClosingPrice(float bitcoinClosingPrice) {
        this.bitcoinClosingPrice = bitcoinClosingPrice;
    }

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

    @Override
    public String toString() {
        return "AggregatedTweets{" +
                "timestamp='" + timestamp + '\'' +
                ", filename='" + filename + '\'' +
                ", sentimentScore=" + sentimentScore +
                ", numberOfTweets=" + numberOfTweets +
                ", bitcoinClosingPrice=" + bitcoinClosingPrice +
                '}';
    }
}
