package com.mldtech.nilapp;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, Deque<Long>> requests = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 20; // per minute
    private final long WINDOW = 60_000;

    public boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();

        requests.putIfAbsent(ip, new ArrayDeque<>());
        Deque<Long> times = requests.get(ip);

        while (!times.isEmpty() && times.peekFirst() < now - WINDOW) {
            times.pollFirst();
        }

        if (times.size() >= MAX_REQUESTS) {
            return false;
        }

        times.addLast(now);
        return true;
    }
}
