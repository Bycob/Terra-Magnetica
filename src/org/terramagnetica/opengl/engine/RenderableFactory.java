package org.terramagnetica.opengl.engine;

public final class RenderableFactory {

	/**
	 * Crée un {@link RenderableCompound} composé d'un même rendu d'une case,
	 * répété un certain nombre de fois. Cela peut servir par exemple pour
	 * des objets représentant une petite partie d'un mur à longueur variable.
	 * <p>L'objet de rendu obtenu sera centré sur les coordonnées de l'entité.
	 * Le Renderable situé au centre du dessin sera en position (0, 0).
	 * @param render - Le rendu à répeter.
	 * @param size - La quantité de rendu à concatener.
	 * @param horizontal - {@code true} si les rendus sont rangés de gauche à
	 * droite, {@code false} s'ils sont rangés de haut en bas.
	 * @return Un {@link RenderableCompound} correspondant à la description ci-dessus.
	 */
	public static RenderableCompound createCaseArrayRender(Renderable render, int size, boolean horizontal) {
		
		RenderableCompound r = new RenderableCompound();
		
		int startIndex = - (size / 2);
		
		for (int i = startIndex ; i < startIndex + size ; i++) {
			Renderable unit = render.clone();
			unit.setPositionOffset(horizontal ? i : 0, horizontal ? 0 : i, 0);
			
			r.addRenders(unit);
		}
		
		return r;
	}
}
