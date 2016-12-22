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

package org.terramagnetica.opengl.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Shape;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

/** Cette classe sert � plein de choses.*/
public class Painter {

	public static enum Primitive {
		QUADS(GL11.GL_QUADS, 4),
		LINES(GL11.GL_LINES, 2),
		LINE_STRIP(GL11.GL_LINE_STRIP, -1),
		TRIANGLES(GL11.GL_TRIANGLES, 3),
		TRIANGLES_FAN(GL11.GL_TRIANGLE_FAN, -1),
		TRIANGLES_STRIP(GL11.GL_TRIANGLE_STRIP, -1);
		
		private int glDrawMode;
		private int verticeCount;
		
		private Primitive(int drawMode, int verticeCount) {
			this.glDrawMode = drawMode;
			this.verticeCount = verticeCount;
		}
	
		public int getGlDrawMode() {
			return glDrawMode;
		}
		
		public int getVerticeCount() {
			return verticeCount;
		}
		
		public boolean supportsAutoTexturing() {
			return this.verticeCount != -1;
		}
	}
	
	public static final Vec3d DEFAULT_NORMAL = new Vec3d(0, 0, 1);
	private static final int VERTICES_MAX = 1024;
	
	private ProgramRegistry programs;
	private GLConfiguration configuration = GLConfiguration.default2DConfiguration();
	private final GLConfiguration painter2DConfig = GLConfiguration.default2DConfiguration();
	private final GLConfiguration painter3DConfig = GLConfiguration.default3DConfiguration();
	
	//Buffers
	private int verticesMax = VERTICES_MAX;
	private int verticesCount = 0;
	
	private FloatBuffer verticesBuf = BufferUtils.createFloatBuffer(this.verticesMax * 3);
	private FloatBuffer texCoordsBuf = BufferUtils.createFloatBuffer(this.verticesMax * 2);
	private FloatBuffer normalsBuf = BufferUtils.createFloatBuffer(this.verticesMax * 3);
	private ByteBuffer colorsBuf = BufferUtils.createByteBuffer(this.verticesMax * 4);
	
	private VAO defaultVAO = new VAO();
	
	//Param�tres
	private Primitive primitive = Primitive.QUADS;
	private Color4f color;
	private Texture texture;
	private Viewport viewport;
	private LightModel lightModel = new LightModel();
	
	private LinkedList<Transform> transforms = new LinkedList<Transform>();
	private Deque<Integer> transformsSaves = new ArrayDeque<Integer>(8);
	
	//Contenu
	private DisplayList recordedList;
	private CameraFrustum camFrustum;
	
	//Tracking
	private int texID = 0;

	public Painter() {
		initBuffers();
		
		this.programs = new ProgramRegistry(this);
		
		this.configuration.painter = this;
		this.lightModel.painter = this;
		
		// D�finition des param�tres du VAO
		this.defaultVAO.setAttrib(StdAttrib.VERTEX, new VBO().withDataUsage(GL15.GL_DYNAMIC_DRAW), 3, GL11.GL_FLOAT);
		this.defaultVAO.setAttrib(StdAttrib.NORMAL, new VBO().withDataUsage(GL15.GL_DYNAMIC_DRAW), 3, GL11.GL_FLOAT);
		this.defaultVAO.setAttrib(StdAttrib.TEX_COORD, new VBO().withDataUsage(GL15.GL_DYNAMIC_DRAW), 2, GL11.GL_FLOAT);
		this.defaultVAO.setAttrib(StdAttrib.COLOR, new VBO().withDataUsage(GL15.GL_DYNAMIC_DRAW), 4, GL11.GL_BYTE);
	}
	
	/** Avertit le painter qu'une propri�t� ext�rieure (relevant de la
	 * configuration ou du LightModel) <b>va �tre modifi�e</b>. Ainsi
	 * le buffer est vid� pour �viter que ces changements n'affecte
	 * le dessin � venir. */
	void notifyExternalChanges() {
		flush();
	}
	
