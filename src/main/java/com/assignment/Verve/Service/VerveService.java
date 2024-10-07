package com.assignment.Verve.Service;

import com.assignment.Verve.Exception.VerveServiceException;

public interface VerveService {

    void callRequestEndPoint(String id, String endPoint, Boolean doPostToEndPoint) throws VerveServiceException;
}
