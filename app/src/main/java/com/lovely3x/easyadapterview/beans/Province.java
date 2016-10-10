package com.lovely3x.easyadapterview.beans;

import java.util.List;

/**
 * Created by lovely3x on 16/9/26.
 */

public class Province implements Displayable {

    public String name;

    public int id;

    public List<City> child;

    @Override
    public String display() {
        return name;
    }
}
