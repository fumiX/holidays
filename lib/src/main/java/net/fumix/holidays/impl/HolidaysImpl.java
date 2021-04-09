package net.fumix.holidays.impl;


import net.fumix.holidays.DayCategory;
import net.fumix.holidays.Holidays;
import net.fumix.holidays.config.Holiday;
import net.fumix.holidays.config.Region;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Categorized dates to holidays, weekends and normal workdays. Knows about German federal holidays as of 2021.
 *
 * Used by Inworks export.
 */
public class HolidaysImpl implements Holidays {

	final Region region;
	final Cache cache;

	public HolidaysImpl(Region region) {
		this.region = region;
		this.cache = new Cache(region);
	}

	public DayCategory dayCategory(LocalDate date) {
		return cache.dayCategory(date);
	}

	public Optional<Holiday> at(LocalDate date) {
		return cache.at(date);
	}
}
