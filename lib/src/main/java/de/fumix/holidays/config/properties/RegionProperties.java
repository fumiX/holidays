package de.fumix.holidays.config.properties;

import de.fumix.holidays.config.Holiday;
import de.fumix.holidays.config.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionProperties {
	private static final Logger LOG = LoggerFactory.getLogger(RegionProperties.class);

	final static Pattern fullPattern = Pattern.compile("(\\d*)-(\\d*)");
	final static Pattern yearPattern = Pattern.compile("(\\d+)");

	/**
	 * Creates a list of {@link Region}s from the properties map (initialized from a properties file).
	 *
	 * @param regionProperties Content of a properties file with region definitions.
	 * @return The created region objects.
	 */
	public static void from(LinkedHashMap<String, String> regionProperties, Map<String, Holiday> holidays,
	                        Consumer<Region> regionConsumer) {
		final String logPrefix = "[from()]";
		LOG.debug("{} Adding regions from properties map {}", logPrefix);

		HashMap<String, Region> cachedRegions = new HashMap<>(); // Keep created regions to link regions with their parents.

		final TreeProperties regionProps = TreeProperties.from(regionProperties);

		final Collection<PropNode> regionNodes = regionProps.root.get("region")
				.map(PropNode::getChildren)
				.orElse(Collections.emptyList());

		for (PropNode regionNode : regionNodes) {
			final String regionKey = regionNode.getName();
			final String regionName = regionNode.get("name")
					.flatMap(PropNode::getValue)
					.orElseThrow(() -> new IllegalArgumentException("region-properties: Region " + regionKey + " must have a 'name'"));

			final Optional<Region> parent = regionNode.get("parent")
					.flatMap(PropNode::getValue)
					.map(parentKey -> {
						Region parentRegion = cachedRegions.get(parentKey);
						if (parentRegion == null) {
							throw new IllegalArgumentException("region-properties: Region " + regionKey + " refers to parent region '" + parentKey + "', which is not defined yet (parent regions have to be defined before).");
						} else {
							return parentRegion;
						}
					});
			Region region = new Region(regionName, parent, regionKey);

			LOG.debug("{} Setting up region {}", logPrefix, region);
			final Collection<PropNode> regionHolidayNodes = regionNode.get("holiday")
					.map(PropNode::getChildren)
					.orElse(Collections.emptyList());
			for (PropNode rhn : regionHolidayNodes) {
				final Holiday holiday = holidays.get(rhn.getName());
				if (holiday == null) {
					throw new IllegalArgumentException("Holiday '" + rhn.getName() + "' referred to for region '" + regionKey + "' is not defined.");
				}

				// TODO: process valid year ranges.
				final Optional<String> value = rhn.getValue();
				if (value.isPresent() && !value.get().trim().isEmpty()) {
					final String[] rangeExpressions = value.get().split(",");
					for (String rangeExpr : rangeExpressions) {
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
							region.withHoliday(holiday, yearStart, yearEnd);
							continue;
						}

						throw new IllegalArgumentException("Validity expression '" + rangeExpr + "' for holiday assignment for region '" + region.getAbbrev() + "' and holiday '" + holiday.getName() + "' is invalid.");
					}
				} else {
					// The holiday has no range expression and is always valid.
					region.withHoliday(holiday);
				}
			}
			cachedRegions.put(region.abbrev, region);
			regionConsumer.accept(region);
		}
	}
}
