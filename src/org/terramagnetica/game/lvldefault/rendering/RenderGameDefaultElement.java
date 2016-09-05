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

package org.terramagnetica.game.lvldefault.rendering;

import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.Painter;

/**
 * Un élément de rendu du jeu. Cet objet dessine quelque chose
 * à l'écran pendant le jeu. Par exemple : le décor, les entités
 * mais aussi les indications à l'écran telles que la mini-map,
 * l'inventaire, etc...
 * @author Louis JEAN
 */
public abstract class RenderGameDefaultElement {
	
	protected GLConfiguration sharedConfiguration;
	
	protected GLConfiguration getDefault3DConfiguration() {
		return this.sharedConfiguration == null ? GLConfiguration.default3DConfiguration() : this.sharedConfiguration;
	}
	
	public abstract void render(GamePlayingDefault game, Painter painter);
}
