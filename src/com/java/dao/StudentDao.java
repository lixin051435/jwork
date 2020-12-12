package com.java.dao;

import com.java.domain.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class StudentDao {

    private static List<Student> students = new ArrayList<>();
    private static String filepath = null;

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = StudentDao.class.getResourceAsStream("/db.properties");
            properties.load(inputStream);
            filepath = properties.getProperty("FILEPATH");
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                students = new ArrayList<>();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
                String line;
                String[] data;
                String id, name, gender, phone, region, profession;
                Integer age;
                while ((line = bufferedReader.readLine()) != null) {
                    data = line.split(",");
                    id = data[0];
                    name = data[1];
                    gender = data[2];
                    age = Integer.parseInt(data[3]);
                    phone = data[4];
                    region = data[5];
                    profession = data[6];
                    students.add(new Student(id, name, gender, age, phone, region, profession));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void list2file() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filepath);
            for (int i = 0; i < students.size(); i++) {
                fileWriter.write(students.get(i).toString());
                // 换行
                fileWriter.write(System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void insert(Student student) {
        students.add(student);
        list2file();
    }

    public void update(Student student) {
        for (int i = 0; i < students.size(); i++) {
            Student entity = students.get(i);
            if (student.getId().equals(entity.getId())) {
                entity.setName(student.getName());
                entity.setGender(student.getGender());
                entity.setAge(student.getAge());
                entity.setPhone(student.getPhone());
                entity.setRegion(student.getRegion());
                entity.setProfession(student.getProfession());
            }
        }
        list2file();
    }

    public void delete(String id) {
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(id)) {
                iterator.remove();
            }
        }
        list2file();
    }

    public Student findById(String id) {
        Student student = null;
        for (int i = 0; i < students.size(); i++) {
            Student entity = students.get(i);
            if (id.equals(entity.getId())) {
                student = entity;
            }
        }
        return student;
    }

    public List<Student> findAll() {
        return students;
    }
}
