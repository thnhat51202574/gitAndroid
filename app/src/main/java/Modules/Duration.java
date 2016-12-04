package Modules;

/**
 * Created by 51202_000 on 12/11/2016.
 */

public class Duration {
    public String text;
    public int value;

    public Duration(int value){
        String textreturn = String.format("%.2f",(double) value/3600.0) + " h";
        this.text = textreturn;
        this.value = value;
    }

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
