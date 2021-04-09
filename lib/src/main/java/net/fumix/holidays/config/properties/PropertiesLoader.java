package net.fumix.holidays.config.properties;

import net.fumix.holidays.config.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.function.BiConsumer;

public class PropertiesLoader {

	/**
	 * Read a properties file from the resources, i.e. packaged with the application.
	 *
	 * @param propsPath
	 * @param propConsumer Called with property key & value for the properties in the file (ordered).
	 */
	public static void load(String propsPath, BiConsumer<String, String> propConsumer) {
		try (InputStream inputStream = Config.class.getResourceAsStream(propsPath)) {
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
	 * @param inStream
	 * @param propConsumer Called with property key & value for the properties read from the stream (ordered).
	 */
	public static void load(InputStream inStream, BiConsumer<String, String> propConsumer) throws IOException {

		Properties properties = new Properties() {
			public Object put(Object key, Object value) {
				propConsumer.accept((String) key, (String) value);
				return null;
			}
		};

		properties.load(inStream);
	}


}
