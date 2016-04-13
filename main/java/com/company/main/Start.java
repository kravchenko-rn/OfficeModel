package com.company.main;

import com.company.entities.Booker;
import com.company.entities.Director;
import com.company.entities.Employee;
import com.company.entities.Position;
import com.company.exceptions.InvalidRandomBoundariesException;
import com.company.lists.EmployeeList;

public class Start {
    private static EmployeeList employeeList = EmployeeList.INSTANCE;

    public static void main(String[] args) throws Exception {
        int startHour;
        Employee newEmployee;

        createRequiredEmployees();

        // создание сотрудников
        int numberOfEmployees = getRandomValue(10, 100);
        for (int i = 0; i < numberOfEmployees; i++) {
            startHour = getRandomValue(7, 12);
            newEmployee = new Employee(startHour, startHour + 8);
            employeeList.add(newEmployee);
        }

        // запуск модели
        for (Employee employee : employeeList.getAll()) {
            new Thread(employee).start();
        }
    }

    /**
     * Создание обязательных (по заданию) сотрудников
     * @throws InvalidRandomBoundariesException неверное указание параметров функции getRandomValue()
     */
    public static void createRequiredEmployees() throws InvalidRandomBoundariesException {
        int startHour;
        Employee employee;

        startHour = getRandomValue(7, 12);
        employee = new Director(startHour, startHour + 8);
        employeeList.add(employee);

        startHour = 12;
        employee = new Booker(startHour, startHour + 8);
        employeeList.add(employee);

        startHour = getRandomValue(7, 12);
        do {
            employee = new Employee(startHour, startHour + 8);
        } while (!employee.getPositions().contains(Position.MANAGER));
        employeeList.add(employee);
    }

    /**
     * Генерация случайного числа в заданном диапазоне
     * @param min минимальное значение случайного числа
     * @param max максимальное значение случайного числа
     * @return сгенерированное число
     * @throws InvalidRandomBoundariesException ошибка в случае, если min > max
     */
    public static int getRandomValue(int min, int max) throws InvalidRandomBoundariesException {
        if (max < min) {
            throw new InvalidRandomBoundariesException();
        }
        return (int) (Math.random() * (max - min) + min);
    }
}
