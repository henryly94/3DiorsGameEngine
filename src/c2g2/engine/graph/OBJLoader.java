package c2g2.engine.graph;

import java.io.BufferedReader;
import java.io.FileReader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;


public class OBJLoader {
    public static Mesh loadMesh(String fileName, String textFileName, boolean dds) throws Exception {
    	//// --- student code ---

        //Initializing
        ArrayList<Float> positions_ = new ArrayList<Float>();
        ArrayList<Float> textCoords_ = new ArrayList<Float>();
        ArrayList<Float> norms_ = new ArrayList<Float>();
        ArrayList<Integer> indices_ = new ArrayList<Integer>();
        boolean hasNorm = false;
        boolean hasTexture = false;

        //Open Obj file
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null){
            if (line.length() > 0){ // skip empty line and only deal with v/vt/vn/f
                switch (line.charAt(0)) {
                    case 'v':
                        switch (line.charAt(1)) {
                            case 'n':// Vertex normal
                                for (String norm : line.substring(3).split(" ")) {
                                    norms_.add(Float.parseFloat(norm));
                                }
                                break;
                            case 't':// Vertex texture
                                int t = 0;
                                for (String textCoord : line.substring(3).split(" ")) {
                                    if (t == 2) break;
                                    if (t == 1) {
                                        if (dds) textCoords_.add(1 -Float.parseFloat(textCoord));
                                        else textCoords_.add(Float.parseFloat(textCoord));
                                    }
                                    if (t == 0) textCoords_.add(Float.parseFloat(textCoord));
                                    t += 1;
                                }
                                break;
                            case ' ':
                                for (String position : line.substring(2).split(" ")) {
                                    positions_.add(Float.parseFloat(position));
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case 'f':
                        String[] words = line.split(" ");
                        for (String word : words){
                            System.out.print(word + ",");
                        }
                        System.out.print(">>>>>");
                        // To deal with surface with more than 3 point
                        // split them into several triangles
                        for (int i = 2; i + 1 < words.length; i++) {
                            for (int j : new int[]{1, i, i + 1}) {
                                System.out.print(words[j] + " ");
                                String[] indices = words[j].split("/");
                                for (String indice : indices) {
                                    if (indice.length() != 0) {
                                        indices_.add(Integer.parseInt(indice) - 1);
                                    } else {
                                        indices_.add(null);
                                    }
                                }
                                for (int k=0; k<3-indices.length; k++){
                                    indices_.add(null);
                                }

                                System.out.print(" | ");
                            }
                        }
                        System.out.println();
                        break;
                    default:
                        break;
                }
            }
        }

        int face_amt = indices_.size() / 9;

        float[] positions = new float[face_amt * 3 *3];
        float[] textCoords = new float[face_amt * 3 * 2];
        float[] norms = new float[face_amt * 3 * 3];
        int[] indices = new int[face_amt * 3];

//        for (int i=0; i<positions_.size()/3; i++){
//            for(int j=0; j<3; j++){
//                positions[i*3+j] = positions_.get(i*3+j);
//            }
//
//        }

        for (int i=0; i<face_amt * 3; i++){
            for (int j=0; j<3; j++){
                positions[i*3+j] = positions_.get(indices_.get(i*3) * 3 + j);
            }
            indices[i] = i;
            if (indices_.get(i*3+1) != null){
                textCoords[i*2] = textCoords_.get(indices_.get(i * 3 + 1) * 2);
                textCoords[i*2+1] = textCoords_.get(indices_.get(i * 3 + 1) * 2 + 1);
            } else {
                textCoords[i*2] = textCoords[i*2+1] = 0.0f;
            }

            if (indices_.get(i*3+2) != null){
                for (int j=0; j<3; j++) {
                    norms[i * 3 + j] = norms_.get(indices_.get(i * 3 + 2) * 3 + j);
                }
            } else {
                norms[i*3] = norms[i*3+1] = norms[i*3+2] = 0.0f;
            }
        }

        Texture texture = null;
        if (textFileName != null) {
            texture = new Texture(0);
            texture.setTexture(textFileName);
        }


        return new Mesh(positions, textCoords, norms, indices, texture);
    }

}
