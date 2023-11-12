package net.skds.lib.event;

import lombok.Getter;
import lombok.Setter;

public class Event {
	@Setter
	@Getter
	protected boolean canceled = false;
}
