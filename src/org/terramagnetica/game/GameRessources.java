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

package org.terramagnetica.game;

import static org.terramagnetica.game.lvldefault.PlayerStates.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.WallTile;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.SoundSet;
import org.terramagnetica.ressources.TextureSet;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Util;

public class GameRessources {
	
	/* **************************************************
	 * SOMMAIRE :
	 * 
	 * I - sets de textures de base
	 * II - sets de textures - interface graphique
	 * III - sets de textures - décors
	 * IV - sets de textures - niveaux
	 * V - sets de modèles - décors
	 * VI - sets de modèles - niveaux
	 * VII - set de son général
	 * 
	 * Les identifiants sont rangés par ordre alphabétiques.
	 ****************************************************/
	
	
	
	//---------------------- I -------------------------
	
	
	
	public static final String 
	SPEC_PATH_TERRAIN = "decor/terrain%d.png",
	PATH_COMPOSANTS = "composants/composants.png",
	PATH_DIALOG_BACKGROUND = "composants/dialogBackground.png",
	PATH_DIRECTION_ARROW = "composants/directionArrow.png",//Flèche qui indique au joueur où est la sortie.
	PATH_DIRECTIONS_CIRCLE = "composants/directionsCircle.png",
	PATH_MAP = "composants/carte.png",
	PATH_PLAYER = "composants/player1.png",
	SPEC_TEX_MUR = ".mur%d",
	TEX_CRYSTAL = ".aimant",
	TEX_DIALOG_BACKGROUND_LEFT = ".left",
	TEX_DIALOG_BACKGROUND_CENTER = ".center",
	TEX_DIALOG_BACKGROUND_RIGHT = ".right",
	TEX_EXIT0 = ".exit0",
	TEX_GENERATOR = ".generator",
	TEX_INACCESSIBLE = ".inaccessible",
	TEX_LAMP_IN = ".lampe.in",
	TEX_LAMP_OUT = ".lampe.ex",
	TEX_LAMP_PERTURBATRICE_OFF = ".lampe.perturbatrice.off",
	TEX_LAMP_PERTURBATRICE_ON = ".lampe.perturbatrice.on",
	TEX_LASERS = ".lasers",
	TEX_MAGNETIC_FIELD_INDICATOR = ".indicator",
	TEX_MINI_ELECTROCRYSTAL = ".miniCrystal",
	TEX_PERTURBATEUR = ".perturb",
	TEX_PLAYER_STANDING = ".playerStanding",
	TEX_PORTAL = ".portal",
	TEX_SOL = ".sol",
	TEX_WAVES = ".waves",
	TEX_WAVE_GENERATOR_EAST = ".wavegenerator.east",
	TEX_WAVE_GENERATOR_NORD = ".wavegenerator.nord",
	TEX_WAVE_GENERATOR_SOUTH = ".wavegenerator.south",
	TEX_WAVE_GENERATOR_WEST = ".wavegenerator.west",
			
	TEX_MAP_AIMANT = ".map.aimant",
	TEX_MAP_BACKGROUND = ".map.background",
	TEX_MAP_GENERATOR = "map.generator",
	TEX_MAP_LAMP = ".map.lamp",
	TEX_MAP_LAMP_RANDOM = ".map.lamp.random",
	TEX_MAP_PLAYER = ".map.player";
	
	public static final String
	PATH_ANIM000_DANGEROUS_CRYSTAL = "composants/anim000_dangerous_crystal.png",
	PATH_ANIM003_PERMANENT_MODE_LAMP = "composants/anim003_permanent_mode_lamp.png";
	
