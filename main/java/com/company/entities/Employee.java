package com.company.entities;

import java.util.*;

public class Employee implements Runnable {

    protected static int idBasis = 1;
    protected int id;
    protected List<Position> positions = new ArrayList<Position>();
    protected List<Order> scheduledTasks = Collections.synchronizedList(new ArrayList<Order>());
    protected Order currentTask;
    protected List<Order> timeSheet = Collections.synchronizedList(new LinkedList<Order>());
    protected Set<Integer> workDays;
    protected int startHour;
    protected int endHour;

    int currentHour = 0;
    int currentDay = 1;

    protected Employee() {
    }

    public Employee(int startHour, int endHour) {
        id = idBasis++;
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDays = defineWorkDays();

        addPositions();
    }

    /**
     * Запуск обекта "сотрудник"
     * В определенное время и дни недели выполняет работу (в моменты совпадения с графиком)
     * Для Топ-Менеджеров в начале каждого дня добавляется выполненный таск "Checked in"
     * т.к. у них фиксированная З/П а задания за день может и неприйти.
     * Механизм начисления З/П основан на выполненных тасках за день
     */
    public void run() {
        while (true) {
            int dayOfWeek = currentDay % 7 == 0 ? 7 : currentDay % 7;
            if (workDays.contains(dayOfWeek) && currentHour >= startHour && currentHour < endHour) {
                if (currentHour == startHour) {
                    if (this.positions.contains(Position.DIRECTOR) || this.positions.contains(Position.BOOKER) ||
                            this.positions.contains(Position.MANAGER)) {
                        timeSheet.add(new Order("Checked in", null, currentDay));
                    }
                }
                doWork();
            }

            if (currentDay == 31) {
                break;
            }
            if (currentHour == 24) {
                currentDay++;
                currentHour = 0;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("Hour " + currentHour + ". Day " + currentDay + "\r");
            currentHour++;
        }
    }

    /**
     * Выполнение работы сотрудником
     * Случайным обраом определяется будет ли таск выполнен за 1 час или за 2
     * После выполнения каждого таска или его части (если выполнение разбито на 2 часа), он заносится в таймшит
     * а из запланированных задач убирается
     */
    protected void doWork() {
        int timeForCurrentTask;

        if (currentTask == null) {
            timeForCurrentTask = getRandomValue(2);
            synchronized (scheduledTasks) {
                if (scheduledTasks.size() > 0) {
                    currentTask = getTask();
                    scheduledTasks.remove(currentTask);
                } else {
                    return;
                }
            }
            timeSheet.add(currentTask);
            if (timeForCurrentTask == 1) {
                currentTask = null;
            }
        } else {
            timeSheet.add(currentTask);
            currentTask = null;
        }
    }

    /**
     * Получение задачи с наивысшей оплатой среди задач с наивысшим приоритетом.
     * Задачи с наивысшим приоритетом находятся в начале списка.
     * (список сортируется по приоритету при добавлении задачи)
     *
     * @return задача
     */
    protected Order getTask() {
        Order result = scheduledTasks.get(0);

        for (Order order : scheduledTasks) {
            if (order.getPriority() < result.getPriority()) {
                break;
            }
            if (order.getTargetPosition().getSalaryPerHour() > result.getTargetPosition().getSalaryPerHour()) {
                result = order;
            }
        }

        return result;
    }

    /**
     * Добавление сотруднику должностей случайным образом
     * Должность директора и бухгалтера возможно совместить только с должностью менеджера согласно условию
     * Должность сотрудника клининговой компании нельзя совместить ни с какой другой должностью согласно условию
     * Максимальное кол-во должностей у сотрудника не оговорено в условии - установил 5
     */
    protected void addPositions() {
        int numberOfPositions = getRandomValue(5);
        for (int i = 0; i < numberOfPositions; i++) {
            Position position = getRandomPosition();
            if (!positions.contains(position) && !positions.contains(Position.CLEANER)) {
                if (position == Position.CLEANER && positions.size() == 0) {
                    positions.add(position);
                    break;
                }
                // совмещение должности директора только с должностью менеджера
                if (position == Position.DIRECTOR) {
                    if (positions.size() == 1 && positions.get(0) == Position.MANAGER) {
                        positions.add(position);
                        break;
                    }
                    if (positions.size() == 0) {
                        positions.add(position);
                        continue;
                    }
                }
                // совмещение должности бухгалтера только с должностью менеджера
                if (position == Position.BOOKER) {
                    if (positions.size() == 1 && positions.get(0) == Position.MANAGER) {
                        positions.add(position);
                        break;
                    }
                    if (positions.size() == 0) {
                        positions.add(position);
                        continue;
                    }
                }
                if (position == Position.MANAGER) {
                    if (positions.contains(Position.DIRECTOR) ||
                            positions.contains(Position.BOOKER)) {
                        positions.add(position);
                        break;
                    }
                }
                if (!positions.contains(Position.DIRECTOR) && !positions.contains(Position.BOOKER) &&
                        !positions.contains(Position.CLEANER) &&
                        position != Position.BOOKER && position != Position.DIRECTOR &&
                        position != Position.CLEANER) {
                    positions.add(position);
                }
            }
        }
    }

    /**
     * Определение пяти рабочих дней для сотрудника случайным образом
     * @return набор дней
     */
    protected Set<Integer> defineWorkDays() {
        Set<Integer> result = new HashSet<Integer>(5);

        while (result.size() != 5) {
            result.add(getRandomValue(7));
        }

        return result;
    }

    /**
     * Добавление в расписание сотрудника нового задания для выполнения
     * @param newOrder новая задача
     */
    public void addTask(Order newOrder) {
        synchronized (scheduledTasks) {
            scheduledTasks.add(newOrder);
            Collections.sort(scheduledTasks); // сортировка по приоритету
        }
    }

    /**
     * Определение может ли сотрудник выполнить задание
     * @param duty наименование задания
     * @return true/false
     */
    public boolean canDoTheDuty(String duty) {
        for (Position employeePosition : positions) {
            for (String employeeDuty : employeePosition.getDuties()) {
                if (employeeDuty.equals(duty)) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getNumberOfScheduledTasks() {
        return scheduledTasks.size();
    }

    protected Position getRandomPosition() {
        int numberOfPositions = Position.values().length;
        int positionIndex = getRandomValue(numberOfPositions) - 1;

        return Position.values()[positionIndex];
    }

    protected int getRandomValue(int maxValue) {
        return (int) (Math.random() * maxValue + 1);
    }

    public int getId() {
        return id;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public List<Order> getTimeSheet() {
        return timeSheet;
    }
}
