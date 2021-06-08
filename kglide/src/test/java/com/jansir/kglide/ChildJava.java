package com.jansir.kglide;

import org.junit.Test;

public class ChildJava extends ParentJava {
    private String name;

    public ChildJava(String name) {
        super(name);
        this.name = name;

    }

    public String getName() {
        return name;
    }

}