	public static final String
	ID_AIMANT = PATH_COMPOSANTS + TEX_CRYSTAL,
	ID_EXIT0 = PATH_COMPOSANTS + TEX_EXIT0,
	ID_GENERATOR = PATH_COMPOSANTS + TEX_GENERATOR,
	ID_LAMP_IN = PATH_COMPOSANTS + TEX_LAMP_IN,
	ID_LAMP_OUT = PATH_COMPOSANTS + TEX_LAMP_OUT,
	ID_LAMP_PERTURBATRICE_OFF = PATH_COMPOSANTS + TEX_LAMP_PERTURBATRICE_OFF,
	ID_LAMP_PERTURBATRICE_ON = PATH_COMPOSANTS + TEX_LAMP_PERTURBATRICE_ON,
	ID_LASERS = PATH_COMPOSANTS + TEX_LASERS,
	ID_MAGNETIC_FIELD_INDICATOR = PATH_COMPOSANTS + TEX_MAGNETIC_FIELD_INDICATOR,
	ID_MINI_ELECTROCRYSTAL = PATH_COMPOSANTS + TEX_MINI_ELECTROCRYSTAL,
	ID_PERTURBATEUR = PATH_COMPOSANTS + TEX_PERTURBATEUR,
	ID_PLAYER_STANDING = PATH_PLAYER + TEX_PLAYER_STANDING,
	ID_PORTAL = PATH_COMPOSANTS + TEX_PORTAL,
	ID_WAVES = PATH_COMPOSANTS + TEX_WAVES,
	ID_WAVE_GENERATOR_EAST = PATH_COMPOSANTS + TEX_WAVE_GENERATOR_EAST,
	ID_WAVE_GENERATOR_NORD = PATH_COMPOSANTS + TEX_WAVE_GENERATOR_NORD,
	ID_WAVE_GENERATOR_SOUTH = PATH_COMPOSANTS + TEX_WAVE_GENERATOR_SOUTH,
	ID_WAVE_GENERATOR_WEST = PATH_COMPOSANTS + TEX_WAVE_GENERATOR_WEST,
			
	ID_MAP_CRYSTAL = PATH_MAP + TEX_CRYSTAL,
	ID_MAP_BACKGROUND = PATH_MAP + TEX_MAP_BACKGROUND,
	ID_MAP_GENERATOR = PATH_MAP + TEX_MAP_GENERATOR,
	ID_MAP_LAMP = PATH_MAP + TEX_MAP_LAMP,
	ID_MAP_LAMP_RANDOM = PATH_MAP + TEX_MAP_LAMP_RANDOM,
	ID_MAP_PLAYER = PATH_MAP + TEX_MAP_PLAYER;
	
	public static final TextureSet gameTextureSet = new TextureSet();
	
	
	
	//---------------------- II ------------------------
	
	
	
	/** set de textures - interface graphique. */
	public static final TextureSet guiTextureSet = new TextureSet();
	
	public static final String 
	PATH_GUI = "gui/game/gameGui.png",
	PATH_TERRAIN_GUI = Util.formatDecimal(GameRessources.SPEC_PATH_TERRAIN, 1),
	PATH_TITLE_SCREEN = "gui/game/titleScreen.png",
	PATH_BONUS_LEVEL_BUTTONS = "niveaux/bonus/boutons0.png",
	

	TEX_BACKGROUND = GameRessources.TEX_SOL,
	TEX_BONUS_BUTTON_0_ENFERS = ".boutons.0gorgeDesEnfers",
	TEX_BUTTON = ".button",
	TEX_BUTTON_OVER = ".buttonOver",
	TEX_BUTTON_PRESSED = ".buttonPressed",
	TEX_TEXTFIELD1 = ".textField1";
	
	public static final String
	ID_BUTTON = PATH_GUI + TEX_BUTTON,
	ID_BUTTON_OVER = PATH_GUI + TEX_BUTTON_OVER,
	ID_BUTTON_PRESSED = PATH_GUI + TEX_BUTTON_PRESSED,
	ID_TEXTFIELD1 = PATH_GUI + TEX_TEXTFIELD1,
	ID_BACKGROUND = PATH_TERRAIN_GUI + TEX_BACKGROUND;
	
