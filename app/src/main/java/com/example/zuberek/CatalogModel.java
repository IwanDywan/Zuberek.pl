package com.example.zuberek;

public class CatalogModel {
    //Integer age;
    String  name;
    //Integer weight;
    String turl;

    public CatalogModel(){}

    public CatalogModel(String name, String turl) {
        this.name = name;
        this.turl = turl;
    }
    /*public CatalogModel(Integer age, String name, Integer weight, String turl) {
        this.age = age;
        this.name = name;
        this.turl = turl;
        this.weight = weight;
    }*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTurl() {
        return turl;
    }

    public void setTurl(String turl) {
        this.turl = turl;
    }

    /*public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }*/

}
