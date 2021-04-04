package net.fumix.holidays.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Config {

	Map<String, Region> regions = new HashMap<>();

	public Optional<Region> regionOf(String abbrev) {
		return Optional.ofNullable(regions.get(abbrev));
	}



	public static Config fromResources() {
		final TreeProperties holidayProps = TreeProperties.from(loadProperties("/holiday.properties"));

		final Collection<PropNode> holidayNodes = holidayProps.root.get("holiday")
				.map(PropNode::getChildren)
				.orElse(Collections.emptyList());

		List<Holiday> holidays = holidayNodes.stream()
				.filter(hn -> hn.getValue().isPresent())
				.map(hn -> Holiday.fromConfig(hn.getName(), hn.getValue().get()))
				.peek(h -> System.out.println("Loaded: " + h))
				.collect(Collectors.toList());

		return new Config(holidays);
	}

	public Config(List<Holiday> holidays) {
		Region DE = new Region("Deutschland", null, "DE");
		holidays.stream().forEach(DE::withHoliday);
		regions.put("DE", DE);
		regions.put("DE_BER", DE);
		regions.put("DE_BW", DE);
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
