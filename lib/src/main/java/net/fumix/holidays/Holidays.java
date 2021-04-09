package net.fumix.holidays;

import net.fumix.holidays.DayCategory;
import net.fumix.holidays.config.Holiday;

import java.time.LocalDate;
import java.util.Optional;

public interface Holidays {
	DayCategory dayCategory(LocalDate date);
	Optional<Holiday> at(LocalDate date);
}
