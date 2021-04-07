package net.fumix.holidays.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Region {
	public final String name;
	public final Optional<Region> parent;
	public final String abbrev;
	public final List<HolidayRange> holidays = new ArrayList<>();

	public String getName() {
		return name;
	}

	public Optional<Region> getParent() {
		return parent;
	}

	public String getAbbrev() {
		return abbrev;
	}

	public List<HolidayRange> getHolidays() {
		return holidays;
	}

	static class HolidayRange {
		Optional<Integer> fromYear;
		Optional<Integer> toYear;
		Holiday holiday;

		public HolidayRange(Holiday holiday, Optional<Integer> fromYear, Optional<Integer> toYear) {
			this.holiday = holiday;
			this.fromYear = fromYear;
			this.toYear = toYear;
		}
	}

	public Region(String name, Optional<Region> parent, String abbrev) {
		this.name = name;
		this.parent = parent;
		this.abbrev = abbrev;
		parent.map(Region::getHolidays).ifPresent(parentHolidays -> this.holidays.addAll(parentHolidays));
	}

	public Region withHoliday(Holiday holiday) {
		return withHoliday(holiday, Optional.empty(), Optional.empty());
	}

	public Region withHoliday(Holiday holiday, Optional<Integer> from, Optional<Integer> to) {
		final HolidayRange holidayRange = new HolidayRange(holiday, from, to);
		holidays.add(holidayRange);
		return this;
	}

	public Stream<Holiday> holidaysIn(int year) {
		return holidays.stream()
				.filter(h -> !h.fromYear.isPresent() || h.fromYear.get() <= year)
				.filter(h -> !h.toYear.isPresent() || h.toYear.get() >= year)
				.map(h -> h.holiday);
	}
}
