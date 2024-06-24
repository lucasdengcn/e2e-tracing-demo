package com.service.model;

public enum Status {

    CREATED("CREATED"),
    SUBMITTED("SUBMITTED"),
    REJECTED("REJECTED"),
    ACCEPTED("ACCEPTED");

    private String name;
    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Status find(String name){
        if (name.equalsIgnoreCase(CREATED.name)){
            return CREATED;
        }
        if (name.equalsIgnoreCase(SUBMITTED.name)){
            return SUBMITTED;
        }
        if (name.equalsIgnoreCase(REJECTED.name)){
            return REJECTED;
        }
        if (name.equalsIgnoreCase(ACCEPTED.name)){
            return ACCEPTED;
        }
        return null;
    }

}
