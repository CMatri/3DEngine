package com.base.engine.rendering;

import com.base.engine.core.*;
import com.base.engine.leaves.BaseLight;
import com.base.engine.leaves.Camera;
import com.base.engine.rendering.resourceManagement.MappedValues;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderingEngine extends MappedValues {

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;

    private Shader forwardAmbient;
    private Shader shadowMapShader;
    private Camera mainCamera;

    private Camera altCamera;
    private GameBranch altCameraBranch;

    private Matrix4f lightMatrix;

    private static final Matrix4f biasMatrix = new Matrix4f().initScale(0.5f, 0.5f, 0.5f).mul(new Matrix4f().initTranslation(1.0f, 1.0f, 1.0f));

    public RenderingEngine() {
        super();

        lights = new ArrayList<BaseLight>();
        samplerMap = new HashMap<String, Integer>();
        samplerMap.put("diffuse", 0);
        samplerMap.put("normalMap", 1);
        samplerMap.put("dispMap", 2);
        samplerMap.put("shadowMap", 3);

        setVector3f("ambient", new Vector3f(0.03f, 0.03f, 0.03f));
        setTexture("shadowMap", new Texture(1024, 1024, null, GL_TEXTURE_2D, GL_NEAREST, GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT, true, GL_DEPTH_ATTACHMENT));

        forwardAmbient = new Shader("forward-ambient");
        shadowMapShader = new Shader("shadowMapGenerator");

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_TEXTURE_2D);

        altCamera = new Camera(new Matrix4f().initIdentity());
        altCameraBranch = new GameBranch().addLeaf(altCamera);
        altCamera.getTransform().rotate(new Vector3f(0, 1, 0), (float) Math.toRadians(180));

        lightMatrix = new Matrix4f();
    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        throw new IllegalArgumentException(uniformName + " is not a supported type in Rendering Engine");
    }

    public void render(GameBranch branch) {
        Window.bindAsRenderTarget();

        glClearColor(0f, 0f, 0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        branch.renderAll(forwardAmbient, this);
        for (BaseLight light : lights) {
            activeLight = light;

            BaseLight.ShadowInfo shadowInfo = light.getShadowInfo();
            getTexture("shadowMap").bindAsRenderTarget();
            glClear(GL_DEPTH_BUFFER_BIT);

            if (shadowInfo != null) {
                altCamera.setProjection(shadowInfo.getProjection());
                altCamera.getTransform().setPos(activeLight.getTransform().getTransformedPos());
                altCamera.getTransform().setRot(activeLight.getTransform().getTransformedRot());

                lightMatrix = biasMatrix.mul(altCamera.getViewProjection());

                Camera temp = mainCamera;
                mainCamera = altCamera;

                branch.renderAll(shadowMapShader, this);

                mainCamera = temp;
            }

            Window.bindAsRenderTarget();
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
            glDepthMask(false);
            glDepthFunc(GL_EQUAL);

            branch.renderAll(light.getShader(), this);

            glDepthFunc(GL_LESS);
            glDepthMask(true);
            glDisable(GL_BLEND);
        }
    }

    public static String getOpenGLVersion() {
        return glGetString(GL_VERSION);
    }

    public void addLight(BaseLight light) {
        lights.add(light);
    }

    public void addCamera(Camera camera) {
        mainCamera = camera;
    }

    public BaseLight getActiveLight() {
        return activeLight;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public Matrix4f getLightMatrix() {
        return lightMatrix;
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public int getSamplerSlot(String samplerName) {
        return samplerMap.get(samplerName);
    }

    public void setClearColor(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }
}
