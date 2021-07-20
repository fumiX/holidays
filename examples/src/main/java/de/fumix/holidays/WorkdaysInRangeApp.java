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
 * Counts the workdays in the specified date range
 */
public class WorkdaysInRangeApp {

	private static final Logger LOG = LoggerFactory.getLogger(WorkdaysInRangeApp.class);

	/**
	 *
	 * @param args Required args are 'region startDate endDate' with the dates in Iso-Local-Date format.
	 */
	public static void main(String[] args) {
		final String logPrefix = "[main()]";

		Config config = Config.fromResources();
		ResourceBundle holidaysBundle = Config.getHolidaysBundle(Locale.getDefault());

		// Parse the args
		if (args.length < 3) {
			System.err.println("Usage: region startDate endDate (e.g. 'DE_BW 2021-07-01 2021-07-19). Both dates are inclusive.");
			System.exit(1);
		}
		final Region region = config.regionOf(args[0]).orElseThrow(() -> {
			final String validRegions = config.getRegions().stream()
					.map(r -> r.getName(holidaysBundle) + " : " + r.getRegionId())
					.collect(Collectors.joining());
			return new IllegalArgumentException("Undefined region '" + args[0] + "'. Valid are: " + validRegions);
		});

		final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
		LocalDate startDate = LocalDate.parse(args[1], df);
		LocalDate endDate = LocalDate.parse(args[2], df);

		// Order start and end date.
		if (startDate.isAfter(endDate)) {
			final LocalDate tmp = endDate;
			endDate = startDate;
			startDate = tmp;
		}

		final HolidaysImpl holidays = new HolidaysImpl(region);

		// Monate
			int totalDays = 0;
			int workDays = 0;
			int freeDays = 0;


			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
				final DayCategory cat = holidays.dayCategory(date);
				totalDays += 1;
				if (cat == DayCategory.WEEKDAY) {
					workDays += 1;
				} else {
					freeDays += 1;
				}
			}

			System.out.println("Arbeitstage: " + workDays);
			System.out.println("freie Tage:  " + freeDays);
			System.out.println("Summe:       " + totalDays);
		}

}
