package com.base.game.TicTacToe;

import com.base.engine.core.*;
import com.base.engine.leaves.*;
import com.base.engine.rendering.*;
import com.base.engine.rendering.primitives.Box;
import com.base.game.Game;
import org.lwjgl.opengl.GL11;

public class MainTicTacToe extends Game {

	private PerspectiveCamera camera;

	@Override
	public void init() {
		MaterialBag.instantiate();

		camera = new PerspectiveCamera((float) Math.toRadians(70.0f), (float) Window.getWidth() / (float) Window.getHeight(), 0.01f, 1000.0f);

		Vertex[] vertices = new Vertex[100];
		int[] indices = new int[402];

		for(int i = 0; i < 100; i++) {
			vertices[i] = new Vertex(new Vector3f(1 + i, 1, 1 + i), new Vector2f(i % 2, i %2), new Vector3f(0, 1, 0));
		}

		Mesh m = new Mesh(vertices, indices);
//		m.setDrawType(GL11.GL_TRIANGLES);

		addObject(new GameBranch().addLeaf(new DirectionalLight(new Vector3f(1, 1, 1), 0.4f)));
		addObject(new GameBranch().addLeaf(new MeshRenderer(m, MaterialBag.brick)));
		addObject(new GameBranch().addLeaf(camera).addLeaf(new FlyLook()).addLeaf(new FlyMove()));

	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	public void input(float delta) {
		super.input(delta);
	}
}