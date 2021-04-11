package de.fumix.holidays.config.properties;

import de.fumix.holidays.config.Holiday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * Reads holiday definitions from a properties file.
 */
public class HolidayProperties {
	private static final Logger LOG = LoggerFactory.getLogger(HolidayProperties.class);

	/**
	 * Creates a list of {@link Holiday}s from the properties map (initialized from a properties file).
	 *
	 * @param holidayProperties Content of a properties file with holiday definitions.
	 */
	public static void from(LinkedHashMap<String, String> holidayProperties, Consumer<Holiday> holidayConsumer) {
		final String logPrefix = "[from()]";

		final TreeProperties holidayProps = TreeProperties.from(holidayProperties);

		final Collection<PropNode> holidayNodes = holidayProps.root.get("holiday")
				.map(PropNode::getChildren)
				.orElse(Collections.emptyList());

		holidayNodes.stream()
				.filter(hn -> hn.getValue().isPresent())
				.map(hn -> Holiday.fromConfig(hn.getName(), hn.getValue().get()))
				.peek(h -> LOG.debug("{} Loaded holiday: {}", logPrefix, h))
				.forEach(holidayConsumer);
	}
}
