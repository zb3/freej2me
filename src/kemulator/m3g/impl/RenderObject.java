package kemulator.m3g.impl;

import javax.microedition.m3g.*;

public final class RenderObject {
	public Node node;
	public Transform trans;

	public int submeshIndex;
	public int sortKey;
	public float alphaFactor;

	public RenderObject(Node node, Transform trans, int submeshIndex, RenderPipe renderPipe) {
		this.node = node;
		this.trans = new Transform(trans);
		this.submeshIndex = submeshIndex;

		Appearance ap;
		if (node instanceof Sprite3D) {
			sortKey = getSortKey(((Sprite3D) node).getAppearance());
		} else {
			sortKey = getSortKey(((Mesh) node).getAppearance(submeshIndex));
		}

		alphaFactor = renderPipe.getEffectiveAlphaFactor(node);
	}

	private int getSortKey(Appearance ap) {
		int sortKey = ap.getLayer() * 2;

		if (ap.getCompositingMode() != null && ap.getCompositingMode().getBlending() != CompositingMode.REPLACE) {
			sortKey++;
		}

		return sortKey;
	}
}
