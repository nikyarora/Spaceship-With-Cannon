import java.awt.*;
import java.awt.event.*;	
import javax.swing.*;
import java.awt.Color;
/******************************************************************************
 * 
 * Name:		Nikhar Arora
 * Block:		B
 * Date:		5/1/14
 * 
 * Program #22: Spaceship With Cannon
 * Description: 
 *  This program has a spaceship that has a cannon attached to it. The user can
 *  enter the following keys for the spaceship to do something:
 *  Up Arrow: Thrust Forwards
 *  Down Arrow: Thrust Backwards
 *  Right Arrow: Rotate Cannon Right
 *  Left Arrow: Rotate Cannon Left
 *  s: Shoot A Bullet
 ******************************************************************************/ 
public class SpaceshipWithCannon
extends JFrame 
implements ActionListener,
KeyListener
{
	// DATA:
	private static Spaceship s;	
	private static Bullet [] bullets = new Bullet[30];

	private static final int MAX_WIDTH = 800;	
	private static final int MIN_WIDTH = 0;
	private static final int MAX_HEIGHT = 600;		
	private static final int MIN_HEIGHT = 22;

	private static final int START_RADIUS = 40;
	private static final int START_X_AND_Y = 100;
	private static final int START_VELOCITY = 2;

	private static int bulletNumber = 0;

	private static final Color START_COLOR = Color.darkGray;

	private static final int DELAY_IN_MILLISEC = 30;

	private static Image spaceBackground;

	// METHODS:

	/**
	 * main -- Start up the window.
	 * 
	 * @param	args
	 */
	public static void main(String args[])
	{
		// Create the window.
		SpaceshipWithCannon gp = new SpaceshipWithCannon();
		gp.setSize(MAX_WIDTH, MAX_HEIGHT);
		gp.setVisible(true);
		spaceBackground = new ImageIcon("spaceArt.png").getImage();

		gp.addKeyListener(gp);

	}

	/**
	 * Constructor for Static class.  
	 * Creates one Spaceship object, starts up the window and starts the timer.
	 */
	public SpaceshipWithCannon()
	{
		int radius = START_RADIUS;
		int x = START_X_AND_Y;
		int y = START_X_AND_Y;
		int dx = START_VELOCITY;
		int dy = START_VELOCITY;

		s = new Spaceship(x, y, dx , dy, radius, START_COLOR);			

		setSize(MAX_WIDTH, MAX_HEIGHT);
		setVisible(true);	

		Timer clock= new Timer(DELAY_IN_MILLISEC, this);			


		clock.start();								
	}

	/**
	 * Method called when a key is pressed. 
	 * Calls the methods
	 */
	public void keyPressed(KeyEvent e)			
	{
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_RIGHT)
		{
			s.moveCannonRight();
		}
		else if(keyCode == KeyEvent.VK_LEFT)
		{
			s.moveCannonLeft();
		}
		else if(keyCode == KeyEvent.VK_UP)
		{
			s.forwardThrust();
		}
		else if(keyCode == KeyEvent.VK_DOWN)
		{
			s.reverseThrust();
		}
		else if(keyCode == KeyEvent.VK_S)
		{
			if(bulletNumber >= bullets.length)
			{
				bulletNumber = 0;
			}
			bullets[bulletNumber] = s.makeBullets();
			bulletNumber++;
		}
		repaint();
	}

	/**
	 * Called when typing of a key is completed
	 * Required for any KeyListener
	 * 
	 * @param e		Contains info about the key typed
	 */
	public void keyTyped(KeyEvent e)
	{
	}

	/**
	 * Called when a key is released
	 * Required for any KeyListener
	 * 
	 * @param e		Contains info about the key released
	 */
	public void keyReleased(KeyEvent e)	
	{
	}


	/**
	 * actionPerformed() is called automatically by the timer every time the requested 
	 * delay has elapsed.  It will keep being called until the clock is stopped or the
	 * program ends.  All actions that we want to happen then should be performed here.
	 * Any class that implements ActionListener MUST have this method.
	 * 
	 * In this example it is called to move the ball every DELAY_IN_MILLISEC.
	 * 
	 * @param e		Contains info about the event that caused this method to be called
	 */
	public void actionPerformed(ActionEvent e)		
	{
		// Move the balls.
		s.move();
		s.wrap(MIN_WIDTH, MAX_WIDTH, MIN_HEIGHT, MAX_HEIGHT);

		for(int i = 0; i < bulletNumber; i++)
		{
			bullets[i].moveBullets();
		}

		// Update the window.
		repaint();
	}

	/**
	 * paint 		draw the window
	 * 
	 * @param g		Graphics object to draw on
	 */
	public void paint(Graphics g)
	{
		// Clear the window.
		g.drawImage(spaceBackground, 0, 0, 800, 600, this);

		// Tell the balls to draw themselves.
		s.draw(g);
		for(int i = 0; i < bulletNumber; i++)
		{
			bullets[i].drawBullets(g);
		}
	}
}

/*********************************************************************************
 * Spaceship class
 * Stores all of the information about a single spaceship including:
 * 		center, velocity, radius and color
 * It provides methods to move the spaceship, draw the spaceship and control the cannon 
 * of the spaceship
 ***********************************************************************************/
class Spaceship
{
	// DATA:
	private double x, y;						// Center of the spaceship
	private double dx, dy;						// Velocity - how much to change x and y by when the the spaceship moves
	private static double radius;				// Radius of the ball
	private static Color color;					// Color of the ball
	private double cannonRadius = 10;		//Radius of the cannon
	private double cannonAngle = 30;		//Angle of the cannon
	private static final int THRUST_AMOUNT = 3;	//Amount the spaceship thrusts
	private static final int BULLET_VELOCITY = 4;
	private static final double CANNON_STEP_SIZE = 0.5;


