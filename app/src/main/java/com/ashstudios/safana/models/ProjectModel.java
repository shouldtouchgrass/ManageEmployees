package com.ashstudios.safana.models;

import java.util.ArrayList;

public class ProjectModel {
    private String projectID;
    private ArrayList<String> taskStatusList;
    private String title;
    private String startDate;
    private String dueDate;

    public ProjectModel(String projectID, ArrayList<String> taskStatusList, String title, String startDate, String dueDate){
        this.projectID = projectID;
        this.taskStatusList = taskStatusList;
        this.title = title;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public String getProjectID(){
        return projectID;
    }

    public ArrayList<String> getTaskStatusList(){
        return taskStatusList;
    }

    public String getTitle(){
        return title;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getDueDate(){
        return dueDate;
    }
}
