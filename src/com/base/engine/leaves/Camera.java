package com.base.engine.leaves;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Vector3f;

public class Camera extends GameLeaf {
    protected Matrix4f projection;

    public Camera(Matrix4f projection) {
        this.projection = projection;
    }

    public Matrix4f getViewProjection() {
        Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();
        Vector3f cameraPos = getTransform().getTransformedPos().mul(-1);
        Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

        return projection.mul(cameraRotation.mul(cameraTranslation));
    }

    public void setProjection(Matrix4f projection) {
        this.projection = projection;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    @Override
    public void addToEngine(CoreEngine engine) {
        engine.getRenderingEngine().addCamera(this);
    }
}
