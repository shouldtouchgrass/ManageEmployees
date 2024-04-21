package com.ashstudios.safana.models;

public class TaskModel {

    private String taskID;
    private String name;
    private String date;
    private String status;
    private String empid;

    public TaskModel(String status, String taskID, String name, String date, String empid) {
        this.status = status;
        this.taskID = taskID;
        this.name = name;
        this.date = date;
        this.empid= empid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getTaskID(){
        return taskID;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
    public String getStatus(){
        return status;
    }
}