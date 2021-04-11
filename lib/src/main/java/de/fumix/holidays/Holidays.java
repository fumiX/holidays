package de.fumix.holidays;

import de.fumix.holidays.config.Holiday;

import java.time.LocalDate;
import java.util.Optional;

public interface Holidays {
	DayCategory dayCategory(LocalDate date);
	Optional<Holiday> at(LocalDate date);
}
