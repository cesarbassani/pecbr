package com.cesarbassani.pecbr.constants;

public class DataBaseConstants {

    public static final int SELECAO_CAMERA = 100;
    public static final int SELECAO_GALERIA = 200;

    private DataBaseConstants() {

    }

    public static class GUEST {

        public static final String TABLE_NAME = "Guest";


        public static class COLUMNS {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String PRESENCE = "presence";
            public static final String DOCUMENT = "document";
        }
    }
}
