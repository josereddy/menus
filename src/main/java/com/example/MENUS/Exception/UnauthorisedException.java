package com.example.MENUS.Exception;

public class UnauthorisedException extends  RuntimeException{

    public UnauthorisedException(String msg)
    {
        super(msg);
    }
}
