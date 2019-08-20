package com.stylefeng.guns.rest.common.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

// 令牌桶法限流工具
public class TokenBucket {
    private int capacity = 100; //桶大小
    private int rate = 1; //桶令牌增添速率（每毫秒）
    private volatile int nowTokens;  //当前令牌数
    private long timestamp; //时间戳

    public TokenBucket() {
        nowTokens = this.capacity;
        timestamp = getNowTime();
    }


    private long getNowTime() {
        return System.currentTimeMillis();
    }

    public synchronized boolean getToken() {
        long addTokens = (getNowTime() - timestamp) * rate;

        nowTokens += addTokens;
        timestamp = getNowTime();
        nowTokens = Math.min(capacity, nowTokens);
        if (nowTokens > 0) {
            nowTokens--;
            return true;
        } else {
            return false;
        }
    }

    @Test
    public void testToken() throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket();
        for (int i = 0; i < 200; i++) {
            if (i == 80) {
                Thread.sleep(80);
            }
            System.out.println("当前请求" + i + "的令牌：" + tokenBucket.getToken());
        }
    }
}
