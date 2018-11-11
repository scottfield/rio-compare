package com.jackiew.demo.rio.excel;

/**
 * define not find found exception such as method or field etc.
 */
class NotFoundException extends RuntimeException {
    NotFoundException(String message) {
        super(message);
    }
}
