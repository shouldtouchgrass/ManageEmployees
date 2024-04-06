package com.ashstudios.safana.models;

public class TaskModel {
    private String taskID;
    private String name;
    private String date;

    public TaskModel(String taskID, String name, String date) {
        this.taskID = taskID;
        this.name = name;
        this.date = date;
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
}
