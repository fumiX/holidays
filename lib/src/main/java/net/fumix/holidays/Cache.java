package net.fumix.holidays;

import net.fumix.holidays.config.Holiday;
import net.fumix.holidays.config.Region;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cache {
	final Region region;

	final Map<Integer, LocalDate> easterCache = new HashMap<>();
	final Map<Integer, Map<LocalDate, Holiday>> holidayMap = new HashMap<>();
	final Map<Integer, LinkedHashMap<LocalDate, DayCategory>> yearCache = new HashMap<>();

	public Cache(Region region) {
		this.region = region;
	}

	public LocalDate easter(int year) {
		LocalDate d = easterCache.get(year);
		if (d == null) {
			d = EasterDate(year);
			easterCache.put(year, d);
		}
		return d;
	}

	public DayCategory dayCategory(LocalDate date) {
		final LinkedHashMap<LocalDate, DayCategory> daysOfYear = yearDate(date.getYear());
		return daysOfYear.get(date);
	}


	LinkedHashMap<LocalDate, DayCategory> yearDate(int year) {
		LinkedHashMap<LocalDate, DayCategory> daysOfYear = yearCache.get(year);
		if (daysOfYear == null) {
			daysOfYear = new LinkedHashMap<>();
			LocalDate date = LocalDate.of(year, 1, 1);
			for(int i = 1; i<= Year.of(year).length(); i++) {
				daysOfYear.put(date, category(date));
				date  = date.plusDays(1);
			}
			yearCache.put(year, daysOfYear);
		}
		return daysOfYear;

	}

	DayCategory category(LocalDate date) {
		return at(date).map(d -> DayCategory.HOLIDAY)
				.orElseGet(() -> {
					if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
						return DayCategory.WEEKEND;
					} else {
						return DayCategory.WEEKDAY;
					}
				});
	}

	Optional<Holiday> at(LocalDate date) {
		Map<LocalDate, Holiday> holidays = holidayMap.get(date.getYear());
		if (holidays == null) {
			holidays = region.holidaysIn(date.getYear())
					.collect(Collectors.toMap(h -> h.atDate(date.getYear(), this), h -> h));
			holidayMap.put(date.getYear(), holidays);
		}
		return Optional.ofNullable(holidays.get(date));
	}

	/**
	 * Algorithm for calculating the date of Easter Sunday
	 * (Meeus/Jones/Butcher Gregorian algorithm)
	 * http://en.wikipedia.org/wiki/Computus#Meeus.2FJones.2FButcher_Gregorian_algorithm
	 *
	 * @param year A valid Gregorian year
	 * @return Easter Sunday
	 */
	static LocalDate EasterDate(int year) {
		int Y = year;
		int a = Y % 19;
		int b = Y / 100;
		int c = Y % 100;
		int d = b / 4;
		int e = b % 4;
		int f = (b + 8) / 25;
		int g = (b - f + 1) / 3;
		int h = (19 * a + b - d - g + 15) % 30;
		int i = c / 4;
		int k = c % 4;
		int L = (32 + 2 * e + 2 * i - h - k) % 7;
		int m = (a + 11 * h + 22 * L) / 451;
		int month = (h + L - 7 * m + 114) / 31;
		int day = ((h + L - 7 * m + 114) % 31) + 1;
		LocalDate dt = LocalDate.of(year, month, day);
		return dt;
	}

}
