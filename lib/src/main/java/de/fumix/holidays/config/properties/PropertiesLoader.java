package de.fumix.holidays.config.properties;

import de.fumix.holidays.config.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.BiConsumer;

public class PropertiesLoader {

	/**
	 * Read a properties file from the resources, i.e. packaged with the application.
	 *
	 * @param propsPath Resource path to the properties file.
	 * @param propConsumer Called with property key and value for the properties in the file (ordered).
	 */
	public static void load(String propsPath, BiConsumer<String, String> propConsumer) {
		try (InputStream inputStream = Config.class.getResourceAsStream(propsPath)) {
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
