package com.wutian2.operate;

public class TranslateStarter {
    public static void main(String[] args) {
        addTranslate();
//        getTranslate();
    }

    private static void addTranslate() {
        TranslateHelpers.addTranslate("/Users/maxy/Desktop/res");
    }

    private static void getTranslate() {
        TranslateHelpers.getTranslate("v4.5.58_ww");
    }
}
