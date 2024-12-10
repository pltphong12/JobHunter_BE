package org.example.jobhunter.util;

import jakarta.servlet.http.HttpServletResponse;
import org.example.jobhunter.domain.response.RestResponse;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpResponse.getStatus();
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);
        // case error
        if (status >= 400) {
            return body;
        }else {
            restResponse.setData(body);
            ApiMessage apiMessage = returnType .getMethodAnnotation(ApiMessage.class);
            String message = (apiMessage != null) ? apiMessage.value() : "CALL API SUCCESSFULLY";
            restResponse.setMessage(message);
        }
        return restResponse;
    }
}
