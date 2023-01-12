package com.camunda.zqh.model;



public class Student {
    private Integer studentId;
    private String studentName;
    private String branch;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Student(Integer studentId) {
        super();
        this.studentId = studentId;
    }

    public Student(Integer studentId, String studentName, String branch) {
        super();
        this.studentId = studentId;
        this.studentName = studentName;
        this.branch = branch;
    }

    public Student() {
        super();
    }


}
