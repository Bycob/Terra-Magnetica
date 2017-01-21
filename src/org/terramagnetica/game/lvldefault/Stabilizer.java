package org.terramagnetica.game.lvldefault;

import org.terramagnetica.physics.Force;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;

public class Stabilizer {
	public interface StabilizerFunc {
		/** Indique la position voulue de l'entit� pass�e en param�tre, au bout de deux
		 * tours de jeu.
		 * @param dt Le temps d'un tick, en ms. */
		StabilizerData getNextNextPosition(Entity ent, GamePlayingDefault game, float dt);
	}
	
	public final static class StabilizerData {
		private Vec2f nextnextPos;
		private Vec2f nextSpeed;
		
		public StabilizerData(Vec2f nextnextPos, Vec2f nextSpeed) {
			this.nextnextPos = nextnextPos;
			this.nextSpeed = nextSpeed;
		}
	}
	
	public static final float DEFAULT_MAX_FORCE = 40;
	
	private StabilizerFunc func = null;
	private float maxForce = DEFAULT_MAX_FORCE;
	
	public Stabilizer(StabilizerFunc func) {
		setStabilizerFunc(func);
	}
	
	public void setStabilizerFunc(StabilizerFunc func) {
		this.func = func;
	}
	
	public StabilizerFunc getStabilizerFunc() {
		return this.func;
	}
	
	public void setMaxForce(float maxForce) {
		this.maxForce = maxForce;
	}
	
	public float getMaxForce() {
		return this.maxForce;
	}
	
	/** Donne une force � l'entit� pour la stabiliser sur une certaine trajectoire que l'on
	 * veut qu'elle suive. Les mouvements de l'entit� ne sont plus d�finis par sa vitesse, 
	 * mais par l'endroit o� l'on veut qu'elle aille. De plus, cette m�thode produira un
	 * r�sultat physiquement r�aliste : l'entit� aura tendance � se diriger vers son point
	 * stable, mais ne s'y t�l�portera pas. 
	 * @param dt Le temps d'un tick, en ms. */
	public void stabilize(Entity ent, GamePlayingDefault game, float dt) {
		if (this.func == null || ent.getHitbox().isStatic()) {
			System.err.println("Useless stabilizer : no function or entity is static");
			//Thread.dumpStack();
			return;
		}
		if (dt == 0) return;
		
		float t = dt / 1000;
		StabilizerData data = this.func.getNextNextPosition(ent, game, dt);
		
		Vec2f currentPos = ent.getPositionf();
		Vec2f nextnextPos = data.nextnextPos;
		Vec2f speed = ent.getHitbox().getSpeed();
		Vec2f nextSpeed = data.nextSpeed;
		
		// Calcul de l'acceleration n�cessaire pour atteindre le point
		float axp = (nextnextPos.x - currentPos.x - 2 * speed.x * t) / (t * t);
		float ayp = (nextnextPos.y - currentPos.y - 2 * speed.y * t) / (t * t);
		
		// Calcul de l'acceleration n�cessaire pour atteindre la vitesse
		float axv = (nextSpeed.x - speed.x) / t;
		float ayv = (nextSpeed.y - speed.y) / t;
		
		// Pond�ration
		float coefp = (float) (MathUtil.getLength(nextnextPos, currentPos) / 2);
		float coefv = (float) (MathUtil.getLength(nextSpeed, speed));
		float sum = coefp + coefv;
		
		float ax = (axp * coefp + axv * coefv) / sum;
		float ay = (ayp * coefp + ayv * coefv) / sum;
		
		// On plafonne l'acceleration pour ne pas avoir un effet trop instantan�
		float squaredLength = ax * ax + ay * ay;
		
		if (squaredLength > this.maxForce * this.maxForce) {
			float length = (float) Math.sqrt(squaredLength);
			ax /= length;
			ay /= length;
		}
		
		// Application de la force � l'entit�
		ent.getHitbox().addForce(new Force(ax, ay));
	}
}
