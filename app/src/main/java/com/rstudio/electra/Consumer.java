package com.rstudio.electra;

public class Consumer {
    private String name;
    private String address;
    private String email;
    private String district;
    private String division;
    private long consumerno;
    private long phone;

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public Consumer(){

    }
    public Consumer(String name, String address, String district, String division, long consumerno) {
        this.name = name;
        this.address = address;
        this.district = district;
        this.division = division;
        this.consumerno = consumerno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public long getConsumerno() {
        return consumerno;
    }

    public void setConsumerno(long consumerno) {
        this.consumerno = consumerno;
    }
}
