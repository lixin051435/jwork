package com.java;

import com.java.dao.StudentDao;
import com.java.domain.Student;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static StudentDao studentDao = new StudentDao();

    // 从控制台获取用户输入 生成学生对象
    public static Student console2Student() {
        Scanner scanner = new Scanner(System.in);
        Student student = new Student();
        System.out.println("请输入学生学号:");
        student.setId(scanner.next());
        System.out.println("请输入学生姓名:");
        student.setName(scanner.next());
        System.out.println("请输入学生性别:");
        student.setGender(scanner.next());
        System.out.println("请输入学生年龄:");
        student.setAge(Integer.parseInt(scanner.next()));
        System.out.println("请输入学生电话:");
        student.setPhone(scanner.next());
        System.out.println("请输入学生地区:");
        student.setRegion(scanner.next());
        System.out.println("请输入学生专业:");
        student.setProfession(scanner.next());
        return student;
    }

    public static void menu() {
        System.out.println("1.增加学生");
        System.out.println("2.删除学生");
        System.out.println("3.修改学生");
        System.out.println("4.根据学号查询学生");
        System.out.println("5.查看所有学生");
        System.out.println("0.系统退出");
        System.out.println("请输入序号:");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.next();
        if (option.equals("1")) {
            add();
        } else if (option.equals("2")) {
            remove();
        } else if (option.equals("3")) {
            modify();
        } else if (option.equals("4")) {
            getById();
        } else if (option.equals("5")) {
            getAll();
        } else if (option.equals("0")) {
            System.exit(0);
        }
    }

    public static void getAll() {
        List<Student> studentList = studentDao.findAll();
        Student student = null;
        for (int i = 0; i < studentList.size(); i++) {
            student = studentList.get(i);
            System.out.println(String.format("学号:%s,姓名:%s,性别:%s,年龄:%d,电话:%s,地区:%s,专业:%s", student.getId(), student.getName(), student.getGender(), student.getAge(),student.getPhone(), student.getRegion(), student.getProfession()));
        }
    }

    public static void add() {
        Student student = console2Student();
        studentDao.insert(student);
        System.out.println("添加成功");
    }

    public static void modify() {
        Student student = console2Student();
        studentDao.update(student);
        System.out.println("修改成功");
    }

    public static void getById() {
        System.out.println("请输入学生学号");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.next();
        Student student = studentDao.findById(id);
        if (student == null) {
            System.out.println(String.format("没有查到学号为%s的信息", id));
        } else {
            System.out.println("学生信息为：");
            System.out.println(String.format("学号:%s,姓名:%s,性别:%s,年龄:%d,电话:%s,地区:%s,专业:%s", student.getId(), student.getName(), student.getGender(), student.getAge(),student.getPhone(), student.getRegion(), student.getProfession()));

        }
    }

    public static void remove() {
        System.out.println("请输入学生学号");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.next();
        studentDao.delete(id);
        System.out.println("删除成功");
    }

    public static void main(String[] args) throws IOException {
        while(true){
            menu();
        }
    }


}
