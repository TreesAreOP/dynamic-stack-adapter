package de.taop.hskl.libtest;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Adrian on 25.07.2017.
 */

public class Data implements Serializable {
    String text;

    public Data() {
        text = "" + new Random().nextLong();
    }


}
