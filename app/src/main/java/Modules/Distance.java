package Modules;

/**
 * Created by 51202_000 on 12/11/2016.
 */

public class Distance {
    public String text;
    public int value;

    public Distance(){
        this.text = "0 km";
        this.value = 0;
    }

    public Distance(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
