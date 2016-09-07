package com.startcat.fishy.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starcat.fishy.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		config.width = 1920;
		config.height = 1080;
		config.fullscreen = true;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
