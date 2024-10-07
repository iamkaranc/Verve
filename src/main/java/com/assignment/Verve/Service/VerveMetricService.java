package com.assignment.Verve.Service;

public interface VerveMetricService {

    void increaseRequestCount(String time, String id);

    Integer getRequestCountForId(String time, String id);
}
