package com.kafka.watcher.kafkawatcher.Utils;

import org.apache.kafka.common.KafkaFuture;
import org.springframework.stereotype.Service;

import com.kafka.watcher.kafkawatcher.Models.MethodResult;

@Service
public class KafkaFutureTaskUtil {
    
    public KafkaFutureTaskUtil(){

    }

    public <T> MethodResult waitTask(KafkaFuture<T> task)
    {
        MethodResult result = new MethodResult();
        while (!task.isCancelled() && !task.isDone()) {
            try {
                System.out.println(String.format("Wait for task status %s - %s", task, task.state()));
                Thread.sleep(100);

            } catch (InterruptedException e) {
                result.setMessage(e.toString());
                result.setResult(false);
                return result;
            }
        }
        if(task.isCompletedExceptionally())
        {
            System.out.println("Task failed with exception");
            result.setMessage(task.exceptionNow().toString());
            result.setResult(false);
            return result;
        }
        
        if(task.isCancelled())
        {
            System.out.println("Task is cancaled.");
            result.setMessage("Task canceled.");
            result.setResult(true);
            return result;
        }
        if(task.isDone())
        {
            
            result.setMessage("");
            result.setResult(true);
            return result;
            
        }
        return result;

    }

}
