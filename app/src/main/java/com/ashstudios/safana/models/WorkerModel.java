package com.ashstudios.safana.models;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class WorkerModel {
    String name,role,profile_img,emp_id,mail,mobile,sex,birthdate,password;
    List<String> allowance_ids;
    boolean isSelected;

    public WorkerModel() {
    }

    public WorkerModel(String name, String role, String profile_img, String emp_id, String mail, String mobile, String sex, String birthdate, String password,  List<String> allowance_ids) {
        this.name = name;
        this.role = role;
        this.profile_img = profile_img;
        this.emp_id = emp_id;
        this.mail = mail;
        this.mobile = mobile;
        this.sex = sex;
        this.birthdate = birthdate;
        this.password = password;
        this.allowance_ids = allowance_ids;
        isSelected = false;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @PropertyName("profile_image")
    public String getImgUrl() {
        return profile_img;
    }

    @PropertyName("profile_image")
    public void setImgUrl(String profile_img) {
        this.profile_img = profile_img;
    }

    @PropertyName("birth_date")
    public String getBirthdate() {
        return birthdate;
    }

    @PropertyName("birth_date")
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public List<String> getAllowance_ids() {
        return allowance_ids;
    }

    public void setAllowance_ids(List<String> allowance_ids) {
        this.allowance_ids = allowance_ids;
    }
}
