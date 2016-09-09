package com.starcat.fishy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;

/**
 * Created by Vincent on 2/10/2015.
 *
 * load all assets here and call load from one of your screens
 * this also makes use of assetmanager to load assets asynchronously so you can display
 * other things such as a splash screen or loading bar. most textures are also stored
 * in texture atlas's for more efficient loading/rendering
 *
 * if ram is short then I could make more methods such as loadMenuAssets
 * then unload before the next screen (probably not necessary)
 */
public class AssetLoader {

    private static AssetManager manager;

    private static BitmapFont largeFont, smallFont;
    private static TextureAtlas ui, fishyAtlas;
    public static Skin uiSkin;
    public static TiledMap map;
    public static Sprite fish;
    public static Sprite fish01, fish02, fish03, fish04;
    public static Sprite bubble;
    public static Sprite skeleton1, skeleton2, skeleton3;
    public static ArrayList<Sprite> fishes = new ArrayList<Sprite>();
    public static Sound burp;




    public static void load() {
        debug("load");

        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        manager = new AssetManager();

        manager.load("ui.atlas", TextureAtlas.class);
        manager.load("pack.atlas", TextureAtlas.class);
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load("map.tmx", TiledMap.class);
        manager.load("burp.ogg", Sound.class);
    }

    public static boolean update() {
        debug("update");

        return manager.update();
    }

    public static void initAssets() {
        debug("initAssets");

        largeFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 10, Color.WHITE);
        smallFont = createFont("DroidSans.ttf", Gdx.graphics.getHeight() / 15, Color.WHITE);
        ui = manager.get("ui.atlas", TextureAtlas.class);
        uiSkin = new Skin(Gdx.files.internal("ui.json"));
        uiSkin.addRegions(ui);
        uiSkin.get("default", Label.LabelStyle.class).font = smallFont;

        fishyAtlas = manager.get("pack.atlas", TextureAtlas.class);

        map = manager.get("map.tmx", TiledMap.class);

        bubble = fishyAtlas.createSprite("fishTile_125");
        fish = fishyAtlas.createSprite("fishTile_075");
        fish01 = fishyAtlas.createSprite("fishTile_073");
        fish02 = fishyAtlas.createSprite("fishTile_077");
        fish03 = fishyAtlas.createSprite("fishTile_079");
        fish04 = fishyAtlas.createSprite("fishTile_081");
        fishes.add(fish01);
        fishes.add(fish02);
        fishes.add(fish03);
        fishes.add(fish04);

        TextureAtlas.AtlasRegion region;
        skeleton1 = fishyAtlas.createSprite("fishTile_092");
        skeleton2 = fishyAtlas.createSprite("fishTile_090");
        skeleton3 = fishyAtlas.createSprite("fishTile_096");

        burp = manager.get("burp.ogg", Sound.class);
    }

    //creates a freetype bitmap font
    private static BitmapFont createFont(String font, int size, Color color) {
        BitmapFont textFont;
        FileHandle fontFile = Gdx.files.internal(font);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        textFont = generator.generateFont(parameter);
        generator.dispose();
        return textFont;
    }

    //returns a float between 0 and 1. this doesnt work well for atlas's
    public static float getProgress() {
        return manager.getProgress();
    }

    //returns the number of assets left to load. this fluctuates?
    public static int getQueuedAssets() {
        return manager.getQueuedAssets();
    }

    //dispose all assets and manager
    public static void dispose() {
        manager.dispose();
    }

    //disposes all assets but does not dispose of the manager itself
    public static void clearManager() {
        manager.clear();
    }



    //method for writing to the log
    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            //Gdx.app.log("Asset Loader", message);
        }
    }
}
