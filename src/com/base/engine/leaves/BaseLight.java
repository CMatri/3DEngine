package com.base.engine.leaves;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

public class BaseLight extends GameLeaf {
    private Vector3f color;
    private float intensity;
    private Shader shader;
    private ShadowInfo shadowInfo;

    public BaseLight(Vector3f color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public void addToEngine(CoreEngine engine) {
        engine.getRenderingEngine().addLight(this);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public ShadowInfo getShadowInfo() {
        return shadowInfo;
    }

    protected void setShadowInfo(ShadowInfo shadowInfo) {
        this.shadowInfo = shadowInfo;
    }

    public class ShadowInfo {
        private Matrix4f projection;
        private float bias;
        private boolean flipFaces;

        public ShadowInfo(Matrix4f projection, float bias, boolean flipFaces) {
            this.projection = projection;
            this.bias = bias;
            this.flipFaces = flipFaces;
        }

        public float getBias() {
            return bias;
        }

        public void setBias(float bias) {
            this.bias = bias;
        }

        public boolean isFlipFaces() {
            return flipFaces;
        }

        public void setFlipFaces(boolean flipFaces) {
            this.flipFaces = flipFaces;
        }

        public Matrix4f getProjection() {
            return projection;
        }
    }
}
