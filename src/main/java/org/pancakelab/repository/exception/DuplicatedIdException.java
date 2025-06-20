package org.pancakelab.repository.exception;

import org.pancakelab.model.exception.DomainException;

public class DuplicatedIdException extends DomainException {
    public DuplicatedIdException(String message) {
        super(message);
    }
}
