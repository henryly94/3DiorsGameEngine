package c2g2.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

    private int vaoId;

    private List<Integer> vboIdList;

    private int vertexCount;

    private Material material;

    private float[] pos = null;
    private float[] textco = null;
    private float[] norms = null;
    private int[] inds = null;
    private Texture texture;



    
    public Mesh(){
       this(new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.5f,0.0f,0.5f,0.0f,0.0f,0.5f,0.5f,0.5f,0.0f,0.0f,0.5f,0.0f,0.5f,0.5f,0.5f,0.0f,0.5f,0.5f,0.5f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f}, 
    			new int[]{0,6,4,0,2,6,0,3,2,0,1,3,2,7,6,2,3,7,4,6,7,4,7,5,0,4,5,0,5,1,1,5,7,1,7,3});
    }


    public void setMesh(float[] positions, float[] textCoords, float[] normals, int[] indices, Texture texture){
    	pos = positions;
    	textco = textCoords;
    	norms = normals;
    	inds = indices;

        if (texture != null) {
            this.texture = texture;
        }

    	FloatBuffer posBuffer = null;
        FloatBuffer textbuf = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        System.out.println("create mesh:");
        System.out.println("v: "+positions.length+" t: "+textCoords.length+" n: "+normals.length+" idx: "+indices.length);
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<Integer>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);


            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0,3, GL_FLOAT, false,0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textbuf = MemoryUtil.memAllocFloat(textCoords.length);
            textbuf.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textbuf, GL_STATIC_DRAW);
            glVertexAttribPointer(1,2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false,0, 0);


            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);



            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textbuf != null) {
                MemoryUtil.memFree(textbuf);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }


    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
    	setMesh(positions, textCoords, normals, indices, null);
    }


    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, Texture texture) {
        setMesh(positions, textCoords, normals, indices, texture);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        if (texture != null){
            material.setTextured(true);
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {

        // Draw the mesh

        // Bind Texture
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);

            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }


        glBindVertexArray(getVaoId());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);


        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }
        // Delete the Texture
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void cleanMesh(){

        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

    }
    
    public void scaleMesh(float sx, float sy, float sz){
    	cleanMesh(); //clean up buffer
    	//Reset position of each point
    	//Do not change textco, norms, inds
    	//student code 
    	for (int i = 0; i < pos.length/3; i++) {
            pos[i*3] *= sx;
            pos[i*3 + 1] *= sy;
            pos[i*3 + 2] *= sz;
		}   	
    	setMesh(pos, textco, norms, inds, texture);
    }
    
    public void translateMesh(Vector3f trans){
    	cleanMesh();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
    	for(int i=0; i< pos.length/3; i++){
            pos[i*3] += trans.x;
            pos[i*3 + 1] += trans.y;
            pos[i*3 + 2] += trans.z;
    		
    	}
    	setMesh(pos, textco, norms, inds, texture);
    }
    
    public void rotateMesh(Vector3f axis, float angle){
    	cleanMesh();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        float tmp_x, tmp_y, tmp_z;
//        AxisAngle4f angle4f = new AxisAngle4f(axis.x, axis.y, axis.z, angle);
//        Quaternionf q = new Quaternionf(angle4f);
    	for(int i=0; i< pos.length/3; i++){
    		tmp_x = (cos + (1-cos) * axis.x * axis.x) * pos[i*3] +
                    ((1-cos) * axis.x * axis.y - sin * axis.z) * pos[i*3+1] +
                    ((1-cos) * axis.x * axis.z + sin * axis.y) * pos[i*3+2];
    		tmp_y = ((1-cos) * axis.y * axis.x + sin * axis.z) * pos[i*3] +
                    (cos + (1-cos) * axis.y * axis.y) * pos[i*3+1] +
                    ((1-cos) * axis.y * axis.z - sin * axis.x) * pos[i*3+2];
    		tmp_z = ((1-cos) * axis.z * axis.x - sin * axis.y) * pos[i*3] +
                    ((1-cos) * axis.z * axis.y + sin * axis.x) * pos[i*3+1] +
                    (cos + (1-cos) * axis.z * axis.z) * pos[i*3+2];
    		pos[i*3] = tmp_x;
    		pos[i*3+1] = tmp_y;
    		pos[i*3+2] = tmp_z;
    	}
    	setMesh(pos, textco, norms, inds, texture);
    }
    
    public void reflectMesh(Vector3f p, Vector3f n){
    	cleanMesh();
    	//reset position of each point
    	//Do not change textco, norms, inds
    	//student code
    	for(int i=0; i< pos.length/3; i++){
    	    Vector3f x = new Vector3f(pos[i*3]-p.x, pos[i*3+1]-p.y, pos[i*3+2]-p.z).reflect(n);
    	    pos[i*3] = x.x + p.x;
    	    pos[i*3+1] = x.y + p.y;
    	    pos[i*3+2] = x.z + p.z;
    	}
    	setMesh(pos, textco, norms, inds, texture);
    }

    public Texture getTexture() {
        return texture;
    }
}
