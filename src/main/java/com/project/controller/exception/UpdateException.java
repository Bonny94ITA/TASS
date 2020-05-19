package com.project.controller.exception;

import java.util.Objects;

public class UpdateException extends Exception {
    private String exceptionDescription;

    public UpdateException(String error) {
        this.exceptionDescription = error;
    }

    public String getExceptionDescription() {
        return exceptionDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateException that = (UpdateException) o;
        return Objects.equals(exceptionDescription, that.exceptionDescription);
    }

    @Override
    public String toString() {
        return "UpdateException{" +
                "exceptionDescription='" + exceptionDescription + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionDescription);
    }

    public void setExceptionDescription(String exceptionDescription) {
        this.exceptionDescription = exceptionDescription;
    }
}
