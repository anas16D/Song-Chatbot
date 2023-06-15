package com.example.majorchatbot;

import java.util.Observable;

public class storedData extends Observable {

    public static String paragraph = "";
    public static int messageCount = 0;
    public static String mood = "sad";
    public static  String trackName = "Tum mile";
    public static boolean showMessage = false;
    public static String BotReply = "";

    public void incrementMsgCount() {
        messageCount++;
        setChanged();
        notifyObservers();
    }

    public static void showMessage(String message){

    }
}