	public void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}
	
	public void initFrame() {
		this.configuration.setup();
	}
	
	public void flushAndSet(GLConfiguration config) {
		flush();
		
		this.setConfiguration(config);
	}
	
	/** Dessine tout le contenu stock� dans les buffers et les vide. */
	public void flush() {
		if (this.verticesCount == 0) {
			return;
		}
		
		this.verticesBuf.flip();
		this.texCoordsBuf.flip();
		this.normalsBuf.flip();
		this.colorsBuf.flip();
		
		beforeDrawing();
		
		//D�but
		this.defaultVAO.getAttribBuffer(StdAttrib.COLOR).setData(this.colorsBuf);
		
		if (this.texture != null) {
			bindTexture(this.texture.getGLTextureID());
			this.defaultVAO.getAttribBuffer(StdAttrib.TEX_COORD).setData(this.texCoordsBuf);
		}
		else {
			bindTexture(0);
		}
		
		this.defaultVAO.getAttribBuffer(StdAttrib.NORMAL).setData(this.normalsBuf);
		this.defaultVAO.getAttribBuffer(StdAttrib.VERTEX).setData(this.verticesBuf);
		
		GL11.glDrawArrays(this.primitive.glDrawMode, 0, this.verticesCount);
		
		//Fin
		afterDrawing();
		
		initBuffers();
	}
	
	private void beforeDrawing() {
		if (this.recordedList == null) {
			//TODO remove - d� � la non utilisation des shaders... et la non praticit� d'openGL old (reset des matrices inopportun)
			this.configuration.getCamera().pushCamera(this);
			
			if (this.viewport != null) {
				drawViewport();
			}
			else if (!isCamera3D()) {
				GL11.glDisable(GL11.GL_STENCIL_TEST);
			}
			applyTransforms();
		}
	}
	
	private void afterDrawing() {
		
		if (this.recordedList == null) {
			this.configuration.clearConfig();
		}
	}
	
	
	//Dessin point par point
	private void initBuffers() {
		this.verticesBuf.clear();
		this.texCoordsBuf.clear();
		this.normalsBuf.clear();
		this.colorsBuf.clear();
		
		this.verticesCount = 0;
	}
	
	private void bindTexture(int id) {
		if (id != this.texID) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			this.texID = id;
		}
	}
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(double x, double y) { addVertex(x, y, 0); }
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(double x, double y, double z) { addVertex(new Vec3d(x, y, z)); }

	/** Ajoute un vertex au tampon de dessin. La normale est prise
	 * par d�faut � (0, 0, 1).
	 * <p>Le painter impl�mente par d�faut l'autotexturing : les coordonn�es
	 * correspondant � ce vertex sur la texture sont d�termin�s automatiquement
	 * selon une rotation dans le sens horaire. Le point de d�part d�pend de la
	 * texture. Veuillez ajouter les points dans l'ordre, sp�cifi� par la m�thode
	 * {@link Texture#getSTSommets()} de la texture en cours d'utilisation pour
	 * un r�sultat optimal. */
	public void addVertex(Vec3d vertex) { addVertex(vertex, DEFAULT_NORMAL); }
	
	public void addVertex(Vec3d vertex, double s, double t) { addVertex(vertex, DEFAULT_NORMAL, s, t); }
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(Vec3d vertex, Vec3d normal) {
		//Auto-texturing
		double s = 0, t = 0;
		
		if (this.texture != null) {
			Vec2d[] texCoords = this.texture.getSTSommets();
			
			if (texCoords.length == this.primitive.verticeCount) {
				Vec2d st = texCoords[verticesCount % texCoords.length];
				s = st.x;
				t = st.y;
			}
		}
		
		addVertex(vertex, normal, s, t);
	}
	
	public void addVertex(Vec3d vertex, Vec3d normal, double s, double t) {
		if (this.verticesCount + 32 >= this.verticesMax && this.verticesCount % this.primitive.verticeCount == 0) {
			flush();
		}
		
		this.verticesBuf.put(new float[] {(float) vertex.x, (float) vertex.y, (float) vertex.z});
		
		if (this.texture != null) {
			this.texCoordsBuf.put(new float[] {(float) s, (float) t});
		}
		
		this.normalsBuf.put(new float[] {(float) normal.x, (float) normal.y, (float) normal.z});
		
		if (this.color != null) {
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				this.colorsBuf.put(new byte[] {
						(byte) this.color.getRed(),
						(byte) this.color.getGreen(),
						(byte) this.color.getBlue(),
						(byte) this.color.getAlpha()});
			}
			else {
				this.colorsBuf.put(new byte[] {
						(byte) this.color.getAlpha(),
						(byte) this.color.getBlue(),
						(byte) this.color.getGreen(),
						(byte) this.color.getRed()});
			}
		}
		
		this.verticesCount++;
	}
	
	public void setConfiguration(GLConfiguration config) {
		if (config == null) throw new NullPointerException("config == null");
		
		flush();
		this.configuration.clearConfig();
		this.configuration.painter = null;
		
		this.configuration = config;
		
		this.configuration.painter = this;
		this.configuration.setup();
	}
	
	public GLConfiguration getConfiguration() {
		return this.configuration;
	}
	
	public ProgramRegistry getProgramRegistry() {
		return this.programs;
	}
	
	public boolean isCamera3D() {
		return this.configuration.isCamera3D();
	}
	
	/** Si la configuration actuelle n'est pas 2D, alors red�finit une
	 * configuration 2D par d�faut et l'applique au painter. */
	public void set2DConfig() {
		if (isCamera3D()) {
			setConfiguration(this.painter2DConfig.clone());
		}
	}
	
	/** Si la configuration actuelle n'est pas 3D, alors red�finit une
	 * configuration 3D par d�faut et l'applique au painter. */
	public void set3DConfig() {
		if (!isCamera3D()) {
			setConfiguration(this.painter3DConfig.clone());
		}
	}
	
	public LightModel getLightModel() {
		return this.lightModel;
	}
	
	/** Cr�e un objet de frustum contenant les informations
	 * de la pyramide de vue de la cam�ra actuelle appliqu�e
	 * au painter. */
	public CameraFrustum createCameraFrustum() {
		if (!isCamera3D()) throw new IllegalStateException("No frustum in 2D !");
		
		CameraFrustum camFrustum = new CameraFrustumRadar();
		this.configuration.getCamera3D().setUpFrustum(camFrustum);
		
		return camFrustum;
	}
	
	//Param�tres
	/** D�finit la texture � utiliser pour les dessins suivants.
	 * @return La texture pr�c�demment utilis�e.*/
	public Texture setTexture(Texture tex) {
		if (this.texture == null && tex == null) return null;
		
		Texture oldTexture = this.texture;
		
		if (oldTexture == null || tex == null || oldTexture.getGLTextureID() != tex.getGLTextureID()) {
			flush();
		}
		
		if (tex instanceof AnimatedTexture) {
			this.texture = ((AnimatedTexture) tex).get();
		}
		else {
			this.texture = tex;
		}
		
		return oldTexture;
	}
	
	public void setPrimitive(Primitive primitive) {
		if (primitive == null) throw new NullPointerException("primitive == null");
		if (primitive != this.primitive) {
			this.flush();
			this.primitive = primitive;
		}
	}
	
	public void setColor(Color4f color) {
		this.color = color == null ? new Color4f() : color;
	}
	
	public void setViewport(Viewport viewport) {
		if (viewport != this.viewport) flush();
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}
	
	private void drawViewport() {
		if (this.viewport == null) return;
		
		if (this.viewport.list == null) {
			DisplayList list = new DisplayList();
			list.startCompilation();
			
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFFFFFFFF);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
			GL11.glColorMask(false, false, false, false);
			Shape f = viewport.getViewport();
			
			//dessin
			Vec2d[] s = f.getVertices();
			
			if (s.length >= 1) {
				//TODO � remplacer par des VBO
				GL11.glBegin(GL11.GL_TRIANGLES);
				for (int i = 1 ; i < s.length - 1 ; i++) {
					GL11.glVertex2d(s[0].x, s[0].y);
					GL11.glVertex2d(s[i].x, s[i].y);
					GL11.glVertex2d(s[i+1].x, s[i+1].y);
				}
				GL11.glEnd();
			}
			//fin dessin
			
			GL11.glColorMask(true, true, true, true);
			
			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFFFFFFFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			
			list.endOfCompilation();
			
			this.viewport.list = list;
		}
		
		this.viewport.list.callList();
	}
	
	public void addTransform(Transform transform) {
		if (transform == null) {
			throw new NullPointerException("transform == null !");
		}
		
		flush();
		this.transforms.add(transform);
	}
	
	public void setTransform(Transform transform) {
		this.clearTransforms();
		this.addTransform(transform);
	}
	
	public void clearTransforms() {
		if (this.transforms.size() != 0) {
			flush();
			this.transforms.clear();
			this.transformsSaves.clear();
		}
	}
	
	private void applyTransforms() {
		Transform multiTransform = Transform.newMultiTransform(this.transforms);
		multiTransform.applyTransform();
	}
	
	/** Sauvegarde l'�tat des transformation actuel. Retourne un identifiant
	 * correspondant � cet �tat. L'�tat pourra ensuite �tre r�activ� par la
	 * m�thode {@link #popTransformState(int)} */
	public int pushTransformState() {
		int index = this.transforms.size();
		if (index != 0) {
			this.transformsSaves.offer(index);
		}
		
		return this.transformsSaves.size();
	}
	
	/** @see #popTransformState(int)*/
	public boolean popTransformState() {
		return popTransformState(this.transformsSaves.size());
	}
	
	/** R�tablit l'�tat indiqu� par l'identifiant pass� en param�tres.
	 * L'identifiant doit avoir �t� obtenu par la m�thode {@link #pushTransformState()}
	 * pour �tre valide.
	 * @return <code>true</code> si le r�tablissement s'est effectu� normalement
	 * <P><i>Note : avec le set de m�thodes actuel, le r�tablissement d'�tat
	 * ne peut normalement pas �chouer. Ainsi, cette m�thode retournera
	 * toujours <code>true</code></i> */
	public boolean popTransformState(int state) {
		if (state > this.transformsSaves.size()) {
			throw new IllegalArgumentException("Cet �tat n'existe pas. Veuillez utiliser les �tats retourn�s par "
					+ "la m�thode pushTransformState()");
		}
		
		//R�cup�ration de l'indice dans la liste des transformations.
		Integer Index = null;
		while (this.transformsSaves.size() > state) {
			Index = this.transformsSaves.pollLast();
		}
		int index = Index == null ? 0 : Index;
		
		//R�tablissement de l'ancien �tat si c'est possible.
		if (index > this.transforms.size()) {
			return false;
		}
		
		flush();
		if (index == 0) {
			this.clearTransforms();
			return true;
		}
		
		while (index <= this.transforms.size()) {
			this.transforms.removeLast();
		}
		return true;
	}
	
	public void startRecordList(DisplayList list) {
		if (this.recordedList != null) throw new IllegalStateException("Already recording a list");
		
		flush();
		this.recordedList = list;
		list.startCompilation();
	}
	
	public void endRecordList() {
		if (this.recordedList == null) throw new IllegalStateException("No list recording");
		
		flush();
		this.recordedList.endOfCompilation();
		
		this.recordedList = null;
	}
	
	public void drawList(DisplayList list) {
		flush();
		
		beforeDrawing();
		list.callList();
		afterDrawing();
	}
	
	public void drawListAt(DisplayList list, Vec3d position) {
		drawListAt(list, position, new Vec3d(0, 0, 1), 0);
	}
	
	public void drawListAt(DisplayList list, Vec3d position, Vec3d rotAxis, float rotAngle) {
		clearTransforms();

		addTransform(Transform.newRotation(rotAngle, rotAxis));
		addTransform(Transform.newTranslation((float) position.x, (float) position.y, (float) position.z));
		
		drawList(list);
		
		clearTransforms();
	}
}
