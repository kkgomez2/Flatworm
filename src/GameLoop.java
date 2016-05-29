import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/*
 * What's new in 1.3:
 * -Intro screen text
 * -Logic for 4 round wins is a match win
 * -No random surname in a match
 * -Rematch screen, corresponding key listener
 * -Recovery time added to moves, using threading
 * -Fixed attacking while blocking glitch
 * -Cleaned up some code
 */
public class GameLoop extends Applet implements KeyListener{
	
	private ArrayList<Integer> keysDown; //A list of input key presses
	int phase = 1; //An int to keep track of which phase of the program it's in (ex. 1 -> pregame, 2 -> fight, 3 postgame)

	//Textfields for name entry
	private TextField p1Text;
	private TextField p2Text;
	
	//Buttons to confirm/submit
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
	
	//Two players
	private Flatworm p1;
	private Flatworm p2;
	
	//Indicates which player wins
	String winnerName = "";
	int winnerNumber = 0;
	
	//Limit of round losses
	int lossLimit = 3;
	
	//Two images to draw
	private Image p1Img;
	private Image p2Img;
	
	//Parsed name vectors
	NameParser firstNames;
	NameParser lastNames;

	//Milliseconds of attack recovery
	int recovery = 535;
	/*
	 * First method that runs,
	 * Initialize the hashmaps, run the first game method,
	 * Set up key listener and keysDown array
	 * @see java.applet.Applet#init()
	 */
	public void init(){
		hashInit();
		try {
			namesInit();
		} catch (IOException e) {
			// If text files cannot be found, handle the error.
			e.printStackTrace();
		}
		preGame();
		addKeyListener(this);
		keysDown = new ArrayList<Integer>();
	}

	public void paint(Graphics g){
		//Creates an applet window of 800x450
		setSize(800, 450);
		Graphics2D g2 = (Graphics2D) g;
		//Draw player 1 and 2 Flatworm images
		g2.drawImage(p1Img, 100, 100, 550, 400, 0, 0, 900, 600, this);
		g2.drawImage(p2Img, 700, 100, 250, 400, 0, 0, 900, 600, this);
		
		//Use different sizes of fonts for different things
		Font normalFont = g2.getFont();
		Font largeFont = normalFont.deriveFont(normalFont.getSize() * 1.4F);
		
		//Variable to adjust spacing of intro text together or dependant on winner
		int spacing;
		
		//If in fight phase, additionally draw the names
		if(phase == 1){
			spacing = 40;
			//Draw intro text/description
			g2.setFont(largeFont);
			g2.drawString("FLATWORM",350,30+spacing);
			g2.setFont(normalFont);
			//Wikipedia definition
			g2.drawString("\"The flatworms, or Platyhelminthes, Plathelminthes, or platyhelminths ",
							180, 80+spacing);
			g2.drawString("from the Greek πλατύ, platy, meaning \"flat\" and ἕλμινς (root: ἑλμινθ-), helminth-, meaning \"worm\")",
							95, 100+spacing);
			g2.drawString("are a phylum of relatively simple bilaterian, unsegmented, soft-bodied invertebrates. \"",
							130, 120+spacing);
			g2.drawString( "-- Wikipedia entry for Flatworm", 450, 150+spacing);

			g2.drawString("Flatworms are hermaphroditic creatures, meaning they have both male and female reproductive organs.",
							90, 220+spacing);

			//National Geographic excerpt
			g2.drawString("\"For flatworms, \"Who's your daddy?\" is a loaded question. In a bizarre bout lasting up to an hour,",
					115, 290+spacing);
			g2.drawString("the first flatworm to stab and inseminate its mate becomes the father.\"",
					190, 310+spacing);
			g2.drawString("-- National Geographic",
					490, 340+spacing);
			
		}
		else if(phase == 2){
			g2.setFont(largeFont);
			g2.drawString(p1.getName(),40,30);
			g2.drawString("VS.",100,45);
			g2.drawString(p2.getName(),40,60);
		} 
		//Ask for a rematch
		else if (phase == 3){
			//Change the horizontal spacing of the win text based on who won
			if(winnerNumber == 1){
				spacing = 0;
			}
			else {
				spacing = 350;
			}
			g2.setFont(largeFont);
			g2.drawString("WINNER!", 370, 30);
			g2.setFont(normalFont);
			g2.drawString(winnerName + " WINS!", 100+spacing, 60);
			g2.drawString("Rematch? Press SPACE to play again.",100+spacing, 80);
		}
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		//Add the pressed key onto the list of key presses
		if(!keysDown.contains(e.getKeyCode()))
			keysDown.add(new Integer(e.getKeyCode()));

		keyHandler();	
	}

