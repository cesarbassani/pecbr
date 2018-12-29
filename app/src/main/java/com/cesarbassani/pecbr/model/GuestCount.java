package com.cesarbassani.pecbr.model;

public class GuestCount {

    private int presentCount;
    private int absentCount;
    private int allInvitedCount;

    public GuestCount(int present, int absent, int total) {
        this.presentCount = present;
        this.absentCount = absent;
        this.allInvitedCount = total;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public int getAllInvitedCount() {
        return allInvitedCount;
    }

    public void setAllInvitedCount(int allInvitedCount) {
        this.allInvitedCount = allInvitedCount;
    }
}
