package com.base.engine.leaves;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

public class DirectionalLight extends BaseLight {

	private Vector3f origColor;

	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		this.origColor = color;
		setShader(new Shader("forward-directional"));
        setShadowInfo(new ShadowInfo(new Matrix4f().initOrthographic(-40, 40, -40, 40, -40, 40), 1.5f, true));
	}

	public Vector3f getDirection() {
		return getTransform().getTransformedRot().getForward();
	}
}
