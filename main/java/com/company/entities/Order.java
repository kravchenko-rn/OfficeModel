package com.company.entities;

public class Order implements Comparable {
    private static int idBasis = 1;
    private int id = idBasis++;
    private int day;
    private String name;
    private int priority;
    private Position targetPosition;

    Order(String name, Position targetPosition, int day) {
        this.name = name;
        this.targetPosition = targetPosition;
        this.day = day;
        setRandomPriority();
    }

    public int compareTo(Object object) {
        Order order = (Order) object;
        if (this.priority > order.priority) {
            return 1;
        }
        if (this.priority < order.priority) {
            return -1;
        }
        return 0;
    }

    private void setRandomPriority() {
        priority = (int) (Math.random() * 5 + 1);
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    public int getDay() {
        return day;
    }
}
