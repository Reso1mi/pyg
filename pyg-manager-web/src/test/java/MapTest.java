import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MapTest {
	
	@Test
	public void MapTest() {
		String[] atp = {"Rafael Nadal", "Novak Djokovic",
				"Stanislas Wawrinka",
				"David Ferrer","Roger Federer",
				"Andy Murray","Tomas Berdych",
				"Juan Martin Del Potro"};
		List<String> players =  Arrays.asList(atp);
// 以前的循环方式
		for (String player : players) {
			System.out.print(player + "; ");
		}

// 使用 lambda 表达式以及函数操作(functional operation)
		players.forEach((player) -> System.out.print(player + "; "));

// 在 Java 8 中使用双冒号操作符(double colon operator)
		players.forEach(System.out::println);
	}
}
