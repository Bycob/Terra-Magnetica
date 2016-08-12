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

package org.terramagnetica.game.lvldefault;

import org.terramagnetica.game.InterruptionPhases;
import org.terramagnetica.game.lvldefault.DialogInGame.Statement;
import org.terramagnetica.game.lvldefault.rendering.CameraTrackPoint3D;
import org.terramagnetica.game.lvldefault.rendering.RenderElementDialog;

import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.physic.Trajectoire2D;
import net.bynaryscode.util.physic.TrajectoireDroite2DSmooth;

/** Définit plein de phases usuelles dans les interruptions par phase des niveaux
 * de type {@link LevelDefault}. */
public class InterruptionPhasesLvlDefault extends InterruptionPhases {
	

	public final PhaseScrolling scrollingToPlayer(GamePlayingDefault game) {
		PhaseScrolling scrolling = new PhaseScrolling(game.getPlayer().getCoordonnéesf(), game);
		scrolling.isDirectionPlayer = true;
		
		return scrolling;
	}
	
	
	
	
	
	public class PhaseScrolling extends Phase {
		
		private GamePlayingDefault game;
		private Trajectoire2D trajet;
		/** {@code true} si le scrolling revient au joueur à la fin de
		 * la phase. */
		private boolean isDirectionPlayer;
		
		private long duration = 1500l;
		
		public PhaseScrolling(Trajectoire2D trajet, GamePlayingDefault game) {
			// !!! ne jamais vérifier la nullité de ce paramètre (à cause du constructeur qui suit).
			this.trajet = trajet;
			this.game = game;
			this.isDirectionPlayer = false;
		}
		
		public PhaseScrolling(Vec2 direction, GamePlayingDefault game) {
			this((Trajectoire2D) null, game);
			
			Vec2d endPoint = direction.asDouble();
			this.trajet = new TrajectoireDroite2DSmooth(new Vec2d(), endPoint);
		}
		
		@Override
		public void update(long ms) {
			float timePercent = (float) ms / (float) duration();
			Vec2d c = this.trajet.getPointAt(timePercent);
			this.game.render.setCameraLooks(new CameraTrackPoint3D(c.x, c.y, 0));
		}
		
		@Override
		public long duration() {
			return this.duration;
		}
		
		public void setDuration(long d) {
			this.duration = d;
		}
		
		public PhaseScrolling withDuration(long d) {
			this.setDuration(d);
			return this;
		}
		
		@Override
		public void onStart() {
			CameraTrackPoint3D currentTrackPoint = this.game.render.getCameraTrackPoint();
			Vec2d startPoint = new Vec2d(currentTrackPoint.getX(), currentTrackPoint.getY());
			this.trajet.setStartPoint(startPoint);
		}
		
		@Override
		public void onEnd() {
			if (this.isDirectionPlayer) {
				this.game.render.setCameraLooks(this.game.getPlayer().getCameraTrackPoint());
			}
			else {
				this.game.render.setCameraLooks(new CameraTrackPoint3D(trajet.getEndPoint().x, trajet.getEndPoint().y, 0));
			}
		}
	}
	
	
	
	
	
	public class PhasePause extends Phase {
		
		private long duration;
		
		public PhasePause() {
			this(0);
		}
		
		public PhasePause(long duration) {
			this.duration = duration;
		}
		
		@Override
		public void update(long ms) {
			
		}

		@Override
		public long duration() {
			return this.duration;
		}
	}
	
	
	
	
	public class PhaseDialog extends Phase {
		
		private DialogInGame dialog;
		private RenderElementDialog dialogRenderer;
		
		public PhaseDialog(GamePlayingDefault game, DialogInGame dialog) {
			this.dialog = dialog;
			this.dialogRenderer = game.render.getRendering(RenderElementDialog.class);

			if (this.dialog != null) {
				this.dialog.begin();
				this.dialogRenderer.setStatement(this.dialog.getCurrentStatement());
			}
		}
		
		@Override
		public void update(long ms) {
			if (this.dialog != null) {
				if (this.dialog.hasNextStatement() && this.dialogRenderer.isOver()) {
					Statement next = this.dialog.nextStatement();
					this.dialogRenderer.setStatement(next);
				}
			}
		}

		@Override
		public long duration() {
			return this.dialog != null && (this.dialog.hasNextStatement() || !this.dialogRenderer.isOver()) ?
					-1 : 0;
		}
	}
	
	
	
	
	
	public class PhaseEnd extends Phase {
		
		private GamePlayingDefault game;
		
		public PhaseEnd(GamePlayingDefault game) {
			this.game = game;
		}
		
		@Override
		public void onStart() {
			game.setEnd();
		}
		
		@Override
		public void update(long ms) {}
	}
}
