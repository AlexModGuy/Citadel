package com.github.alexthe666.citadel.animation;

/**
 * @author Alexthe666
 * @since 1.0.0
 */
public class Animation {
    @Deprecated
    private int id;
    private int duration;

    private Animation(int duration) {
        this.duration = duration;
    }

    /**
     * @param id       the model id
     * @param duration the model duration
     * @return an model with the given id and duration
     * @deprecated use {@link Animation#create(int)} instead.
     */
    @Deprecated
    public static Animation create(int id, int duration) {
        Animation animation = Animation.create(duration);
        animation.id = id;
        return animation;
    }

    /**
     * @param duration the model duration
     * @return an model with the given id and duration
     * @since 1.1.0
     */
    public static Animation create(int duration) {
        return new Animation(duration);
    }

    /**
     * @return the id of this model
     * @deprecated IDs aren't used anymore since 1.1.0.
     */
    @Deprecated
    public int getID() {
        return this.id;
    }

    /**
     * @return the duration of this model
     */
    public int getDuration() {
        return this.duration;
    }
}