package com.artsolo.bookswap.exceptions;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String data, Long id) {
        super(String.format("%s with id '%d' not found", data, id));
    }

    public NoDataFoundException(String data, Long userId, Long bookId) {
        super(String.format("%s with id ('%d','%d') not found", data, userId, bookId));
    }
}
