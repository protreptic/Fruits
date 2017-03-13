package com.example.mobdev_3.fruits.herald;

public interface Herald {

    interface Builder {

        /**
         *
         * @return
         */
        Herald build();

    }

    /**
     *
     */
    void show();

    /**
     *
     * @return
     */
    boolean isShown();

    /**
     *
     */
    void hide();

}
