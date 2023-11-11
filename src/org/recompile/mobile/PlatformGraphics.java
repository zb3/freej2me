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
package org.recompile.mobile;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class PlatformGraphics extends javax.microedition.lcdui.Graphics
{
	protected BufferedImage canvas;
	protected Graphics2D gc;

	protected Color awtColor;

	protected int strokeStyle = SOLID;

	protected Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

	public PlatformGraphics platformGraphics;
	public PlatformImage platformImage;

	public PlatformGraphics(PlatformImage image)
	{
		canvas = image.getCanvas();
		gc = canvas.createGraphics();
		platformImage = image;

		platformGraphics = this;

		clipX = 0;
		clipY = 0;
		clipWidth = canvas.getWidth();
		clipHeight = canvas.getHeight();

		setColor(0,0,0);
		// gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gc.setBackground(new Color(0, 0, 0, 0));
		gc.setFont(font.platformFont.awtFont);
	}

	public void reset() //Internal use method, resets the Graphics object to its inital values
	{
		translate(-1 * translateX, -1 * translateY);
		setClip(0, 0, canvas.getWidth(), canvas.getHeight());
		setColor(0,0,0);
		setFont(Font.getDefaultFont());
		setStrokeStyle(SOLID);
	}

	public Graphics2D getGraphics2D()
	{
		return gc;
	}

	public BufferedImage getCanvas()
	{
		return canvas;
	}

	public void clearRect(int x, int y, int width, int height)
	{
		gc.clearRect(x, y, width, height);
	}

	public void copyArea(int subx, int suby, int subw, int subh, int x, int y, int anchor)
	{
		x = AnchorX(x, subw, anchor);
		y = AnchorY(y, subh, anchor);

		BufferedImage sub = canvas.getSubimage(subx, suby, subw, subh);

		gc.drawImage(sub, x, y, null);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		gc.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawChar(char character, int x, int y, int anchor)
	{
		drawString(Character.toString(character), x, y, anchor);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y, int anchor)
	{
		char[] str = new char[length];
		for(int i=offset; i<offset+length; i++)
		{
			if(i>=0 && i<data.length)
			{
				str[i-offset] = data[i];
			}
		}	
		drawString(new String(str), x, y, anchor);
	}

	public void drawImage(Image image, int x, int y, int anchor)
	{
		try
		{
			int imgWidth = image.getWidth();
			int imgHeight = image.getHeight();

			x = AnchorX(x, imgWidth, anchor);
			y = AnchorY(y, imgHeight, anchor);

			gc.drawImage(image.platformImage.getCanvas(), x, y, null);
		}
		catch (Exception e)
		{
			System.out.println("drawImage A:"+e.getMessage());
		}
	}

	public void drawImage(Image image, int x, int y)
	{
		try
		{
			gc.drawImage(image.platformImage.getCanvas(), x, y, null);
		}
		catch (Exception e)
		{
			System.out.println("drawImage B:"+e.getMessage());
		}
	}

	public void drawImage2(Image image, int x, int y) // Internal use method called by PlatformImage
	{
		gc.drawImage(image.platformImage.getCanvas(), x, y, null);
	}
	public void drawImage2(BufferedImage image, int x, int y) // Internal use method called by PlatformImage
	{
		gc.drawImage(image, x, y, null);
	}

	public void flushGraphics(Image image, int x, int y, int width, int height)
	{
		// called by MobilePlatform.flushGraphics/repaint
		try
		{
			BufferedImage sub = image.platformImage.getCanvas().getSubimage(x, y, width, height);
			gc.drawImage(sub, x, y, null);
		}
		catch (Exception e)
		{
			//System.out.println("flushGraphics A:"+e.getMessage());
		}
	}

	public void drawRegion(Image image, int subx, int suby, int subw, int subh, int transform, int x, int y, int anchor)
	{
		try
		{
			if(transform == 0)
			{
				BufferedImage sub = image.platformImage.getCanvas().getSubimage(subx, suby, subw, subh);
				x = AnchorX(x, subw, anchor);
				y = AnchorY(y, subh, anchor);
				gc.drawImage(sub, x, y, null);
			}
			else
			{
				PlatformImage sub = new PlatformImage(image, subx, suby, subw, subh, transform);
				x = AnchorX(x, sub.width, anchor);
				y = AnchorY(y, sub.height, anchor);
				gc.drawImage(sub.getCanvas(), x, y, null);
			}
		}
		catch (Exception e)
		{
			//System.out.println("drawRegion A (x:"+x+" y:"+y+" w:"+subw+" h:"+subh+"):"+e.getMessage());
		}
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha)
	{
		if(width<1 || height<1) { return; }
		if(!processAlpha)
		{
			for (int i=offset; i<rgbData.length; i++) { rgbData[i] &= 0x00FFFFFF; rgbData[i] |= 0xFF000000; }
		}
		else
		{	// Fix Alpha //
			for (int i=offset; i<rgbData.length; i++) { rgbData[i] |= 0x00000000; rgbData[i] &= 0xFFFFFFFF; }
		}
		// Copy from new image.  This avoids some problems with games that don't
		// properly adapt to different display sizes.
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		temp.setRGB(0, 0, width, height, rgbData, offset, scanlength);	
		gc.drawImage(temp, x, y, null);
	}


	public void drawLine(int x1, int y1, int x2, int y2)
	{
		gc.drawLine(x1, y1, x2, y2);
	}

	public void drawRect(int x, int y, int width, int height)
	{
		gc.drawRect(x, y, width, height);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		gc.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawString(String str, int x, int y, int anchor)
	{
		if(str!=null)
		{
			x = AnchorX(x, gc.getFontMetrics().stringWidth(str), anchor);
			int ascent = gc.getFontMetrics().getAscent();
			int height = gc.getFontMetrics().getHeight();

			y += ascent;
			
			if((anchor & VCENTER)>0) { y = y+height/2; }
			if((anchor & BOTTOM)>0) { y = y-height; }
			if((anchor & BASELINE)>0) { y = y-ascent; }

			gc.drawString(str, x, y);
		}
	}

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor)
	{
		if (str.length() >= offset + len)
		{
			drawString(str.substring(offset, offset+len), x, y, anchor);
		}
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		gc.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillRect(int x, int y, int width, int height)
	{
		gc.fillRect(x, y, width, height);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		gc.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		gc.fillRect(x, y, width, height);
	}

	//public int getBlueComponent() { }
	//public Font getFont() { return font; }
	//public int getColor() { return color; }
	//public int getGrayScale() { }
	//public int getGreenComponent() { }
	//public int getRedComponent() { }
	//public int getStrokeStyle() { return strokeStyle; }

	public void setColor(int rgb)
	{
		setColor((rgb>>16) & 0xFF, (rgb>>8) & 0xFF, rgb & 0xFF);
	}

	public void setColor(int r, int g, int b)
	{
		color = (r<<16) + (g<<8) + b;
		awtColor = new Color(r, g, b);
		gc.setColor(awtColor);
	}

	public void setFont(Font font)
	{
		super.setFont(font);
		gc.setFont(font.platformFont.awtFont);
	}
	//public void setGrayScale(int value)
	//public void setStrokeStyle(int style)

	public void setClip(int x, int y, int width, int height)
	{
		gc.setClip(x, y, width, height);
		clipX = (int)gc.getClipBounds().getX();
		clipY = (int)gc.getClipBounds().getY();
		clipWidth = (int)gc.getClipBounds().getWidth();
		clipHeight = (int)gc.getClipBounds().getHeight();
	}

	public void clipRect(int x, int y, int width, int height)
	{
		gc.clipRect(x, y, width, height);
		clipX = (int)gc.getClipBounds().getX();
		clipY = (int)gc.getClipBounds().getY();
		clipWidth = (int)gc.getClipBounds().getWidth();
		clipHeight = (int)gc.getClipBounds().getHeight();
	}

	//public int getTranslateX() { }
	//public int getTranslateY() { }

	public void translate(int x, int y)
	{
		translateX += x;
		translateY += y;
		gc.translate(x, y);
		clipX -= x;
		clipY -= y;
	}

	private int AnchorX(int x, int width, int anchor)
	{
		int xout = x;
		if((anchor & HCENTER)>0) { xout = x-(width/2); }
		if((anchor & RIGHT)>0) { xout = x-width; }
		if((anchor & LEFT)>0) { xout = x; }
		return xout;
	}

	private int AnchorY(int y, int height, int anchor)
	{
		int yout = y;
		if((anchor & VCENTER)>0) { yout = y-(height/2); }
		if((anchor & TOP)>0) { yout = y; }
		if((anchor & BOTTOM)>0) { yout = y-height; }
		if((anchor & BASELINE)>0) { yout = y+height; }
		return yout;
	}

	public void setAlphaRGB(int ARGB)
	{
		gc.setColor(new Color(ARGB, true));
	}

	// Nokia Direct Graphics helper functions (others are in DirectGraphicsImp.java)

	public void drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints)
	{
		int[] x = new int[nPoints];
		int[] y = new int[nPoints];

		for(int i=0; i<nPoints; i++)
		{
			x[i] = xPoints[xOffset+i];
			y[i] = yPoints[yOffset+i];
		}
		gc.drawPolygon(x, y, nPoints);
	}

	public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints)
	{
		int[] x = new int[nPoints];
		int[] y = new int[nPoints];

		for(int i=0; i<nPoints; i++)
		{
			x[i] = xPoints[xOffset+i];
			y[i] = yPoints[yOffset+i];
		}
		gc.fillPolygon(x, y, nPoints);
	}

	public void getPixels(int[] pixels, int offset, int scanlength, int x, int y, int width, int height)
	{
		//System.out.println("getPixels B");
		canvas.getRGB(x, y, width, height, pixels, offset, scanlength);
	}
}
