package com.bam.mokracki.projekt.entity;

public class SecretEntity {
    Integer id;
    String creditCardNumber;

    public SecretEntity(Integer id, String creditCardNumber) {
        this.id = id;
        this.creditCardNumber = creditCardNumber;
    }

    public SecretEntity(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
