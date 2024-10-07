//Thought process for the Verve Application

-Created a Spring Boot application which will be able to process atleast 10K requests per sec.
-Used Controller approach for writing the REST API.

PRE-REQUISITES:
Java 17, 
IDE (preferably Intellij)
Kafka (For additional extensions)

1. API's
    1.1 Written a GET API named /healthcheck to check whether the application is up and running or not.
    1.2 Written a GET API named /accept 
    1.3 Takes String id, String endPoint as a parameter
    1.4 This API returns OK if the request is successfully processed or returns FAILED if there is any error.
    1.5 Below is the simple curl for the same

    curl --location 'localhost:8080/api/verve/accept?id=2&endPoint=https%3A%2F%2Fjsonplaceholder.typicode.com%2Ftodos%2F1' \
    --header 'Cookie: JSESSIONID=5D1F692BF02392868F45FC9D8EDE8924'

    1.6 Endpoint is a dummy endpoint which is used to check if the request is returning success response or not.

2. Packages and Classes
    2.1 Made different packages namely Controller, Exception, Helper, Service to separate the similar type of classes or interfaces.
    2.2 Wrote a VerveServiceException class to create my own exception instead of using the generic ones.
    2.3 Constants Class for all the constants at the one place
    2.4 ServiceHelper and VerveMetric for pushing the metrics.

3. Logic
    3.1 Controller has a api gateway which calls the VerveServiceImpl via an interface.
    3.2 VerveServiceImpl builds queryMap template for the endpoint to be hit using the REST template.
    3.3 Metric Service is used to send the metrics for every 1 minute. I have written the CRON for the samme. 
    3.4 For the cron, I took a concurrent hashMap which keeps the track of id and it's count.
    3.5 The same cron will write in the app.log file which is created in the /logs folder. This will be replaced after every 10 MB mentioned in log4j2.xml file.
    3.6 The same cron will send the data in the kafka topic namely (verve) every minute which is a part of an extension.

4. Extensions
    4.1 Extension 1:
    4.2 Did the Post REST call to the endPoint if the api parameter is not null, namely doPostToEndPoint
    4.3 CURL 
    curl --location 'localhost:8080/api/verve/accept?id=2&endPoint=https%3A%2F%2Fjsonplaceholder.typicode.com%2Ftodos%2F1&doPostToEndPoint=true' \
    --header 'Cookie: JSESSIONID=5D1F692BF02392868F45FC9D8EDE8924'
    4.4 The above curl will make the POST call to the given endpoint
    4.5 Extension 2:
    4.6 Id deduplication works even when it is behind the load balancer.
    4.7 Extension 3:
    4.8 The count of unique Ids has been sent to the distributed kafka system as well.


