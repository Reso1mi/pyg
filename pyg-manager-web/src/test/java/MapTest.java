import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MapTest {
	
	@Test
	public void MapTest() {
		Map map=new HashMap();
		map.put("id=1", "text=手机");
		map.put("id=2", "text=手机");
		map.put("id=3", "text=手机");
		map.put("id=4", "text=手机");
		map.put("id=5", "text=手机");
		System.out.println(map);
	}
}
