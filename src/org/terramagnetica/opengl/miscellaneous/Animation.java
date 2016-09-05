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

package org.terramagnetica.opengl.miscellaneous;

import org.terramagnetica.opengl.engine.TextureQuad;

public interface Animation {
	
	/** D�marre l'animation. C'est � dire, l'animation passe d'un
	 * �tat fixe � un �tat mobile, sans �tre r�initialis�. */
	void start();
	
	/** Arr�te l'animation. Cela consiste tout simplement � la mettre
	 * en pause. */
	void stop();
	
	/** R�initialise l'animation. */
	void reset();
	TextureQuad get();
}
