package de.fumix.holidays;

import de.fumix.holidays.config.Config;
import de.fumix.holidays.config.Holiday;
import de.fumix.holidays.config.Region;
import de.fumix.holidays.impl.HolidaysImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Check, if given dates are holidays. Loads the holiday and region information from the resources (i.e. German and
 * Austrian holidays are available).
 *
 * Arguments: REGION DATES...
 *
 * where REGION is one of DE, DE_BAY, DE_BW ... (see src/main/resources/region_DE.properties) or AT,
 * and DATES is are dates in ISO format, like 2021-04-09.
 */
public class HolidaysApp {
	private static final Logger LOG = LoggerFactory.getLogger(HolidaysApp.class);

	public static void main(String[] args) {
		final String logPrefix = "[main()]";

		Config config = Config.fromResources();

		final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
		final Region region = config.regionOf(args[0]).orElseThrow(() -> new IllegalArgumentException("Undefined region '" + args[0] + "'"));
		LOG.info("{} Test run for {}", logPrefix, region);


		final HolidaysImpl holidays = new HolidaysImpl(region);
		for(int i=1; i<args.length; i++) {
			final LocalDate date = LocalDate.parse(args[i], df);
			final DayCategory cat = holidays.dayCategory(date);
			System.out.println(args[i] + ": " + cat
					+ ": " + holidays.at(date).map(Holiday::getName).orElse(date.getDayOfWeek().toString())
			);
		}
	}
}
