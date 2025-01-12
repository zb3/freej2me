package javax.microedition.m3g;

import kemulator.m3g.utils.G3DUtils;

public class Camera extends Node {
	public static final int GENERIC = 48;
	public static final int PARALLEL = 49;
	public static final int PERSPECTIVE = 50;
	private int projectionType = 48;
	private Transform generic = new Transform();
	private float[] projection = new float[4];

	protected Object3D duplicateObject() {
		Camera var1;
		(var1 = (Camera) super.duplicateObject()).generic = new Transform(this.generic);
		var1.projection = (float[]) this.projection.clone();
		return var1;
	}

	public Camera() {
		this.projection[0] = 2.0F;
		this.projection[1] = 1.0F;
		this.projection[2] = -1.0F;
		this.projection[3] = 1.0F;
	}

	public void setParallel(float var1, float var2, float var3, float var4) {
		if (var1 > 0.0F && var2 > 0.0F) {
			this.projection[0] = var1;
			this.projection[1] = var2;
			this.projection[2] = var3;
			this.projection[3] = var4;
			this.projectionType = 49;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setPerspective(float var1, float var2, float var3, float var4) {
		if (var1 > 0.0F && var1 < 180.0F && var2 > 0.0F && var3 > 0.0F && var4 > 0.0F) {
			this.projection[0] = var1;
			this.projection[1] = var2;
			this.projection[2] = var3;
			this.projection[3] = var4;
			this.projectionType = 50;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setGeneric(Transform var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			this.generic.set(var1);
			this.projectionType = 48;
		}
	}

	public int getProjection(Transform var1) {
		if (var1 != null) {
			float var2;
			float var3;
			float var4;
			float[] var5;
			if (this.projectionType == 49) {
				var2 = this.projection[0];
				var3 = this.projection[1] * var2;
				if ((var4 = this.projection[3] - this.projection[2]) == 0.0F) {
					throw new ArithmeticException("near == far");
				}

				(var5 = new float[16])[0] = 2.0F / var3;
				var5[5] = 2.0F / var2;
				var5[10] = -2.0F / var4;
				var5[11] = -(this.projection[2] + this.projection[3]) / var4;
				var5[15] = 1.0F;
				var1.set(var5);
			} else if (this.projectionType == 50) {
				var2 = (float) Math.tan(Math.toRadians((double) (this.projection[0] / 2.0F)));
				var3 = this.projection[1] * var2;
				if ((var4 = this.projection[3] - this.projection[2]) == 0.0F) {
					throw new ArithmeticException("near == far");
				}

				(var5 = new float[16])[0] = 1.0F / var3;
				var5[5] = 1.0F / var2;
				var5[10] = -(this.projection[2] + this.projection[3]) / var4;
				var5[11] = -2.0F * this.projection[2] * this.projection[3] / var4;
				var5[14] = -1.0F;
				var1.set(var5);
			} else {
				var1.set(this.generic);
			}
		}

		return this.projectionType;
	}

	public int getProjection(float[] var1) {
		if (var1 != null && var1.length < 4) {
			throw new IllegalArgumentException();
		} else {
			if (var1 != null && this.projectionType != 48) {
				System.arraycopy(this.projection, 0, var1, 0, 4);
			}

			return this.projectionType;
		}
	}

	protected void updateProperty(int property, float[] values) {
		if (this.projectionType != 48) {
			switch (property) {
				case 263:
					this.projection[3] = this.projectionType != 50 ? values[0] : G3DUtils.limitPositive(values[0]);
					return;
				case 264:
					this.projection[0] = this.projectionType != 50 ? G3DUtils.limitPositive(values[0]) : G3DUtils.limit(values[0], 0.0F, 180.0F);
					return;
				case 265:
				case 266:
				default:
					break;
				case 267:
					this.projection[2] = this.projectionType != 50 ? values[0] : G3DUtils.limitPositive(values[0]);
					return;
			}
		}

		super.updateProperty(property, values);
	}

	protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
		return false;
	}
}
