package com.example.linkup.exception;

public class UnexpectedNullElementException extends Exception{

    public UnexpectedNullElementException(){
        super("意外的错误，请稍后重试");
    }

    public UnexpectedNullElementException(String message){
        super(message);
    }
}
