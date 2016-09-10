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

package org.terramagnetica.game.lvldefault;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityCompound;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.Circle;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2f;

/**
 * Un générateur de champs. Les générateurs sont des engins assez grands
 * (deux case sur deux cases) avec un nombre plus ou moins grand de petits
 * cristaux électrifiés tournant autour, à une vitesse variable.
 * <p>Deux cas peuvent alors se présenter :
 * <ul><li>La rotation est lente. Pour passer, le joueur doit suivre le
 * mouvement.
 * <li>La rotation est rapide, le joueur doit éviter le générateur à tout
 * prix.
 * </ul>
 * @author Louis JEAN
 *
 */
public class MagneticFieldGenerator extends CaseEntity {

	private static final long serialVersionUID = 1L;
	
	public static final float DEFAULT_ROTATE_SPEED = 10f;
	public static final int DEFAULT_MINI_CRYSTAL_COUNT = 5;
	
	private List<MiniElectrocrystal> cristaux = new ArrayList<MiniElectrocrystal>();
	private List<Float> rayons = new ArrayList<Float>();
	private float crystalSpeed = DEFAULT_ROTATE_SPEED;
	
	public MagneticFieldGenerator() {
		this(DEFAULT_MINI_CRYSTAL_COUNT);
	}
	
	public MagneticFieldGenerator(int count) {
		this(0, 0, count);
	}
	
	public MagneticFieldGenerator(float x, float y) {
		this(x, y, DEFAULT_MINI_CRYSTAL_COUNT);
	}
	
	public MagneticFieldGenerator(int x, int y) {
		this(x, y, DEFAULT_MINI_CRYSTAL_COUNT);
	}
	
	public MagneticFieldGenerator(float x, float y, int count) {
		this.setPositionf(x, y);
		this.generateCrystals(count, 2.5f);
	}
	
	public MagneticFieldGenerator(int x, int y, int count) {
		super(x, y);
		this.generateCrystals(count, 2.5f);
	}
	
	private void generateCrystals(int count, float rayon) {
		this.cristaux.clear();
		this.rayons.clear();
		
		Vec2f centre = this.getPositionf();
		
		Circle c = new Circle(centre.x, centre.y, rayon);
		c.setNbSommets(count);
		Vec2d[] sommets = c.getSommets();
		for (Vec2d c0 : sommets) {
			this.cristaux.add(new MiniElectrocrystal((float) c0.x, (float) c0.y));
			this.rayons.add(rayon);
		}
	}
	
	@Override
	public void setPositionf(float x, float y) {
		Vec2f oldCentre = this.getPositionf();
		super.setPositionf(x, y);
		Vec2f newCentre = this.getPositionf();
		
		if (this.cristaux != null) {
			for (MiniElectrocrystal c : this.cristaux) {
				
				float difX = newCentre.x - oldCentre.x;
				float difY = newCentre.y - oldCentre.y;
				
				Vec2f cf = c.getPositionf();
				c.setPositionf(cf.x + difX, cf.y + difY);
			}
		}
	}
	
	/**
	 * Définit la vitesse de rotation des cristaux autour du générateur
	 * en nombre de tours par minute.
	 * @param speed - la vitesse de rotation en nombre de tours par
	 * minutes. Le signe détermine le sens de rotation : positif pour
	 * une rotation dans le sens inverse des aiguilles d'une montre et négatif
	 * pour une rotation dans l'autre sens.
	 */
	public void setRotationSpeed(float speed) {
		this.crystalSpeed = speed;
	}
	
	/**
	 * @return La vitesse de rotation des cristaux autour du générateur en
	 * tour par minutes. Le signe détermine le sens de rotation : positif
	 * pour le sens inverse des aiguilles d'une montre, négatif pour le sens
	 * des auguilles d'une montre.
	 */
	public float getRotationSpeed() {
		return this.crystalSpeed;
	}
	
