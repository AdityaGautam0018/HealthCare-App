package com.example.healthcare_app;

public class UserDetails {
    public String Name, DOB, Height,Weight, Gender;
    public UserDetails(){};

    public UserDetails(String textName, String textDOB, String textHeight,String textWeight, String textGender){
        this.Name = textName;
        this.DOB = textDOB;
        this.Height = textHeight;
        this.Weight = textWeight;
        this.Gender = textGender;

    }
}
