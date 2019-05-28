package com;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;

public class TestIn {


private static Callable<Integer> updateReimAgentsCall = new Callable<Integer>() {
    @Override
    public Integer  call() throws Exception {
    	int i=0;
       try{
    	   System.out.println("1111");
    	   i=2;
    	   throw new Exception("查询异常");
    	   
       }
       catch(Exception e ){
    	   System.out.println("2222");
    	   
       }
       // List<OAReimAgents> oaReimAgents = JSON.parseArray(result, OAReimAgents.class);
       /* if(CollectionUtils.isNotEmpty(oaReimAgents)){
            CacheUtil.put(Constants.REIM_AGENT_KEY,oaReimAgents);
            return true;
        }*/
        return i;
    }

};
public static void main(String[] args) {
	
	Retryer<Integer> retryer = RetryerBuilder
			.<Integer>newBuilder()
			//抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。 //如果异常会重试
			.retryIfException()
			.retryIfRuntimeException()// 设置异常重试源
			//返回false也需要重试
			//.retryIfResult(Predicates.equalTo(false))
			.retryIfResult(Predicates.equalTo(2))
			//重调策略
			//.withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.SECONDS))// 设置每次重试间隔，5秒
			.withWaitStrategy(WaitStrategies.incrementingWait(3, TimeUnit.SECONDS,1,TimeUnit.SECONDS))
			//.withAttemptTimeLimiter(AttemptTimeLimiters.<Integer>fixedTimeLimit(100, TimeUnit.SECONDS))
			.withRetryListener(new MyRetryListener())
			//尝试次数
			.withStopStrategy(StopStrategies.stopAfterAttempt(3))// 设置重试5次，同样可以设置重试超时时间
			.build();
             Map map = new HashMap<>();
			try {
				int result = retryer.call(new Callable<Integer>() {  
			        @Override
			        public Integer call() throws Exception { 
			          try { 
			            //特别注意：返回false说明无需重试，返回true说明需要继续重试 
			            //return uploadToOdps(map); 
			        	  return 2; 
			          } catch (Exception e) { 
			            throw new Exception(e); 
			          } 
			        } 
			      }); 
			} catch (ExecutionException e) {
			    System.out.println("------");
			} catch (RetryException e) {
			  // logger.error("更新可代理报销人异常,需要发送提醒邮件");
				System.out.println("--更新可代理报销人异常,需要发送提醒邮件--");
			}
}

	
}
