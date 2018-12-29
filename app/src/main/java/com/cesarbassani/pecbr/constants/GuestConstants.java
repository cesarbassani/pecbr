package com.cesarbassani.pecbr.constants;

import android.Manifest;

public class GuestConstants {

    public static class BundleConstants {
        public static final String GUEST_ID = "guestID";
    }

    public static class CONFIRMATION {
        public static final int NOT_CONFIRMED = 1;
        public static final int PRESENT = 2;
        public static final int ABSENT = 3;
    }

    public static class PERMISSIONS {
        public static final int REQUEST_EXTERNAL_STORAGE = 1;
        public static String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }
}
