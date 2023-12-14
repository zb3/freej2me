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

package com.siemens.mp.game;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.siemens.mp.misc.NativeMem;

public class ExtendedImage extends com.siemens.mp.misc.NativeMem
{
	private int[] palette = { 0xFFFFFFFF, 0xFF000000 };
	private int[] paletteAlpha = { 0x00000000, 0xFFFFFFFF, 0xFF000000, 0xFF000000 };

	private PlatformImage image;

	private PlatformGraphics gc;

	private int width;

	private int height; 
	private boolean hasAlpha;
	
	public ExtendedImage(Image img)
	{
		image = new PlatformImage(img);
		width = image.getWidth();
		height = image.getHeight();
		gc = image.getGraphics();

		if (img.hasSiemensAlpha()) {
			hasAlpha = true;
		} else {
			// mostly useless check..
			int[] colors = new int[width * height];
			image.getRGB(colors, 0, width, 0, 0, width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if ((((colors[y*width+x] >> 24) & 0xFF)) != 255) {
						hasAlpha = true;
					}
				}
			}
		}
	}

	public void clear(byte color)
	{
		gc.clearARGB(0, 0, width, height, hasAlpha ? paletteAlpha[color&0x3] : palette[color&1]);
		gc.setColor(palette[0]);
	}

	public Image getImage() { return image; }

	public int getPixel(int x, int y)
	{
		int argb = image.getPixel(x, y);
		if (hasAlpha) {
			return ((argb & 0xff000000) == 0) ? 0 : ((argb & 0xffffff) == 0xffffff ? 1 : 2);
		} else {
			return argb == 0xffffffff ? 0 : 1;
		}
	}

	public void setPixel(int x, int y, byte color)
	{
		image.setPixel(x, y, hasAlpha ? paletteAlpha[color&0x3] : palette[color&1]);
	}

	public void getPixelBytes(byte[] pixels, int x, int y, int width, int height) { 
		int[] colors = new int[width * height];
		image.getRGB(colors, 0, width, x, y, width, height);

		if (hasAlpha) {
			final int dataLen = colors.length / 4;
			for (int i = 0, k = 0; i < dataLen; i++) {
				int data = 0;
				for (int j = 0; j < 4; j++) {
					data <<= 2;
					int color = colors[k++];
					if ((color & 0xFF000000) != 0xFF000000) continue;
					if ((color & 0xFFFFFF) == 0xFFFFFF) data |= 1;
					else data |= 2;
				}
				pixels[i] = (byte) data;
			}
		} else {
			final int dataLen = colors.length / 8;
			for (int i = 0, k = 0; i < dataLen; i++) {
				int data = 0;
				for (int j = 0; j < 8; j++) {
					data <<= 1;
					if ((colors[k++] & 0xFFFFFF) != 0xFFFFFF) {
						data |= 1;
					}
				}
				pixels[i] = (byte) data;
			}
		}
	}

	public void setPixels(byte[] pixels, int x, int y, int width, int height) {
		int right = x + width;
		int bottom = y + height;
		if (x >= this.width || right <= 0 || y >= this.height || bottom <= 0) {
			return;
		}
		x = Math.max(x, 0); y = Math.max(y, 0);
		width = Math.min(width, this.width - x);
		height = Math.min(height, this.height - y);

		int[] colors = new int[width * height];
		if (hasAlpha) {
			final int dataLen = Math.min(pixels.length, colors.length / 4);
			for (int i = 0, k = 0; i < dataLen; i++) {
				final int data = pixels[i];
				for (int j = 3; j >= 0; j--) {
					int color = (data >> j) & 0b11;
					if (color == 0) {
						colors[k++] = 0;
					} else {
						colors[k++] = paletteAlpha[color];
					}
				}
			}
		} else {
			final int dataLen = Math.min(pixels.length, colors.length / 8);
			for (int i = 0, k = 0; i < dataLen; i++) {
				final int data = pixels[i];
				for (int j = 7; j >= 0; j--) {
					final int color = (data >> j) & 1;
					colors[k++] = palette[color];
				}
			}
		}

		image.getCanvas().setRGB(x, y, width, height, colors, 0, width);
	}
	
	public void blitToScreen(int x, int y) // from Micro Java Game Development By David Fox, Roman Verhovsek
	{
		Displayable current = Display.getDisplay(null).getCurrent();
		if (current instanceof Canvas) {
			((Canvas)current).gc.getGraphics2D().drawImage(image.getCanvas(), x, y, null);
			((Canvas)current).repaint();
		}
		//Mobile.getPlatform().flushGraphics(image, x, y, width, height);
	} 
}