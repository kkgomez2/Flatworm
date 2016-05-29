import static org.junit.Assert.*;

import org.junit.Test;

public class FlatwormTest {

	@Test
	public void nameTests() {
		Flatworm f = new Flatworm();
		assertEquals("John Smith", f.getName());
		
		Flatworm p = new Flatworm("Peter", "Parker");
		assertEquals("Parker", p.getLastName());
		assertEquals("Peter", p.getFirstName());
		
		p.appendName("Smith");
		assertEquals("Parker-Smith", p.getLastName());
		
		f.appendName(p.getLastName());
		assertEquals("Smith-Parker-Smith", f.getLastName());
	}
	
	@Test
	public void vulnurablityTests(){
		Flatworm f = new Flatworm();
	
		f.attackHigh();
		assertTrue(f.isImmuneHigh());
		assertFalse(f.isImmuneMid());
		assertFalse(f.isImmuneLow());

		f.attackMid();
		assertFalse(f.isImmuneHigh());
		assertTrue(f.isImmuneMid());
		assertFalse(f.isImmuneLow());
	
		f.attackLow();
		assertFalse(f.isImmuneHigh());
		assertFalse(f.isImmuneMid());
		assertTrue(f.isImmuneLow());
		
		f.blockLowMid();
		assertFalse(f.isImmuneHigh());
		assertTrue(f.isImmuneMid());
		assertTrue(f.isImmuneLow());

		f.blockLowHigh();
		assertTrue(f.isImmuneHigh());
		assertFalse(f.isImmuneMid());
		assertTrue(f.isImmuneLow());
		
		f.blockMidHigh();
		assertTrue(f.isImmuneHigh());
		assertTrue(f.isImmuneMid());
		assertFalse(f.isImmuneLow());
		
	}
	

}
