package net.fumix.holidays;

import net.fumix.holidays.config.Config;
import net.fumix.holidays.config.Holiday;
import net.fumix.holidays.config.Region;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
	public static void main(String[] args) {
		Config config = Config.fromResources();

		final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
		final Region region = config.regionOf(args[0]).orElseThrow(() -> new IllegalArgumentException("Undefined region '" + args[0] + "'"));
		final Holidays holidays = new Holidays(region);
		for(int i=1; i<args.length; i++) {
			final LocalDate date = LocalDate.parse(args[i], df);
			final DayCategory cat = holidays.dayCategory(date);
			System.out.println(args[i] + ": " + cat
					+ ": " + holidays.at(date).map(Holiday::getName).orElse(date.getDayOfWeek().toString())
			);
		}
	}
}
