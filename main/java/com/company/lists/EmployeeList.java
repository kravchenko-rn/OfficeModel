package com.company.lists;

import com.company.entities.Employee;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EmployeeList {
    public static final EmployeeList INSTANCE = new EmployeeList();
    private List<Employee> list = Collections.synchronizedList(new LinkedList<Employee>());

    private EmployeeList() {}

    public void add(Employee newEmployee) {
        list.add(newEmployee);
    }

    public List<Employee> getAppropriateEmployees(String duty) {
        List<Employee> resultList = new LinkedList<Employee>();

        for (Employee employee : list) {
            if (employee.canDoTheDuty(duty)) {
                resultList.add(employee);
            }
        }

        return resultList;
    }

    public List<Employee> getAll() {
        return list;
    }
}
