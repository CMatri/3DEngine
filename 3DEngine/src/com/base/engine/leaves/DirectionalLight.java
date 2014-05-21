package com.base.engine.leaves;

import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

	private Vector3f direction;
	private Vector3f origColor;
	private boolean enabled = true;

	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		this.origColor = color;
		setShader(new Shader("forward-directional"));
	}

	public Vector3f getDirection() {
		return getTransform().getTransformedRot().getForward();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled)
			setColor(new Vector3f(0, 0, 0));
		else
			setColor(origColor);
	}

	public boolean isEnabled() {
		return enabled;
	}
}
