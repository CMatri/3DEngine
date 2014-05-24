package com.base.engine.rendering;

import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.OBJModel;
import com.base.engine.rendering.resourceManagement.MeshResource;
import com.base.engine.rendering.resourceManagement.RenderingMode;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	private static HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();
	private MeshResource resource;
	private RenderingMode renderingMode = RenderingMode.FULL;
	private String fileName;
	private int drawType = GL_TRIANGLES;

	public Mesh(String fileName) {
		this.fileName = fileName;
		MeshResource oldResource = loadedModels.get(fileName);

		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		} else {
			loadMesh(fileName);
			loadedModels.put(fileName, resource);
		}
	}

	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, false);
	}

	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
		fileName = "";
		addVertices(vertices, indices, calcNormals);
	}

	@Override
	protected void finalize() {
		if (resource.removeReference() && !fileName.isEmpty()) {
			loadedModels.remove(fileName);
		}
	}

	private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {
		if (calcNormals) {
			calcNormalsAndTangents(vertices, indices);
		}

		resource = new MeshResource(indices.length);

		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
	}

	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);

		if (!renderingMode.equals(RenderingMode.FULL))
			glPolygonMode(GL_FRONT_AND_BACK, renderingMode.getMode());

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glDrawElements(drawType, resource.getSize(), GL_UNSIGNED_INT, 0);

		if (!renderingMode.equals(RenderingMode.FULL))
			glPolygonMode(GL_FRONT_AND_BACK, RenderingMode.FULL.getMode());

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
	}

	private void calcNormalsAndTangents(Vertex[] vertices, int[] indices) {
		for (int i = 0; i < indices.length; i += 3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			Vector3f normal = v1.cross(v2).normalized();

			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setNormal(vertices[i].getNormal().normalized());

			Vector3f c1 = vertices[i].getNormal().cross(new Vector3f(0, 0, 1));
			Vector3f c2 = vertices[i].getNormal().cross(new Vector3f(0, 1, 0));
			Vector3f tangent;

			if (c1.length() > c2.length())
				tangent = c1;
			else
				tangent = c2;

			tangent = tangent.normalized();

			vertices[i].getTangent().set(tangent);
		}
	}

	private Mesh loadMesh(String fileName) {
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		if (!ext.equals("obj")) {
			System.err.println("Error: File format not supported for mesh data: " + ext);
			new Exception().printStackTrace();
			System.exit(1);
		}

		OBJModel test = new OBJModel("./res/models/" + fileName);
		IndexedModel model = test.toIndexedModel();
		model.calcNormalsAndTangents();

		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(new Vertex(model.getPositions().get(i),
					model.getTexCoords().get(i),
					model.getNormals().get(i),
					model.getTangents().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indexData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indexData);

		addVertices(vertexData, Util.toIntArray(indexData), false);

		return null;
	}

	public void setRenderingMode(RenderingMode renderingMode) {
		this.renderingMode = renderingMode;
	}

	public RenderingMode renderingMode() {
		return renderingMode;
	}

	public void setDrawType(int drawType) {
		this.drawType = drawType;
	}
}