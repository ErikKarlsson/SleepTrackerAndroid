package net.erikkarlsson.simplesleeptracker.feature.appwidget.animation;

import android.graphics.Bitmap;

public interface IAnimatable {

	boolean animationFinished();

	void draw(Bitmap canvas);

}
