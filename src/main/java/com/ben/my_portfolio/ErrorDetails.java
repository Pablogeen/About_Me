package com.ben.my_portfolio;

import java.time.LocalDateTime;

public record ErrorDetails( String errorCode, String message, String details, LocalDateTime timeStamp) {}
