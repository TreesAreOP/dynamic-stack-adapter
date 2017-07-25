package de.taop.hskl.libtest;

import java.util.Random;

/**
 * Created by Adrian on 25.07.2017.
 */

public class Data {
    String text;

    public Data() {
        text = ""+new Random().nextLong();
    }
}
