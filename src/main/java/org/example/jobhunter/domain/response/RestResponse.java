package org.example.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResponse<T> {

    private int statusCode;
    private String error;
    // message maybe string or arraylist
    private Object message;
    private T data;
}
