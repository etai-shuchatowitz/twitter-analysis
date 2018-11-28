package client;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

public class S3Connection {

    private BasicAWSCredentials awsCredentials;
    private AWSCredentialsProviderChain credentials;
    private AmazonS3 s3Client;
    private String bucketName;

    public S3Connection(String username, String password, String bucketName) {
        awsCredentials = new BasicAWSCredentials(username, password);
        credentials = new AWSCredentialsProviderChain(new AWSStaticCredentialsProvider(awsCredentials));
        s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentials).withRegion("us-east-1").build();
        this.bucketName = bucketName;
    }

    public List<String> getKeys() {
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result result;
        List<String> keys = new ArrayList<>();
        do {
            result = s3Client.listObjectsV2(req);

            for (S3ObjectSummary objectSummary :
                    result.getObjectSummaries()) {
                keys.add((objectSummary.getKey()));
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() +
                        ")");
            }
            System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
            req.setContinuationToken(result.getNextContinuationToken());
        } while(result.isTruncated());

        return keys;
    }

    public S3Object getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        return s3Client.getObject(getObjectRequest);
    }

}
