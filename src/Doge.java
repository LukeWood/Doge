package goat;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JWindow;

import javafx.util.Pair;

public class Doge extends JComponent implements Runnable
{
	/**
	 * DOGE is much cool
	 */
	private static final long serialVersionUID = -8298751448893596967L;

	/*
	 * program wide constants.  Load static variables
	 */
	//Loads despite varying graphics environments/screen setups
	public static int width;
	public static int height;
	
	//We only need one instance of random
	private static final Random r = new Random();
	public enum Action
	{
		STANDING,WALKING,JUMP,DRAG, THINK, MEME, SIT
	}
	
	//Loads in all images
	private static Image[] walking = null;
	private static Image[] meme = null;
	private static Image[] sit = null;
	private static Image[] drag = null;
	private static Image[] jump = null;
	
	private static ArrayList<Pair<Double,Double>> memeSizes;
	
	static
	{
		//Graphics environment fails on a few machines
		//Toolkit works, but it can fail on multiple monitor setups.  This is only as a backup plan.
		try
		{
			GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = g.getScreenDevices();
			width = devices[0].getDisplayMode().getWidth();
			height = devices[0].getDisplayMode().getHeight();
		}
		catch(Exception e)
		{
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			width = dim.width;
			height = dim.height;
		}

		BufferedImage sheet = null;
		try {
			sheet = ImageIO.read(Doge.class.getResource("/doge.png"));
		} catch (IOException e) 
		{
			System.exit(0);
		}
		int dx = 38;
		int dy = 32;
		walking = new Image[2];
		for(int i = 0; i < 2; i++)
		{
			walking[i] = sheet.getSubimage(dx*i, dy*1, dx, dy);
		}
		jump = new Image[2];
		for(int i = 0; i < 2; i++)
		{
			jump[i] = sheet.getSubimage(dx*i, dy*2, dx, dy);
		}
		sit = new Image[5];
		for(int i = 0; i < sit.length; i++)
		{
			sit[i] = sheet.getSubimage(dx*i, 0, dx, dy);
		}
		drag = new Image[1];
		drag[0] = sheet.getSubimage(0, dy*4, dx, dy);
		
		meme = new Image[4];
		memeSizes = new ArrayList<Pair<Double,Double>>();
		int tdy = dy+12;
		int tdx = dx;
		meme[0] = sheet.getSubimage(sheet.getWidth()-tdx, sheet.getHeight()-tdy, tdx, tdy);
		memeSizes.add(new Pair<Double,Double>((double)tdx/(double)dx,(double)tdy/(double)dy));
	}
	/*
	 * 	Instance specific variables
	 */
	private final int DOGEWIDTH = 200, DOGEHEIGHT = 200;

	private int x = 0;
	private int y = 0;
	private int direction = 1;
	private int ydirection = 0;
	private int speed = 1;
	private int frameTickDelay = 0;
	private int duration =  0;
	private Image[] frames = null;
	
	private JWindow parent;
	private int frame = 0;
	private Action action = Action.WALKING;
		
	/*
	 *   End instance specific
	*/
	
	/*
	 * Method Specific Variables
	 */
	
	//For dragging the doge
	int startX;
	int startY;
	
	
	public Doge(JWindow frame)
	{
		super();

		frames = walking;
		this.parent = frame;
		this.setSize(DOGEWIDTH,DOGEHEIGHT);
		setLocation(r.nextInt(width),r.nextInt(height));
		new Thread(this).start();
	}

	public void setAction(Action action2)
	{
		frame = 0;
		frameTickDelay = 100;
		if(action2 == Action.WALKING)
			this.frames = walking;
		else if(action2 == Action.SIT)
		{
			this.frames = sit;
			duration = r.nextInt()%25;
		}
		else if(action2 == Action.JUMP){
			this.frames = jump;
			duration = r.nextInt()%3;
		}
		else if(action2 == Action.DRAG)
		{
			this.frames = drag;
			nextFrame();
			startX = MouseInfo.getPointerInfo().getLocation().x - this.x;
			startY = MouseInfo.getPointerInfo().getLocation().y - this.y;
		}
		if(action2 == Action.MEME)
		{
			this.frames = meme;
			this.setSize((int)(memeSizes.get(frame).getKey()*DOGEWIDTH),(int)(memeSizes.get(frame).getValue()*DOGEHEIGHT));
			this.parent.repaint();
		}
		action = action2;
	}
	
	private void walk()
	{
		x+=direction*speed;
		y+=ydirection*speed;
		if(frameTickDelay++ >= 3)
		{
			nextFrame();
		}
		if((y > height - DOGEHEIGHT && direction ==1) || (y < DOGEHEIGHT+10 && direction ==-1)||r.nextDouble()>.997)
			switchDirectionY();
		if((x > width - (DOGEWIDTH+10) && direction ==1) || (x < DOGEWIDTH+10 && direction == -1)||r.nextDouble() > .997)
			switchDirection();		
		
		double choice = r.nextDouble();
		if(choice>.98)
		{
			setAction(Action.SIT);
		}
		else if(choice>.96)
		{
			setAction(Action.JUMP);
		}
		//else if (choice > .92)
		//{
		//	setAction(Action.MEME);
		//}
	}
	private void nextFrame()
	{
		frame++;
		if(frame >= frames.length)
				frame = 0;
		parent.repaint();
		frameTickDelay=0;
	}
	private void switchDirectionY()
	{
		if(ydirection ==0)
			ydirection = 1;
		else if(r.nextBoolean())
			ydirection = 0;
		ydirection = -ydirection;
		parent.repaint();
	}

	private void sit()
	{
		if(frameTickDelay++ > 6)
		{
			nextFrame();
			duration--;
			if(duration <= 0)
			{
				setAction(Action.WALKING);
			}
		}
	}
	
	private void jump()
	{
		x+=direction*speed;
		ydirection = 0;
		if(frame ==1 && frameTickDelay < 12)
			y-=2;
		else if(frame ==1)
			y+=2;
		if(frameTickDelay++ > (frame*20) +4)
		{
			if(frame ==1)
				duration--;
			nextFrame();
		}
		if(duration <=0)
			setAction(Action.WALKING);
	}
	
	private void drag()
	{

		Point p = MouseInfo.getPointerInfo().getLocation();
		x=p.x - startX;
		y=p.y - startY;
	}
	
	private void meme()
	{

	}
	private void dogeAction()
	{
		if(action == Action.WALKING)
			walk();
		if(action == Action.SIT)
			sit();
		if(action == Action.JUMP)
			jump();
		if(action == Action.DRAG)
			drag();
		if(action == Action.MEME);
			meme();
	}

	private void switchDirection()
	{
		direction = -direction;
		parent.repaint();
	}
	@Override 
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(direction > 0)
			g.drawImage(frames[frame], 0, 0,getWidth()*direction,getHeight(), null);
		else
			g.drawImage(frames[frame], getWidth(), 0,getWidth()*direction,getHeight(), null);
	}

	@Override public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	@Override
	public void run() 
	{
		while(true)
		{
			dogeAction();
			parent.setLocation(x, y);


			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
