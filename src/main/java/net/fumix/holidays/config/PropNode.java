package net.fumix.holidays.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

class PropNode {
	final String name;
	Optional<String> value;
	LinkedHashMap<String, PropNode> children = new LinkedHashMap<>();

	PropNode(String name) {
		this.name = name;
		this.value = Optional.empty();
	}

	/**
	 * Just for initialization.
	 */
	PropNode getCreateChild(String name) {
		PropNode child = children.get(name);
		if (child == null) {
			child = new PropNode(name);
			children.put(name, child);
		}
		return child;
	}

	public Collection<PropNode> getChildren() {
		return children.values();
	}

	public Optional<PropNode> get(String subKey) {
		String[] subKeys = subKey.split("\\.");
		return get(subKeys);
	}

	public Optional<PropNode> get(String[] subKeys) {
		PropNode pn = this;
		for (String subKey : subKeys) {
			pn = pn.children.get(subKey);
			if (pn == null) break;
		}
		return Optional.ofNullable(pn);
	}

	public String getName() {
		return name;
	}
	public Optional<String> getValue() {
		return value;
	}
}
