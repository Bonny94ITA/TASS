package com.project.controller.DataFormatter;

import java.io.Serializable;
import java.util.Objects;

public class OutputData implements Serializable {
    public static enum ResultCode {
        RESULT_OK, INSERT_ERROR, UPDATE_ERROR, DELETE_ERROR
    }

    private Object returnedValue;
    private ResultCode resultCode;

    public OutputData() {
    }

    public Object getReturnedValue() {
        return returnedValue;
    }

    public void setReturnedValue(Object returnedValue) {
        this.returnedValue = returnedValue;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "DataFormatter{" +
                "returnedValue=" + returnedValue +
                ", resultCode=" + resultCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputData that = (OutputData) o;
        return resultCode == that.resultCode &&
                Objects.equals(returnedValue, that.returnedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnedValue, resultCode);
    }
}
