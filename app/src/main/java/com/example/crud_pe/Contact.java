package com.example.crud_pe;

public class Contact {

    private String id ;
    private String name ;
    private String email ;
    private String company ;
    private String address ;
    private String mImageUrl;

    public Contact(){} ;

    public Contact(String id, String name, String email, String company, String address, String mImageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.company = company;
        this.address = address;
        this.mImageUrl = mImageUrl;
    }

    public Contact(String id, String name, String email, String company, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.company = company;
        this.address = address;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
