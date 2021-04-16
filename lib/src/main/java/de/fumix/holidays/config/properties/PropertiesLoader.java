package de.fumix.holidays.config.properties;

import de.fumix.holidays.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.function.BiConsumer;

public class PropertiesLoader {
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

	/**
	 * Read a properties file from the resources, i.e. packaged with the application.
	 *
	 * @param propsPath Resource path to the properties file.
	 * @param propConsumer Called with property key and value for the properties in the file (ordered).
	 */
	public static void load(String propsPath, BiConsumer<String, String> propConsumer) {
		final String logPrefix = "[load()]";

		final URL resource = PropertiesLoader.class.getResource(propsPath);
		LOG.debug("{} Loading property resources '{}'", logPrefix, resource);
		try (InputStream inputStream = resource.openStream()) {
			if (inputStream == null) {
				throw new IllegalArgumentException("Property file not found on classpath: '" + propsPath + "'");
			}
			load(inputStream, propConsumer);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Resource file not found: '" + propsPath + "'", e);
		} catch (IOException e) {
			throw new RuntimeException("Error reading resource file: '" + propsPath + "'", e);
		}

	}

	/**
	 * Read properties from a stream.
	 *
	 * @param inStream Fresh input stream to the properties source (i.e. properties file).
	 * @param propConsumer Called with property key and value for the properties read from the stream (ordered).
	 *
	 * @throws IOException on probles with the properties file's stream
	 */
	public static void load(InputStream inStream, BiConsumer<String, String> propConsumer) throws IOException {

		@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
		Properties properties = new Properties() {
			public Object put(Object key, Object value) {
				propConsumer.accept((String) key, (String) value);
				return null;
			}
		};

		properties.load(inStream);
	}


}
