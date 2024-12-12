package com.hookify.core;

public record RetryPolicy(int maxRetries, long retryDelay) {

}
