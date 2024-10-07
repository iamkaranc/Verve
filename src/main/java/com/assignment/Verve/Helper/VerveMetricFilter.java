package com.assignment.Verve.Helper;

import com.assignment.Verve.Service.Impl.VerveMetricServiceImpl;
import com.assignment.Verve.Service.VerveMetricService;
import jakarta.servlet.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.assignment.Verve.Helper.Constants.DATE_FORMAT;

@Component
public class VerveMetricFilter implements Filter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(String.valueOf(VerveMetricFilter.class));

    private VerveMetricService metricService;

    @Autowired
    public VerveMetricFilter(VerveMetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {
        String time = DATE_FORMAT.format(new Date());
        request.setAttribute("timestamp", time);

        String id = request.getParameter("id");
        logger.info("id:" + id);
        if(id != null) {
            metricService.increaseRequestCount(time, id);
        }

        chain.doFilter(request, response);

    }
}
