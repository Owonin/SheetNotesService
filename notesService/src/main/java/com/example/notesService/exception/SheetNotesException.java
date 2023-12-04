package com.example.notesService.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class SheetNotesException extends ResponseStatusException {

    public SheetNotesException(HttpStatusCode status, String reason) {
        super(status, reason);
        this.setDetail(reason);
    }
}
