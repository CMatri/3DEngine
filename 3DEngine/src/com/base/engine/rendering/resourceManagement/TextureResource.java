package com.base.engine.rendering.resourceManagement;

import com.base.engine.core.Util;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class TextureResource {
    private int[] id;
    private int frameBuffer;
    private int renderBuffer;
    private int refCount;
    private int width, height;
    private int textureTarget;

    public TextureResource(int textureTarget, int width, int height, int numTextures, ByteBuffer data, int[] filters, int[] internalFormat, int[] format, boolean clamp, int[] attachments) {
        id = new int[numTextures];
        this.textureTarget = textureTarget;
        this.width = width;
        this.height = height;

        initTextures(data, filters, internalFormat, format, clamp);
        initRenderTargets(attachments);

        this.refCount = 1;
        this.frameBuffer = 0;
    }

    @Override
    protected void finalize() {
        for (int i = 0; i < id.length; i++) {
            glDeleteTextures(id[i]);
        }
        if (frameBuffer != 0) {
            glDeleteFramebuffers(frameBuffer);
        }
    }

    public void addReference() {
        refCount++;
    }

    public boolean removeReference() {
        refCount--;
        return refCount == 0;
    }

    public void initTextures(ByteBuffer data, int[] filters, int[] internalFormat, int[] format, boolean clamp) {
        for (int i = 0; i < id.length; i++) {
            id[i] = glGenTextures();
        }
        for (int i = 0; i < id.length; i++) {
            glBindTexture(textureTarget, id[i]);

            glTexParameteri(textureTarget, GL_TEXTURE_MIN_FILTER, filters[i]);
            glTexParameteri(textureTarget, GL_TEXTURE_MAG_FILTER, filters[i]);

            glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_REPEAT);

            if (clamp) {
                glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
                glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
            }

            glTexImage2D(textureTarget, 0, internalFormat[i], width, height, 0, format[i], GL_UNSIGNED_BYTE, data);
        }
    }

    public void initRenderTargets(int[] attachments) {
        if (attachments == null)
            return;

        int[] drawBuffers = new int[id.length];
        boolean hasDepth = false;

        for (int i = 0; i < id.length; i++) {
            if (attachments[i] == GL_DEPTH_ATTACHMENT) {
                drawBuffers[i] = GL_NONE;
                hasDepth = true;
            } else
                drawBuffers[i] = attachments[i];

            if (attachments[i] == GL_NONE)
                continue;

            if (frameBuffer == 0) {
                frameBuffer = glGenFramebuffers();
                glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
            }

            glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, attachments[i], textureTarget, id[i], 0);
        }
        if (frameBuffer == 0)
            return;

        if (!hasDepth) {
            renderBuffer = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_FRAMEBUFFER, renderBuffer);
        }

        glDrawBuffers(Util.createIntBuffer(drawBuffers.length).put(drawBuffers));

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Util.err("Framebuffer creation failed!");
            System.exit(-1);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(int textureNum) {
        glBindTexture(GL_TEXTURE_2D, id[textureNum]);
    }

    public int getId() {
        return id[0];
    }

    public void bindAsRenderTarget() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
