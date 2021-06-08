package com.jansir.kglide;

public class ParentJava {
    private String name;
    private int i;

    public String getName() {
        return name;
    }

    public ParentJava(String name) {
        this.name = name;
        i = getName().length();
    }

}
