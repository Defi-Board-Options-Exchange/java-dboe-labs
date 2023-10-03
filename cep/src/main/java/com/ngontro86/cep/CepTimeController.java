package com.ngontro86.cep;

import java.util.Date;

public class CepTimeController {

    private static CepEngine engine;

    public static Date newDate() {
        return new Date(getCurrentTimeMillis());
    }

    public static long getCurrentTimeMillis() {
        return engine == null ? System.currentTimeMillis() : engine.getCurrentTimeMillis();
    }

    public static void setCepEngine(CepEngine controller) {
        engine = controller;
    }

}
