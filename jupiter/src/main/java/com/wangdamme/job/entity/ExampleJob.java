package com.wangdamme.job.entity;

public class ExampleJob {

    public String title;
    public int salary;
    public String starting;
    public boolean remote;
    public ExampleCoordinates coordinates;

    //快捷键 windows+alt+insert
    public ExampleJob(String title, int salary, String starting, boolean remote, ExampleCoordinates coordinates) {
        this.title = title;
        this.salary = salary;
        this.starting = starting;
        this.remote = remote;
        this.coordinates = coordinates;
    }


}
