package com.yaksha.assignment;

// Enum representing the days of the week
enum Day {
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// Class containing an enum to demonstrate Enum inside a Class
class WeekScheduler {

	// Enum inside a class
	enum WeekStatus {
		WEEKDAY, WEEKEND
	}

	// Method to check if the given day is a weekday or weekend
	public String getDayStatus(Day day) {
		switch (day) {
		case SATURDAY:
		case SUNDAY:
			return WeekStatus.WEEKEND.toString();
		default:
			return WeekStatus.WEEKDAY.toString();
		}
	}
}

public class EnumsAssignment {
	public static void main(String[] args) {
		// Accessing the enum Day
		Day today = Day.MONDAY;
		System.out.println("Today is: " + today);

		// Using the WeekScheduler class with the WeekStatus enum inside it
		WeekScheduler scheduler = new WeekScheduler();
		System.out.println("Is today a weekday or weekend? " + scheduler.getDayStatus(today));
	}
}
