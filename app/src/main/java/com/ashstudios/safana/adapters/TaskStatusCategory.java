package com.ashstudios.safana.adapters;

public class TaskStatusCategory {
    private String name;
    public TaskStatusCategory(String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
