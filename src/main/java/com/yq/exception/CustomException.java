package com.yq.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 自定义异常
 */
@Data
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private String msg;//异常消息
}
