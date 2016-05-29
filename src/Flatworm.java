/*
 *  Represents a Flatworm.
 *  More info on 
 *  @author Kevin Gomez kkgomez2@illinois.edu kevinkgomez.com
 */

public class Flatworm {
	
	/*
	 * Private variables that refer to the Flatworm's name
	 * and whether they are vunurable in high/mid/low positions
	 */
	private String firstName;
	private String lastName;
	private boolean highImmune;
	private boolean midImmune;
	private boolean lowImmune;
	
	/*
	 * New Flatworm with no parameters defaults to "John Smith"
	 */
	public Flatworm(){
		firstName = "John";
		lastName = "Smith";
		highImmune = false;
		midImmune = false;
		lowImmune = false;
	}
	
	/*
	 * @param f The first name of the Flatworm
	 * @param l The last name of the Flatworm
	 */
	public Flatworm(String f, String l){
		firstName = f;
		lastName = l;
		highImmune = false;
		midImmune = false;
		lowImmune = false;
	}
	
	public String getName(){
		return firstName + " " + lastName;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}

	public boolean isImmuneHigh(){
		return highImmune;
	}
	
	public boolean isImmuneMid(){
		return midImmune;
	}
	
	public boolean isImmuneLow(){
		return lowImmune;
	}

	public void changeFirstName(String newFirstName){
		firstName = newFirstName;
	}
	
	public void changeLastName(String newLastName){
		lastName = newLastName;
	}
	/*
	 * When a Flatworm loses, their child has their surname appended
	 * with the surname of the victor
	 */
	public void appendName(String moreName){
		//TODO: Error checking
		lastName = lastName + "-" + moreName;
	}
	
	
	/*
	 * attackX and blockX methods that determine whether the Flatworm is
	 * immune or not in its corresponding high/mid/low positions
	 */
	
	public void attackHigh(){
		highImmune = true;
		midImmune = false;
		lowImmune = false;
	}
	
	public void attackMid(){
		highImmune = false;
		midImmune = true;
		lowImmune = false;
	}
	
	public void attackLow(){
		highImmune = false;
		midImmune = false;
		lowImmune = true;
	}

	public void blockMidHigh(){
		highImmune = true;
		midImmune = true;
		lowImmune = false;
	}
	
	public void blockLowHigh(){
		highImmune = true;
		midImmune = false;
		lowImmune = true;
	}
	
	public void blockLowMid(){
		highImmune = false;
		midImmune = true;
		lowImmune = true;
	}
	
	public void vulnurable(){
		highImmune = false;
		midImmune = false;
		lowImmune = false;
	}
	
}
