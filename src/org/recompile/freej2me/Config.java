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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;

class Item {
	public String id;
	public String label;

	public Item(String idAndLabel) {
		this(idAndLabel, idAndLabel);
	}

	public Item(String id, String label) {
		this.id = id;
		this.label = label;
	}
}

public class Config
{
	public boolean isRunning = false;

	private PlatformImage lcd;
	private Graphics gc;
	private int width;
	private int height;

	private Map<String, Item[]> menuMap;
	private String currentMenu = "main";
	private int currentItem = 0;

	private File file;
	private String configPath = "";
	private String configFile = "";

	public Runnable onChange;

	HashMap<String, String> settings = new HashMap<String, String>(4);

	public Config()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;

		menuMap = new HashMap<String, Item[]>() {{
			put("main", new Item[] {
				new Item("resume", "Resume Game"),
				new Item("size", "Display Size"),
				new Item("sound", "Sound"),
				new Item("fps", "Limit FPS"),
				new Item("phone", "Phone"),
				new Item("compat", "Compatibility"),
				new Item("rotate", "Rotate"),
				new Item("exit", "Exit")
			});
			
			put("size", new Item[] {
				new Item("96x65"), new Item("96x96"), new Item("104x80"), new Item("128x128"), new Item("132x176"), new Item("128x160"), new Item("176x208"), new Item("176x220"), new Item("208x208"), new Item("240x320"), new Item("320x240"), new Item("240x400"), new Item("352x416"), new Item("360x640"), new Item("640x360"), new Item("480x800"), new Item("800x480")
			});

			put("restart", new Item[]{new Item("quit", "Quit"), new Item("main", "Main Menu")});
			put("rotate", new Item[]{new Item("On"), new Item("Off")});

			put("phone", new Item[]{new Item("Standard"), new Item("Nokia"), new Item("Siemens"), new Item("Motorola"), new Item("SonyEricsson")});

			put("compat", new Item[]{
				new Item("forceFullscreen", "Force fullscreen canvas"),
				new Item("forceVolatileFields", "Force volatile fields"),
				new Item("dgFormat", "DG native format")}
			);

			put("compat/dgFormat", new Item[]{
				new Item("default", "Default"),
				new Item("444", "444 RGB"),
				new Item("4444", "4444 ARGB"),
				new Item("565", "565 RGB")}
			);

			put("fps", new Item[]{new Item("auto", "Auto"), new Item("60", "60 - Fast"), new Item("30", "30 - Slow"), new Item("15", "15 - Turtle")});
			
		}};
  


