package com.assignment.Verve.Service.Impl;

import com.assignment.Verve.Service.VerveMetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static com.assignment.Verve.Helper.Constants.COUNT_OF_UNIQUE_REQUESTS_IN_CURRENT_MINUTE_MSG;
import static com.assignment.Verve.Helper.Constants.DATE_FORMAT;
import static com.assignment.Verve.Helper.ServiceHelper.calculateLastMinute;
//import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;

@Service
public class VerveMetricServiceImpl implements VerveMetricService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(VerveMetricServiceImpl.class));
    private Map<String, Map<String, Integer>> timeMap = new ConcurrentHashMap<>();
    // Autowiring Kafka Template
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "verve";

    @Override
    public synchronized void increaseRequestCount(String time, String id) {

        Map<String, Integer> idUniqueCountMap = timeMap.get(time);
        if (idUniqueCountMap == null) {
            idUniqueCountMap = new ConcurrentHashMap<>();
        }

        Integer uniqueCount = idUniqueCountMap.get(id);
        if (uniqueCount == null) {
            uniqueCount = 1;
        } else {
            uniqueCount++;
        }
        idUniqueCountMap.put(id, uniqueCount);
        timeMap.put(time, idUniqueCountMap);
    }

    @Override
    public synchronized Integer getRequestCountForId(String time, String id) {
        return timeMap.get(time) != null ? timeMap.get(time).get(id) : 0 ;
    }

    @Scheduled(cron = "${log.counter.cron}")
    public void logCountOfUniqueRequests() {
        logger.info(COUNT_OF_UNIQUE_REQUESTS_IN_CURRENT_MINUTE_MSG);
        timeMap.getOrDefault(calculateLastMinute(), Collections.emptyMap()).entrySet().forEach(uniqueRequestEntry ->
                logger.info(String.format("\tid: %s \tCount: %d", uniqueRequestEntry.getKey(), uniqueRequestEntry.getValue())));


        //Extension 3: Sending the logs to the kafka distributed service

        String lastMinute = calculateLastMinute();
        Map<String, Integer> myMap = timeMap.get(lastMinute);
        if(myMap != null) {
            for(Map.Entry<String, Integer> stringIntegerMap: myMap.entrySet()) {
                String dataToSend = "id: " + stringIntegerMap.getKey() + ", count: " + stringIntegerMap.getValue();
                kafkaTemplate.send(TOPIC, dataToSend);
            }
        }

        //kafkaTemplate.send(TOPIC, );

    }

    @Scheduled(cron = "${counter.cleanup.cron}")
    public void cleanup() {
        logger.info("TimeMap cleanup");
        String now = DATE_FORMAT.format(String.valueOf(new Date()));
        String lastMinute = calculateLastMinute();
        if (timeMap.get(now) == null && timeMap.get(lastMinute) == null) {
            timeMap.clear();
        } else {
            Map<String, Map<String, Integer>> backupTimeMap = Collections.emptyMap();
            if(timeMap.containsKey(now)) {
                backupTimeMap.put(now, timeMap.get(now));
            }
            if(timeMap.containsKey(lastMinute)) {
                backupTimeMap.put(lastMinute, timeMap.get(lastMinute));
            }

            timeMap.clear();
            timeMap.putAll(backupTimeMap);
        }
    }
}
