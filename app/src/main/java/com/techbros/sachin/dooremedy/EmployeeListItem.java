package com.techbros.sachin.dooremedy;

/**
 * Created by saini on 12-Apr-17.
 */

public class EmployeeListItem {
    String name;
    String detail;
    String image;

    public EmployeeListItem(String name, String id, String image) {
        this.name = name;
        this.detail = id;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.detail = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return detail;
    }

    public String getImage() {
        return image;
    }
}
