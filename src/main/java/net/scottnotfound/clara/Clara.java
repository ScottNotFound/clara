package net.scottnotfound.clara;

import java.io.IOException;

public class Clara {

    public static final String APP_NAME = "Clara";
    public static final String APP_VERSION = "0.0.003";
    public static final boolean isSNAPSHOT = true;

    private static Clara instance;

    private Clara(){}


    public static void main(String[] args) throws IOException {


    }

    public static void shutdown() {
        System.exit(0);
    }

    public static void shutdown(int status) {
        System.exit(status);
    }

    public static Clara getInstance() {
        if (instance == null) {
            instance = new Clara();
        }
        return instance;
    }
}
