package de.fumix.holidays.config.properties;


import java.util.Map;
import java.util.Optional;

public class TreeProperties {

	PropNode root = new PropNode("");

	public static TreeProperties from(Map<String, String> props) {
		TreeProperties treeProps = new TreeProperties();
		props.keySet().stream().forEach(key -> treeProps.addProperty(key, props.get(key)));
		return treeProps;
	}

	void addProperty(String propKey, String value) {
		final String[] parts = propKey.split("\\.");

		PropNode pn = root;
		for (String part : parts) {
			pn = pn.getCreateChild(part);
		}
		pn.value = Optional.of(value); // Should never be null, see http://javahowto.blogspot.com/2013/11/javautilproperties-value.html

	}

}
