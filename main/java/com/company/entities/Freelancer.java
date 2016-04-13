package com.company.entities;

import java.util.HashSet;
import java.util.Set;

public class Freelancer extends Employee {
    public Freelancer(int startHour, int endHour) {
        id = idBasis++;
        this.startHour = startHour;
        this.endHour = endHour;
        this.workDays = defineWorkDays();
        currentHour = startHour;
    }

    @Override
    protected Set<Integer> defineWorkDays() {
        Set<Integer> result = new HashSet<Integer>();
        result.add(1);
        return result;
    }
}
