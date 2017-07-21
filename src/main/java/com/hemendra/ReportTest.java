package com.hemendra;

import net.sf.jasperreports.engine.JRException;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by BIP034 on 7/19/2017.
 */
public class ReportTest {
    public static void main(String[] args) throws JRException, IOException {

        List<Map<String, Object>> strings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<String, Object>();

        Student student = null;
        List<Student> studentList = new ArrayList<>();
        for (int i=0; i< 100; i++) {
            student = new Student();
            student.setName("A "+ (char)i);
            student.setBranch("Branch-"+  i*10);
            student.setRoll("Roll-"+ i+23*12);
            student.setMob("mobile-"+ i);

            studentList.add(student);
        }

        parameters.put("studentdDataSource", studentList);




        JasperCustomReport jasperCustomReport = new JasperCustomReport();
        byte[] anythings = jasperCustomReport.exportReportToPdf(parameters, "test.jrxml", "studentdDataSource");

        OutputStream out = new FileOutputStream("out.pdf");
        out.write(anythings);
        out.close();

        System.out.println(anythings);
    }
}
