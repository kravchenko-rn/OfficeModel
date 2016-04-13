package com.company.exceptions;

public class InvalidRandomBoundariesException extends Exception {
    public InvalidRandomBoundariesException() {
        super("�������� 'min' ������ �������� 'max'.");
    }
}