	/*
	 * Returns true whether a key press is a Player 1 command or false if it isn't.
	 */
	public boolean p1Commands(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S
				|| e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_Z
				|| e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_C)
			return true;
		else return false;
	}

	/*
	 * Returns true whether a key press is a Player 2 command or false if it isn't.
	 */
	public boolean p2Commands(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_J || e.getKeyCode() == KeyEvent.VK_K
				|| e.getKeyCode() == KeyEvent.VK_L || e.getKeyCode() == KeyEvent.VK_M
				|| e.getKeyCode() == KeyEvent.VK_COMMA || e.getKeyCode() == KeyEvent.VK_PERIOD)
			return true;
		else return false;
	}

	@Override
	public void keyReleased(KeyEvent e) {

		//Remove the pressed key onto the list of key presses
		keysDown.remove(new Integer(e.getKeyCode()));

		if(phase == 2){
			//Set the Flatworms vulnurable after they release a key
			if(p1Commands(e) && !isBlocking(1)){
				p1.vulnurable();
				p1Img = getImage(getDocumentBase(), "img/fl_idle.png");
				repaint();
			}
			else if(p2Commands(e) && !isBlocking(2)){
				p2.vulnurable();
				p2Img = getImage(getDocumentBase(), "img/fl_idle.png");
				repaint();
			}
		}
	}

	/*
	 * Methods that set up the game, run it, and handles post game
	 */
	
	/*
	 * Initializes NameParsers and fills them with names
	 */
	public void namesInit() throws IOException{
		firstNames = new NameParser("txt/first-names.txt");
		lastNames = new NameParser("txt/last-names.txt");
	}
	
	/*
	 * Set up the text boxes and buttons for name entry.
	 * Call the action handler submit names for Flatworm creation
	 */
	public void preGame(){
	    p1Text = new TextField(20);
	    p2Text = new TextField(20);
	    b1 = new Button("P1 Name");
	    b3 = new Button("Randomize Names!");
	    b2 = new Button("P2 Name");

	    add(b1);
	    add(p1Text);
	    add(b3);
	    add(p2Text);
	    add(b2);

	    NameFlatwormAction setNameAction1 = new NameFlatwormAction(p1Text, 1);
	    b1.addActionListener(setNameAction1);
	    p1Text.addActionListener(setNameAction1);
	    
	    NameFlatwormAction setNameAction2 = new NameFlatwormAction(p2Text, 2);
	    b2.addActionListener(setNameAction2);
	    p2Text.addActionListener(setNameAction2);
	    
	    RandomNameAction randomNames = new RandomNameAction();
	    b3.addActionListener(randomNames);
	    
	}
	
	/*
	 * Initialize the game.
	 * Set the two images to display the two Flatworms initially idle
	 */
	public void newGame(){
		phase = 2;
		winnerName = "";
		winnerNumber = 0;
		p1Img = getImage(getDocumentBase(), "img/fl_idle.png");
		p2Img = getImage(getDocumentBase(), "img/fl_idle.png");
		repaint();
		System.out.println(p1.getName() + " vs. " + p2.getName());
	}
	
	/*
	 * Declare a winner, append names, and ask for rematch
	 */
	public void postGame(int winner){
		if(winner != 1 && winner != 2){
			return;
		}
		if (winner == 1){
			p2.appendName(p1.getLastName());
			p2.changeFirstName(firstNames.getRandomName());
			
			int hyphenCount = p2.getLastName().length() - p2.getLastName().replace("-", "").length();
			
			if(hyphenCount > lossLimit){
				winnerName = p1.getName();
				winnerNumber = 1;
				p2Img = getImage(getDocumentBase(), "img/fl_lose.png");
				phase = 3;
			}
		}
		else {
			p1.appendName(p2.getLastName());
			p1.changeFirstName(firstNames.getRandomName());

			int hyphenCount = p1.getLastName().length() - p1.getLastName().replace("-", "").length();

			if(hyphenCount > lossLimit){
				winnerName = p2.getName();
				winnerNumber = 2;
				p1Img = getImage(getDocumentBase(), "img/fl_lose.png");
				phase = 3;
			}
		}
	}

	HashMap<Integer, String> keyPressPrint = new HashMap<Integer, String>();
	HashMap<Integer, Runnable> ownAttackFunction = new HashMap<Integer, Runnable>();
	HashMap<Integer, Runnable> ownBlockFunction = new HashMap<Integer, Runnable>();
	HashMap<Integer, String> imageFilename = new HashMap<Integer, String>();
	HashMap<Integer, String> blockPrint = new HashMap<Integer, String>();
	
	/*
	 * Initializes all Hash Maps called by attack() and block()
	 * Ties key presses to a corresponding value for:
	 * console prints, image filenames, function calls.
	 */
	public void hashInit(){
		//A,S,D,J,K,L
		int[] attackKeyCodes = {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D,
						KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L};
		//Z,X,C,M,COMMA,PERIOD
		int[] blockKeyCodes = {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C,
							KeyEvent.VK_M, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD};
		
		String[] attackPressPrints = {"Low Attack", "Mid Attack", "High Attack"};
		String[] blockPressPrints = {"Low + Mid Block", "Low + High Block", "Mid + High Block"};
		String[] attackImageFilenames = {"img/fl_atklow.png", "img/fl_atkmid.png", "img/fl_atkhigh.png"};
		String[] blockImageFilenames = {"img/fl_block_lm.png", "img/fl_block_lh.png", "img/fl_block_mh.png"};
		String[] blockPrints = {"Low!", "Mid!", "High!"};
		
		for (int i = 0; i < 6 ; i++){
			keyPressPrint.put(attackKeyCodes[i], attackPressPrints[i % 3]);
			keyPressPrint.put(blockKeyCodes[i], blockPressPrints[i % 3]);
			imageFilename.put(attackKeyCodes[i], attackImageFilenames[i % 3]);
			imageFilename.put(blockKeyCodes[i], blockImageFilenames[i % 3]);
			blockPrint.put(attackKeyCodes[i], blockPrints[i % 3]);
		}
		
		//Tie button presses to functions to run
		ownAttackFunction.put(KeyEvent.VK_A, new Runnable() {
		    public void run() { p1.attackLow();}
		});
		
		ownAttackFunction.put(KeyEvent.VK_S, new Runnable() {
		    public void run() { p1.attackMid();}
		});
		
		ownAttackFunction.put(KeyEvent.VK_D, new Runnable() {
		    public void run() { p1.attackHigh();}
		});
		
		ownAttackFunction.put(KeyEvent.VK_J, new Runnable() {
		    public void run() { p2.attackLow();}
		});
		
		ownAttackFunction.put(KeyEvent.VK_K, new Runnable() {
		    public void run() { p2.attackMid();}
		});
		
		ownAttackFunction.put(KeyEvent.VK_L, new Runnable() {
		    public void run() { p2.attackHigh();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_Z, new Runnable() {
		    public void run() { p1.blockLowMid();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_X, new Runnable() {
		    public void run() { p1.blockLowHigh();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_C, new Runnable() {
		    public void run() { p1.blockMidHigh();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_M, new Runnable() {
		    public void run() { p2.blockLowMid();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_COMMA, new Runnable() {
		    public void run() { p2.blockLowHigh();}
		});
		
		ownBlockFunction.put(KeyEvent.VK_PERIOD, new Runnable() {
		    public void run() { p2.blockMidHigh();}
		});
		
	}
	
	/*
	 * Checks if the enemy is immune in a particular zone (low, mid, high)
	 * @return Whether the enemy is immune to thebutton press and its corresponding attack
	 */
	public boolean enemyImmune(int keyEvent){
		if (keyEvent == KeyEvent.VK_A) {
			return p2.isImmuneLow();
		}
		else if (keyEvent == KeyEvent.VK_S){
			return p2.isImmuneMid();
		}
		else if (keyEvent == KeyEvent.VK_D){
			return p2.isImmuneHigh();
		}
		else if (keyEvent == KeyEvent.VK_J){
			return p1.isImmuneLow();
		}
		else if (keyEvent == KeyEvent.VK_K){
			return p1.isImmuneMid();
		}
		else if (keyEvent == KeyEvent.VK_L){
			return p1.isImmuneHigh();
		}
		else return true;
	}
	
	/*
	 * Returns whether the player is currently blocking in any way.
	 */
	public boolean isBlocking(int p){
		if(p == 2){
			return keysDown.contains(KeyEvent.VK_M)
					|| keysDown.contains(KeyEvent.VK_COMMA)
					|| keysDown.contains(KeyEvent.VK_PERIOD);
		}
		else {
			return keysDown.contains(KeyEvent.VK_Z)
					|| keysDown.contains(KeyEvent.VK_X)
					|| keysDown.contains(KeyEvent.VK_C);
		}
	}
	
	boolean p1Recovery = false;
	boolean p2Recovery = false;
	
	/*
	 * Attack or block handler.
	 * Checks to see if the corresponding key press is in the key press array.
	 * If so, call its corresponding sequence of functions.
	 */
	public void keyHandler(){
		if(phase == 3){
			if(keysDown.contains(KeyEvent.VK_SPACE)){
				p1.changeFirstName(firstNames.getRandomName());
				p1.changeLastName(lastNames.getRandomName());
				p2.changeFirstName(firstNames.getRandomName());
				p2.changeLastName(lastNames.getRandomName());
				newGame();
			}
		}
		
		if(phase == 2){
			if(!p1Recovery){
				//Moves should start recovery frames
				if (keysDown.contains(KeyEvent.VK_A) && !isBlocking(1)){
					(new P1RecoveryFrames()).start();
					attacks(1, KeyEvent.VK_A);
				}
				
				if (keysDown.contains(KeyEvent.VK_S) && !isBlocking(1)){
					(new P1RecoveryFrames()).start();
					attacks(1, KeyEvent.VK_S);
				}
				
				if (keysDown.contains(KeyEvent.VK_D) && !isBlocking(1)){
					(new P1RecoveryFrames()).start();
					attacks(1, KeyEvent.VK_D);
				}
				if (keysDown.contains(KeyEvent.VK_Z)){
					(new P1RecoveryFrames()).start();
					blocks(1, KeyEvent.VK_Z);
				}
				
				if (keysDown.contains(KeyEvent.VK_X)){
					(new P1RecoveryFrames()).start();
					blocks(1, KeyEvent.VK_X);
				}
				
				if (keysDown.contains(KeyEvent.VK_C)){
					(new P1RecoveryFrames()).start();
					blocks(1, KeyEvent.VK_C);
				}
			}
			
			if(!p2Recovery){
				//Moves should start recovery frames
				if (keysDown.contains(KeyEvent.VK_J) && !isBlocking(2)){
					(new P2RecoveryFrames()).start();
					attacks(2, KeyEvent.VK_J);
				}
				
				if (keysDown.contains(KeyEvent.VK_K) && !isBlocking(2)){
					(new P2RecoveryFrames()).start();
					attacks(2, KeyEvent.VK_K);
				}
				
				if (keysDown.contains(KeyEvent.VK_L) && !isBlocking(2)){
					(new P2RecoveryFrames()).start();
					attacks(2, KeyEvent.VK_L);
				}
				//Blocks should not
				if (keysDown.contains(KeyEvent.VK_M)){
					(new P2RecoveryFrames()).start();
					blocks(2, KeyEvent.VK_M);
				}
				
				if (keysDown.contains(KeyEvent.VK_COMMA)){
					(new P2RecoveryFrames()).start();
					blocks(2, KeyEvent.VK_COMMA);
				}
				
				if (keysDown.contains(KeyEvent.VK_PERIOD)){
					(new P2RecoveryFrames()).start();
					blocks(2, KeyEvent.VK_PERIOD);
				}	
			}
		}
	}
	
	/*
	 * Generic method for a player attack.
	 * Prints to console, gets from hashmaps the corresponding value to print or function to run.
	 * (Print attack to console, run attack, draw corresponding image, repaint, check if you won.)
	 */
	public void attacks(int player, int keyEvent){
		
		if (player == 1){
			System.out.print("PRESS: P1 ");
		} else if (player == 2){
			System.out.print("PRESS: P2 ");
		}
		System.out.println(keyPressPrint.get(keyEvent));
		
		Runnable attack = ownAttackFunction.get(keyEvent);
		if (attack != null){
			attack.run();
		}
		
		if (player == 1){
			p1Img = getImage(getDocumentBase(), imageFilename.get(keyEvent));
		} else if (player == 2){
			p2Img = getImage(getDocumentBase(), imageFilename.get(keyEvent));
		}
		
		repaint();
		
		if(!(enemyImmune(keyEvent))){
			postGame(player);
		} else {
			if (player == 1){
				System.out.print("P2 is BLOCKING ");
			} else if (player == 2){
				System.out.print("P1 is BLOCKING ");
			}
			System.out.println(blockPrint.get(keyEvent));
		}
	}
	
	/*
	 * Generic method for a player block.
	 * Prints to console, gets from hashmaps the corresponding value to print or function to run.
	 * (Print block to console and draw corresponding image, repaint.)
	 */
	public void blocks(int player, int keyEvent){
		
		if(player == 1){
			System.out.print("PRESS: P1 ");
		} else if (player == 2){
			System.out.print("PRESS: P2 ");
		}
		System.out.println(keyPressPrint.get(keyEvent));

		Runnable block = ownBlockFunction.get(keyEvent);
		if (block != null){
			block.run();
		}
		
		if(player == 1){
			p1Img = getImage(getDocumentBase(), imageFilename.get(keyEvent));
		} else if (player == 2){
			p2Img = getImage(getDocumentBase(), imageFilename.get(keyEvent));
		}
		repaint();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Sets P1 to a non-inputting state for the recovery time
	 */
	public class P1RecoveryFrames extends Thread {
		public void run() {
			p1Recovery = true;
			try {
				TimeUnit.MILLISECONDS.sleep(recovery);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p1Recovery = false;
		}
	}

	/*
	 * Sets P2 to a non-inputting state for the recovery time
	 */
	public class P2RecoveryFrames extends Thread {
		public void run() {
			p2Recovery = true;
			try {
				TimeUnit.MILLISECONDS.sleep(recovery);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p2Recovery = false;
		}
	}
	//Set to true once name is submitted
	boolean p1Ready;
	boolean p2Ready;
	
	/*
	 * Action Listener that gives P1 and P2 random names, cleans up, and starts the game
	 */
	class RandomNameAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			p1 = new Flatworm(firstNames.getRandomName(), lastNames.getRandomName());
			p2 = new Flatworm(firstNames.getRandomName(), lastNames.getRandomName());

			remove(b1);
			remove(p1Text);

			remove(b2);
			remove(p2Text);
			
			p1Ready = true;
			p2Ready = true;
			
			remove(b3);
			b3.removeActionListener(this);
			newGame();
		}
	}
	
	/*
	 * Action listener that gives Flatworms name input by player.
	 * Cleans up and begins match once they both are ready.
	 */
	class NameFlatwormAction implements ActionListener {

		private TextField in;
		private String first;
		private String last;
		private int count; //There are two names, keep track of which one is submitted
		private int playerNumber;
		
		public NameFlatwormAction(TextField in, int playerNumber) {
			this.in = in;
			count = 0;
			this.playerNumber = playerNumber;
			//Ask for First Name first.
			in.setText("Enter First Name");
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			String s = in.getText();
			System.out.println(s);
			if (count == 0){
				first = s.toUpperCase();
				in.setText("Enter Last Name");
			}
			else if (count == 1){
				last = s.toUpperCase();
				//When both names are submitted, remove the buttons/textbox
				//Create the Flatworm with the set name
				//Declare player 1 or 2 ready
				if (playerNumber == 1){
					remove(b1);
					remove(p1Text);
					p1 = new Flatworm(first, last);
					p1Ready = true;
				} else if (playerNumber == 2){
					remove(b2);
					remove(p2Text);
					p2 = new Flatworm(first, last);
					p2Ready = true;
				}
				//If they're both ready, proceed to the fight
				 if(p1Ready && p2Ready){
					 remove(b3);
					 newGame();
				 }
			}
			count++;
		}
	}
}


