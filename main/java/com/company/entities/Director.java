package com.company.entities;

import com.company.lists.EmployeeList;

import java.util.List;

public class Director extends Employee {
    private EmployeeList employeeList = EmployeeList.INSTANCE;

    public Director(int startHour, int endHour) {
        id = idBasis++;
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDays = defineWorkDays();

        addPositions();
    }

    /**
     * Выполнение работы директора
     * После выполнения задач из расписания генерирует случайным образом новые задачи для сотрудников (включая себя)
     * Сотрудник выбирается тот, у которого меньше запланированных задач среди тех, кто может выполнить новую задачу
     * В случае, если соответствующего сотрудника нет, задача передается фрилансеру
     * при условии, что задача не состоит в уборке офиса (согласно условию)
     */
    @Override
    protected void doWork() {
        super.doWork();

        int numberOfNewOrders = getRandomValue(30);
        System.out.println("Director created " + numberOfNewOrders + " orders at " + currentHour);

        for (int i = 0; i < numberOfNewOrders; i++) {
            Position position = getRandomPosition();
            String newTask = getRandomDuty(position);
            Order newOrder = new Order(newTask, position, currentDay);

            // даем новое задание подходящему сотруднику, у которого меньше заданий, чем у остальных подходящих
            List<Employee> appropriateEmployees = employeeList.getAppropriateEmployees(newTask);
            System.out.print("- задание: " + newTask);
            if (appropriateEmployees.size() > 0) {
                Employee appropriateEmployee = appropriateEmployees.get(0);
                for (Employee employee : appropriateEmployees) {
                    if (appropriateEmployee.getNumberOfScheduledTasks() > employee.getNumberOfScheduledTasks()) {
                        appropriateEmployee = employee;
                    }
                }

                appropriateEmployee.addTask(newOrder);
                System.out.println("; назначено сотруднику " + appropriateEmployee.getId());
            } else {
                if (position != Position.CLEANER) {
                    Employee freelancer = new Freelancer(currentHour, currentHour + 2);
                    freelancer.addTask(newOrder);
                    employeeList.add(freelancer);
                    new Thread(freelancer).start();
                    System.out.println("; фрилансер");
                } else {
                    System.out.println(" - уборщиков нет!");
                }
            }
        }
    }

    private String getRandomDuty(Position position) {
        String[] duties = position.getDuties();
        int numberOfDuties = duties.length;
        int dutyIndex = getRandomValue(numberOfDuties) - 1;

        return duties[dutyIndex];
    }

    @Override
    protected void addPositions() {
        positions.add(Position.DIRECTOR);

        Position position = getRandomPosition();
        if (position.equals(Position.MANAGER)) {
            positions.add(position);
        }
    }
}
