import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
public class Test_bot {
	@Test
	public void test_test() {
		assertEquals("privet", testbot.Main.test("/start"));
		assertEquals("privet", testbot.Main.test("/start"));
	}
}