	// METHODS:

	/**
	 * Spaceship constructor initializes the Spaceship object
	 * 
	 * @param xIn		x coordinate of center
	 * @param yIn		y coordinate of center
	 * @param dxIn		x velocity
	 * @param dyIn		y velocity
	 * @param radiusIn	radius
	 * @param colorIn	color
	 */
	public Spaceship(int xIn, int yIn, int dxIn, int dyIn, int radiusIn, Color colorIn)
	{
		// Nothing to do but save the data in the object's data fields.
		x = xIn;
		y = yIn;
		dx = dxIn;
		dy = dyIn;
		radius = radiusIn;
		color = colorIn;
	}

	/**
	 * Methods that adds velocity to the spaceship
	 */
	public void move()
	{
		x = x + dx;
		y = y + dy;
	}
	
	/**
	 * Method that makes the spaceship wrap around the screen when hitting the side
	 * @param xLow	The lowest that the x can be
	 * @param xHigh	The highest that the x can be
	 * @param yLow	The lowest that the y can be
	 * @param yHigh The highest that the y can be
	 */
	public void wrap(int xLow, int xHigh, int yLow, int yHigh)
	{
		if(x >= xHigh)
		{
			x = x - xHigh;
		}
		else if(x <= xLow)
		{
			x = x + xHigh;
		}
		else if(y >= yHigh)
		{
			y = y - yHigh;
		}
		else if(y <= yLow)
		{
			y = y + yHigh;
		}
	}

	/**
	 * Makes the cannon move towards the right
	 */
	public void moveCannonRight()
	{
		cannonAngle = cannonAngle + CANNON_STEP_SIZE;
	}

	/**
	 * Makes the cannon move towards the left
	 */
	public void moveCannonLeft()
	{
		cannonAngle = cannonAngle - CANNON_STEP_SIZE;
	}

	/**
	 * Makes the spaceship thrust forwards in the direction of the cannon
	 */
	public void forwardThrust()
	{
		dx = dx + (THRUST_AMOUNT * Math.cos(cannonAngle));
		dy = dy + (THRUST_AMOUNT * Math.sin(cannonAngle));
	}

	/**
	 * Makes the spaceship thrust backwards in the direction of the cannon
	 */
	public void reverseThrust()
	{
		dx = dx - (THRUST_AMOUNT * Math.cos(cannonAngle));
		dy = dy - (THRUST_AMOUNT * Math.sin(cannonAngle));
	}

	/**
	 * Draw the spaceship and the cannon
	 * @param g	Graphics parameter
	 */
	public void draw(Graphics g)
	{
		g.setColor(color);
		g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius),(int)(2 * radius));
		double xCannon = x + (radius + cannonRadius) * (Math.cos(cannonAngle));
		double yCannon = y + (radius + cannonRadius) * (Math.sin(cannonAngle));
		g.fillOval((int)(xCannon - cannonRadius),(int)(yCannon - cannonRadius), (int)(cannonRadius * 2),(int)(cannonRadius * 2));
	}

	/**
	 * Creates a bullet everytime s is selected
	 * @return
	 */
	public Bullet makeBullets()
	{
		double bulletX = x + (radius + cannonRadius) * (Math.cos(cannonAngle));
		double bulletY = y + (radius + cannonRadius) * (Math.sin(cannonAngle));
		double bulletDx = dx + Math.cos(cannonAngle) * BULLET_VELOCITY;
		double bulletDy = dy + Math.sin(cannonAngle) * BULLET_VELOCITY;
		double radius = cannonRadius/2;
		Color color = Color.red;
		Bullet bullet = new Bullet(bulletX, bulletY, bulletDx, bulletDy, radius, color);


		return bullet;
	}
}
/*********************************************************************************
 * Bullet class
 * Creates the bullet, moves the bullet around the screen, and draws the bullet
 * Stores information like x and y of the bullet, dx and dy of the bullet, bullet radius
 * and nullet color
 ***********************************************************************************/
class Bullet
{
	private double x, y;
	private double dx,dy;
	private double bulletRadius;
	private static Color color;

	/**
	 * Bullet constructor that initializes all the variables of the bullet
	 * @param xIn		//The x location of the center of the bullet
	 * @param yIn		//The y location of the center of the bullet
	 * @param dxIn		//The x velocity of the bullet
	 * @param dyIn		//The y velocity of the bullet
	 * @param bulletRadiusIn	//The radius of the bullet
	 * @param colorIn		//The color of the bullet
	 */
	public Bullet(double xIn,double yIn, double  dxIn, double  dyIn,double  bulletRadiusIn, Color colorIn)
	{
		x = xIn;
		y = yIn;
		dx = dxIn;
		dy = dyIn;
		bulletRadius = bulletRadiusIn;
		color = colorIn;
	}

	/**
	 * Method that adds a velocity to the bullets
	 */
	public void moveBullets()
	{
		x = x + dx;
		y = y + dy;
	}

	/**
	 * Method that draws the bullets
	 * @param g		Graphics variable
	 */
	public void drawBullets(Graphics g)
	{
		g.setColor(color);
		g.fillOval((int)(x - bulletRadius), (int)(y - bulletRadius), (int)(2 * bulletRadius),(int)(2 * bulletRadius));
	}
}