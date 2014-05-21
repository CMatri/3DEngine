package com.base.engine.rendering;

public class MaterialBag {
	public static Material crackedBrick;
	public static Material brick;
	public static Material brick2;

	public static void instantiate() {
		crackedBrick = new Material();
		crackedBrick.addTexture("diffuse", new Texture("swall1.jpg"));
		crackedBrick.addTexture("normalMap", new Texture("swall1_normal.jpg"));
		crackedBrick.addTexture("dispMap", new Texture("swall1_height.jpg"));
		crackedBrick.addFloat("specularIntensity", 2);
		crackedBrick.addFloat("specularPower", 80);
		float offset = 0.0f;
		float scale = 0.02f;
		float baseBias = scale / 2.0f;
		crackedBrick.addFloat("dispScale", scale);
		crackedBrick.addFloat("dispBias", -baseBias + baseBias * offset);

		brick = new Material();
		brick.addTexture("diffuse", new Texture("bricks2.jpg"));
		brick.addTexture("normalMap", new Texture("bricks2_normal.jpg"));
		brick.addTexture("dispMap", new Texture("bricks2_disp.jpg"));
		brick.addFloat("specularIntensity", 2);
		brick.addFloat("specularPower", 8);
		float offset1 = 0.0f;
		float scale1 = 0.05f;
		float baseBias1 = scale1 / 2.0f;
		brick.addFloat("dispScale", scale1);
		brick.addFloat("dispBias", -baseBias1 + baseBias1 * offset1);

		brick2 = new Material();
		brick2.addTexture("diffuse", new Texture("bricks.jpg"));
		brick2.addTexture("normalMap", new Texture("bricks_normal.jpg"));
		brick2.addTexture("dispMap", new Texture("bricks_disp.png"));
		brick2.addFloat("specularIntensity", 1);
		brick2.addFloat("specularPower", 8);
		float offset2 = -0.4f;
		float scale2 = 0.06f;
		float baseBias2 = scale2 / 2.0f;
		brick2.addFloat("dispScale", scale1);
		brick2.addFloat("dispBias", -baseBias2 + baseBias2 * offset2);
	}
}
