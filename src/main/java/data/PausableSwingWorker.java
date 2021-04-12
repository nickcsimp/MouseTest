package data;

import javax.swing.*;

abstract class PausableSwingWorker<K, V> extends SwingWorker<K, V> {

    private volatile boolean isPaused; // The Java volatile keyword guarantees visibility of changes to variables across threads

    public final void pause() {
        if (!isPaused() && !isDone()) {
            isPaused = true;
        }
    }

    public final void resume() {
        if (isPaused() && !isDone()) {
            isPaused = false;
        }
    }

    public final boolean isPaused() {
        return isPaused;
    }
}