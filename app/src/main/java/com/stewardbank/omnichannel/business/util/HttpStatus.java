package com.stewardbank.omnichannel.business.util;

/**
 * @uthor Tasu Muzinda
 */
public enum HttpStatus {

    OK(1), CONFLICT(2), ERROR(3);

    private final Integer status;

    private HttpStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }
}
