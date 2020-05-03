package com.project.controller.DataException;

import com.project.controller.DataFormatter.OutputData;

import java.util.Objects;

public class InsertException extends Exception {
    private OutputData.ResultCode exceptionCode;
    private String exceptionDescription;

    public InsertException(OutputData.ResultCode insertError, String error) {
        this.exceptionCode = insertError;
        this.exceptionDescription = error;
    }

    public OutputData.ResultCode getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(OutputData.ResultCode exceptionCode) {
        this.exceptionCode = exceptionCode;
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
        return exceptionCode == that.exceptionCode &&
                Objects.equals(exceptionDescription, that.exceptionDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exceptionCode, exceptionDescription);
    }

    @Override
    public String toString() {
        return "InsertException{" +
                "exceptionCode=" + exceptionCode +
                ", exceptionDescription='" + exceptionDescription + '\'' +
                '}';
    }
}
