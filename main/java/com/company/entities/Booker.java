package com.company.entities;

import com.company.lists.EmployeeList;

import javax.swing.filechooser.FileSystemView;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Booker extends Employee {
    private EmployeeList employeeList = EmployeeList.INSTANCE;
    private Map<Employee, List<Order>> employeeArchive = new HashMap<Employee, List<Order>>();

    public Booker(int startHour, int endHour) {
        id = idBasis++;
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDays = defineWorkDays();


        addPositions();
    }

    /**
     * Выполнение работы бухгалтера
     * В конце рабочего дня производится расчет с фрилансерами
     * В конце недели производится расчет со штатными сотрудниками
     * В конце месяца производится формирование отчета
     */
    @Override
    protected void doWork() {
        super.doWork();

        if (isEndOfDay()) {
            Iterator<Employee> employeeIterator = employeeList.getAll().iterator();
            while (employeeIterator.hasNext()) {
                Employee employee = employeeIterator.next();

                if ((isEndOfWeek() || isEndOfMonth()) && employee.getClass() != Freelancer.class) {
                    calculatePayout(employee);
                }
                if (employee.getClass() == Freelancer.class) {
                    calculatePayout(employee);
                    employeeIterator.remove();
                }
            }
            if (isEndOfMonth()) {
                createReport();
            }
        }
    }

    @Override
    protected void addPositions() {
        positions.add(Position.BOOKER);

        Position position = getRandomPosition();
        if (position.equals(Position.MANAGER)) {
            positions.add(position);
        }
    }

    /**
     * Добавление рабочих дней организовано таким образом, чтобы последний день недели был рабочим
     * остальные дни выбираются случайно
     * @return
     */
    @Override
    protected Set<Integer> defineWorkDays() {
        Set<Integer> result = new HashSet<Integer>(5);

        result.add(7);
        while (result.size() != 5) {
            result.add(getRandomValue(7));
        }

        return result;
    }

    /**
     * Формирование выплаты за фактически отработанные часы
     * Для директора, бухгалтера и менеджера выплачивается фиксированная плата
     *
     * @param employee сотрудник, для которого производится расчет
     * @return сума выплаты
     */
    private double calculatePayout(Employee employee) {
        double result = 0;
        Set<Integer> workDaysSet = new HashSet<Integer>();
        List<Position> employeePositions = employee.getPositions();

        List<Order> employeeTimeSheet = employee.getTimeSheet();
        synchronized (employeeTimeSheet) {
            Iterator<Order> employeeTimeSheetIterator = employeeTimeSheet.iterator();
            while (employeeTimeSheetIterator.hasNext()) {
                Order executedTask = employeeTimeSheetIterator.next();
                workDaysSet.add(executedTask.getDay());
                if (!employeeArchive.containsKey(employee)) {
                    employeeArchive.put(employee, new LinkedList<Order>());
                }
                employeeArchive.get(employee).add(executedTask);
                employeeTimeSheetIterator.remove();

                if (!employeePositions.contains(Position.DIRECTOR) && !employeePositions.contains(Position.BOOKER) &&
                        !employeePositions.contains(Position.MANAGER)) {
                    if(executedTask.getDay() % 6 == 0 || executedTask.getDay() % 7 == 0) {
                        result += executedTask.getTargetPosition().getSalaryPerHour() * 2;
                    } else {
                        result += executedTask.getTargetPosition().getSalaryPerHour();
                    }
                }
            }
        }

        if (employeePositions.contains(Position.DIRECTOR)) {
            return Position.DIRECTOR.getSalaryPerHour() * 8 * workDaysSet.size();
        }
        if (employeePositions.contains(Position.BOOKER)) {
            return Position.BOOKER.getSalaryPerHour() * 8 * workDaysSet.size();
        }
        if (employeePositions.contains(Position.MANAGER)) {
            return Position.MANAGER.getSalaryPerHour() * 8 * workDaysSet.size();
        }

        return result;
    }

    /**
     * Формирование отчета в конце месяца.
     * Текстовый файл отчета сохраняется в директории "Мои документы".
     */
    private void createReport() {
        String defaultDirectory = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/";
        try {
            FileWriter fw = new FileWriter(defaultDirectory + "QATL Test Task Report.txt");
            StringBuilder buff = getDutyPayout();
            fw.write(buff.toString());
            buff = getEmployeePayoutAndTotal();
            fw.write(buff.toString());

            fw.write("____________________________________________________________________________________________________________________\n");
            fw.write("` - фиксированная выплата по ставке директора, бухгалтера или менеджера\n");
            fw.write("`` - у сотрудника есть должности менеджера и либо бухгалтера, либо директора. Выплата начислена по старшей должности");
            fw.flush();
            fw.close();

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Формирование отчета о выполненных работах.
     *
     * @return отчет в текстовом виде, готовый для сохранения.
     */
    private StringBuilder getDutyPayout() {
        StringBuilder result = new StringBuilder();
        Map<String, Double> dutyReport = new HashMap<String, Double>();

        for (Map.Entry<Employee, List<Order>> archiveEntry : employeeArchive.entrySet()) {
            Employee employee = archiveEntry.getKey();
            List<Position> employeePositions = employee.getPositions();

            if (!employeePositions.contains(Position.DIRECTOR) && !employeePositions.contains(Position.BOOKER) &&
                    !employeePositions.contains(Position.MANAGER)) {
                for (Order executedTask : archiveEntry.getValue()) {
                    String executedTaskName = executedTask.getName();
                    double executedTaskPayout;
                    if(executedTask.getDay() % 6 == 0 || executedTask.getDay() % 7 == 0) {
                        executedTaskPayout = executedTask.getTargetPosition().getSalaryPerHour() * 2;
                    } else {
                        executedTaskPayout = executedTask.getTargetPosition().getSalaryPerHour();
                    }
                    if (!dutyReport.containsKey(executedTaskName)) {
                        dutyReport.put(executedTaskName, executedTaskPayout);
                    } else {
                        double currentAmount = dutyReport.get(executedTaskName);
                        dutyReport.put(executedTaskName, currentAmount + executedTaskPayout);
                    }
                }
            }
        }
        result.append("Отчет по выполненным работам:\n");
        for (String duty : dutyReport.keySet()) {
            result.append("- ")
                    .append(duty)
                    .append(" - выплачено: ")
                    .append(dutyReport.get(duty))
                    .append("\n");
        }

        return result;
    }

    /**
     * Формирование отчета о выплатах по каждому сотруднику, кроме фрилансеров.
     * По фрилансерам просто считается общая сума выплаты.
     *
     * @return отчет в текстовом виде, готовый для сохранения
     */
    private StringBuilder getEmployeePayoutAndTotal() {
        StringBuilder result = new StringBuilder();
        Set<Integer> workDaysSet = new HashSet<Integer>();
        double freelancePayout = 0.0;
        double totalPayout = 0.0;

        result.append("===============================================\n")
                .append("Отчет по сотрудникам\n");

        for (Map.Entry<Employee, List<Order>> archiveEntry : employeeArchive.entrySet()) {
            Employee employee = archiveEntry.getKey();
            List<Position> employeePositions = employee.getPositions();
            workDaysSet.clear();
            double employeePayout = 0.0;

            for (Order executedTask : archiveEntry.getValue()) {
                if (!employeePositions.contains(Position.DIRECTOR) && !employeePositions.contains(Position.BOOKER) &&
                        !employeePositions.contains(Position.MANAGER)) {
                    if(executedTask.getDay() % 6 == 0 || executedTask.getDay() % 7 == 0) {
                        employeePayout += executedTask.getTargetPosition().getSalaryPerHour() * 2;
                    } else {
                        employeePayout += executedTask.getTargetPosition().getSalaryPerHour();
                    }
                }
                workDaysSet.add(executedTask.getDay());
            }

            if (employee.getClass() == Freelancer.class) {
                freelancePayout += employeePayout;
            } else {
                result.append("Сотрудник ID: ")
                        .append(employee.getId());
                if (employee.getPositions().contains(Position.MANAGER)) {
                    employeePayout = Position.MANAGER.getSalaryPerHour() * 8 * workDaysSet.size();
                    result.append("`");
                }
                if (employee.getPositions().contains(Position.DIRECTOR)) {
                    employeePayout = Position.DIRECTOR.getSalaryPerHour() * 8 * workDaysSet.size();
                    result.append("`");
                }
                if (employee.getPositions().contains(Position.BOOKER)) {
                    employeePayout = Position.BOOKER.getSalaryPerHour() * 8 * workDaysSet.size();
                    result.append("`");
                }
                totalPayout += employeePayout;

                result.append(", выплачено ")
                        .append(employeePayout)
                        .append("\n");
            }
        }
        result.append("===============================================\n")
                .append("Выплачено фрилансерам: ")
                .append(freelancePayout)
                .append("\n===============================================\n")
                .append("Всего выплачено: ")
                .append(totalPayout)
                .append("\n");

        return result;
    }

    private boolean isEndOfDay() {
        return currentHour == endHour - 1;
    }

    private boolean isEndOfWeek() {
        return currentDay % 7 == 0;
    }

    private boolean isEndOfMonth() {
        return currentDay == 30 || currentDay == 31;
    }
}
