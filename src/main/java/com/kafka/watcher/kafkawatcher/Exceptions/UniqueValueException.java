package com.kafka.watcher.kafkawatcher.Exceptions;


public class UniqueValueException extends Exception
{
    public UniqueValueException(String message){
        super("Value must be unique." + message);
    }
    public UniqueValueException(){
        super("Value must be unique");
    }
    public UniqueValueException(Throwable cause){
        super("Value must be unique",cause);
    }
    public UniqueValueException(String message,Throwable cause){
        super("Value must be unique." + message,cause);
    }
}

