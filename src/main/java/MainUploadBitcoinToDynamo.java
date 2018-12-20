import client.AggregateDao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MainUploadBitcoinToDynamo {

    public static void main(String[] args) {
        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String tableName = "etai-50000-by-hour";

        AggregateDao aggregateDao = new AggregateDao(username, password, tableName);

        String csvFile = "../GOOD_BTC_RAW.csv";
        BufferedReader br;
        String line;
        String cvsSplitBy = ",";

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                String[] data = line.split(cvsSplitBy);
                if (!data[1].equals("Date")) {
                    String timestamp = data[1].replace("T:", "T");
                    float price = Float.parseFloat(data[6]);
                    Instant instant = Instant.from(timeFormatter.parse(timestamp)).truncatedTo(ChronoUnit.HOURS);
                    aggregateDao.updateWithBTCPrice(instant.toString(), price);
                    System.out.println("Did " + timestamp);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
