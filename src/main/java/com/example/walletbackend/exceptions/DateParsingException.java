package com.example.walletbackend.exceptions;

import java.text.ParseException;

public class DateParsingException extends Exception {


    public DateParsingException(String errorMessage){

        super(errorMessage);
    }

}
