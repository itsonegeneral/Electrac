package com.rstudio.electra;

public class Request {
    private String requestId;
    private String senderId;
    private String receiverId;
    private String time;
    private String requesttype;
    private String senderName;
    private String senderMessage;
    private long senderPhone;
    private long senderconsumerno,complaintconsumerno;
    private String status;
    private String senderOneSignalId;
    private boolean isMapAvailable,isSpecifyMoreAvailable;
    private double positionX;
    private double positionY;


    public Request(String time, String requesttype, String senderMessage, String status) {
        this.time = time;
        this.requesttype = requesttype;
        this.senderMessage = senderMessage;
        this.status = status;
    }

    public long getConsumerno() {
        return complaintconsumerno;
    }

    public void setConsumerno(long consumerno) {
        this.complaintconsumerno = consumerno;
    }

    public long getSenderconsumerno() {
        return senderconsumerno;
    }

    public void setSenderconsumerno(long senderconsumerno) {
        this.senderconsumerno = senderconsumerno;
    }


    public boolean isMapAvailable() {
        return isMapAvailable;
    }

    public void setMapAvailable(boolean mapAvailable) {
        isMapAvailable = mapAvailable;
    }

    public boolean isSpecifyMoreAvailable() {
        return isSpecifyMoreAvailable;
    }

    public void setSpecifyMoreAvailable(boolean specifyMoreAvailable) {
        isSpecifyMoreAvailable = specifyMoreAvailable;
    }

    public String getSenderOneSignalId() {
        return senderOneSignalId;
    }


    public long getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(long senderPhone) {
        this.senderPhone = senderPhone;
    }


    public void setSenderOneSignalId(String senderOneSignalId) {
        this.senderOneSignalId = senderOneSignalId;
    }

    public double getPositionX() {
        return positionX;
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Request() {
        //Empty Constructor
    }

    public Request(String senderId, String receiverId, String time, String senderName, String senderMessage) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.senderName = senderName;
        this.senderMessage = senderMessage;
    }

    public String getSenderId() {

        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }
}
