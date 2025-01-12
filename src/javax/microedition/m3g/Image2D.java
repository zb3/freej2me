package javax.microedition.m3g;

import javax.microedition.lcdui.Image;

import kemulator.m3g.gles2.Emulator3D;
import pl.zb3.freej2me.bridge.gles2.TextureHolder;

public class Image2D extends Object3D implements TextureHolder {
	public static final int ALPHA = 96;
	public static final int LUMINANCE = 97;
	public static final int LUMINANCE_ALPHA = 98;
	public static final int RGB = 99;
	public static final int RGBA = 100;
	private int type;
	private int width;
	private int height;
	private byte[] imageData;
	private boolean mutable;
	private static byte[] tmp;
	public boolean loaded;
	private int id;

	public Image2D(int var1, Object var2) {
		if (var2 == null) {
			throw new NullPointerException();
		} else if (!checkType(var1)) {
			throw new IllegalArgumentException();
		} else if (var2 instanceof Image) {
			Image var3 = (Image) var2;
			this.width = var3.getWidth();
			this.height = var3.getHeight();
			this.mutable = false;
			this.type = var1;
			this.imageData = convert(var1, var3);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Image2D(int var1, int var2, int var3, byte[] var4) {
		if (var4 == null) {
			throw new NullPointerException();
		} else if (var2 > 0 && var3 > 0 && checkType(var1)) {
			int var5 = var2 * var3 * bytesPerPixel(var1);
			if (var4.length < var5) {
				throw new IllegalArgumentException();
			} else {
				this.width = var2;
				this.height = var3;
				this.mutable = false;
				this.type = var1;
				this.imageData = new byte[var5];
				System.arraycopy(var4, 0, this.imageData, 0, var5);
//				allocateBuffer(var5);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Image2D(int var1, int var2, int var3, byte[] var4, byte[] var5) {
		if (var4 != null && var5 != null) {
			int var6 = var2 * var3;
			if (var2 > 0 && var3 > 0 && checkType(var1) && var4.length >= var6) {
				int var7 = bytesPerPixel(var1);
				if (var5.length < 256 * var7 && var5.length % var7 != 0) {
					throw new IllegalArgumentException();
				} else {
					this.width = var2;
					this.height = var3;
					this.mutable = false;
					this.type = var1;
					this.imageData = new byte[var6 * var7];

					for (int var8 = 0; var8 < var6; ++var8) {
						System.arraycopy(var5, (var4[var8] & 255) * var7, this.imageData, var8 * var7, var7);
					}
				}
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new NullPointerException();
		}
	}

	public Image2D(int var1, int var2, int var3) {
		if (var2 > 0 && var3 > 0 && checkType(var1)) {
			this.width = var2;
			this.height = var3;
			this.mutable = true;
			this.type = var1;
			int var4 = var2 * bytesPerPixel(var1);
			this.imageData = new byte[var4 * var3];
			int var5;
			if (tmp == null || tmp.length < var4) {
				tmp = new byte[var4];

				for (var5 = var4 - 1; var5 >= 0; --var5) {
					tmp[var5] = -1;
				}
			}

			for (var5 = 0; var5 < var3; ++var5) {
				System.arraycopy(tmp, 0, this.imageData, var5 * var4, var4);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void set(int var1, int var2, int var3, int var4, byte[] var5) {
		if (var5 == null) {
			throw new NullPointerException();
		} else if (this.mutable && var1 >= 0 && var2 >= 0 && var3 > 0 && var4 > 0 && var1 + var3 <= this.width && var2 + var4 <= this.height) {
			int var6 = this.getBitsPerColor();
			if (var5.length < var3 * var4 * var6) {
				throw new IllegalArgumentException();
			} else {
				for (int var7 = 0; var7 < var4; ++var7) {
					System.arraycopy(var5, var7 * var3 * var6, this.imageData, ((var2 + var7) * this.width + var1) * var6, var3 * var6);
				}
				this.invalidate();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean isMutable() {
		return this.mutable;
	}

	public int getFormat() {
		return this.type;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getBitsPerColor() {
		return bytesPerPixel(this.type);
	}

	private static int bytesPerPixel(int type) {
		switch (type) {
			case ALPHA:
			case LUMINANCE:
				return 1;
			case LUMINANCE_ALPHA:
				return 2;
			case RGB:
				return 3;
			case RGBA:
				return 4;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static boolean checkType(int var0) {
		return var0 >= 96 && var0 <= 100;
	}

	public final byte[] getImageData() {
		return this.imageData;
	}

	private static byte[] convert(int type, Image image) {

		byte[] var3 = null;

		final int width = image.getWidth();
		final int height = image.getHeight();

		int[] data = new int[width * height];
		image.getRGB(data, 0, width, 0, 0, width, height);

		int var4 = data.length;
		int var5;
		if (image.isMutable()) {
			switch (type) {
				case 96:
				case 97:
					var3 = new byte[var4];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5] = (byte) (((data[var5] >> 16 & 255) + (data[var5] >> 8 & 255) + (data[var5] & 255)) / 3 & 255);
					}

					return var3;
				case 98:
					var3 = new byte[var4 * 2];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 2] = (byte) (((data[var5] >> 16 & 255) + (data[var5] >> 8 & 255) + (data[var5] & 255)) / 3 & 255);
						var3[var5 * 2 + 1] = -1;
					}

					return var3;
				case 99:
					var3 = new byte[var4 * 3];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 3] = (byte) (data[var5] >> 16 & 255);
						var3[var5 * 3 + 1] = (byte) (data[var5] >> 8 & 255);
						var3[var5 * 3 + 2] = (byte) (data[var5] & 255);
					}

					return var3;
				case 100:
					var3 = new byte[var4 * 4];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 4] = (byte) (data[var5] >> 16 & 255);
						var3[var5 * 4 + 1] = (byte) (data[var5] >> 8 & 255);
						var3[var5 * 4 + 2] = (byte) (data[var5] & 255);
						var3[var5 * 4 + 3] = -1;
					}
			}
		} else {
			switch (type) {
				case 96:
					var3 = new byte[var4];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5] = (byte) (data[var5] >> 24 & 255);
					}

					return var3;
				case 97:
					var3 = new byte[var4];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5] = (byte) (((data[var5] >> 16 & 255) + (data[var5] >> 8 & 255) + (data[var5] & 255)) / 3 & 255);
					}

					return var3;
				case 98:
					var3 = new byte[var4 * 2];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 2] = (byte) (((data[var5] >> 16 & 255) + (data[var5] >> 8 & 255) + (data[var5] & 255)) / 3 & 255);
						var3[var5 * 2 + 1] = (byte) (data[var5] >> 24 & 255);
					}

					return var3;
				case 99:
					var3 = new byte[var4 * 3];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 3] = (byte) (data[var5] >> 16 & 255);
						var3[var5 * 3 + 1] = (byte) (data[var5] >> 8 & 255);
						var3[var5 * 3 + 2] = (byte) (data[var5] & 255);
					}

					return var3;
				case 100:
					var3 = new byte[var4 * 4];

					for (var5 = var4 - 1; var5 >= 0; --var5) {
						var3[var5 * 4] = (byte) (data[var5] >> 16 & 255);
						var3[var5 * 4 + 1] = (byte) (data[var5] >> 8 & 255);
						var3[var5 * 4 + 2] = (byte) (data[var5] & 255);
						var3[var5 * 4 + 3] = (byte) (data[var5] >> 24 & 255);
					}
			}
		}

		return var3;
	}

	protected Object3D duplicateObject() {
		Image2D var1 = (Image2D) super.duplicateObject();
		var1.imageData = (byte[]) this.imageData.clone();
		return var1;
	}

	public void invalidate() {
		this.setLoaded(false);
	}

	public void dispose() {
		if (id == 0) return;

		((Emulator3D) Graphics3D.getImpl()).textureResourceManager.resetHolder(this);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean b) {
		loaded = b;
	}

	public void setId(int id) {
		this.id = id;
		this.loaded = false;
	}

	public int getId() {
		return id;
	}

	public int size() {
		return imageData.length;
	}

	public void getPixels(byte[] dist) {
		byte[] b = getImageData();
		System.arraycopy(b, 0, dist, 0, b.length);
	}

	public boolean isPalettized() {
		// zb3: is it accurate?
		return false;
	}

	public void getPalette(byte[] array) {
	}

	void setRGB(Image image) {

		final int width = image.getWidth();
		final int height = image.getHeight();

		int[] data = new int[width * height];
		image.getRGB(data, 0, width, 0, 0, width, height);

		int l = data.length;
		if (imageData == null || imageData.length != l * 3) {
			imageData = new byte[l * 3];
		}

		byte[] var3 = imageData;

		for (int var5 = l - 1; var5 >= 0; --var5) {
			var3[var5 * 3] = (byte) (data[var5] >> 16 & 255);
			var3[var5 * 3 + 1] = (byte) (data[var5] >> 8 & 255);
			var3[var5 * 3 + 2] = (byte) (data[var5] & 255);
		}

		this.invalidate();
	}

	public void clearId() {
		setId(0);
	}
}
