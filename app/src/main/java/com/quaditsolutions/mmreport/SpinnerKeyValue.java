package com.quaditsolutions.mmreport;

/**
 * Created by Khyrz on 9/28/2017.
 */

public class SpinnerKeyValue {

    //Spinner Key and Value
    private String id, name, id2, name2;
    public SpinnerKeyValue(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof SpinnerKeyValue)
        {
            SpinnerKeyValue c = (SpinnerKeyValue) obj;

            return c.getName().equals(name) && c.getId() == id;
        }

        return false;
    }
}