package com.cesarbassani.pecbr.fcm;


import com.cesarbassani.pecbr.data.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        new SharedPref(this).setFcmRegId(refreshedToken);
    }
}
