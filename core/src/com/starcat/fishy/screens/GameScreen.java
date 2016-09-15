package com.starcat.fishy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.starcat.fishy.AssetLoader;
import com.starcat.fishy.CollisionFlags;
import com.starcat.fishy.ContactListener;
import com.starcat.fishy.CustomMapRenderer;
import com.starcat.fishy.Fish;
import com.starcat.fishy.GameUtils;
import com.starcat.fishy.MyGdxGame;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vincent on 6/19/2015.
 */
public class GameScreen implements Screen, InputProcessor {

    private boolean paused = false;

    public boolean leftPressed, rightPressed, upPressed, downPressed = false;

    private MyGdxGame game;

    private OrthographicCamera gameCamera, uiCamera;
    private Viewport gameViewport, uiViewport;
    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private Table table;

    private Label stateLabel;
    private StringBuilder stringBuilder;

    private Touchpad touchpad;

    private SpriteBatch spriteBatch;

    //private FPSLogger fpsLogger;

    private OrthogonalTiledMapRenderer renderer;
    private ShaderProgram shader;
    float angleWave = 0;
    float angleWaveSpeed = 1;
    private float amplitudeWave = .2f;

    private int STATE = 1;
    private static int SCORE = 0;
    private Vector2 tempVector2;
    private InputEvent fakeTouchEvent;
    private World world;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private Box2DDebugRenderer debugRenderer;

    private Fish fish;
    private ArrayList<Fish> enemyFish;
    private ArrayList<Sprite> bubbles;
    private Timer timer;
    private Pool<Fish> fishPool = new Pool<Fish>() {
        @Override
        protected Fish newObject() {
            Fish fish = new Fish();
            return fish;
        }
    };
    private Pool<Sprite> bubblePool = new Pool<Sprite>() {
        @Override
        protected Sprite newObject() {
            Sprite bubble = new Sprite(AssetLoader.bubble);
            tempVector2.set((fish.getWidth() / 2 * MyGdxGame.UNIT_SCALE * fish.getSize()), 0);
            tempVector2.rotate(fish.getRotation());
            bubble.setCenter(fish.getPosition().x + tempVector2.x, fish.getPosition().y + tempVector2.y);
            bubble.setScale(MathUtils.random(.02f, .04f) * fish.getSize());
            return bubble;
        }
    };

