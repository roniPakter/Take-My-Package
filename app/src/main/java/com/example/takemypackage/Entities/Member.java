package com.example.takemypackage.Entities;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class Member {
    private String fName;
    private String lName;
    //private String id;
    private String address;
    private String email;
    private String phone;

    private String password;

    //---------------------------------------------------------------------------------------------------------------------
    public Member() {
    }

    public Member(String fName, String lName, String address, String phone, String email, String password) {
        this.fName = fName;
        this.lName = lName;
        //this.id = id;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    //---------------------------------------------------------------------------------------------------------------------
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

//   public String getId() {
//      return id;
//   }
//
//   public void setId(String id) {
//      this.id = id;
//   }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //-----------------------------------------Operations----------------------------------------------------------------------------

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
