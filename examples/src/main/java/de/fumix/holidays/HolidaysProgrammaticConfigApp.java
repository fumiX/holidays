package de.fumix.holidays;

import de.fumix.holidays.config.Config;
import de.fumix.holidays.config.Holiday;
import de.fumix.holidays.config.Region;
import de.fumix.holidays.impl.HolidaysImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Example to show how holidays and regions can be setup programmatically.
 *
 * Call with arguments:  REGION DATE...
 * Valid regions are "birthdays" and "fundays" (also containing birthdays).
 * Defined birthdays are Dec. 3 (since 1942) and Nov. 20. (1925-1967).
 * Defined fundays are the birthdays and 7 days before easter and 3 days after easter every year.
 */
public class HolidaysProgrammaticConfigApp {
	private static final Logger LOG = LoggerFactory.getLogger(HolidaysProgrammaticConfigApp.class);

	public static void main(String[] args) {
		final String logPrefix = "[main()]";

		Config config = new Config();

		// Some birthdays.
		final Holiday alice = Holiday.atDate("Alice", 12, 3);
		final Holiday bob = Holiday.atDate("Bob", 11, 20);

		final Region birthdays = new Region(Optional.empty(), "birthdays");
		birthdays.withHoliday(alice, Optional.of(1942), Optional.empty()); // Alice was born 1942 and is alive.
		birthdays.withHoliday(bob, Optional.of(1925), Optional.of(1967)); // Bob was born 1925, but died 1968 before his birthday.

		config.addHoliday(alice, bob).addRegion(birthdays);

		// Some more fun days.
		final Holiday eggPainting = Holiday.fromEaster("Egg-Painting-Day", -7); // Point the eggs a week before easter.
		final Holiday eggEating = Holiday.fromEaster("Egg-Eating-Day", 3); // Finaly eat them on Wednesday after easter.

		final Region fundays = new Region(Optional.of(birthdays), "fundays"); // birthdays are fundays, too.
		fundays.withHoliday(eggPainting);
		fundays.withHoliday(eggEating);

		config.addHoliday(eggPainting, eggEating).addRegion(fundays);

		//
		// Resolve the CLI args.
		//
		final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
		final Region region = config.regionOf(args[0]).orElseThrow(() -> new IllegalArgumentException("Undefined region '" + args[0] + "'"));
		LOG.info("{} Test run for {}", logPrefix, region);

		final HolidaysImpl holidays = new HolidaysImpl(region);
		for(int i=1; i<args.length; i++) {
			final LocalDate date = LocalDate.parse(args[i], df);
			final DayCategory cat = holidays.dayCategory(date);
			System.out.println(args[i] + ": " + cat
					+ ": " + holidays.at(date).map(Holiday::getHolidayId).orElse(date.getDayOfWeek().toString())
			);
		}
	}
}
