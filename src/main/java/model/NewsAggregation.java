package model;

public class NewsAggregation {

    private String timestamp;
    private float titleSentiments;
    private float descriptionSentiments;
    private float contentSentiments;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getTitleSentiments() {
        return titleSentiments;
    }

    public void setTitleSentiments(float titleSentiments) {
        this.titleSentiments = titleSentiments;
    }

    public float getDescriptionSentiments() {
        return descriptionSentiments;
    }

    public void setDescriptionSentiments(float descriptionSentiments) {
        this.descriptionSentiments = descriptionSentiments;
    }

    public float getContentSentiments() {
        return contentSentiments;
    }

    public void setContentSentiments(float contentSentiments) {
        this.contentSentiments = contentSentiments;
    }

    @Override
    public String toString() {
        return "NewsAggregation{" +
                "timestamp='" + timestamp + '\'' +
                ", titleSentiments=" + titleSentiments +
                ", descriptionSentiments=" + descriptionSentiments +
                ", contentSentiments=" + contentSentiments +
                ", url='" + url + '\'' +
                '}';
    }
}
