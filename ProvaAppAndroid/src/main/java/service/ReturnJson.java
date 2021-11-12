package service;

import DAO.BookedRepetitions;

public class ReturnJson {
    private boolean done;
    private String account;
    private String name;
    private String surname;
    private BookedRepetitions[] bookedRepetitions;


    public ReturnJson(boolean done, String account, String name, String surname) {
        this.done = done;
        this.account = account;
        this.name = name;
        this.surname = surname;
    }
}
