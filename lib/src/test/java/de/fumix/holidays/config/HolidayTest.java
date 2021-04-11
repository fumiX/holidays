package de.fumix.holidays.config;

import de.fumix.holidays.impl.Cache;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HolidayTest {

	@Test
	void fromConfig() {
		final Holiday easter = Holiday.fromConfig("EASTER", "{easter}+0");
		assertNotNull(easter);
	}

	@Test
	void weekdayRelHoliday() {
		Config config = new Config();
		Region region = new Region("Testland", Optional.empty(), "TE");
		Cache cache = new Cache(region);

		Holiday.WeekdayRelativeHoliday h = new Holiday.WeekdayRelativeHoliday("TEST", DayOfWeek.WEDNESDAY, true, 4, 4); // 2021: Sunday
		assertEquals(LocalDate.of(2021, 3, 31), h.atDate(2021, cache));

		Holiday.WeekdayRelativeHoliday h2 = new Holiday.WeekdayRelativeHoliday("TEST2", DayOfWeek.MONDAY, false, 4, 4); // 2021: Sunday
		assertEquals(LocalDate.of(2021, 4, 5), h2.atDate(2021, cache));

		Holiday.WeekdayRelativeHoliday h3 = new Holiday.WeekdayRelativeHoliday("TEST3", DayOfWeek.SUNDAY, true, 4, 7); // 2021: Wednesday
		assertEquals(LocalDate.of(2021, 4, 4), h3.atDate(2021, cache));

		Holiday.WeekdayRelativeHoliday h4 = new Holiday.WeekdayRelativeHoliday("TEST4", DayOfWeek.SUNDAY, false, 3, 29); // 2021: Monday
		assertEquals(LocalDate.of(2021, 4, 4), h4.atDate(2021, cache));

	}
}