    public GameScreen(MyGdxGame game, OrthographicCamera gameCamera, Viewport gameViewport, OrthographicCamera uiCamera, Viewport uiViewport) {
        debug("constructor");
        
        this.game = game;
        this.gameCamera = gameCamera;
        this.gameViewport = gameViewport;
        this.uiCamera = uiCamera;
        this.uiViewport = uiViewport;

        Gdx.input.setCatchBackKey(true);

        gameCamera.setToOrtho(false, 16, 9);
        gameViewport.apply();
        gameCamera.update();

        tempVector2 = new Vector2();
        fakeTouchEvent = new InputEvent();
        fakeTouchEvent.setType(InputEvent.Type.touchDown);

        uiViewport.apply();

        initUI();
        initPhysicsWorld();

        gameViewport.apply();
        gameCamera.update();

        renderer = new CustomMapRenderer(AssetLoader.map, MyGdxGame.UNIT_SCALE);

        enemyFish = new ArrayList<Fish>();
        bubbles = new ArrayList<Sprite>();

        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (STATE == 2) {
                    Fish fish = spawnEnemyFish();
                    fish.setPosition(-fish.getWidth() * 2 * MyGdxGame.UNIT_SCALE, MathUtils.random((MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE * 2/9) + (fish.getHeight() * MyGdxGame.UNIT_SCALE), MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE));
                    fish.setSize(MathUtils.random(.1f, 3f)).setSpeed(MathUtils.random(5, 13));
                    fish.setAlive(true);
                    enemyFish.add(fish);
                }
            }
        }, 0, 1.3f);

        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (STATE == 2) {
                    Fish fish = spawnEnemyFish();
                    fish.getSprite().flip(true, false);
                    fish.setPosition((MyGdxGame.GAME_WIDTH * MyGdxGame.UNIT_SCALE) + (fish.getWidth() * 2 * MyGdxGame.UNIT_SCALE), MathUtils.random((MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE * 2/9) + (fish.getHeight() * MyGdxGame.UNIT_SCALE), MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE));
                    fish.setSize(MathUtils.random(.1f, 3f)).setSpeed(-MathUtils.random(5, 13));
                    fish.setAlive(true);
                    enemyFish.add(fish);
                }
            }
        }, 0, 1.3f);

        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (STATE == 2) {
                    if (fish != null) {
                        bubbles.add(bubblePool.obtain());
                    }
                }
            }
        }, 0, .7f);

        //fpsLogger = new FPSLogger();

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(gameCamera.combined);
        stringBuilder = new StringBuilder();
    }



    @Override
    public void show() {
        debug("show");
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(176f / 255f, 233f / 255f, 250f / 252f, 1);

        gameViewport.apply();
        gameCamera.update();

        renderer.setView(gameCamera);
        renderer.render(new int[] {0, 1});

        switch (STATE) {
            case 1: {
                break;
            } case 2: {
                if (!fish.isAlive()) {
                    setState(3);
                }

                if (fish.getSize() >= 10) {
                    setState(4);
                }

                if (touchpad.getKnobPercentY() != 0 || touchpad.getKnobPercentX() != 0) {
                    float angle = GameUtils.getTouchpadAngle(touchpad.getKnobPercentX(), touchpad.getKnobPercentY());
                    if (angle > -180 && angle < 180) {
                        fish.setRotation(angle);
                    }
                    if (fish.getFloatSpeed() < 15) {
                        fish.setSpeed(fish.getFloatSpeed() + .3f);
                    } else {
                        fish.setSpeed(fish.getFloatSpeed());
                    }
                    fish.update();
                } else {
                    if (fish.getFloatSpeed() > 0) {
                        fish.setSpeed(fish.getFloatSpeed() - .4f);
                    } else {
                        fish.setSpeed(fish.getFloatSpeed());
                    }
                    if (fish.getFloatSpeed() < 0) {
                        fish.setSpeed(0);
                    }
                    fish.update();
                }

                int angle = -1;
                if (upPressed) {
                    if (leftPressed) {
                        angle = 135;
                    } else if (rightPressed) {
                        angle = 45;
                    } else {
                        angle = 90;
                    }
                } else if (downPressed) {
                    if (leftPressed) {
                        angle = 225;
                    } else if (rightPressed) {
                        angle = 315;
                    } else {
                        angle = 270;
                    }
                } else if (leftPressed) {
                    angle = 180;
                } else if (rightPressed) {
                    angle = 0;
                }

                if (angle != -1) {
                    fish.setSpeed(15);
                    fish.setRotation(angle);
                }

                spriteBatch.setProjectionMatrix(gameCamera.combined);
                spriteBatch.begin();
                fish.render(spriteBatch);
                for (Fish fish : enemyFish) {
                    fish.update();
                    fish.render(spriteBatch);

                }
                Iterator<Sprite> iterator = bubbles.iterator();
                while(iterator.hasNext()) {
                    Sprite bubble = iterator.next();
                    bubble.draw(spriteBatch);
                    bubble.setPosition(bubble.getX(), bubble.getY() + 1.5f * MyGdxGame.UNIT_SCALE);
                    if (bubble.getColor().a > 0) {
                        bubble.setAlpha(bubble.getColor().a - .005f);
                    }
                    if (bubble.getColor().a <= 0) {
                        bubble.setAlpha(.9f);
                        iterator.remove();
                    }
                }
                spriteBatch.end();

                //debugRenderer.render(world, gameCamera.combined);
                doPhysicsStep(delta);

                break;
            } case 3: {
                break;
            }
        }

        renderer.render(new int[] {2, 3});

        drawScore();

        Iterator<Fish> iterator = enemyFish.iterator();
        while (iterator.hasNext()) {
            Fish fish = iterator.next();
            if (!fish.isAlive()) {
                iterator.remove();
                fish.getBody().setActive(false);
                //fishPool.free(fish);
            }
        }

        uiViewport.apply();
        uiCamera.update();

        if (!touchpad.isTouched()) {
            touchpad.setVisible(false);
        }

        stage.act();
        stage.draw();

        //fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
        debug("resize");

        gameViewport.update(width, height);
        gameViewport.apply();
        uiViewport.update(width, height);
        uiCamera.position.set(uiCamera.viewportWidth / 2, uiCamera.viewportHeight / 2, 0);
        gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);
    }

    @Override
    public void pause() {
        debug("pause");

        paused = true;
    }

    @Override
    public void resume() {
        debug("resume");
    }

    @Override
    public void hide() {
        debug("hide");

        dispose();
    }

    @Override
    public void dispose() {
        debug("dispose");

        spriteBatch.dispose();
        stage.dispose();

        System.gc();
    }

    private void initUI() {
        debug("initUI");

        touchpad = new Touchpad(0, AssetLoader.uiSkin);
        touchpad.setVisible(false);
        stateLabel = new Label("Touch To Start", AssetLoader.uiSkin);

        table = new Table();
        table.setFillParent(true);
        table.add(stateLabel);

        stage = new Stage(uiViewport);
        stage.addActor(table);
        stage.addActor(touchpad);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initPhysicsWorld() {
        world = new World(new Vector2(0, 0), false);
        world.setContactListener(new ContactListener());
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        debugRenderer = new Box2DDebugRenderer();

        ChainShape shape = new ChainShape();
        shape.createChain(new float[] {0, MyGdxGame.GAME_HEIGHT * 2/9 * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_WIDTH * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_HEIGHT * 2/9 * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_WIDTH * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE, 0, MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE, 0, 0});
        fixtureDef.shape = shape;

        bodyDef.type = BodyDef.BodyType.StaticBody;
        fixtureDef.filter.categoryBits = CollisionFlags.GROUND_FLAG;
        fixtureDef.filter.maskBits = CollisionFlags.GROUND_MASK;
        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        fixtureDef.shape = new CircleShape();

    }

    private Fish spawnFish() {
        Fish fish = new Fish();

        if (fish.getBody() == null) {
            Body body = world.createBody(bodyDef);
            body.setActive(false);
            fixtureDef.filter.categoryBits = CollisionFlags.FISH_FLAG;
            fixtureDef.filter.maskBits = CollisionFlags.FISH_MASK;
            body.createFixture(fixtureDef);
            fish.init(AssetLoader.fish, body);
        } else {
            fish.init(AssetLoader.fish, fish.getBody());
        }
        fish.setPosition(MyGdxGame.GAME_WIDTH / 2 * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_HEIGHT / 2 * MyGdxGame.UNIT_SCALE);
        fish.setSize(.5f);
        fish.setAlive(true);
        return fish;
    }

    private Fish spawnEnemyFish() {
        Fish fish = fishPool.obtain();
        Sprite sprite = new Sprite(AssetLoader.fishes.get(MathUtils.random(0, 3)));

        if (fish.getBody() == null) {
            Body body = world.createBody(bodyDef);
            body.setActive(false);
            fixtureDef.filter.categoryBits = CollisionFlags.ENEMY_FISH_FLAG;
            fixtureDef.filter.maskBits = CollisionFlags.ENEMY_FISH_MASK;
            body.createFixture(fixtureDef);
            fish.init(sprite, body);
        } else {
            fish.init(sprite, fish.getBody());
        }
        return fish;
    }

    private void setState(int STATE) {
        this.STATE = STATE;

        switch(STATE) {
            case 1: {
                SCORE = 0;
                touchpad.setVisible(false);
                stateLabel.setVisible(true);
                stateLabel.setText("Touch To Start");
                break;
            } case 2: {
                if (fish == null) {
                    fish = spawnFish();
                } else {
                    fish.setPosition(MyGdxGame.GAME_WIDTH / 2 * MyGdxGame.UNIT_SCALE, MyGdxGame.GAME_HEIGHT / 2 * MyGdxGame.UNIT_SCALE);
                    fish.setSpeed(0);
                    fish.setSize(.5f);
                    fish.setAlive(true);
                }
                stateLabel.setVisible(false);
                break;
            } case 3: {
                for (int i = 0; i < enemyFish.size(); i++) {
                    fishPool.free(enemyFish.get(i));
                }
                enemyFish.clear();
                SCORE -=1;
                touchpad.setVisible(false);
                stateLabel.setVisible(true);
                stateLabel.setText("Dead :(");
                System.gc();
                break;
            } case 4: {
                touchpad.setVisible(false);
                for (int i = 0; i < enemyFish.size(); i++) {
                    fishPool.free(enemyFish.get(i));
                }
                enemyFish.clear();
                stateLabel.setVisible(true);
                stateLabel.setText("          You Ate All The Fish \n And Destroyed The Ecosystem");
                break;
            }
        }
    }

    private void drawScore() {
        float x = 0;
        float y = (MyGdxGame.GAME_HEIGHT * MyGdxGame.UNIT_SCALE) - (AssetLoader.skeleton1.getHeight() * MyGdxGame.UNIT_SCALE);

        spriteBatch.begin();
        for (int i = 0; i < (SCORE / 25); i++) {
            spriteBatch.draw(AssetLoader.skeleton3, x, y, AssetLoader.skeleton1.getWidth() * MyGdxGame.UNIT_SCALE, AssetLoader.skeleton1.getHeight() * MyGdxGame.UNIT_SCALE);
            x += 120 * MyGdxGame.UNIT_SCALE;
        }

        if (SCORE / 25 > 0) {
            y -= 100 * MyGdxGame.UNIT_SCALE;
            x = 0;
        }

        for (int i = 0; i < ((SCORE % 25) / 5); i++) {
            spriteBatch.draw(AssetLoader.skeleton2, x, y, AssetLoader.skeleton1.getWidth() * MyGdxGame.UNIT_SCALE, AssetLoader.skeleton1.getHeight() * MyGdxGame.UNIT_SCALE);
            x += 120 * MyGdxGame.UNIT_SCALE;
        }

        if ((SCORE % 25) / 5 > 0) {
            y -= 100 * MyGdxGame.UNIT_SCALE;
            x = 0;
        }

        for (int i = 0; i < ((SCORE % 25) % 5); i++){
            spriteBatch.draw(AssetLoader.skeleton1, x, y, AssetLoader.skeleton1.getWidth() * MyGdxGame.UNIT_SCALE, AssetLoader.skeleton1.getHeight() * MyGdxGame.UNIT_SCALE);
            x += 120 * MyGdxGame.UNIT_SCALE;
        }
        spriteBatch.end();
    }

    private float accumulator = 0;
    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }
    }

    public static void incrementScore() {
        SCORE++;
    }

    private static void debug(String message) {
        if (MyGdxGame.DEBUG) {
            //Gdx.app.log("Game Screen", message);
        }
    }



    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            SCORE = 0;
            AssetLoader.dispose();
            Gdx.app.exit();
            System.gc();
        } else if (keycode == Input.Keys.UP) {
            upPressed = true;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = true;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = true;
        } else if (keycode == Input.Keys.SPACE) {
            GameUtils.takeScreenshot();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP) {
            upPressed = false;
        } else if (keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        } else if (keycode == Input.Keys.DOWN) {
            downPressed = false;
        } else if (keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (STATE) {
            case 1: {
                AssetLoader.click.play();
                setState(2);
                break;
            } case 2: {
                tempVector2.set(screenX, screenY);
                tempVector2 = stage.screenToStageCoordinates(tempVector2);
                touchpad.setPosition(tempVector2.x - (touchpad.getWidth() / 2), tempVector2.y  - (touchpad.getHeight() / 2));
                touchpad.setVisible(true);
                touchpad.fire(fakeTouchEvent);
                break;
            } case 3: {
                AssetLoader.click.play();
                setState(1);
                break;
            }case 4: {
                AssetLoader.click.play();
                setState(1);
                break;
            }
        }
        return  true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchpad.setVisible(false);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}