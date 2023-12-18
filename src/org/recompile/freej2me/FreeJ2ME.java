/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package org.recompile.freej2me;

/*
	FreeJ2ME - AWT
*/

import org.recompile.mobile.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Display;
import javax.sound.sampled.AudioSystem;
import java.util.HashMap;
import java.util.Map;

public class FreeJ2ME
{
	public static void main(String args[])
	{
		for (String arg: args) {
			if (arg.equals("-help") || arg.equals("--help")) {
				printHelp();
				return;
			}
		}

		FreeJ2ME app = new FreeJ2ME(args);
	}

	private static void printHelp() {
		System.out.println("freej2me.jar [-w width] [-h height] [-scale x] [-ap | --app-property key=value]... [-sp | --system-property key=value]... [-c | --config key=value]... [-m main_class] [-ff | --force-fullscreen] [-fv | --force-volatile] JAR_OR_JAD_FILE");
	}

	private Frame main;
	private int lcdWidth;
	private int lcdHeight;
	private int scaleFactor = 1;

	private LCD lcd;

	private int xborder;
	private int yborder;

	private PlatformImage img;

	private Config config;
	private boolean useNokiaControls = false;
	private boolean useSiemensControls = false;
	private boolean useMotorolaControls = false;
	private boolean rotateDisplay = false;
	private int limitFPS = 0;
	
	private boolean[] pressedKeys = new boolean[128];

