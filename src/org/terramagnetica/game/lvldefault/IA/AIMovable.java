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
along with Terra Magnetica. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.game.lvldefault.IA;

import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;

import net.bynaryscode.util.path.Path;

public abstract class AIMovable<T extends EntityMoving> extends AIBase {
	
	protected T entity;
	
	protected Path path = null;
	protected PathSeeker mainPathSeeker;
	
	
	public AIMovable(T entity) {
		setEntity(entity);
	}
	
	@Override
	public void setGame(GamePlayingDefault game) {
		super.setGame(game);
		
		if (this.mainPathSeeker == null) this.mainPathSeeker = createPathSeeker(game);
		else this.mainPathSeeker.setGame(game);
	}
	
	public void setEntity(T entity) {
		if (entity == null) throw new NullPointerException("Impossible de créer l'intelligence artificielle");
		this.entity = entity;
	}
	
	/** Cette méthode permet de définir les paramètres du PathSeeker principal à sa
	 * création. */
	protected PathSeeker createPathSeeker(GamePlayingDefault game) {
		return new PathSeeker(game);
	}
	
	public PathSeeker getPathSeeker() {
		return this.mainPathSeeker;
	}
	
	/** Adapte le {@link PathSeeker} pour qu'il puisse être utilisé sans problème
	 * avec cette intelligence artificielle. */
	public void bindPathSeeker(PathSeeker pathSeeker) {
		if (pathSeeker != null && pathSeeker.getGame() != this.getGame()) {
			pathSeeker.setGame(this.getGame());
		}
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	/** Donne le chemin que l'entité suit. Si l'entité ne suit aucun
	 * chemin, retourne {@code null}. */
	public Path getPath() {
		return this.path;
	}
	
	public T getEntity() {
		return this.entity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AIMovable<T> clone() {
		AIMovable<T> clone = (AIMovable<T>) super.clone();
		
		clone.path = this.path == null ? null : this.path.clone();
		
		return clone;
	}
}
