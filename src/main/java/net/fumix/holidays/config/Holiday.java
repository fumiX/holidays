package net.fumix.holidays.config;

import net.fumix.holidays.Cache;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Holiday {

//	public static Holiday NEW_YEAR_JAN1 = new FixedDateHoliday(1,1);
//	public static Holiday EPIPHANIAS_JAN6 = new FixedDateHoliday(1,6);
//	public static Holiday WOMENS_DAY_MAR8 = new FixedDateHoliday(3,8);
//	public static Holiday LABOR_DAY_MAY1 = new FixedDateHoliday(5,1);
//	public static Holiday ASSUMPTION_DAY_AUG15 = new FixedDateHoliday(8,15);
//	public static Holiday CHILDRENS_DAY_SEP20 = new FixedDateHoliday(9,20);
//	public static Holiday GERMAN_REUNIFICATION_OCT3 = new FixedDateHoliday(10,3);
//	public static Holiday REFORMATION_DAY_OCT31 = new FixedDateHoliday(10,31);
//	public static Holiday ALL_SAINTS_NOV1 = new FixedDateHoliday(11,1);
//	public static Holiday CHRISTMAS_DAY_DEC25 = new FixedDateHoliday(12,25);
//	public static Holiday BOXING_DAY_DEC26 = new FixedDateHoliday(12,26);
//
//	public static Holiday GOOD_FRIDAY = new EasterRelativeHoliday(-2);
//	public static Holiday EASTER = new EasterRelativeHoliday(0);
//	public static Holiday EASTER_MONDAY = new EasterRelativeHoliday(1);
//	public static Holiday ASCENCION_DAY = new EasterRelativeHoliday(39);
//	public static Holiday PENTECOST = new EasterRelativeHoliday(49);
//	public static Holiday PENTECOST_MONDAY = new EasterRelativeHoliday(50);
//	public static Holiday CORPUS_CHRISTI = new EasterRelativeHoliday(60);

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
