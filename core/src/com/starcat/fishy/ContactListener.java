package com.starcat.fishy;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.starcat.fishy.screens.GameScreen;

/**
 * Created by Vincent on 9/4/2016.
 */
public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    @Override
    public void beginContact(Contact contact) {

        if (contact.getFixtureA().getBody().getUserData() instanceof Fish &&
                contact.getFixtureB().getBody().getUserData() instanceof Fish) {
            Fish fish1 = (Fish) (contact.getFixtureA().getBody().getUserData());
            Fish fish2 = (Fish) (contact.getFixtureB().getBody().getUserData());

            //Gdx.app.log("fish", "collision");

            GameScreen.incrementScore();

            if (fish1.getSize() > fish2.getSize()) {
                fish2.setAlive(false);
                if (fish2.getSize() < 1) {
                    fish1.setSize(fish1.getSize() + .015f);
                } else {
                    fish1.setSize(fish1.getSize() + fish2.getSize() * .015f);
                }
            } else {
                fish1.setAlive(false);
                if (fish1.getSize() < 1) {
                    fish2.setSize(fish2.getSize() + .015f);
                } else {
                    fish2.setSize(fish2.getSize() + fish1.getSize() * .015f);
                }
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
