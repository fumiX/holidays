package de.fumix.holidays.config.properties;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TreePropertiesTest {

	@Test
	void addProperty() {
		TreeProperties props = new TreeProperties();
		props.addProperty("holiday.FOO", "foo");
		props.addProperty("holiday.BAR", "bar");

		final Optional<PropNode> holiday1 = props.root.get("holiday");
		assertTrue(holiday1.isPresent());

		final Collection<PropNode> holidayNodes = holiday1.get().getChildren();
		assertEquals(2, holidayNodes.size());
		final PropNode[] propNodeArr = holidayNodes.toArray(new PropNode[2]);

		final PropNode foo1 = propNodeArr[0];
		assertNotNull(foo1);
		assertEquals("FOO", foo1.getName());
		assertEquals("foo", foo1.getValue().get());

		final PropNode bar1 = propNodeArr[1];
		assertNotNull(bar1);
		assertEquals("BAR", bar1.getName());
		assertEquals("bar", bar1.getValue().get());

	}
}
