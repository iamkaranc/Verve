package com.assignment.Verve.Controller;

import com.assignment.Verve.Exception.VerveServiceException;
import com.assignment.Verve.Service.Impl.VerveMetricServiceImpl;
import com.assignment.Verve.Service.VerveService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/verve")
public class VerveController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(VerveController.class));
    private static final Gson gson = new GsonBuilder().setDateFormat("dd/mm/yyyy HH:mm:ss").create();

    public static final String OK = "ok";
    public static final String FAILED = "failed";

    @Autowired
    private VerveService verveService;

    @GetMapping("/healthCheck")
    public ResponseEntity<String> healthCheck() {
        logger.info("Successfully started the KeyManagement Application");
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("/accept")
    public String acceptRequest(@RequestParam String id, @RequestParam(required = false) String endPoint, @RequestParam(required = false) Boolean doPostToEndpoint) {
        logger.info("*** Inside the acceptRequest Method***");

        if(endPoint != null) {
            try {
                verveService.callRequestEndPoint(id, endPoint, doPostToEndpoint);
            } catch (Exception | VerveServiceException e) {
                logger.info("Unable to parse the request body");
                return FAILED;
            }
        }
        return OK;


    }

}
