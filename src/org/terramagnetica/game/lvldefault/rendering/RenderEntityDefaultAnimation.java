/* <LICENSE>
Copyright (C) 2013-2016 Louis JEAN

This file is part of Terra Magnetica.

Terra Magnetica is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Terra Magnetica is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.game.lvldefault.rendering;

import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;

public class RenderEntityDefaultAnimation extends RenderEntityDefault {
	
	private AnimationManager animation;
	
	private int state;
	private int defaultState;
	
	public RenderEntityDefaultAnimation() {
		this(new AnimationManager(), angleDefault);
	}
	
	public RenderEntityDefaultAnimation(AnimationManager animation) {
		this(animation, angleDefault);
	}
	
	public RenderEntityDefaultAnimation(AnimationManager animation, float radius) {
		super(radius);
		
		this.animation = animation;
		
		this.defaultState = this.animation.getFirstState();
		this.state = this.defaultState;
		
		this.animation.stop();
		this.animation.reset();
		this.texture = this.animation.get();
		
		this.calculVertices();
	}
	
	@Override
	protected void calculVertices() {
		if (!(this.animation == null) && !this.animation.areDimensionsFixes()) {
			this.defineDimensions();
		}
		super.calculVertices();
	}
	
	public AnimationManager getAnimationManager() {
		return this.animation;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
	
	@Override
	public void renderEntity3D(float x, float y, Painter painter) {
		updateTexture();
		super.renderEntity3D(x, y, painter);
	}
	
	protected void updateTexture() {
		this.texture = this.animation.get(this.state);
		
		if (!this.animation.areDimensionsFixes()) {
			this.calculVertices();
		}
	}
}