	/** set de textures - menu du jeu libre */
	public static final TextureSet freeGameTextureSet = new TextureSet();
	
	
	
	//--------------------- III ------------------------
	
	
	
	/** set de textures - décor 1 de montagne */
	public static final TextureSet decorMONTSTextureSet = new TextureSet();
	/** set de textures - décor 2 de grotte */
	public static final TextureSet decorGROTTETextureSet = new TextureSet();
	
	/** set de textures - décor bonus des enfers */
	public static final TextureSet decorENFERSTextureSet = new TextureSet();
	
	
	
	//--------------------- IV -------------------------
	
	
	
	/** set de textures - niveau 1 */
	public static final TextureSet level1TextureSet = new TextureSet();
	
	public static final String
	PATH_LVL1_TEXTURES = "niveaux/niveau1/decoration0.png",
	
	TEX_ARBRE = ".arbre",
	TEX_ENTREE_GROTTE = ".grotte",
	TEX_HERBE = ".herbe",
	TEX_SOL_VEGETAL0 = ".solVegetal0",
	TEX_SOL_VEGETAL1 = ".solVegetal1",
	TEX_SOL_VEGETAL2 = ".solVegetal2";
	
	/** set de textures - niveau 2 */
	public static final TextureSet level2TextureSet = new TextureSet();
	
	public static final String
	PATH_ANIM001_PLASMATIC_WALL = "niveaux/niveau2/anim001_plasmatic_wall.png",
	PATH_ANIM002_OPENING_PORTAL = "niveaux/niveau2/anim002_opening_portal.png",
	PATH_ANIM004_TRAP_OFF = "niveaux/niveau2/anim004_trap_off.png",
	PATH_CREATURE = "niveaux/niveau2/creature.png",
	PATH_LVL2_TEXTURES = "niveaux/niveau2/decoration1.png",
	
	TEX_MAP_ROCK = ".map.lvl2.rock",
	TEX_PENTAGRAM = ".pentagram",
	TEX_PLASMATIC_WALL = ".plasmaticWall",
	TEX_PORTAL_OFF = ".portal.off",
	TEX_PORTAL_ON = ".portal.on",
	TEX_TRAP_OFF_IMAGE = ".trap.off",
	TEX_TRAP_ACTIVE = ".trap.active";
	
	
	
	//---------------------- V -------------------------
	
	
	
	public static final String
	SPEC_DIR_MODEL_TERRAIN = "decor/terrain%d/";
	
	public static final String
	SPEC_PATH_MODEL_MUR_DROIT = SPEC_DIR_MODEL_TERRAIN + "mur.obj",
	SPEC_PATH_MODEL_MUR_COIN = SPEC_DIR_MODEL_TERRAIN + "mur2.obj",
	SPEC_PATH_MODEL_MUR_ANGLE = SPEC_DIR_MODEL_TERRAIN + "mur3.obj";
	
	/** set de modèles - décor 1 de montagne */
	public static final List<String> decorMONTSModelSet = new ArrayList<String>();
	/** set de modèles - décor 2 de grotte */
	public static final List<String> decorGROTTEModelSet = new ArrayList<String>();
	
	/** set de modèles - décor bonus des enfers */
	public static final List<String> decorENFERSModelSet = new ArrayList<String>();
	
	
	
	//------------------------ VI ----------------------
	
	
	
	public static final ArrayList<String> level1ModelSet = new ArrayList<String>();
	
	public static final ArrayList<String> level2ModelSet = new ArrayList<String>();
	
	public static final String
	PATH_MODEL_LVL2_CONTROLPANE = "niveaux/niveau2/controlPane.obj",
	PATH_MODEL_LVL2_ROCKS = "niveaux/niveau2/rocks.obj";
	
	
	
	//----------------------- VII ----------------------
	
	
	
	public static final SoundSet tmSoundSet = new SoundSet();
	
