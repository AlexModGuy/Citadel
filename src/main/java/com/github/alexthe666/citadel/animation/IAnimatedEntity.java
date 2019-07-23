package com.github.alexthe666.citadel.animation;

/**
 * @author Alexthe666
 * @since 1.0.0
 */
public interface IAnimatedEntity {
    /**
     * An empty model container.
     */
    Animation NO_ANIMATION = Animation.create(0);

    /**
     * @return the current model tick
     */
    int getAnimationTick();

    /**
     * Sets the current model tick to the given value.
     *
     * @param tick the new tick
     */
    void setAnimationTick(int tick);

    /**
     * @return the current playing model
     */
    Animation getAnimation();

    /**
     * Sets the currently playing model.
     *
     * @param animation the new model
     */
    void setAnimation(Animation animation);

    /**
     * @return an array of all the Animations this entity can play
     */
    Animation[] getAnimations();
}