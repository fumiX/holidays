package net.fumix.holidays.config;

import net.fumix.holidays.impl.Cache;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Holiday {

	final static Pattern fixedDatePattern = Pattern.compile("(\\d\\d)-(\\d\\d)");
	final static Pattern easterRelativePattern = Pattern.compile("\\{easter\\}([+-]\\d+)");
	final static Pattern weekdayRelPattern = Pattern.compile("([a-zA-Z]+)([<>])(\\d\\d)-(\\d\\d)");

	public abstract LocalDate atDate(int year, Cache cache) ;


	public final String name;
	public String getName() { return name; }


	public static Holiday fromConfig(String name, String expression) {
		final Matcher fixedDateMatcher = fixedDatePattern.matcher(expression);
		if (fixedDateMatcher.matches()) {
			int month = Integer.parseInt(fixedDateMatcher.group(1));
			int day = Integer.parseInt(fixedDateMatcher.group(2));
			return new FixedDateHoliday(name, month, day);
		}

		final Matcher easterMatcher = easterRelativePattern.matcher(expression);
		if (easterMatcher.matches()) {
			int offset = Integer.parseInt(easterMatcher.group(1));
			return new EasterRelativeHoliday(name, offset);
		}

		final Matcher weekdayRelMatcher = weekdayRelPattern.matcher(expression);
		if (weekdayRelMatcher.matches()) {
			String wd = weekdayRelMatcher.group(1);
			String op = weekdayRelMatcher.group(2);
			String month = weekdayRelMatcher.group(3);
			String day = weekdayRelMatcher.group(4);
			return new WeekdayRelativeHoliday(name,
					DayOfWeek.valueOf(wd.toUpperCase(Locale.ROOT)),
					op.equals("<"),
					Integer.parseInt(month),
					Integer.parseInt(day));
		}

		throw new IllegalArgumentException("Invalid holiday pattern for '" + name + "': '" + expression + "'");
	}

	public static Holiday atDate(String name, int month, int day) {
		return new FixedDateHoliday(name, month, day);
	}

	public static Holiday fromEaster(String name, int offset) {
		return new EasterRelativeHoliday(name, offset);
	}

	Holiday(String name) {
		this.name = name;
	}

	public static class FixedDateHoliday extends Holiday {
		final int month;
		final int day;

		public FixedDateHoliday(String name, int month, int day) {
			super(name);
			this.month = month;
			this.day = day;
		}

		@Override
		public LocalDate atDate(int year, Cache cache) {
			return LocalDate.of(year, month, day);
		}

		@Override
		public String toString() {
			return "FixedDateHoliday{" +
					"name='" + name + '\'' +
					", month=" + month +
					", day=" + day +
					'}';
		}
	}

	public static class EasterRelativeHoliday extends Holiday {
		final int dayOffset;

		public EasterRelativeHoliday(String name, int dayOffset) {
			super(name);
			this.dayOffset = dayOffset;
		}

		@Override
		public LocalDate atDate(int year, Cache cache) {
			return cache.easter(year).plus(dayOffset, ChronoUnit.DAYS);
		}

		@Override
		public String toString() {
			return "EasterRelativeHoliday{" +
					"name='" + name + '\'' +
					", dayOffset=" + dayOffset +
					'}';
		}
	}

	public static class WeekdayRelativeHoliday extends Holiday {

		final DayOfWeek dayOfWeek;
		final boolean before;
		final int month;
		final int day;

		public WeekdayRelativeHoliday(String name, DayOfWeek dayOfWeek, boolean before, int month, int day) {
			super(name);
			this.dayOfWeek = dayOfWeek;
			this.before = before;
			this.month = month;
			this.day = day;
		}

		@Override
		public LocalDate atDate(int year, Cache cache) {
			final LocalDate refDate = LocalDate.of(year, month, day);
			final DayOfWeek refDayOfWeek = refDate.getDayOfWeek();
			int diff = before ?
					-diffDays(dayOfWeek, refDayOfWeek) :
					diffDays(refDayOfWeek, dayOfWeek);
			return refDate.plusDays(diff);
		}


		int diffDays(DayOfWeek dowFirst, DayOfWeek dowSecond) {
			final int d1 = dowFirst.getValue();
			final int d2 = dowSecond.getValue();
			final int diff = d2 - d1;
			if (diff < 0)
				return diff + 7;
			else
				return diff;
		}

		@Override
		public String toString() {
			return "WeekdayRelativeHoliday{" +
					"name='" + name + '\'' +
					", dayOfWeek=" + dayOfWeek +
					", before=" + before +
					", month=" + month +
					", day=" + day +
					'}';
		}
	}

}
