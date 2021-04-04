package net.fumix.holidays.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HolidayTest {

	@Test
	void fromConfig() {
		final Holiday easter = Holiday.fromConfig("EASTER", "{easter}+0");
		assertNotNull(easter);
	}
}
