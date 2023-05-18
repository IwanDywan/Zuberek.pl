package com.example.zuberek;

public class CatalogModel {
    String age;
    String  name;
    String weight;
    String turl;

    public CatalogModel(){}

    public CatalogModel(String age, String name, String weight, String turl) {
        this.age = age.toString();
        this.name = name;
        this.turl = turl;
        this.weight = weight.toString();
    }
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age.toString();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight.toString();
    }

}
