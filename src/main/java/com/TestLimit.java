package com;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Predicates;

public class TestLimit {
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");

    public static void main(String[] args) throws Exception {

        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfException()
                .retryIfResult(Predicates.equalTo(false))
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .build();

        System.out.println("begin..." + df.format(new Date()));

        try {
            retryer.call(buildTask());
        } catch (Exception e) {
            System.err.println("still failed after retry." + e.getCause().toString());
        }

        System.out.println("end..." + df.format(new Date()));

    }

    private static Callable<Boolean> buildTask() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                System.out.println("called");
                long start = System.currentTimeMillis();
                long end = start;
                while (end - start <= 10 * 1000) {
                    end = System.currentTimeMillis();
                }

                return true;
            }
        };
    }
}
