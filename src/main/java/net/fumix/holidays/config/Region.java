package net.fumix.holidays.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Region {
	public final String name;
	public final Region parent;
	public final String abbrev;
	public final List<HolidayRange> holidays = new ArrayList<>();

	static class HolidayRange {
		Integer fromYear;
		Integer toYear;
		Holiday holiday;

		public HolidayRange(Holiday holiday, Integer fromYear, Integer toYear) {
			this.holiday = holiday;
			this.fromYear = fromYear;
			this.toYear = toYear;
		}
	}

	public Region(String name, Region parent, String abbrev) {
		this.name = name;
		this.parent = parent;
		this.abbrev = abbrev;
		if (parent != null) {
			this.holidays.addAll(parent.holidays);
		}
	}

	public Region withHoliday(Holiday holiday) {
		return withHoliday(holiday, null, null);
	}

	public Region withHoliday(Holiday holiday, Integer from, Integer to) {
		final HolidayRange holidayRange = new HolidayRange(holiday, from, to);
		holidays.add(holidayRange);
		return this;
	}

	public Stream<Holiday> holidaysIn(int year) {
		return holidays.stream()
				.filter(h -> h.fromYear == null || h.fromYear <= year)
				.filter(h -> h.toYear == null || h.toYear >= year)
				.map(h -> h.holiday);
	}
}
