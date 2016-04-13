package com.company.entities;

public enum Position {
    PROGRAMMER ("Программист", 20, new String[] {"Писать код", "Фиксить баги", "Выпить кофе ☺"}),
    DESIGNER ("Дизайнер", 17, new String[] {"Рисовать макет"}),
    TESTER ("Тестировщик", 20, new String[] {"Тестировать программу"}),
    MANAGER ("Менеджер", 14, new String[] {"Продавать услуги"}),
    BOOKER ("Бухгалтер", 15, new String[] {"Составить отчетность"}),
    HR ("HR", 13, new String[] {"Искать новые таланты"}),
    DIRECTOR ("Директор", 30, new String[] {"Организационные вопросы"}),
    CLEANER ("Уборщик", 7, new String[] {"Выполнить уборку"});

    private final String name;
    private final double salaryPerHour;
    private final String[] duties;

    Position(String name, double salaryPerHour, String[] duties) {
        this.name = name;
        this.salaryPerHour = salaryPerHour;
        this.duties = duties;
    }

    public String getName() {
        return name;
    }

    public double getSalaryPerHour() {
        return salaryPerHour;
    }

    public String[] getDuties() {
        return duties;
    }
}
