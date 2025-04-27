package com.kafka.watcher.kafkawatcher.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtil extends Exception {
    

    @Override
    public String toString() {
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        super.printStackTrace(pw);
        String message = sw.toString();
        return message;
    }
}
