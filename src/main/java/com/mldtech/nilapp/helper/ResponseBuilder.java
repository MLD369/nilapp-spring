package com.mldtech.nilapp.helper;

import org.springframework.http.HttpStatus;

public class ResponseBuilder {

    public static <T> CustomResponse<T> success(T object) {
        return new CustomResponse<>(
                object,
                HttpStatus.OK,
                "200"
        );
    }

    public static <T> CustomResponse<T> message(String msg) {
        return new CustomResponse<>(
                msg,
                null,
                HttpStatus.OK,
                "200"
        );
    }

    public static <T> CustomResponse<T> error(String msg) {
        return new CustomResponse<>(
                msg,
                null,
                HttpStatus.BAD_REQUEST,
                "400"
        );
    }
}
