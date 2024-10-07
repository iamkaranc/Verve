package com.assignment.Verve.Service.Impl;

import com.assignment.Verve.Controller.VerveController;
import com.assignment.Verve.Exception.VerveServiceException;
import com.assignment.Verve.Service.VerveMetricService;
import com.assignment.Verve.Service.VerveService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.assignment.Verve.Helper.Constants.*;
import static com.assignment.Verve.Helper.ServiceHelper.*;

@Service
public class VerveServiceImpl implements VerveService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(VerveServiceImpl.class));

    @Autowired
    VerveMetricService verveMetricService;
    private RestTemplate restTemplate;

    @Autowired
    public VerveServiceImpl(VerveMetricService verveMetricService, RestTemplate restTemplate) {
        this.verveMetricService = verveMetricService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void callRequestEndPoint(String id, String endPoint, Boolean doPostToEndPoint) throws VerveServiceException {
        String endpointUrlTemplate = buildEndpointUrlTemplate(endPoint);

        String timestamp = Calendar.getInstance().toString();
        Map<String, Integer> params = buildQueryParamMap(id, timestamp);

        try {
            if(doPostToEndPoint == null) {

                //Do the GET call if the doPostToEndPoint parameter is NULL

                ResponseEntity<Object> responseEntity = restTemplate.getForEntity(endpointUrlTemplate, Object.class, params);
                logger.info(String.format(ENDPOINT_RESPONSE_STATUS, responseEntity.getStatusCode()));
            }
            else {
                //Do the POST call if the doPostToEndPoint parameter is NULL
                ResponseEntity<Object> responseEntity = restTemplate.postForEntity(endpointUrlTemplate, Object.class, Object.class, params);
                logger.info(String.format(ENDPOINT_RESPONSE_STATUS, responseEntity.getStatusCode()));
            }

        } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {

            logger.info(String.format(ENDPOINT_RESPONSE_STATUS, httpClientOrServerExc.getStatusCode()));
            throw new VerveServiceException(String.format(REST_SERVICE_EXCEPTION_ERROR_MSG, id, endPoint));

        } catch (Exception ex) {
            logger.info(ex.getMessage());
            logger.info(String.format(APPLICATION_COULD_NOT_PROCESS_THE_RESPONSE_ERROR_MSG, id, endPoint));
            throw ex;

        }
    }

    private Map<String, Integer> buildQueryParamMap(String id, String timestamp) {
        Map<String, Integer> params = new HashMap<>();
        Integer uniqueRequestCount = getUniqueRequestCount(id, timestamp);
        params.put("count", uniqueRequestCount);
        return params;
    }

    private Integer getUniqueRequestCount(String id, String timestamp) {
        return verveMetricService.getRequestCountForId(timestamp, id);
    }

}
