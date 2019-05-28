package com;

import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

public class RetryUtil {
	//出现异常则执行重试，每次任务执行最长执行时间限定为 3 s，重试间隔时间初始为 3 s，最多重试 10秒，随着重试次数的增加每次递增 1 s，每次重试失败，打印日志；
    public static <T> Retryer<T> getADefaultRetryer() {
        Retryer<T> retryer = RetryerBuilder.<T>newBuilder()
            .retryIfException()
            .withStopStrategy(
                StopStrategies.stopAfterDelay(10, TimeUnit.SECONDS))
            .withWaitStrategy(
                WaitStrategies.incrementingWait(3, TimeUnit.SECONDS, 1, TimeUnit.SECONDS))
            .withAttemptTimeLimiter(
                AttemptTimeLimiters.fixedTimeLimit(3, TimeUnit.SECONDS))
            .withRetryListener(new RetryListener() {
                @Override
                public <V> void onRetry(Attempt<V> attempt) {
                    //log.debug("retry time = " + attempt.getAttemptNumber());
                    if (attempt.hasException()) {
                        //log.error("try failed", attempt.getExceptionCause());
                    }
                }
            })
            .build();

        return retryer;
    }

}
