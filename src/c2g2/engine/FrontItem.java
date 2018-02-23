package c2g2.engine;

import c2g2.engine.graph.Mesh;
import c2g2.engine.graph.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class FrontItem extends GameItem {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private String text;

    private Texture texture;

    private int numCols;

    private int numRows;
    public FrontItem(String text, String fontFileName, int numCols, int numRows ) throws IOException {
        super(new Mesh());
        this.text = text;
        this.numCols = numCols;
        this.numRows = numRows;
        texture = new Texture(0);
        texture.setTexture(fontFileName);
        buildMesh(texture, this.numCols, this.numRows);
    }

    public FrontItem(Mesh mesh){
        super(mesh);
    }

    private void buildMesh(Texture texture, int numCols, int numRows){
        byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
        int numChars = chars.length;
        System.out.println("Here:" + new String(chars));

        List<Float> positions = new ArrayList();
        List<Float> textCoords = new ArrayList();
        float[] normals   = new float[]{0, 0, 1};
        List<Integer> indices   = new ArrayList();

        float tileWidth = (float)texture.getWidth() / (float)numCols;
        float tileHeight = (float)texture.getHeight() / (float)numRows;
        for(int i=0; i<numChars; i++) {
            byte currChar = chars[i];
            System.out.println("Here>>" + currChar);
            int col = currChar % numCols;
            int row = currChar / numCols;
            System.out.println(col + "|" + row);

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add((float)i*tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            indices.add(i*VERTICES_PER_QUAD);
            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );

            // Left Bottom vertex
            positions.add((float)i*tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z

            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
        }
        float[] pos = new float[positions.size()];
        float[] textCoord = new float[textCoords.size()];
        int[] indice = new int[indices.size()];
        for (int i=0; i<pos.length; i++){
            pos[i] = positions.get(i);
        }
        for (int i=0; i<textCoord.length; i++){
            textCoord[i] = textCoords.get(i);
        }
        for (int i=0; i<indice.length; i++){
            indice[i] = indices.get(i);
        }

        getMesh().cleanMesh();
        getMesh().setMesh(pos, textCoord, normals, indice, texture);
    }

    public void changeText(String newText){
        text = newText;
        buildMesh(texture, numCols, numRows);
    }

    public void render() {
        getMesh().render();
    }
}
