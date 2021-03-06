package gameEngine.meshs;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import gameEngine.ShaderProgram;
import gameEngine.items.MyItem;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Mesh {
	private int vaoId;
	
	private int vboVertex;
	private int vboNorm;
	private int vboIndices;
	
	private float[] vertices;
	private float[] normals;
	private int[] indices;

	private int weight;
	
	private Material material;
	
	public Mesh(float[] vertices, float cx, float cy, float cz, float reflectance,float[] normals, int[] indices, int weight) {
		super();
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.material = new Material(new Vector3f(cx, cy, cz), reflectance);		
		this.weight = weight;
	}
	
	public Mesh(float[] vertices, Material material,float[] normals, int[] indices, int weight) {
		super();
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.material = material;
		this.weight = weight;
	}
	
	public Mesh(float[] vertices, Material material,float[] normals, int[] indices) {
		super();
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.material = material;
		this.weight = vertices.length;
	}
	
	public Mesh(float[] vertices, float cx, float cy, float cz, float reflectance,float[] normals, int[] indices) {
		super();
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.material = new Material(new Vector3f(cx, cy, cz), reflectance);		
		this.weight = vertices.length;
	}
	
	public void init(){
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		//Initialization of VBO
		//VBO of vertex
		FloatBuffer verticeBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticeBuffer.put(vertices).flip();
		vboVertex = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertex);
		glBufferData(GL_ARRAY_BUFFER, verticeBuffer, GL_STATIC_DRAW);

		//We explain to OpenGL how to read our Buffers.
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		//VBO of normals
		FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normals.length);
		normBuffer.put(normals).flip();
		vboNorm = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboNorm);
		glBufferData(GL_ARRAY_BUFFER, normBuffer, GL_STATIC_DRAW);

		//We explain to OpenGL how to read our Buffers.
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

		//VBO of indices
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		vboIndices = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
	}
	
	public void draw(MyItem item, ShaderProgram sh,Matrix4f viewMatrix){
		// Bind to the VAO
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		sh.setUniform("material", material);
		sh.setUniform("modelViewMatrix", item.getWorldMatrix());
		// Draw the vertices
		glDrawElements(GL_TRIANGLES, indices.length,GL_UNSIGNED_INT, 0);

		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public void draw(ArrayList<MyItem> items, ShaderProgram sh, Matrix4f viewMatrix){
		// Bind to the VAO
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		sh.setUniform("material", material);
		for(MyItem i: items){
			Matrix4f modelViewMat = new Matrix4f(viewMatrix).mul( i.getWorldMatrix());
			sh.setUniform("modelViewMatrix", modelViewMat);
			// Draw the vertices
			glDrawElements(GL_TRIANGLES, indices.length,GL_UNSIGNED_INT, 0);
		}
		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public void cleanUp(){
		//deallocation of VAO and VBO
		glDisableVertexAttribArray(0);

		// Delete the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboVertex);
		glDeleteBuffers(vboIndices);
		glDeleteBuffers(vboNorm);

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
	
	public void setMaterial(Material material){
		this.material=material;
	}
	
	public float[] getVertices() {
		return vertices;
	}

	public Material getMaterial() {
		return material;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	public int getWeight(){
		return weight;
	}
}
