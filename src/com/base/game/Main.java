package com.base.game;

import com.base.engine.core.CoreEngine;
import com.base.game.TestGame;
import com.base.game.TicTacToe.MainTicTacToe;
import com.base.game.voxel.VoxelMain;

public class Main {
	public static void main(String[] args) {
		CoreEngine engine = new CoreEngine(1080, 720, 60, new TestGame());
		engine.createWindow("Testing Engine");
//		engine.getRenderingEngine().setClearColor(0, 0, 1, 1);
		engine.start();
	}
}
