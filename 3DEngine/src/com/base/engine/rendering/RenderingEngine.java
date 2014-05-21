package com.base.engine.rendering;

import com.base.engine.core.GameBranch;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.leaves.BaseLight;
import com.base.engine.leaves.Camera;
import com.base.engine.rendering.resourceManagement.MappedValues;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderingEngine extends MappedValues {

    private HashMap<String, Integer> samplerMap;
    private ArrayList<BaseLight> lights;
    private BaseLight activeLight;

    private Shader forwardAmbient;
    private Camera mainCamera;

    //Temp vars
    private Texture tempTexture;
    private Material tempMat;
    private Mesh tempPlane;
    private Camera altCamera;
    private GameBranch altCameraBranch;
    private Transform tempTransform;

    public RenderingEngine() {
        super();

        lights = new ArrayList<BaseLight>();
        samplerMap = new HashMap<String, Integer>();
        samplerMap.put("diffuse", 0);
        samplerMap.put("normalMap", 1);
        samplerMap.put("dispMap", 2);

        addVector3f("ambient", new Vector3f(1f, 1f, 1f));

        forwardAmbient = new Shader("forward-ambient");

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_TEXTURE_2D);

        //temp stuff
        altCamera = new Camera(new Matrix4f().initIdentity());
        altCameraBranch = new GameBranch().addLeaf(altCamera);
        altCamera.getTransform().rotate(new Vector3f(0, 1, 0), (float) Math.toRadians(180));

        tempTexture = new Texture(Window.getWidth() / 5, Window.getHeight() / 5, (ByteBuffer) null, GL_TEXTURE_2D, GL_NEAREST, GL_RGBA, GL_RGBA, false, GL_COLOR_ATTACHMENT0);

        tempMat = new Material();
        tempMat.addTexture("diffuse", tempTexture);
        tempMat.addFloat("specularIntensity", 1);
        tempMat.addFloat("specularPower", 8);
        tempTransform = new Transform();
        tempTransform.setScale(new Vector3f(0.9f, 0.9f, 0.9f));
        tempTransform.rotate(new Vector3f(1, 0, 0), (float) Math.toRadians(90.0f));
        tempTransform.rotate(new Vector3f(0, 0, 1), (float) Math.toRadians(180.0f));

        tempPlane = new Mesh("plane.obj");

    }

    public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
        throw new IllegalArgumentException(uniformName + " is not a supported type in Rendering Engine");
    }

    public void render(GameBranch branch) {
//        Window.bindAsRenderTarget();
        tempTexture.bindAsRenderTarget();

        glClearColor(0f, 0f, 0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        branch.renderAll(forwardAmbient, this);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glDepthMask(false);
        glDepthFunc(GL_EQUAL);
        for (BaseLight light : lights) {
            activeLight = light;

            branch.renderAll(light.getShader(), this);
        }
        glDepthFunc(GL_LESS);
        glDepthMask(true);
        glDisable(GL_BLEND);

        //temp render
        Window.bindAsRenderTarget();
        Camera temp = mainCamera;
        mainCamera = altCamera;
        glClearColor(0f, 0f, 0.5f, 0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        forwardAmbient.bind();
        forwardAmbient.updateUniforms(tempTransform, tempMat, this);
        tempPlane.draw();
        mainCamera = temp;
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
