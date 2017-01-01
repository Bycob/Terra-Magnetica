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
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.terramagnetica.opengl.gui.GuiWindow;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Shape;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

/** Cette classe sert à plein de choses.*/
public class Painter {

	public static enum Primitive {
		QUADS(GL11.GL_TRIANGLES, 4, true),
		LINES(GL11.GL_LINES, 2),
		LINE_STRIP(GL11.GL_LINE_STRIP, -1),
		TRIANGLES(GL11.GL_TRIANGLES, 3),
		TRIANGLES_FAN(GL11.GL_TRIANGLE_FAN, -1),
		TRIANGLES_STRIP(GL11.GL_TRIANGLE_STRIP, -1);
		
		private int glDrawMode;
		private int verticeCount;
		private boolean triangulate;
		
		private Primitive(int drawMode, int verticeCount) {
			this.glDrawMode = drawMode;
			this.verticeCount = verticeCount;
		}
		
		private Primitive(int drawMode, int verticeCount, boolean triangulate) {
			this(drawMode, verticeCount);
			this.triangulate = true;
		}
	
		public int getGlDrawMode() {
			return glDrawMode;
		}
		
		public int getVerticeCount() {
			return verticeCount;
		}
		
		public boolean needsTriangulation() {
			return this.triangulate;
		}
		
		public boolean supportsAutoTexturing() {
			return this.verticeCount > 2;
		}
	}
	
	public static final Vec3d DEFAULT_NORMAL = new Vec3d(0, 0, 1);
	private static final int VERTICES_MAX = 1024;
	private static final int STRIDE = 3 * 4 + 2 * 4 + 3 * 4 + 4;
	
	private GuiWindow myWindow;
	private GLBindings bindings = new GLBindings(this);
	
	private ProgramRegistry programs;
	private Program currentProgram;
	
	private GLConfiguration configuration = GLConfiguration.default2DConfiguration();
	private final GLConfiguration painter2DConfig = GLConfiguration.default2DConfiguration();
	private final GLConfiguration painter3DConfig = GLConfiguration.default3DConfiguration();
	
	//Buffers
	private int verticesMax = VERTICES_MAX;
	private int verticesCount = 0;
	private int indicesCount = 0;
	
	private ByteBuffer dataBuf = BufferUtils.createByteBuffer(this.verticesMax * STRIDE);
	private IntBuffer indices = BufferUtils.createIntBuffer(this.verticesMax * 6);
	
	private VAO defaultVAO = new VAO();
	
	//Paramètres
	private Primitive primitive = Primitive.QUADS;
	private Color4f color;
	private Texture texture;
	private Viewport viewport;
	private LightModel lightModel = new LightModel();
	
	private LinkedList<Transform> transforms = new LinkedList<Transform>();
	private Deque<Integer> transformsSaves = new ArrayDeque<Integer>(8);
	
	//Contenu
	private CameraFrustum camFrustum;
	
	//Tracking
	private int texID = 0;

	public Painter(GuiWindow window) {
		initBuffers();
		
		this.myWindow = window;
		
		this.configuration.painter = this;
		this.lightModel.painter = this;
		
		// Définition des paramètres du VAO
		VBO commonVBO = new VBO().withDataUsage(GL15.GL_DYNAMIC_DRAW);
		this.defaultVAO.setAttrib(StdAttrib.VERTEX, commonVBO, 3, GL11.GL_FLOAT, false, STRIDE, 0);
		this.defaultVAO.setAttrib(StdAttrib.TEX_COORD, commonVBO, 2, GL11.GL_FLOAT, false, STRIDE, 3 * 4);
		this.defaultVAO.setAttrib(StdAttrib.NORMAL, commonVBO, 3, GL11.GL_FLOAT, false, STRIDE, (3 + 2) * 4);
		this.defaultVAO.setAttrib(StdAttrib.COLOR, commonVBO, 4, GL11.GL_UNSIGNED_BYTE, true, STRIDE, (3 + 2 + 3) * 4);
		
		this.defaultVAO.setIndicesBuffer(new VBO(GL15.GL_ELEMENT_ARRAY_BUFFER).withDataUsage(GL15.GL_DYNAMIC_DRAW));
		
		//Création des programmes et initialisation du contexte avec le programme par défaut
		this.programs = new ProgramRegistry(this);
		setCurrentProgram(ProgramRegistry.DEFAULT_PROGRAM_ID);
	}
	
