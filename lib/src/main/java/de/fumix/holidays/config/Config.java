package de.fumix.holidays.config;

import de.fumix.holidays.Holidays;
import de.fumix.holidays.config.properties.HolidayProperties;
import de.fumix.holidays.config.properties.PropertiesLoader;
import de.fumix.holidays.config.properties.RegionProperties;
import de.fumix.holidays.impl.HolidaysImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	// Resource paths to the configuration.
	final static String HOLIDAY_PROPERTIES = "classpath:/holiday.properties";
	final static String REGION_DE_PROPERTIES = "classpath:/region_DE.properties";
	final static String REGION_AT_PROPERTIES = "classpath:/region_AT.properties";


	final LinkedHashMap<String, Holiday> holidays;
	final LinkedHashMap<String, Region> regions;

	public Optional<Region> regionOf(String abbrev) {
		return Optional.ofNullable(regions.get(abbrev));
	}
	public Optional<Holiday> holidayOf(String abbrev) { return Optional.ofNullable(holidays.get(abbrev)); }


	public static ResourceBundle getHolidaysBundle(Locale locale) {
		return ResourceBundle.getBundle("holidays.holidays");
	}

	public static Config fromResources() {
		final String logPrefix = "[fromResources()]";

		final Config config = new Config();

		// HolidaysImpl
		LOG.debug("{} Loading holidays from resource file {}", logPrefix, HOLIDAY_PROPERTIES);
		final LinkedHashMap<String, String> holidayProperties = new LinkedHashMap<>();
		PropertiesLoader.load(HOLIDAY_PROPERTIES, holidayProperties::put);
		HolidayProperties.from(holidayProperties, config::addHoliday);

		// Regions
		final LinkedHashMap<String, String> regionProperties = new LinkedHashMap<>();
		PropertiesLoader.load(REGION_DE_PROPERTIES, regionProperties::put);
		PropertiesLoader.load(REGION_AT_PROPERTIES, regionProperties::put);
		RegionProperties.from(regionProperties, config.holidays, config::addRegion);


		return config;
	}

	public Config() {
		this.holidays = new LinkedHashMap<>();
		this.regions = new LinkedHashMap<>();
	}

	public Config addRegion(Region region) {
		regions.put(region.getRegionId(), region);
		return this;
	}

	public Config addHoliday(Holiday...holidays) {
		Arrays.stream(holidays).forEach(holiday -> this.holidays.put(holiday.getHolidayId(), holiday));
		return this;
	}

	public List<Region> getRegions() {
		return new ArrayList<>(regions.values());
	}

	public Holidays	forRegion(Region region) {
		return new HolidaysImpl(region);
	}
}
