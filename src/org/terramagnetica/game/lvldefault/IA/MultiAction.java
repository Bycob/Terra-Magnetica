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

package org.terramagnetica.game.lvldefault.IA;

import java.util.ArrayList;

import net.bynaryscode.util.Util;

public class MultiAction extends Action {

	private ArrayList<Action> actions = new ArrayList<Action>();
	
	public void addActions(Action... actions) {
		Util.addAll(actions, this.actions, false);
	}
	
	@Override
	public void execute(AIBase ai) {
		for (Action action : this.actions) {
			action.execute(ai);
		}
	}
	
	@Override
	public MultiAction clone() {
		MultiAction clone = (MultiAction) super.clone();
		
		clone.actions = new ArrayList<Action>(this.actions.size());
		for (Action action : this.actions) {
			clone.actions.add(action.clone());
		}
		
		return clone;
	}
}
