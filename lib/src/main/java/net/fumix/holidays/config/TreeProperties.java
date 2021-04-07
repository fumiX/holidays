package net.fumix.holidays.config;

import java.util.*;

public class TreeProperties {

	PropNode root = new PropNode("");

	public static TreeProperties from(Properties props) {
		TreeProperties treeProps = new TreeProperties();

		final Enumeration<?> propEnum = props.propertyNames();
		while (propEnum.hasMoreElements()) {
			String propKey = (String) propEnum.nextElement();
			String value = props.getProperty(propKey);
			treeProps.addProperty(propKey, value);
		}
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