		onChange = new Runnable()
		{
			public void run()
			{
				// placeholder
			}
		};
	}

	public void init(Map<String, String> overrides)
	{
		String appname = Mobile.getPlatform().loader.suitename;
		configPath = Mobile.getPlatform().dataPath + "./config/"+appname;
		configFile = configPath + "/game.conf";
		// Load Config //
		try
		{
			Files.createDirectories(Paths.get(configPath));
		}
		catch (Exception e)
		{
			System.out.println("Problem Creating Config Path "+configPath);
			System.out.println(e.getMessage());
		}

		// Check Config File
		try
		{
			file = new File(configFile);
			if(!file.exists())
			{
				file.createNewFile();
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem Opening Config "+configFile);
			System.out.println(e.getMessage());
		}

		try // Read Records
		{
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				String[] parts;
				while((line = reader.readLine())!=null)
				{
					parts = line.split(":");
					if(parts.length==2)
					{
						parts[0] = parts[0].trim();
						parts[1] = parts[1].trim();
						if(parts[0]!="" && parts[1]!="")
						{
							settings.put(parts[0], parts[1]);
						}
					}
				}
			}
			if(!settings.containsKey("width")) { settings.put("width", ""+width); }
			if(!settings.containsKey("height")) { settings.put("height", ""+height); }
			if(!settings.containsKey("sound")) { settings.put("sound", "on"); }
			if(!settings.containsKey("phone")) { settings.put("phone", "Nokia"); }
			if(!settings.containsKey("rotate")) { settings.put("rotate", "off"); }
			if(!settings.containsKey("fps")) { settings.put("fps", "0"); }

		}
		catch (Exception e)
		{
			System.out.println("Problem Reading Config: "+configFile);
			System.out.println(e.getMessage());
		}

		settings.putAll(overrides);
		saveConfig();

		doUpdateDisplay(Integer.parseInt(settings.get("width")), Integer.parseInt(settings.get("height")));
	}

	public void saveConfig()
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(file);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout));

			for (String key : settings.keySet())
			{
				writer.write(key+":"+settings.get(key)+"\n");
			}
			writer.close();
		}
		catch (Exception e)
		{
			System.out.println("Problem Opening Config "+configFile);
			System.out.println(e.getMessage());
		}
	}

	public void start()
	{
		isRunning = true;
		render();
		Mobile.getPlatform().painter.run();
	}

	public void stop()
	{
		isRunning = false;
		Mobile.getPlatform().painter.run();
	}

	public void keyPressed(int platKey, int keyCode)
	{
		int mobiKey = Mobile.normalizeKey(platKey);

		if (mobiKey == Mobile.NOKIA_UP) {
			currentItem--;
		} else if (mobiKey == Mobile.NOKIA_DOWN) {
			currentItem++;
		} else if (mobiKey == Mobile.NOKIA_SOFT1 || keyCode == KeyEvent.VK_ESCAPE) {
			if (currentMenu.equals("main")) {
				stop();
				return;
			}

			int slashIdx = currentMenu.lastIndexOf('/');
			String lastPart;

			if (slashIdx == -1) {
				lastPart = currentMenu;
				currentMenu = "main";
			} else {
				lastPart = currentMenu.substring(slashIdx + 1);
				currentMenu = currentMenu.substring(0, slashIdx);
			}

			currentItem = findItemIndex(menuMap.get(currentMenu), lastPart);
		} else if (mobiKey == Mobile.NOKIA_SOFT3) {
			doMenuAction();
		}

		currentItem = Math.max(0, Math.min(currentItem, menuMap.get(currentMenu).length-1));

		render();
	}

	public void keyReleased(int platKey, int keyCode) { }
	public void mousePressed(int key) { }
	public void mouseReleased(int key) { }

	public BufferedImage getLCD()
	{
		return lcd.getCanvas();
	}

	public void render()
	{
		if (!isRunning) {
			return;
		}
		/*
		 * technically we'd want title, list of labels and showBack
		 * 
		 */
		String title = "Game Options";
		String[] items = null;

		switch(currentMenu) {
			case "size": title = "Screen Size"; break;
			case "restart": title = "Restart Required"; break;
			case "rotate": title = "Rotate"; break;
			case "phone": title = "Phone type"; break;
			case "compat": title = "Compatibility flags"; break;
			case "compat/dgFormat": title = "DirectGraphics pixel format"; break;
			case "fps": title = "Max FPS"; break;
		}

		Item[] itemObjects = menuMap.get(currentMenu);
		items = new String[itemObjects.length];

		for (int t=0; t<items.length; t++) {
			String id = itemObjects[t].id;
			String label = itemObjects[t].label;

			switch (currentMenu) {
				case "main":
					switch (id) {
						case "sound": label += ": "+ settings.get(id); break;
						case "fps": label += ": "+ settings.get(id); break;
						case "phone": label += ": "+ settings.get(id); break;
						case "rotate": label += ": "+ settings.get(id); break;
					}
				break;

				case "compat":
					label += ": " + settings.getOrDefault(id, id.equals("dgFormat") ? "default" : "off");
				break;
			}

			items[t] = label;
		}

		gc.setColor(0x000080);
		gc.fillRect(0,0,width,height);
		gc.setColor(0xFFFFFF);
		gc.drawString(title, width/2, 2, Graphics.HCENTER);
		gc.drawLine(0, 20, width, 20);
		gc.drawLine(0, height-20, width, height-20);


		if (!currentMenu.equals("main")) {
			gc.setColor(0xFFFFFF);
			gc.drawString("Back", 3, height-17, Graphics.LEFT);
		}

		int ah = (int)((height-50)/(items.length+1));
		if(ah<15) { ah=15; }

		int space = 0;
		if(ah>15) { space = (ah-15) / 2; }

		int max = (int)Math.floor((height-50)/ah);
		int page = (int)Math.floor(currentItem/max);
		int start = (int)(max*page);
		int pages = (int)Math.ceil(items.length/max);

		if(pages>=1)
		{
			gc.setColor(0xFFFFFF);
			gc.drawString("Page "+(page+1)+" of "+(pages+1), width-3, height-17, Graphics.RIGHT);
		}

		for(int i=start; (i<(start+max))&(i<items.length); i++)
		{
			String label = items[i];

			if(i==currentItem)
			{
				gc.setColor(0xFFFF00);
				gc.drawString("> "+label+" <", width/2, (25+space)+(ah*(i-start)), Graphics.HCENTER);
			}
			else
			{
				gc.setColor(0xFFFFFF);
				gc.drawString(label, width/2, (25+space)+(ah*(i-start)), Graphics.HCENTER);
			}
		}

		Mobile.getPlatform().painter.run();
	}

	private void doMenuAction()
	{
		Item activeItem = menuMap.get(currentMenu)[currentItem];

		switch(currentMenu)
		{
			case "main":
				switch(activeItem.id)
				{
					case "resume": stop(); break;
					case "size": 
						currentMenu = "size"; 
						currentItem = findItemIndex(menuMap.get(currentMenu), width+"x"+height);
					break;
					case "sound": toggleSound(); break;
					case "fps":
						currentMenu = "fps";
						currentItem = findItemIndex(menuMap.get(currentMenu), settings.get("fps"));
					break;
					case "phone":
						currentMenu = "phone";
						currentItem = findItemIndex(menuMap.get(currentMenu), settings.get("phone"));
					break;
					case "compat": currentMenu = "compat"; currentItem = 0; break;
					case "rotate": currentMenu = "rotate"; currentItem = 0; break;
					case "exit": System.exit(0); break;
				}
			break;

			case "size":
				String[] t = activeItem.id.split("x");

				updateDisplaySize(Integer.parseInt(t[0]), Integer.parseInt(t[1]));

				currentMenu = "restart"; currentItem = 0;
			break;

			case "restart":
				switch (activeItem.id)
				{
					case "quit": System.exit(0); break;
					case "main": currentMenu = "main"; currentItem = 0;
				}
			break;

			case "phone":
				updatePhone(activeItem.id);
				currentMenu = "main"; currentItem = findItemIndex(menuMap.get("main"), "phone");
			break;

			case "compat":
				switch (activeItem.id) {
					case "dgFormat":
						currentMenu += "/" + activeItem.id;
						currentItem = findItemIndex(menuMap.get(currentMenu), settings.getOrDefault(activeItem.id, "default"));
					break;
					case "forceVolatileFields":
						currentMenu = "restart"; currentItem = 0;
					default:
						toggleCompatFlag(activeItem.id);
				}				
			break;

			case "compat/dgFormat":
				updateDGFormat(activeItem.id);
				currentMenu = "compat";
				currentItem = findItemIndex(menuMap.get(currentMenu), "dgFormat");			
			break;

			case "rotate":
				switch (activeItem.id)
				{
					case "On": updateRotate("on"); break;
					case "Off": updateRotate("off"); break;
				}
				
				currentMenu = "main"; currentItem = findItemIndex(menuMap.get("main"), "rotate");
			break;

			case "fps":
				updateFPS(activeItem.id.equals("auto") ? "0" : activeItem.id);
				currentMenu = "main"; currentItem = findItemIndex(menuMap.get("main"), "fps");
			break;

		}

		render();
	}

	private int findItemIndex(Item[] items, String id) {
		int index = 0;

		for (int t=0; t<items.length; t++) {
			if (items[t].id.equals(id)) {
				index = t;
				break;
			}
		}

		return index;
	}

	private final void doUpdateDisplay(int w, int h) {
		width = w;
		height = h;
		lcd = new PlatformImage(width, height);
		gc = lcd.getGraphics();
		gc.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE__INTERNAL_UI));
	}

	private void updateDisplaySize(int w, int h)
	{
		settings.put("width", ""+w);
		settings.put("height", ""+h);
		saveConfig();
		onChange.run();
		doUpdateDisplay(w, h);
	}

	private void toggleSound()
	{
		if (settings.getOrDefault("sound", "off").equals("on")) {
			settings.put("sound", "off");
		} else {
			settings.put("sound", "on");
		}
		saveConfig();
		onChange.run();
	}

	private void updatePhone(String value)
	{
		System.out.println("Config: phone "+value);
		settings.put("phone", value);
		saveConfig();
		onChange.run();
	}

	private void updateRotate(String value)
	{
		System.out.println("Config: rotate "+value);
		settings.put("rotate", value);
		saveConfig();
		onChange.run();
	}

	private void updateFPS(String value)
	{
		System.out.println("Config: fps "+value);
		settings.put("fps", value);
		saveConfig();
		onChange.run();
	}

	private void toggleCompatFlag(String value)
	{
		if (settings.getOrDefault(value, "off").equals("on")) {
			settings.remove(value);
		} else {
			settings.put(value, "on");
		}

		saveConfig();
		onChange.run();
	}

	private void updateDGFormat(String value)
	{
		settings.put("dgFormat", value);
		saveConfig();
		onChange.run();
	}
}
