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
import java.util.HashMap;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;

import net.bynaryscode.util.Predicate;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.path.Case;
import net.bynaryscode.util.path.Path;
import net.bynaryscode.util.path.Path.Node;

public class SensorPathScanner extends Sensor {
	
	private HashMap<Path, PathScannerInfo> infoMap = new HashMap<Path, PathScannerInfo>();
	private SpaceGraphLevelDefault spaceGraph = null;
	
	private Predicate<Entity> entitySelector = new DefaultSelector();
	
	/** Indique si les chemins disponibles sont vérifiés lors de la mise à jour
	 * du sensor. */
	private boolean precheck = false;
	
	@Override
	public void update(AIBase ai) {
		this.infoMap.clear();
		
		if (this.precheck) {
			AIMovable aiMove = ai.cast(AIMovable.class);
			EntityMoving ent = aiMove.getEntity();
			PathSeeker pathSeeker = aiMove.getPathSeeker();
			
			if (pathSeeker != null) {
				Path[] paths = pathSeeker.seekPaths(ent.getCoordonnéesCase());
				
				for (Path path : paths) {
					checkPath(path);
				}
			}
		}
	}
	
	@Override
	public void setGame(GamePlayingDefault game) {
		super.setGame(game);
		
		this.spaceGraph = new SpaceGraphLevelDefault(game);
	}
	
	/** Renvoie un objet {@link PathScannerInfo} correspondant au chemin passé en
	 * paramètres. Le {@link PathScannerInfo} estsauvegardé dans le scanner.
	 * Ainsi, si cette méthode est appelée plusieurs fois avec le même chemin pendant
	 * un même tour de jeu, alors le {@link PathScannerInfo} retourné est le même. */
	public PathScannerInfo checkPath(Path path) {
		PathScannerInfo result = this.infoMap.get(path);
		if (result != null) return result;
		
		result = checkPath0(path);
		
		this.infoMap.put(path, result);
		return result;
	}
	
	private PathScannerInfo checkPath0(Path path) {
		PathScannerInfo result = new PathScannerInfo();
		
		//Sauvegarde de l'indice de départ du chemin.
		int startIndex = path.getCurrentIndex();
		
		path.begin();
		
		//Parcours du chemin
		while (true) {
			
			Node current = path.getCurrent();
			Vec2i c = current.getPoint().asInteger();
			
			//Lorsque le chemin passe dans une salle, on scanne toutes les cases de la salle en faisant
			//la distinction : case du chemin / case en dehors du chemin.
			if (this.spaceGraph.isRoomCase(c)) {
				ArrayList<Case> roomCases = this.spaceGraph.getRoomCases(c);
				
				//On teste d'abord les cases du chemin.
				while (true) {
					Vec2i current2 = path.getCurrent().getPoint().asInteger();
					roomCases.remove(new Case(current2));
					addEntitiesOnCase(current2.x, current2.y, true, true, result);
					
					addChildren(path, result);
					
					if (path.hasNext() && this.spaceGraph.isRoomCase(path.getNext().getPoint().asInteger())) {
						path.next();
					}
					else {
						break;
					}
				}
				
				//Puis celles en dehors.
				for (Case roomCase : roomCases) {
					addEntitiesOnCase(roomCase.getLocation().x, roomCase.getLocation().y, false, true, result);
				}
			}
			//Cas basique : le chemin passe par une case, on scanne cette case.
			else {
				addEntitiesOnCase(c.x, c.y, true, false, result);
				addChildren(path, result);
			}
			
			//Incrémentation et condition d'arrêt.
			if (path.hasNext()) {
				path.next();
			}
			else {
				break;
			}
		}
		
		//Rétablissement de l'indice de départ du chemin
		path.goToIndex(startIndex);

		return result;
	}
	
	private void addChildren(Path path, PathScannerInfo info) {
		Node n = path.getCurrent();
		for (Path childrenPath : n.getCrossroads()) {
			info.children.add(checkPath0(childrenPath));
		}
	}
	
	
	private void addEntitiesOnCase(int cX, int cY, boolean onPath, boolean inRoom, PathScannerInfo info) {
		Entity[] entities = this.game.getEntitiesOnCase(cX, cY);
		
		for (Entity ent : entities) {
			if (this.entitySelector.test(ent)) {
				EntityInfo entInfo = new EntityInfo(ent);
				
				entInfo.isInRoom = inRoom;
				entInfo.isOnPath = onPath;
				
				info.entityFound.add(entInfo);
			}
		}
	}

	public PathScannerInfo[] checkPath(Path[] paths) {
		ArrayList<PathScannerInfo> result = new ArrayList<PathScannerInfo>();
		
		for (Path path : paths) {
			result.add(checkPath(path));
		}
		
		return result.toArray(new PathScannerInfo[0]);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SensorPathScanner clone() {
		SensorPathScanner clone = (SensorPathScanner) super.clone();
		
		clone.spaceGraph = this.spaceGraph == null ? null : this.spaceGraph.clone();
		clone.infoMap = (HashMap<Path, PathScannerInfo>) this.infoMap.clone();
		
		return clone;
	}

	public class PathScannerInfo {
		
		private Path path;
		
		private ArrayList<EntityInfo> entityFound = new ArrayList<EntityInfo>();
		private ArrayList<PathScannerInfo> children = new ArrayList<PathScannerInfo>();
		
		public PathScannerInfo() {
			this(null);
		}
		
		public PathScannerInfo(Path path) {
			this.path = path;
		}
		
		public EntityInfo[] getFoundEntities() {
			return this.entityFound.toArray(new EntityInfo[0]);
		}
		
		public PathScannerInfo[] getChildren() {
			return this.children.toArray(new PathScannerInfo[0]);
		}
	}
	
	public class EntityInfo {
		
		private Entity entity;
		
		private boolean isInRoom;
		private boolean isOnPath;
		
		public EntityInfo(Entity entity) {
			this.entity = entity;
		}
		
		public Entity getEntity() {
			return this.entity;
		}
		
		public boolean isInRoom() {
			return this.isInRoom;
		}
		
		public boolean isOnPath() {
			return this.isOnPath;
		}
	}
	
	public static class DefaultSelector implements Predicate<Entity> {
		
		@Override
		public boolean test(Entity e) {
			return true;
		}
	}
}
