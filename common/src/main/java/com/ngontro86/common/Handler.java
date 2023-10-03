package com.ngontro86.common;

public interface Handler<T> {
    boolean handle(T obj);
}