	public FreeJ2ME(String args[])
	{
		lcdWidth = 240;
		lcdHeight = 320;
		scaleFactor = Toolkit.getDefaultToolkit().getScreenSize().height > 980 ? 3 : 2;
		Map<String, String> appProperties = new HashMap<>();
		Map<String, String> systemPropertyOverrides = new HashMap<>();
		Map<String, String> configOverrides = new HashMap<>();
		String fileName = null;
		String mainClassOverride = null;
		boolean dimensionsSet = false;
		boolean forceFullscreen = false;
		boolean forceVolatile = false;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-w":
				case "--width":
					lcdWidth = Integer.parseInt(args[++i]);
					dimensionsSet = true;
					break;
				case "-h":
				case "--height":
					lcdHeight = Integer.parseInt(args[++i]);
					dimensionsSet = true;
					break;
				case "-s":
				case "--scale":
					scaleFactor = Integer.parseInt(args[++i]);
					break;
				case "-m":
				case "--main-class":
					mainClassOverride = args[++i];
					break;
				case "-ff":
				case "--force-fullscreen":
					forceFullscreen = true;
					break;
				case "-fv":
				case "--force-volatile":
					forceVolatile = true;
					break;
				case "-ap":
				case "--app-property":
					processKeyValuePairs(args, appProperties, ++i);
					break;
				case "-sp":
				case "--system-property":
					processKeyValuePairs(args, systemPropertyOverrides, ++i);
					break;
				case "-c":
				case "--config":
					processKeyValuePairs(args, configOverrides, ++i);
					break;
				default:
					fileName = args[i];
			}
		}

		if (dimensionsSet) {
			configOverrides.put("width", ""+lcdWidth);
			configOverrides.put("height", ""+lcdHeight);		
		}

		if (forceFullscreen) {
			configOverrides.put("forceFullscreen", "on");
		}
		
		if (forceVolatile) {
			configOverrides.put("forceVolatileFields", "on");
		}

		main = new Frame("FreeJ2ME");
		main.setSize(350,450);
		main.setBackground(new Color(0,0,64));
		try
		{
			main.setIconImage(ImageIO.read(main.getClass().getResourceAsStream("/org/recompile/icon.png")));	
		}
		catch (Exception e) { }

		main.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		// Setup Device //

		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));

		lcd = new LCD();
		lcd.setFocusable(true);
		main.add(lcd);

		config = new Config();
		config.onChange = new Runnable() { public void run() { settingsChanged(); } };

		Mobile.getPlatform().setPainter(new Runnable()
		{
			public void run()
			{
				lcd.paint(lcd.getGraphics());
			}
		});

		Mobile.setDisplay(new Display());

		Mobile.getPlatform().startEventQueue();		

		Mobile.getPlatform().setSystemPropertyOverrides(systemPropertyOverrides);

		lcd.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				int keycode = e.getKeyCode();
				int mobikey = getMobileKey(keycode);

				if (config.isRunning) {
					config.keyPressed(mobikey, keycode);
					return;
				}

				int mobikeyN = (mobikey + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
				
				switch(keycode) // Handle emulator control keys
				{
					// Config //
					case KeyEvent.VK_ESCAPE:
						Mobile.getPlatform().dropQueuedEvents();
						config.start();
						return;

					case KeyEvent.VK_PLUS:
					case KeyEvent.VK_ADD:
						scaleFactor++;
						main.setSize(lcdWidth * scaleFactor + xborder, lcdHeight * scaleFactor + yborder);
					break;
					case KeyEvent.VK_MINUS:
					case KeyEvent.VK_SUBTRACT:
						if( scaleFactor > 1 )
						{
							scaleFactor--;
							main.setSize(lcdWidth * scaleFactor + xborder, lcdHeight * scaleFactor + yborder);
						}
					break;
					case KeyEvent.VK_C:
						if(e.isControlDown())
						{
							ScreenShot.takeScreenshot(false);
						}
					break;
				}
								

				
				if (pressedKeys[mobikeyN] == false)
				{
					//~ System.out.println("keyPressed:  " + Integer.toString(mobikey));
					Mobile.getPlatform().keyPressed(mobikey, e);
				}
				else
				{
					//~ System.out.println("keyRepeated:  " + Integer.toString(mobikey));
					Mobile.getPlatform().keyRepeated(mobikey, e);
				}

				if (mobikey != 0) {
					pressedKeys[mobikeyN] = true;
				}
				
			}

			public void keyReleased(KeyEvent e)
			{
				int keycode = e.getKeyCode();
				int mobikey = getMobileKey(keycode);

				if (config.isRunning) {
					config.keyReleased(mobikey, keycode);
					return;
				}
				
				int mobikeyN = (mobikey + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
				
				if (mobikey != 0) {
					pressedKeys[mobikeyN] = false;
				}
								
				//~ System.out.println("keyReleased: " + Integer.toString(mobikey));
				Mobile.getPlatform().keyReleased(mobikey, e);
			}

			public void keyTyped(KeyEvent e) { }

		});

		lcd.addMouseListener(new MouseListener()
		{
			public void mousePressed(MouseEvent e)
			{
				int x = (int)((e.getX()-lcd.cx) * lcd.scalex);
				int y = (int)((e.getY()-lcd.cy) * lcd.scaley);

				// Adjust the pointer coords if the screen is rotated, same for mouseReleased
				if(rotateDisplay)
				{
					x = (int)((lcd.ch-(e.getY()-lcd.cy)) * lcd.scaley);
					y = (int)((e.getX()-lcd.cx) * lcd.scalex);
				}

				Mobile.getPlatform().pointerPressed(x, y);				
			}

			public void mouseReleased(MouseEvent e)
			{
				int x = (int)((e.getX()-lcd.cx) * lcd.scalex);
				int y = (int)((e.getY()-lcd.cy) * lcd.scaley);

				if(rotateDisplay)
				{
					x = (int)((lcd.ch-(e.getY()-lcd.cy)) * lcd.scaley);
					y = (int)((e.getX()-lcd.cx) * lcd.scalex);
				}

				Mobile.getPlatform().pointerReleased(x, y);				
			}

			public void mouseExited(MouseEvent e) { }
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }

		});

		lcd.addMouseMotionListener(new MouseMotionAdapter() 
		{
			public void mouseDragged(MouseEvent e)
			{
				int x = (int)((e.getX()-lcd.cx) * lcd.scalex);
				int y = (int)((e.getY()-lcd.cy) * lcd.scaley);

				if(rotateDisplay)
				{
					x = (int)((lcd.ch-(e.getY()-lcd.cy)) * lcd.scaley);
					y = (int)((e.getX()-lcd.cx) * lcd.scalex);
				}
				
				Mobile.getPlatform().pointerDragged(x, y);				
			}
		});

		main.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				resize();
			}
		});

		main.setVisible(true);
		main.pack();

		resize();
		main.setSize(lcdWidth*scaleFactor+xborder, lcdHeight*scaleFactor+yborder);

		if (fileName == null)
		{
			FileDialog t = new FileDialog(main, "Open JAR/JAD File", FileDialog.LOAD);
			t.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					//System.out.println(name);
					return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".jad");
				}
			});
			t.setVisible(true);
			fileName = t.getDirectory()+File.separator+t.getFile();
		}
		if(Mobile.getPlatform().load(fileName, appProperties, mainClassOverride))
		{
			config.init(configOverrides);

			settingsChanged();

			Mobile.getPlatform().runJar();
		}
		else
		{
			System.out.println("Couldn't load jar...");
		}
	}

	private static void processKeyValuePairs(String[] args, Map<String, String> map, int index) {
		if (index < args.length) {
			String[] pair = args[index].split("=");
			if (pair.length == 2) {
				map.put(pair[0], pair[1]);
			}
		}
	}

	private void settingsChanged()
	{
		int w = Integer.parseInt(config.settings.get("width"));
		int h = Integer.parseInt(config.settings.get("height"));

		limitFPS = Integer.parseInt(config.settings.get("fps"));
		if(limitFPS>0) { limitFPS = 1000 / limitFPS; }

		String sound = config.settings.get("sound");
		Mobile.sound = false;
		if(sound.equals("on")) { 
			if (AudioSystem.getMixerInfo().length > 0) {
				Mobile.sound = true;
			} else {
				System.out.println("no audio devices found");
			}

		}

		String phone = config.settings.get("phone");
		useNokiaControls = false;
		useSiemensControls = false;
		useMotorolaControls = false;
		Mobile.sonyEricsson = false;
		Mobile.nokia = false;
		Mobile.siemens = false;
		Mobile.motorola = false;
		if(phone.equals("Nokia")) { Mobile.nokia = true; useNokiaControls = true; }
		if(phone.equals("Siemens")) { Mobile.siemens = true; useSiemensControls = true; }
		if(phone.equals("Motorola")) { Mobile.motorola = true; useMotorolaControls = true; }
		if(phone.equals("SonyEricsson")) { Mobile.sonyEricsson = true; useNokiaControls = true; }

		String rotate = config.settings.get("rotate");
		if(rotate.equals("on")) { rotateDisplay = true; }
		if(rotate.equals("off")) { rotateDisplay = false; }

		boolean isForceFullscreen = config.settings.getOrDefault("forceFullscreen", "off").equals("on");
		boolean isForceVolatile = config.settings.getOrDefault("forceVolatileFields", "off").equals("on");
		String dgFormat = config.settings.getOrDefault("dgFormat", "default");

		System.setProperty("freej2me.forceFullscreen", isForceFullscreen ? "true" : "false");
		System.setProperty("freej2me.forceVolatileFields", isForceVolatile ? "true" : "false");

		if (dgFormat.equals("default")) {
			System.clearProperty("freej2me.dgFormat");
		} else {
			System.setProperty("freej2me.dgFormat", dgFormat);
		}

		// Create a standard size LCD if not rotated, else invert window's width and height.
		if(!rotateDisplay) 
		{
			lcdWidth = w;
			lcdHeight = h;

			Mobile.getPlatform().resizeLCD(w, h);
			
			resize();
			main.setSize(lcdWidth*scaleFactor+xborder , lcdHeight*scaleFactor+yborder);
		}
		else 
		{
			lcdWidth = h;
			lcdHeight = w;

			Mobile.getPlatform().resizeLCD(w, h);

			resize();
			main.setSize(lcdWidth*scaleFactor+xborder , lcdHeight*scaleFactor+yborder);
		}

		if (Mobile.sonyEricsson) {
			Mobile.getPlatform().addSystemProperty("microedition.platform", "SonyEricssonK750/JAVASDK");
		}
	}

	private int getMobileKey(int keycode)
	{
		if(useNokiaControls)
		{
			switch(keycode)
			{
				case KeyEvent.VK_UP: return Mobile.NOKIA_UP;
				case KeyEvent.VK_DOWN: return Mobile.NOKIA_DOWN;
				case KeyEvent.VK_LEFT: return Mobile.NOKIA_LEFT;
				case KeyEvent.VK_RIGHT: return Mobile.NOKIA_RIGHT;
				case KeyEvent.VK_ENTER: return Mobile.NOKIA_SOFT3;
			}
		}

		if(useSiemensControls)
		{
			switch(keycode)
			{
				case KeyEvent.VK_UP: return Mobile.SIEMENS_UP;
				case KeyEvent.VK_DOWN: return Mobile.SIEMENS_DOWN;
				case KeyEvent.VK_LEFT: return Mobile.SIEMENS_LEFT;
				case KeyEvent.VK_RIGHT: return Mobile.SIEMENS_RIGHT;
				case KeyEvent.VK_F1:
				case KeyEvent.VK_Q: 
					return Mobile.SIEMENS_SOFT1;
				case KeyEvent.VK_F2:
				case KeyEvent.VK_W:
					return Mobile.SIEMENS_SOFT2;
				case KeyEvent.VK_ENTER: return Mobile.SIEMENS_FIRE;
			}
		}

		if(useMotorolaControls)
		{
			switch(keycode)
			{
				case KeyEvent.VK_UP: return Mobile.MOTOROLA_UP;
				case KeyEvent.VK_DOWN: return Mobile.MOTOROLA_DOWN;
				case KeyEvent.VK_LEFT: return Mobile.MOTOROLA_LEFT;
				case KeyEvent.VK_RIGHT: return Mobile.MOTOROLA_RIGHT;
				case KeyEvent.VK_F1:
				case KeyEvent.VK_Q:
					return Mobile.MOTOROLA_SOFT1;
				case KeyEvent.VK_F2:
				case KeyEvent.VK_W:
					return Mobile.MOTOROLA_SOFT2;
				case KeyEvent.VK_ENTER: return Mobile.MOTOROLA_FIRE;
			}
		}

		switch(keycode)
		{
			case KeyEvent.VK_0: return Mobile.KEY_NUM0;
			case KeyEvent.VK_1: return Mobile.KEY_NUM1;
			case KeyEvent.VK_2: return Mobile.KEY_NUM2;
			case KeyEvent.VK_3: return Mobile.KEY_NUM3;
			case KeyEvent.VK_4: return Mobile.KEY_NUM4;
			case KeyEvent.VK_5: return Mobile.KEY_NUM5;
			case KeyEvent.VK_6: return Mobile.KEY_NUM6;
			case KeyEvent.VK_7: return Mobile.KEY_NUM7;
			case KeyEvent.VK_8: return Mobile.KEY_NUM8;
			case KeyEvent.VK_9: return Mobile.KEY_NUM9;
			case KeyEvent.VK_ASTERISK: return Mobile.KEY_STAR;
			case KeyEvent.VK_NUMBER_SIGN: return Mobile.KEY_POUND;

			case KeyEvent.VK_NUMPAD0: return Mobile.KEY_NUM0;
			case KeyEvent.VK_NUMPAD7: return Mobile.KEY_NUM1;
			case KeyEvent.VK_NUMPAD8: return Mobile.KEY_NUM2;
			case KeyEvent.VK_NUMPAD9: return Mobile.KEY_NUM3;
			case KeyEvent.VK_NUMPAD4: return Mobile.KEY_NUM4;
			case KeyEvent.VK_NUMPAD5: return Mobile.KEY_NUM5;
			case KeyEvent.VK_NUMPAD6: return Mobile.KEY_NUM6;
			case KeyEvent.VK_NUMPAD1: return Mobile.KEY_NUM7;
			case KeyEvent.VK_NUMPAD2: return Mobile.KEY_NUM8;
			case KeyEvent.VK_NUMPAD3: return Mobile.KEY_NUM9;

			case KeyEvent.VK_UP: return Mobile.KEY_NUM2;
			case KeyEvent.VK_DOWN: return Mobile.KEY_NUM8;
			case KeyEvent.VK_LEFT: return Mobile.KEY_NUM4;
			case KeyEvent.VK_RIGHT: return Mobile.KEY_NUM6;

			case KeyEvent.VK_ENTER: return Mobile.KEY_NUM5;

			case KeyEvent.VK_F1:
			case KeyEvent.VK_Q:
				return Mobile.NOKIA_SOFT1;
			case KeyEvent.VK_F2:
			case KeyEvent.VK_W:
				return Mobile.NOKIA_SOFT2;
			case KeyEvent.VK_E: return Mobile.KEY_STAR;
			case KeyEvent.VK_R: return Mobile.KEY_POUND;

			case KeyEvent.VK_A: return -1;
			case KeyEvent.VK_Z: return -2;

			case KeyEvent.VK_SPACE: return Mobile.XKEY_SELECT;
			case KeyEvent.VK_F: return Mobile.XKEY_SOFT1;
			case KeyEvent.VK_G: return Mobile.XKEY_SOFT1;
			case KeyEvent.VK_H: return Mobile.XKEY_SOFT1;

		}
		return 0;
	}

	private void resize()
	{
		xborder = main.getInsets().left+main.getInsets().right;
		yborder = main.getInsets().top+main.getInsets().bottom;

		double vw = (main.getWidth()-xborder)*1;
		double vh = (main.getHeight()-yborder)*1;

		double nw = lcdWidth;
		double nh = lcdHeight;

		nw = vw;
		nh = nw*((double)lcdHeight/(double)lcdWidth);

		if(nh>vh)
		{
			nh = vh;
			nw = nh*((double)lcdWidth/(double)lcdHeight);
		}

		lcd.updateScale((int)nw, (int)nh);
	}

	private class LCD extends Canvas
	{
		public int cx=0;
		public int cy=0;
		public int cw=240;
		public int ch=320;

		public double scalex=1;
		public double scaley=1;

		public void updateScale(int vw, int vh)
		{
			cx = (this.getWidth()-vw)/2;
			cy = (this.getHeight()-vh)/2;
			cw = vw;
			ch = vh;
			scalex = (double)lcdWidth/(double)vw;
			scaley = (double)lcdHeight/(double)vh;
		}

		public void paint(Graphics g)
		{
			try
			{
				Graphics2D cgc = (Graphics2D)this.getGraphics();
				if (config.isRunning)
				{
					if(!rotateDisplay)
					{
						g.drawImage(config.getLCD(), cx, cy, cw, ch, null);
					}
					else
					{
						// If rotated, simply redraw the config menu with different width and height
						g.drawImage(config.getLCD(), cy, cx, cw, ch, null);
					}
				}
				else
				{
					if(!rotateDisplay)
					{
						g.drawImage(Mobile.getPlatform().getLCD(), cx, cy, cw, ch, null);
					}
					else
					{
						// Rotate the FB 90 degrees counterclockwise with an adjusted pivot
						cgc.rotate(Math.toRadians(-90), ch/2, ch/2);
						// Draw the rotated FB with adjusted cy and cx values
						cgc.drawImage(Mobile.getPlatform().getLCD(), 0, cx, ch, cw, null);
					}

					if(limitFPS>0)
					{
						// this is of course a simplification
						Thread.sleep(limitFPS);						
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}
