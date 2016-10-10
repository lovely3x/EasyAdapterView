package com.lovely3x.easyadapterview.beans;

import java.util.List;

/**
 * Created by lovely3x on 16/9/26.
 */

public class City implements Displayable {

    public String name;

    public String id;

    public List<Area> child;

    @Override
    public String display() {
        return name;
    }
}
