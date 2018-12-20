# Twitter and Bitcoin Prices

## Overview

This features all of the code written for the paper "Twitter Does Not Predict the Stock Market". It features three primary components:
1. A python script used to acquire all of the Bitcoin data and put it into S3. This can be found in `src/python/stream/`
2. Java libraries that were done for all data preprocessing. These include
    * `MainWriteSentimentToDynamo` - This is the class that takes in a string for MM-d which gets all tweets in that 10-day range, get their sentiment and upload it to Dynamo. For example,
        passing in "10-1" will run that program for all days between October 10 - October 19. It aggregates by ten-minute interval.
    * `MainWriteAggregatesToDynamo` - This is run after (a) and further aggregates the data in (a) and puts it into a new table
    * `MainUploadBitcoinToDynamo` - This uploads the corresponding Bitcoin data by hour to the dynamo table.
    * `MainRoundToHour` - This rounds the data gotten in (b) to the hour and reuploads it to Dynamo
    * `BackTradeMain` - This runs the backtrade game described in the paper.
    * `MainSlidingWindow` - This runs the sliding window approach described in the paper and outputs all of the data into csvs
3. Two python notebooks which contain all of the modeling described in the paper

## Data

All of the data used in this project can be found:

https://drive.google.com/open?id=1cS_KJhDPqjYWPyPUH5de0Kc5nzPOr_Jw

## How to build

To build simply run 

```
git clone https://github.com/etai-shuchatowitz/twitter-analysis.git
cd twitter-analysis
mvn clean package
```

And I would recommend running in your favorite IDE.

NOTE: All of the Java code uses AWS credentials which have been removed as this is a public repository. Similarly, everything that is a main class
has its table names hard-coded in. It will error out and is not runnable from a different account. I have included it as an illustrative example of how
I did this

## Running 

To run, first you must get the data and download it to your computer. The notebooks are all written under the assumption that data is in

`src/main/resources`

The reason the data is not public here is that there are simply too many csv files used in the sliding-window code, and github did not allow me to upload 
two thousand csvs.

Once it's there, you must get the libraries used, which can be done using pip
```
pip install csv jupyter pandas numpy matplotlib os glob
jupyter notebook
```

All of the corresponding lines can be run in the notebook in the usual way.
