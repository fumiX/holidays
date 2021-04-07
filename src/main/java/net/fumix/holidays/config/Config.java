package net.fumix.holidays.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Config {

	final static Pattern fullPattern = Pattern.compile("(\\d*)-(\\d*)");
	final static Pattern yearPattern = Pattern.compile("(\\d+)");

	Map<String, Holiday> holidays = new HashMap<>();
	Map<String, Region> regions = new HashMap<>();

	public Optional<Region> regionOf(String abbrev) {
		return Optional.ofNullable(regions.get(abbrev));
	}
	public Optional<Holiday> holidayOf(String abbrev) { return Optional.ofNullable(holidays.get(abbrev)); }


	public static Config fromResources() {

		// Holidays
		final TreeProperties holidayProps = TreeProperties.from(loadProperties("/holiday.properties"));

		final Collection<PropNode> holidayNodes = holidayProps.root.get("holiday")
				.map(PropNode::getChildren)
				.orElse(Collections.emptyList());

		List<Holiday> holidays = holidayNodes.stream()
				.filter(hn -> hn.getValue().isPresent())
				.map(hn -> Holiday.fromConfig(hn.getName(), hn.getValue().get()))
				.peek(h -> System.out.println("Loaded: " + h))
				.collect(Collectors.toList());

		final Config config = new Config(holidays);


		// Regions
		final TreeProperties regionProps = TreeProperties.from(loadProperties("/region.properties"));
		final Collection<PropNode> regionNodes = regionProps.root.get("region")
				.map(PropNode::getChildren)
				.orElse(Collections.emptyList());

		for(PropNode regionNode: regionNodes) {
			final String regionKey = regionNode.getName();
			final String regionName = regionNode.get("name")
					.flatMap(PropNode::getValue)
					.orElseThrow(() -> new IllegalArgumentException("region-properties: Region " + regionKey + " must have a 'name'"));

			final Optional<Region> parent = regionNode.get("parent")
					.flatMap(PropNode::getValue)
					.flatMap(config::regionOf);
			Region region = new Region(regionName, parent, regionKey);
			final Collection<PropNode> regionHolidayNodes = regionNode.get("holiday")
					.map(PropNode::getChildren)
					.orElse(Collections.emptyList());
			for(PropNode rhn: regionHolidayNodes) {
				final Holiday holiday = config.holidayOf(rhn.getName())
						.orElseThrow(() -> new IllegalArgumentException("Holiday '" + rhn.getName() + "' referred to for region '" + regionKey + "' is not defined."));
				// TODO: process valid year ranges.
				final Optional<String> value = rhn.getValue();
				if (value.isPresent() && !value.get().trim().isEmpty()) {
					final String[] rangeExpressions = value.get().split(",");
					for(String rangeExpr: rangeExpressions) {
						final Matcher fullMatcher = fullPattern.matcher(rangeExpr);
						if (fullMatcher.matches()) {
							Optional<Integer> yearStart = fullMatcher.group(1).trim().isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(fullMatcher.group(1)));
							Optional<Integer> yearEnd = fullMatcher.group(2).trim().isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(fullMatcher.group(2)));
							region.withHoliday(holiday, yearStart, yearEnd);
							continue;
						}

						final Matcher yearMatcher = yearPattern.matcher(rangeExpr);
						if (yearMatcher.matches()) {
							Optional<Integer> yearStart = Optional.of(Integer.parseInt(yearMatcher.group(1)));
							Optional<Integer> yearEnd = yearStart;
							region.withHoliday(holiday, yearStart, yearStart);
							continue;
						}

						throw new IllegalArgumentException("Validity expression '" + rangeExpr + "' for holiday assignment for region '" + region.getAbbrev() + "' and holiday '" + holiday.getName() + "' is invalid.");
					}
				} else {
					// The holiday has no range expression and is always valid.
					region.withHoliday(holiday);
				}
			}
			config.addRegion(region);
		}

		return config;
	}

	public Config(List<Holiday> _holidays) {
		this.holidays = _holidays.stream()
				.collect(Collectors.toMap(h->h.name, h->h));
	}

	Config addRegion(Region region) {
		regions.put(region.getAbbrev(), region);
		return this;
	}

	private static Properties loadProperties(String propsPath) {
		Properties prop = new Properties();
		try (InputStream inputStream = Config.class.getResourceAsStream(propsPath)) {
			prop.load(inputStream);
			return prop;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Resource file not found: '" + propsPath + "'", e);
		} catch (IOException e) {
			throw new RuntimeException("Error reading resource file: '" + propsPath + "'", e);
		}
	}
}