	/**
	 * @return La vitesse de rotation des cristaux autour du générateur en
	 * radians par secondes (utilisé dans les calculs).
	 */
	public float getRadiusSpeed() {
		return this.crystalSpeed / 60f * 2 * (float) Math.PI;
	}
	
	public static BufferedImage generatorImage = null;
	
	@Override
	public Image getImage() {
		if (generatorImage == null) {
			generatorImage = new BufferedImage(CASE * 5, CASE * 5, BufferedImage.TYPE_INT_ARGB);
			int resultHwidth = (int) (generatorImage.getWidth() / 2f);
			int resultHHeight = (int) (generatorImage.getHeight() / 2f);
			
			Image entityRender = ImagesLoader.get(GameRessources.ID_GENERATOR);
			Image rangeRender = ImagesLoader.get(GameRessources.ID_MAP_GENERATOR);
			
			if (entityRender == null || rangeRender == null) return null;
			
			int entityHWidth = (int) (entityRender.getWidth(null) / 2f);
			int entityHHeight = (int) (entityRender.getHeight(null) / 2f);
			
			Graphics2D g2D = (Graphics2D) generatorImage.getGraphics();
			g2D.drawImage(rangeRender, 0, 0, generatorImage.getWidth(), generatorImage.getHeight(), null);
			g2D.drawImage(entityRender, resultHwidth - entityHWidth, resultHHeight - entityHHeight,
					entityRender.getWidth(null), entityRender.getHeight(null), null);
			g2D.dispose();
			
		}
		return generatorImage;
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(6 * CASE, 6 * CASE);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.ID_MAP_GENERATOR);
	}
	
	@Override
	public void createRender() {
		this.renderManager.putRender("default", new RenderEntityTexture(GameRessources.ID_GENERATOR, (float) (Math.PI / 6)).withPositionOffset(0, 0.6f, 0));
		
		//Cristaux
		RenderEntityCompound crystalsRender = new RenderEntityCompound();
		for (MiniElectrocrystal e : this.cristaux) {
			crystalsRender.addEntityToRender(e);
		}
		this.renderManager.putRender("crystals", crystalsRender);
		this.renderManager.setEffect("crystals", true);
	}
	
	@Override
	public void reloadRender() {
		super.reloadRender();
		for (MiniElectrocrystal crystal : this.cristaux) {
			crystal.reloadRender();
		}
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(512, 512);
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(1);
	}
	
	@Override
	public MagneticFieldGenerator clone() {
		MagneticFieldGenerator clone = (MagneticFieldGenerator) super.clone();
		
		clone.rayons = new ArrayList<Float>(this.rayons.size());
		for (Float f : this.rayons) {
			clone.rayons.add(f.floatValue());
		}
		
		clone.cristaux = new ArrayList<MiniElectrocrystal>(this.cristaux.size());
		for (MiniElectrocrystal e : this.cristaux) {
			clone.cristaux.add((MiniElectrocrystal) e.clone());
		}
		
		return clone;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		
		int i = 0;
		
		for (MiniElectrocrystal crystal : this.cristaux) {
			MagneticFieldUtil.rotate(crystal, this.getPositionf(), this.rayons.get(i), getRadiusSpeed(), dT);
			crystal.updateLogic(dT, game);
			i++;
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeFloatField(this.crystalSpeed, 200);
		out.writeArrayField(this.cristaux.toArray(new MiniElectrocrystal[0]), 201);
		out.writeArrayField(this.rayons.toArray(new Float[0]), 202);
	}
	
	@Override
	public MagneticFieldGenerator decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.crystalSpeed = in.readFloatField(200);
		
		try {
			this.cristaux = new ArrayList<MiniElectrocrystal>(in.readArrayFieldLength(MiniElectrocrystal.class, 201));
			in.readListField(this.cristaux, 201);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.rayons = new ArrayList<Float>(this.cristaux.size());
			in.readListField(this.rayons, 202);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
}
