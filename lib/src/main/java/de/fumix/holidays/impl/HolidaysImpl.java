package de.fumix.holidays.impl;


import de.fumix.holidays.DayCategory;
import de.fumix.holidays.Holidays;
import de.fumix.holidays.config.Holiday;
import de.fumix.holidays.config.Region;

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
