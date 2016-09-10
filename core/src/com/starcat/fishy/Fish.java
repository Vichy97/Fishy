package com.starcat.fishy;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Vincent on 9/2/2016.
 */
public class Fish implements Pool.Poolable {

    protected Body body;
    protected Sprite sprite;
    protected Vector2 speed;
    protected float floatSpeed = 0;
    protected int currentRotation = 0;
    protected boolean alive;
    protected float size = 1;



    public Fish() {
        speed = new Vector2();
    }



    public void init(Sprite sprite, Body body) {
        this.sprite = sprite;
        this.body = body;

        body.setUserData(this);
        sprite.setCenter((int)body.getPosition().x, (int)body.getPosition().y);
        body.setActive(true);
    }

    public void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public void update() {
        sprite.setCenter(body.getPosition().x, body.getPosition().y);
        if (currentRotation > 90 || currentRotation < -90) {
            sprite.setFlip(sprite.isFlipX(), true);
        } else {
            sprite.setFlip(sprite.isFlipX(), false);
        }
    }



    public synchronized Fish setSpeed(float speed) {
        this.floatSpeed = speed;
        this.speed.set(speed, 0).rotate(currentRotation);
        body.setLinearVelocity(this.speed);
        return this;
    }

    public synchronized Fish setPosition(float x, float y) {
        body.setTransform(x, y, 0);
        sprite.setCenter(body.getPosition().x, body.getPosition().y);
        return this;
    }

    public synchronized Fish setRotation(float rotation) {
        sprite.setRotation(rotation);
        currentRotation = (int)rotation;
        return this;
    }

    public synchronized Fish setSize(float size) {
        this.size = size;
        sprite.setScale(size * MyGdxGame.UNIT_SCALE);
        body.getFixtureList().get(0).getShape().setRadius(sprite.getHeight() * sprite.getScaleX() / 4);
        return this;
    }

    public synchronized Fish setAlive(boolean alive) {
        this.alive = alive;
        return this;
    }

    public synchronized Fish setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setCenter(getPosition().x, getPosition().y);
        return this;
    }



    public synchronized boolean isAlive() {
        return alive;
    }

    public float getRotation() {
        return currentRotation;
    }

    public float getSize() {
        return size;
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition()  {
        return body.getTransform().getPosition();
    }

    public float getFloatSpeed() {
        return floatSpeed;
    }




    @Override
    public void reset() {
        setSpeed(0);
        setAlive(false);
        body.setActive(false);
        currentRotation = 0;
        size = 1;
    }
}
