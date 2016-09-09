package com.simonmcdonnell.noted;

public class Utilities {
    public static int getColor(String color){
        int returnColor;
        switch (color){
            case "white":
                returnColor = R.color.white;
                break;
            case "red":
                returnColor = R.color.red;
                break;
            case "blue":
                returnColor = R.color.blue;
                break;
            case "green":
                returnColor = R.color.green;
                break;
            case "yellow":
                returnColor = R.color.yellow;
                break;
            default:
                returnColor = R.color.white;

        }
        return returnColor;
    }

    public static String getActionBarColor(String color){
        String returnColor;
        switch (color){
            case "white":
                returnColor = "#ffffff";
                break;
            case "red":
                returnColor = "#ef5350";
                break;
            case "blue":
                returnColor = "#42a5f5";
                break;
            case "green":
                returnColor = "#4caf50";
                break;
            case "yellow":
                returnColor = "#ffeb3b";
                break;
            default:
                returnColor = "#ffffff";

        }
        return returnColor;
    }

    public static int getNotificationColor(String color){
        int returnColor;
        switch (color){
            case "white":
                returnColor = R.color.white_bar;
                break;
            case "red":
                returnColor = R.color.red_bar;
                break;
            case "blue":
                returnColor = R.color.blue_bar;
                break;
            case "green":
                returnColor = R.color.green_bar;
                break;
            case "yellow":
                returnColor = R.color.yellow_bar;
                break;
            default:
                returnColor = R.color.white_bar;

        }
        return returnColor;
    }
}