	public static final String
	SOUND_GUI_BUTTONS = "sons/changementEcran.wav",
	SOUND_STEPS = "sons/steps.wav";
	
	
	private static final Map<DecorType, TextureSet> decorTextureSetMap = new HashMap<DecorType, TextureSet>();
	private static final Map<DecorType, List<String>> decorModelSetMap = new HashMap<DecorType, List<String>>();
	
	private static final Map<Integer, TextureSet> levelTextureSetMap = new HashMap<Integer, TextureSet>();
	private static final Map<Integer, List<String>> levelModelSetMap = new HashMap<Integer, List<String>>();
	
	
	
	static {
		//---- MAPPING
		decorTextureSetMap.put(DecorType.MONTS, decorMONTSTextureSet);
		decorTextureSetMap.put(DecorType.GROTTE, decorGROTTETextureSet);
		decorTextureSetMap.put(DecorType.ENFERS, decorENFERSTextureSet);
		
		levelTextureSetMap.put(1, level1TextureSet);
		levelTextureSetMap.put(2, level2TextureSet);
		
		decorModelSetMap.put(DecorType.MONTS, decorMONTSModelSet);
		decorModelSetMap.put(DecorType.GROTTE, decorGROTTEModelSet);
		decorModelSetMap.put(DecorType.ENFERS, decorENFERSModelSet);
		
		levelModelSetMap.put(1, level1ModelSet);
		levelModelSetMap.put(2, level2ModelSet);
		
		
		
		
		int width, height;
		
		

		// Textures des décors.
		width = 8; height = 8;
		
		for (DecorType type : DecorType.values()) {
			
			String terrainPath = Util.formatDecimal(SPEC_PATH_TERRAIN, type.getIndex() + 1);
			gameTextureSet.createImage(terrainPath);
			
			String pathsMurs[] = new String[WallTile.NB_IMAGE];
			
			for (int j = 0 ; j < pathsMurs.length ; j++) {
				pathsMurs[j] = terrainPath + Util.formatDecimal(SPEC_TEX_MUR, j);
			}
			
			gameTextureSet.addTextureModel(terrainPath + TEX_SOL, new TextureQuad(1, 1, width, height, 0));
			gameTextureSet.addTextureModel(terrainPath + TEX_INACCESSIBLE, new TextureQuad(4, 1, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.HAUT], new TextureQuad(1, 0, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.DROITE], new TextureQuad(2, 1, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.GAUCHE], new TextureQuad(0, 1, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.BAS], new TextureQuad(1, 2, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.COIN_GAUCHE_HAUT], new TextureQuad(0, 0, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.COIN_DROIT_HAUT], new TextureQuad(2, 0, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.COIN_DROIT_BAS], new TextureQuad(2, 2, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.COIN_GAUCHE_BAS], new TextureQuad(0, 2, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.ANGLE_GAUCHE_HAUT], new TextureQuad(3, 0, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.ANGLE_DROIT_HAUT], new TextureQuad(5, 0, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.ANGLE_DROIT_BAS], new TextureQuad(5, 2, width, height, 0));
			gameTextureSet.addTextureModel(pathsMurs[WallTile.ANGLE_GAUCHE_BAS], new TextureQuad(3, 2, width, height, 0));
		}
		
		
		//Texture des différents composants du jeu.
		
		
		gameTextureSet.createImage(PATH_COMPOSANTS); width = 2048; height = 2048;
		gameTextureSet.addTextureModel(ID_AIMANT, new TextureQuad(0, 1664, 128, 1856, width, height, 0));
		gameTextureSet.addTextureModel(ID_EXIT0, new TextureQuad(256, 1408, 512, 1664, width, height, 0));
		gameTextureSet.addTextureModel(ID_GENERATOR, new TextureQuad(0, 0, 512, 512, width, height, 0));
		gameTextureSet.addTextureModel(ID_LAMP_IN, new TextureQuad(256, 1024, 512, 1408, width, height, 0));
		gameTextureSet.addTextureModel(ID_LAMP_OUT, new TextureQuad(0, 1024, 256, 1408, width, height, 0));
		gameTextureSet.addTextureModel(ID_LAMP_PERTURBATRICE_OFF, new TextureQuad(768, 1408, 1024, 1664, width, height, 0));
		gameTextureSet.addTextureModel(ID_LAMP_PERTURBATRICE_ON, new TextureQuad(512, 1408, 768, 1664, width, height, 0));
		gameTextureSet.addTextureModel(ID_LASERS, new TextureQuad(1280, 1408, 1792, 1664, width, height, 0));
		gameTextureSet.addTextureModel(ID_MINI_ELECTROCRYSTAL, new TextureQuad(0, 1984, 64, 2048, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAGNETIC_FIELD_INDICATOR, new TextureQuad(2016, 2016, 2048, 2048, width, height, 0));
		gameTextureSet.addTextureModel(ID_PERTURBATEUR, new TextureQuad(0, 1856, 128, 1984, width, height, 0));
		gameTextureSet.addTextureModel(ID_PORTAL, new TextureQuad(0, 1408, 256, 1664, width, height, 0));
		gameTextureSet.addTextureModel(ID_WAVE_GENERATOR_EAST, new TextureQuad(768, 256, 1024, 512, width, height, 0));
		gameTextureSet.addTextureModel(ID_WAVE_GENERATOR_NORD, new TextureQuad(512, 0, 768, 256, width, height, 0));
		gameTextureSet.addTextureModel(ID_WAVE_GENERATOR_SOUTH, new TextureQuad(768, 0, 1024, 256, width, height, 0));
		gameTextureSet.addTextureModel(ID_WAVE_GENERATOR_WEST, new TextureQuad(512, 256, 768, 512, width, height, 0));
		gameTextureSet.addTextureModel(ID_WAVES, new TextureQuad(1024, 1408, 1280, 1664, width, height, 0));
		
		gameTextureSet.createImage(PATH_MAP); width = 512; height = 512;
		gameTextureSet.addTextureModel(ID_MAP_CRYSTAL, new TextureQuad(320, 0, 352, 32, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAP_BACKGROUND, new TextureQuad(0, 0, 256, 256, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAP_GENERATOR, new TextureQuad(0, 256, 160, 416, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAP_LAMP, new TextureQuad(288, 0, 320, 32, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAP_LAMP_RANDOM, new TextureQuad(352, 0, 384, 32, width, height, 0));
		gameTextureSet.addTextureModel(ID_MAP_PLAYER, new TextureQuad(256, 0, 288, 32, width, height, 0));
		
		gameTextureSet.createImage(PATH_PLAYER); width = 512; height = 512;
		gameTextureSet.addTextureModel(ID_PLAYER_STANDING, TexturesLoader.createAnimatedTexture(SPRITE_WIDTH, SPRITE_HEIGHT, NB_FRAMES, 0, width, height));
		
		gameTextureSet.createImage(PATH_ANIM000_DANGEROUS_CRYSTAL);
		gameTextureSet.addTextureModel(PATH_ANIM000_DANGEROUS_CRYSTAL, TexturesLoader.createAnimatedTexture(128, 192, 12, 0, 1536, 192));
		
		gameTextureSet.createImage(PATH_ANIM003_PERMANENT_MODE_LAMP);
		gameTextureSet.addTextureModel(PATH_ANIM003_PERMANENT_MODE_LAMP, TexturesLoader.createAnimatedTexture(256, 256, 4, 0, 1024, 256).withFPS(12));
		
		//Gui in game
		gameTextureSet.createImage(PATH_DIALOG_BACKGROUND); width = 256; height = 256;
		gameTextureSet.addTextureModel(TEX_DIALOG_BACKGROUND_LEFT, new TextureQuad(0, 0, 32, height, width, height, 0));
		gameTextureSet.addTextureModel(TEX_DIALOG_BACKGROUND_CENTER, new TextureQuad(32, 0, width - 32, height, width, height, 0));
		gameTextureSet.addTextureModel(TEX_DIALOG_BACKGROUND_RIGHT, new TextureQuad(width - 32, 0, width, height, width, height, 0));
		
		gameTextureSet.createImage(PATH_DIRECTIONS_CIRCLE); width = 128; height = 128;
		gameTextureSet.addTextureModel(PATH_DIRECTIONS_CIRCLE, new TextureQuad(0, 0, width, height, width, height, 0));
		
		gameTextureSet.createImage(PATH_DIRECTION_ARROW); width = 64; height = 64;
		gameTextureSet.addTextureModel(PATH_DIRECTION_ARROW, new TextureQuad(0, 0, width, height, width, height, 0));
		
		//Textures de l'interface graphique
		guiTextureSet.createImage(PATH_GUI); width = 1024; height = 1024;
		guiTextureSet.addTextureModel(ID_BUTTON, new TextureQuad(0, 0, 255, 63, width, height, 0));
		guiTextureSet.addTextureModel(ID_BUTTON_OVER, new TextureQuad(0, 64, 255, 127, width, height, 0));
		guiTextureSet.addTextureModel(ID_BUTTON_PRESSED, new TextureQuad(0, 128, 255, 191, width, height, 0));
		guiTextureSet.addTextureModel(ID_TEXTFIELD1, new TextureQuad(0, 192, 255, 255, width, height, 0));
		
		guiTextureSet.createImage(PATH_TERRAIN_GUI);
		guiTextureSet.addTextureModel(ID_BACKGROUND, gameTextureSet.findTextureModel(ID_BACKGROUND));
		
		guiTextureSet.createImage(PATH_TITLE_SCREEN); width = 896; height = 640;
		guiTextureSet.addTextureModel(PATH_TITLE_SCREEN, new TextureQuad(0, 0, width, height, width, height, 0));
		
		guiTextureSet.createImage(PATH_BONUS_LEVEL_BUTTONS); width = 512; height = 512;
		guiTextureSet.addTextureModel(TEX_BONUS_BUTTON_0_ENFERS, new TextureQuad(0, 0, 256, 256, width, height, 0));
		
		//Textures du menu "Jeu libre"
		for (DecorType decor : DecorType.values()) {
			String terrainPath = Util.formatDecimal(GameRessources.SPEC_PATH_TERRAIN, decor.getIndex() + 1);
			freeGameTextureSet.createImage(terrainPath);
			String id = terrainPath + GameRessources.TEX_INACCESSIBLE;
			freeGameTextureSet.addTextureModel(id, gameTextureSet.findTextureModel(id));
		}
		
		
		
		//Textures du décor MONTS
		//pour l'instant rien de plus.
		
		
		
		//Textures du niveau 1
		level1TextureSet.addImage(gameTextureSet.getImage(GameRessources.PATH_COMPOSANTS));
		
		level1TextureSet.createImage(PATH_LVL1_TEXTURES); width = 2048; height = 2048;
		
		level1TextureSet.addTextureModel(TEX_ARBRE, new TextureQuad(0, 0, 512, 512, width, height, 0));
		level1TextureSet.addTextureModel(TEX_ENTREE_GROTTE, new TextureQuad(768, 1408, 1536, 1664, width, height, 0));
		level1TextureSet.addTextureModel(TEX_HERBE, new TextureQuad(0, 1984, 64, 2048, width, height, 0));
		level1TextureSet.addTextureModel(TEX_SOL_VEGETAL0, new TextureQuad(0, 1408, 256, 1664, width, height, 0));
		level1TextureSet.addTextureModel(TEX_SOL_VEGETAL1, new TextureQuad(256, 1408, 512, 1664, width, height, 0));
		level1TextureSet.addTextureModel(TEX_SOL_VEGETAL2, new TextureQuad(512, 1408, 768, 1664, width, height, 0));
		
		//Textures du niveau 2
		level2TextureSet.createImage(PATH_ANIM001_PLASMATIC_WALL);
		level2TextureSet.addTextureModel(PATH_ANIM001_PLASMATIC_WALL, TexturesLoader.createAnimatedTexture(256, 192, 24, 0, 6144, 192).withFPS(12));
		
		level2TextureSet.createImage(PATH_ANIM002_OPENING_PORTAL);
		level2TextureSet.addTextureModel(PATH_ANIM002_OPENING_PORTAL, TexturesLoader.createAnimatedTexture(256, 256, 12, 0, 3072, 256).withFPS(24));
		
		level2TextureSet.createImage(PATH_ANIM004_TRAP_OFF); width = 768; height = 256;
		level2TextureSet.addTextureModel(PATH_ANIM004_TRAP_OFF, TexturesLoader.createAnimatedTexture(256, 256, 2, 0, width, height).withFPS(4));
		level2TextureSet.addTextureModel(TEX_TRAP_OFF_IMAGE, new TextureQuad(0, 0, 256, 256, width, height, 0));
		level2TextureSet.addTextureModel(TEX_TRAP_ACTIVE, new TextureQuad(512, 0, 768, 256, width, height, 0));
		
		level2TextureSet.createImage(PATH_LVL2_TEXTURES); width = 1024; height = 1024;
		level2TextureSet.addTextureModel(TEX_PENTAGRAM, new TextureQuad(0, 0, 512, 512, width, height, 0));
		level2TextureSet.addTextureModel(TEX_PLASMATIC_WALL, new TextureQuad(0, 640, 256, 832, width, height, 0));
		level2TextureSet.addTextureModel(TEX_PORTAL_OFF, new TextureQuad(768, 0, 1024, 256, width, height, 0));
		level2TextureSet.addTextureModel(TEX_PORTAL_ON, new TextureQuad(512, 0, 768, 256, width, height, 0));
		
		level2TextureSet.createImage(PATH_CREATURE); width = 678; height = 240;
		level2TextureSet.addTextureModel(PATH_CREATURE, new TextureQuad(0, 0, 678, 240, width, height, 0));
		
		level2TextureSet.createImage(PATH_MAP); width = 512; height = 512;
		level2TextureSet.addTextureModel(TEX_MAP_ROCK, new TextureQuad(384, 0, 416, 32, width, height, 0));
		
		
		
		//Modèles de tous les décors
		for (DecorType type : DecorType.values()) {
			int index = type.getIndex() + 1;
			getModelSetByLandscape(type).add(Util.formatDecimal(SPEC_PATH_MODEL_MUR_DROIT, index));
			getModelSetByLandscape(type).add(Util.formatDecimal(SPEC_PATH_MODEL_MUR_COIN, index));
			getModelSetByLandscape(type).add(Util.formatDecimal(SPEC_PATH_MODEL_MUR_ANGLE, index));
		}
		
		
		
		//Modèles du niveau 2
		level2ModelSet.add(PATH_MODEL_LVL2_CONTROLPANE);
		level2ModelSet.add(PATH_MODEL_LVL2_ROCKS);
		
		
		
		//Sons du jeu
		tmSoundSet.addSound(SOUND_GUI_BUTTONS);
		tmSoundSet.addSound(SOUND_STEPS);
	}
	
	
	
	public static TextureSet getTextureSetByLandscape(DecorType type) {
		TextureSet ret = GameRessources.decorTextureSetMap.get(type);
		return ret == null ? new TextureSet() : ret;
	}
	
	public static List<String> getModelSetByLandscape(DecorType type) {
		return GameRessources.decorModelSetMap.get(type);
	}
	
	public static TextureSet getTextureSetByLevel(int levelID) {
		return GameRessources.levelTextureSetMap.get(levelID);
	}
	
	public static List<String> getModelSetByLevel(int levelID) {
		return GameRessources.levelModelSetMap.get(levelID);
	}
	
	
	
	public static void initClassDef() {}
}
