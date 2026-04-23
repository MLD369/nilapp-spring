package com.mldtech.nilapp.helper;


import org.springframework.http.HttpStatus;

import java.time.Instant;

// TODO Might not need this class only used to send custom messages to UI like  update the app by this date
public class CustomResponse<T> {
    private String time = Instant.now().toString();

    private String customMessage; // to send user needs to update?

    private int pageSize; // used for pagination request for amount of records

    private int pageNumber; // used for pagination request to know pageNumber

    private HttpStatus statusText;

    private String statusCode;

    private T object;

    public CustomResponse() {
    }
    public CustomResponse(HttpStatus statusText, String statusCode) {
        this.time = Instant.now().toString();
        this.object = null;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }
    public CustomResponse(T object, HttpStatus statusText) {
        this.time = Instant.now().toString();
        this.object = object;
        this.statusText = statusText;
    }

    public CustomResponse(T object, HttpStatus statusText, String statusCode) {
        this.time = Instant.now().toString();
        this.object = object;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }
//    public CustomResponse(User object, HttpStatus statusText, String statusCode) {
//        this.time = Instant.now().toString();
//        this.object = object;
//        this.statusText = statusText;
//        this.statusCode = statusCode;
//    }
//
//    public CustomResponse(AtmFee object, HttpStatus statusText, String statusCode) {
//        this.time = Instant.now().toString();
//        this.object = object;
//        this.statusText = statusText;
//        this.statusCode = statusCode;
//    }

    public CustomResponse(String customMessage, T object, HttpStatus statusText, String statusCode) {
        this.time = Instant.now().toString();
        this.customMessage = customMessage;
        this.object = object;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }

    public CustomResponse(String customMessage, T object, HttpStatus statusText, String statusCode, int pageSize, int pageNumber) {
        this.customMessage = customMessage;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.object = object;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }

    public CustomResponse(T object, HttpStatus statusText, String statusCode, int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.object = object;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }

    public CustomResponse(String customMessage, HttpStatus statusText, String statusCode) {
        this.time = Instant.now().toString();
        this.customMessage = customMessage;
        this.statusText = statusText;
        this.statusCode = statusCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        this.time = Instant.now().toString();
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setTime(String time) {
        this.time = Instant.now().toString();
    }

    public Object getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public HttpStatus getStatusText() {
        return statusText;
    }

    public void setStatusText(HttpStatus statusText) {
        this.statusText = statusText;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
