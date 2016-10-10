package com.lovely3x.easyadapterview.beans;

/**
 * Created by lovely3x on 16/9/26.
 */

public class Area implements Displayable{

    public String name;

    public String id;

    public String zipcode;

    @Override
    public String display() {
        return name;
    }
}
