package com.example.common.enumeration;

/**
 * @author Arte
 * RabbitMQ 状态
 */
public enum Action {

    /**
     * 处理成功
     */
    ACCEPT,

    /**
     * 可以重试的错误
     */
    RETRY,

    /**
     * 无需重试的错误
     */
    REJECT
}
