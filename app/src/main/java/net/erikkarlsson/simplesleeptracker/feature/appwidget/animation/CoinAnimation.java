package net.erikkarlsson.simplesleeptracker.feature.appwidget.animation;

import android.graphics.Bitmap;

import net.erikkarlsson.simplesleeptracker.R;

public class CoinAnimation implements IAnimatable {
	private Sprite sp = MediaAssets.getInstance().getSprite(R.drawable.walk_6);
	private int currentSprite = 0;
	private int step;
	private float density;

	public CoinAnimation(float Density) {
		step = 10;
		currentSprite = 0;
		density = Density;
	}

	public boolean animationFinished() {
		return step <= 1;
	}

	public void draw(Bitmap canvas) {
		// Draw sprite
		SpriteHelper.DrawSprite(canvas, sp, sp.NextFrame(), SpriteHelper.DrawPosition.Center, 0, 0);
		step--;
		currentSprite++;
		currentSprite %= 4;
	}
}
