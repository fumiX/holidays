package de.fumix.holidays.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Region {
	public final Optional<Region> parent;
	public final String regionId;
	public final List<HolidayRange> holidays = new ArrayList<>();


	public Optional<Region> getParent() {
		return parent;
	}

	public String getRegionId() {
		return regionId;
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

	public Region(Optional<Region> parent, String abbrev) {
		this.parent = parent;
		this.regionId = abbrev;
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

	public String getName(ResourceBundle holidaysBundle) {
		return holidaysBundle.getString("region." + regionId);
	}

	public Stream<Holiday> holidaysIn(int year) {
		return holidays.stream()
				.filter(h -> !h.fromYear.isPresent() || h.fromYear.get() <= year)
				.filter(h -> !h.toYear.isPresent() || h.toYear.get() >= year)
				.map(h -> h.holiday);
	}

	@Override
	public String toString() {
		return "Region{" +
				", abbrev=" + regionId +
				'}';
	}
}
