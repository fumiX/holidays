package net.fumix.holidays.config;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PropNodeTest {

	@Test
	public void test() {
		PropNode root = new PropNode("");
		PropNode holiday = root.getCreateChild("holiday");
		PropNode test = holiday.getCreateChild("TEST");
		test.value = Optional.of("05-13");

		assertEquals("", root.getName());
		assertNotNull(root.get("holiday"));
		final Optional<PropNode> testNode = root.get("holiday.TEST");
		assertTrue(testNode.isPresent());

		final Optional<String> testValue = testNode.get().getValue();
		assertTrue(testValue.isPresent());
		assertEquals("05-13", testValue.get());
	}

	@Test
	public void testMultiple() {
		PropNode root = new PropNode("");
		PropNode holiday = root.getCreateChild("holiday");

		PropNode foo = holiday.getCreateChild("FOO");
		foo.value = Optional.of("05-13");

		PropNode bar = holiday.getCreateChild("BAR");
		bar.value = Optional.of("05-14");

		final Optional<PropNode> holiday1 = root.get("holiday");
		assertTrue(holiday1.isPresent());

		final Collection<PropNode> holidayNodes = holiday1.get().getChildren();
		assertEquals(2, holidayNodes.size());
		final PropNode[] propNodeArr = holidayNodes.toArray(new PropNode[2]);

		final PropNode foo1 = propNodeArr[0];
		assertNotNull(foo1);
		assertEquals("FOO", foo1.getName());
		assertEquals("05-13", foo1.getValue().get());

		final PropNode bar1 = propNodeArr[1];
		assertNotNull(bar1);
		assertEquals("BAR", bar1.getName());
		assertEquals("05-14", bar1.getValue().get());
	}

}
