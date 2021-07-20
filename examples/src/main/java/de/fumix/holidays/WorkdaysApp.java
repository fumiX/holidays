package de.fumix.holidays;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.fumix.holidays.config.Config;
import de.fumix.holidays.config.Region;
import de.fumix.holidays.impl.HolidaysImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Lists the workdays per month for the given year.
 */
public class WorkdaysApp {

	private static final Logger LOG = LoggerFactory.getLogger(WorkdaysApp.class);

	/**
	 *
	 * @param args region year
	 */
	public static void main(String[] args) {
		final String logPrefix = "[main()]";

		Config config = Config.fromResources();
		ResourceBundle holidaysBundle = Config.getHolidaysBundle(Locale.getDefault());

		// Parse the args
		if (args.length < 2) {
			System.err.println("Usage: region year");
			System.exit(1);
		}
		final Region region = config.regionOf(args[0]).orElseThrow(() -> {
			final String validRegions = config.getRegions().stream()
					.map(r -> r.getName(holidaysBundle) + " : " + r.getRegionId())
					.collect(Collectors.joining());
			return new IllegalArgumentException("Undefined region '" + args[0] + "'. Valid are: " + validRegions);
		});

		int year = Integer.parseInt(args[1]);


		final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
		LOG.info("{} Test run for {}", logPrefix, region);

		final HolidaysImpl holidays = new HolidaysImpl(region);


		// Jahr
		int totalDays = 0;

		// Monate
		for (int month = 1; month <= 12; month ++) {
			LocalDate start = LocalDate.of(year, month, 1);
			LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

			int monthDays = 0;
			int workDays = 0;
			int freeDays = 0;


			for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
				final DayCategory cat = holidays.dayCategory(date);
				totalDays += 1;
				monthDays += 1;
				if (cat == DayCategory.WEEKDAY) {
					workDays += 1;
				} else {
					freeDays += 1;
				}
			}

			System.out.println("Monat: " + year + "-" + month);
			System.out.println("Arbeitstage: " + workDays);
			System.out.println("freie Tage:  " + freeDays);
			System.out.println("Summe:       " + monthDays);
			System.out.println("-----");
		}
		System.out.println("-----");
		System.out.println("Jahr: " + year);
		System.out.println("Tage:        " + totalDays);
	}

}