	/** Avertit le painter qu'une propriété extérieure (relevant de la
	 * configuration ou du LightModel) <b>va être modifiée</b>. Ainsi
	 * le buffer est vidé pour éviter que ces changements n'affecte
	 * le dessin à venir. */
	void notifyExternalChanges() {
		flush();
	}
	
	public void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}
	
	public void initFrame() {
		this.configuration.setup(null);
	}
	
	public void flushAndSet(GLConfiguration config) {
		flush();
		
		this.setConfiguration(config);
	}
	
	/** Dessine tout le contenu stocké dans les buffers et les vide. */
	public void flush() {
		if (this.verticesCount == 0) {
			return;
		}
		
		this.dataBuf.flip();
		this.indices.flip();
		
		beforeDrawing();
		
		//Début
		this.defaultVAO.bind(this);
		
		if (this.texture != null) {
			bindTexture(this.texture.getGLTextureID());
		}
		else {
			bindTexture(0);
		}

		this.defaultVAO.getAttribBuffer(StdAttrib.VERTEX).setData(this, this.dataBuf); // Le même buffer pour tous
		this.defaultVAO.getIndicesBuffer().setData(this, this.indices);
		
		//GL11.glDrawArrays(this.primitive.glDrawMode, 0, this.verticesCount);
		GL11.glDrawElements(this.primitive.glDrawMode, this.indicesCount, GL11.GL_UNSIGNED_INT, 0);
		
		//Fin
		afterDrawing();
		
		initBuffers();
	}
	
	public void drawVAO(VAO vao, int vertCount) {
		if (vertCount == 0 || vertCount <= this.primitive.glDrawMode) return;
		beforeDrawing();
		
		if (this.texture != null) {
			bindTexture(this.texture.getGLTextureID());
		}
		else {
			bindTexture(0);
		}
		
		vao.bind(this);
		GL11.glDrawArrays(this.primitive.glDrawMode, 0, vertCount);
		
		afterDrawing();
	}
	
	private void beforeDrawing() {
		if (this.viewport != null) {
			drawViewport();
		}
		else if (!isCamera3D()) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
		applyTransforms();
	}
	
	private void afterDrawing() {
		this.configuration.clearConfig();
	}
	
	
	//Dessin point par point
	private void initBuffers() {
		this.dataBuf.clear();
		this.indices.clear();
		
		this.verticesCount = 0;
		this.indicesCount = 0;
	}
	
	private void bindTexture(int id) {
		if (id != this.texID) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			this.texID = id;
		}
	}
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(double x, double y) { addVertex(x, y, 0); }
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(double x, double y, double z) { addVertex(new Vec3d(x, y, z)); }

	/** Ajoute un vertex au tampon de dessin. La normale est prise
	 * par défaut à (0, 0, 1).
	 * <p>Le painter implémente par défaut l'autotexturing : les coordonnées
	 * correspondant à ce vertex sur la texture sont déterminés automatiquement
	 * selon une rotation dans le sens horaire. Le point de départ dépend de la
	 * texture. Il faut ajouter les points dans l'ordre, spécifié par la méthode
	 * {@link Texture#getSTSommets()} de la texture en cours d'utilisation pour
	 * un résultat optimal. */
	public void addVertex(Vec3d vertex) { addVertex(vertex, DEFAULT_NORMAL); }
	
	public void addVertex(Vec3d vertex, double s, double t) { addVertex(vertex, DEFAULT_NORMAL, s, t); }
	
	/** Infos sur l'autotexturing -> {@link #addVertex(Vec3d)} */
	public void addVertex(Vec3d vertex, Vec3d normal) {
		//Auto-texturing
		double s = 0, t = 0;
		
		if (this.texture != null) {
			Vec2d[] texCoords = this.texture.getSTSommets();
			
			if (this.primitive.verticeCount > 2) {
				int id = Math.min(texCoords.length - 1, this.verticesCount % this.primitive.verticeCount);
				Vec2d st = texCoords[id];
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
		
		this.dataBuf.putFloat((float) vertex.x).putFloat((float) vertex.y).putFloat((float) vertex.z);
		this.dataBuf.putFloat((float) s).putFloat((float) t);
		this.dataBuf.putFloat((float) normal.x).putFloat((float) normal.y).putFloat((float) normal.z);
		
		if (this.color != null) {
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				this.dataBuf.put(new byte[] {
						(byte) this.color.getRed(),
						(byte) this.color.getGreen(),
						(byte) this.color.getBlue(),
						(byte) this.color.getAlpha()});
			}
			else {
				this.dataBuf.put(new byte[] {
						(byte) this.color.getAlpha(),
						(byte) this.color.getBlue(),
						(byte) this.color.getGreen(),
						(byte) this.color.getRed()});
			}
		}
		
		this.indices.put(this.verticesCount);
		this.verticesCount++;
		this.indicesCount++;
		
		
		//TRIANGULARISATION
		if (this.primitive.triangulate) {
			int vertexID = this.verticesCount % this.primitive.verticeCount;
			
			if (vertexID >= 3) {
				//Triangulation en forme de lézard couché
				//Triangularisation du dernier vertex, le malheureux !
				this.indices.put(new int[] {this.verticesCount - vertexID, this.verticesCount - vertexID + 2});
				this.indicesCount += 2;
			}
		}
	}
	
	public GuiWindow getWindow() {
		return this.myWindow;
	}
	
	public GLBindings getBindings() {
		return this.bindings;
	}
	
	public void setConfiguration(GLConfiguration config) {
		if (config == null) throw new NullPointerException("config == null");
		
		flush();
		GLConfiguration oldConfig = this.configuration;
		oldConfig.clearConfig();
		oldConfig.painter = null;
		
		this.configuration = config;
		
		this.configuration.painter = this;
		this.configuration.setup(oldConfig);
		
		this.clearTransforms();
	}
	
	public GLConfiguration getConfiguration() {
		return this.configuration;
	}
	
	public ProgramRegistry getProgramRegistry() {
		return this.programs;
	}
	
	public void setCurrentProgram(String name) {
		Program program = this.programs.getProgram(name);
		if (program == null) {
			program = this.programs.getDefaultProgram();
		}
		
		if (program != this.currentProgram) {
			flush();
			
			this.currentProgram = program;
			this.currentProgram.use();
			
			this.configuration.setup(null);
			this.defaultVAO.bind(this);
		}
	}
	
	public Program getCurrentProgram() {
		return this.currentProgram;
	}
	
	public boolean isCamera3D() {
		return this.configuration.isCamera3D();
	}
	
	/** Si la configuration actuelle n'est pas 2D, alors redéfinit une
	 * configuration 2D par défaut et l'applique au painter. */
	public void set2DConfig() {
		if (isCamera3D()) {
			setConfiguration(this.painter2DConfig.clone());
		}
	}
	
	/** Si la configuration actuelle n'est pas 3D, alors redéfinit une
	 * configuration 3D par défaut et l'applique au painter. */
	public void set3DConfig() {
		if (!isCamera3D()) {
			setConfiguration(this.painter3DConfig.clone());
		}
	}
	
	public LightModel getLightModel() {
		return this.lightModel;
	}
	
	/** Crée un objet de frustum contenant les informations
	 * de la pyramide de vue de la caméra actuelle appliquée
	 * au painter. */
	public CameraFrustum createCameraFrustum() {
		if (!isCamera3D()) throw new IllegalStateException("No frustum in 2D !");
		
		CameraFrustum camFrustum = new CameraFrustumRadar();
		this.configuration.getCamera3D().setUpFrustum(this, camFrustum);
		
		return camFrustum;
	}
	
	//Paramètres
	/** Définit la texture à utiliser pour les dessins suivants.
	 * @return La texture précédemment utilisée.*/
	public Texture setTexture(Texture tex) {
		if (this.texture == null && tex == null) return null;
		
		// On dessine si la texture change réellement
		Texture oldTexture = this.texture;
		
		if (oldTexture == null || tex == null || oldTexture.getGLTextureID() != tex.getGLTextureID()) {
			flush();
		}
		
		// On définit la nouvelle valeur
		if (tex instanceof AnimatedTexture) {
			this.texture = ((AnimatedTexture) tex).get();
		}
		else {
			this.texture = tex;
		}
		
		// Activation ou desactivation des textures
		if ((this.texture != null && this.texture.getGLTextureID() != 0)
				&& (oldTexture == null || oldTexture.getGLTextureID() == 0)) {
			
			this.currentProgram.setUniform1i(StdUniform.USE_TEXTURES, GL11.GL_TRUE);
		}
		else if ((oldTexture != null && oldTexture.getGLTextureID() != 0)
				&& (this.texture == null || this.texture.getGLTextureID() == 0)) {
			
			this.currentProgram.setUniform1i(StdUniform.USE_TEXTURES, GL11.GL_FALSE);
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
		if (viewport == this.viewport) return;
		
		flush();
		
		if (this.viewport != null) {
			if (this.viewport.myVAO != null) 
				this.viewport.myVAO.destroyAll();
		}
		
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}
	
	private void drawViewport() {
		if (this.viewport == null) return;
		
		if (this.viewport.myVAO == null) {
			VAO vao = new VAO();
			vao.setAttrib(StdAttrib.VERTEX, new VBO(), 3, GL11.GL_FLOAT);
			Shape f = viewport.getViewport();
			
			Vec2d[] s = f.getVertices();
			int vertCount = Math.max(0, (s.length - 2) * 3);
			FloatBuffer vertices = BufferUtils.createFloatBuffer(vertCount * 3);
			
			for (int i = 1 ; i < s.length - 1 ; i++) {
				vertices.put(new float[]{(float) s[0].x, (float) s[0].y, 0});
				vertices.put(new float[]{(float) s[i].x, (float) s[i].y, 0});
				vertices.put(new float[]{(float) s[i+1].x, (float) s[i+1].y, 0});
			}
			
			vertices.flip();
			vao.getAttribBuffer(StdAttrib.VERTEX).setData(this, vertices);
			
			this.viewport.vertCount = vertCount;
			this.viewport.myVAO = vao;
		}

		GL11.glColorMask(false, false, false, false);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFFFFFFFF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		this.currentProgram.setUniform1i(StdUniform.STENCIL, GL11.GL_TRUE);
		
		this.viewport.myVAO.bind(this);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.viewport.vertCount);
		
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFFFFFFFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		this.currentProgram.setUniform1i(StdUniform.STENCIL, GL11.GL_FALSE);
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
		Matrix4f model = new Matrix4f();
		multiTransform.applyTransform(model);
		this.currentProgram.setUniformMatrix4f(StdUniform.View.MODEL_MATRIX, model);
	}
	
	/** Sauvegarde l'état des transformation actuel. Retourne un identifiant
	 * correspondant à cet état. L'état pourra ensuite être réactivé par la
	 * méthode {@link #popTransformState(int)} */
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
	
	/** Rétablit l'état indiqué par l'identifiant passé en paramètres.
	 * L'identifiant doit avoir été obtenu par la méthode {@link #pushTransformState()}
	 * pour être valide.
	 * @return <code>true</code> si le rétablissement s'est effectué normalement
	 * <P><i>Note : avec le set de méthodes actuel, le rétablissement d'état
	 * ne peut normalement pas échouer. Ainsi, cette méthode retournera
	 * toujours <code>true</code></i> */
	public boolean popTransformState(int state) {
		if (state > this.transformsSaves.size()) {
			throw new IllegalArgumentException("Cet état n'existe pas. Veuillez utiliser les états retournés par "
					+ "la méthode pushTransformState()");
		}
		
		//Récupération de l'indice dans la liste des transformations.
		Integer Index = null;
		while (this.transformsSaves.size() > state) {
			Index = this.transformsSaves.pollLast();
		}
		int index = Index == null ? 0 : Index;
		
		//Rétablissement de l'ancien état si c'est possible.
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
}
