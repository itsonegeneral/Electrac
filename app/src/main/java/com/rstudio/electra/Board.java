package com.rstudio.electra;

public class Board {
    private String district;
    private String division;
    private String oneSignalId;
    private int sectionId;
    private long phone;

    public Board(){

    }
    public Board(String district, String division, String oneSignalId, int sectionId, long phone) {
        this.district = district;
        this.division = division;
        this.oneSignalId = oneSignalId;
        this.sectionId = sectionId;
        this.phone = phone;
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

    public String getOneSignalId() {
        return oneSignalId;
    }

    public void setOneSignalId(String oneSignalId) {
        this.oneSignalId = oneSignalId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}
