package net.fumix.holidays.config;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidityInterval {
	final static Pattern fullPattern = Pattern.compile("(\\d*)-(\\d+)");
	final static Pattern yearPattern = Pattern.compile("(\\d+)");

	final Optional<Integer> yearStart;
	final Optional<Integer> yearEnd;

	public static ValidityInterval of(Region region, Holiday holiday, String valExpr) {
		final Matcher fullMatcher = fullPattern.matcher(valExpr);
		if (fullMatcher.matches()) {
			Optional<Integer> yearStart = fullMatcher.group(1).trim().isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(fullMatcher.group(1)));
			Optional<Integer> yearEnd = fullMatcher.group(2).trim().isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(fullMatcher.group(2)));
			return new ValidityInterval(yearStart, yearEnd);
		}

		final Matcher yearMatcher = yearPattern.matcher(valExpr);
		if (yearMatcher.matches()) {
			Optional<Integer> yearStart = Optional.of(Integer.parseInt(fullMatcher.group(1)));
			Optional<Integer> yearEnd = yearStart;
			return new ValidityInterval(yearStart, yearEnd);
		}

		throw new IllegalArgumentException("Validity expression '" + valExpr + "' for holiday assignment for region '" + region.getAbbrev() + "' and holiday '" + holiday.getName() + "' is invalid.");
	}

	public ValidityInterval() {
		this(Optional.empty(), Optional.empty());
	}

	public ValidityInterval(Optional<Integer> yearStart, Optional<Integer> yearEnd) {
		this.yearStart = yearStart;
		this.yearEnd = yearEnd;
	}

	public boolean isWithin(int year) {
		boolean afterStart = yearStart.map(s -> s <= year).orElse(true);
		boolean beforeEnd = yearEnd.map(e -> e >= year).orElse(true);
		return afterStart && beforeEnd;
	}
}
