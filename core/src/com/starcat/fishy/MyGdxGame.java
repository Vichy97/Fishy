package com.starcat.fishy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.fishy.screens.GameScreen;
import com.starcat.fishy.screens.LoadingScreen;

public class MyGdxGame extends Game {

	public static final boolean DEBUG = true;

	public static final int GAME_WIDTH = 2048;
	public static final int GAME_HEIGHT = 1152;
	public static float ASPECT_RATIO;
	public static float UNIT_SCALE = .05f;

	private OrthographicCamera gameCamera, uiCamera;
	private Viewport gameViewport, uiViewport;



	@Override
	public void create() {
		//debug("created");

		ASPECT_RATIO = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

		gameCamera = new OrthographicCamera(GAME_HEIGHT/ASPECT_RATIO, GAME_HEIGHT);
		gameViewport = new StretchViewport(2048 * .05f, 1152 * .05f, gameCamera);

		uiCamera = new OrthographicCamera(GAME_WIDTH/ASPECT_RATIO, GAME_HEIGHT);
		uiViewport = new ScreenViewport(uiCamera);

		setScreen("loading");
	}

	@Override
	public void dispose() {
		debug("dispose");

		super.dispose();
		AssetLoader.dispose();
	}



	public void setScreen(String screen) {
		if (screen.equals("loading")) {
			setScreen(new LoadingScreen(this));
		} else if(screen.equals("game")) {
			setScreen(new GameScreen(this, gameCamera, gameViewport, uiCamera, uiViewport));
		} else {
			//debug("Incorrect Screen Name");
			Gdx.app.exit();
		}
		System.gc();
	}

	private static void debug(String message) {
		if (MyGdxGame.DEBUG) {
			//Gdx.app.log("My GDX Game", message);
		}
	}


}
