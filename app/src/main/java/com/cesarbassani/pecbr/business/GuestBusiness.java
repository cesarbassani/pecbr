package com.cesarbassani.pecbr.business;

import android.content.Context;

import com.cesarbassani.pecbr.constants.DataBaseConstants;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.model.GuestCount;
import com.cesarbassani.pecbr.model.GuestEntity;
import com.cesarbassani.pecbr.repository.GuestRerpository;

import java.util.List;

public class GuestBusiness {

    private GuestRerpository mGuestRerpository;

    public GuestBusiness(Context context) {
        this.mGuestRerpository = GuestRerpository.getINSTANCE(context);
    }

    public Boolean insert(GuestEntity guestEntity) {
        return this.mGuestRerpository.insert(guestEntity);
    }

    public Boolean update(GuestEntity guestEntity) {
        return this.mGuestRerpository.update(guestEntity);
    }

    public Boolean remove(int id) {
        return this.mGuestRerpository.remove(id);
    }

    public List<GuestEntity> getInvited() {

        return this.mGuestRerpository.getGuestsByQuery("select * from " + DataBaseConstants.GUEST.TABLE_NAME);
    }

    public List<GuestEntity> getPresent() {

        return this.mGuestRerpository.getGuestsByQuery("select * from " + DataBaseConstants.GUEST.TABLE_NAME + " where " +
                DataBaseConstants.GUEST.COLUMNS.PRESENCE + " = " + GuestConstants.CONFIRMATION.PRESENT);
    }

    public List<GuestEntity> getAbsent() {

        return this.mGuestRerpository.getGuestsByQuery("select * from " + DataBaseConstants.GUEST.TABLE_NAME + " where " +
            DataBaseConstants.GUEST.COLUMNS.PRESENCE + " = " + GuestConstants.CONFIRMATION.ABSENT);
    }

    public GuestEntity load(int id) {
        return this.mGuestRerpository.load(id);
    }

    public GuestCount loadDashboard() {
        return this.mGuestRerpository.loadDashboard();
    }
}
