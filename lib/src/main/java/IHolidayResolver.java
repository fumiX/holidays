import net.fumix.holidays.DayCategory;

import java.time.LocalDate;

@FunctionalInterface
public interface IHolidayResolver {
	DayCategory dayCategory(LocalDate date);
}
