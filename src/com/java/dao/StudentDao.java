package com.java.dao;

import com.java.domain.Student;
import com.java.util.JDBCUtils;

import java.util.List;

public class StudentDao {


    public void insert(Student student) {
        JDBCUtils.add(student);
    }

    public void update(Student student) {
        JDBCUtils.update(student, "id");
    }

    public void delete(String id) {
        Student student = new Student();
        student.setId(id);
        JDBCUtils.delete(student, "id");
    }

    public Student findById(String id) {
        return JDBCUtils.selectList("select * from student where id = ?",new Object[]{id},Student.class).get(0);
    }

    public List<Student> findAll() {
        return JDBCUtils.selectList("select * from student", new Object[]{}, Student.class);
    }
}
