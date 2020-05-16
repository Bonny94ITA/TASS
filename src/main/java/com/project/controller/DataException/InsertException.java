package com.project.controller.DataException;

import java.util.Objects;

public class InsertException extends Exception {
    private String exceptionDescription;

    public InsertException(String error) {
        this.exceptionDescription = error;
    }

    public String getExceptionDescription() {
        return exceptionDescription;
    }

    public void setExceptionDescription(String exceptionDescription) {
        this.exceptionDescription = exceptionDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsertException that = (InsertException) o;
        return Objects.equals(exceptionDescription, that.exceptionDescription);
    }

    @Override
    public String toString() {
        return "InsertException{" +
                "exceptionDescription='" + exceptionDescription + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionDescription);
    }
}
