package com.kafka.watcher.kafkawatcher.Exceptions;

public class ReadOnlyException extends Exception
{
    public ReadOnlyException(String message){
        super("Config key is read only, not editable. : " + message);
    }
    public ReadOnlyException(){
        super("Config key is read only, not editable.");
    }
    public ReadOnlyException(Throwable cause){
        super("Config key is read only, not editable.",cause);
    }
    public ReadOnlyException(String message,Throwable cause){
        super("Config key is read only, not editable." + message,cause);
    }
}