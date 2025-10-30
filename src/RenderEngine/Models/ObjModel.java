package RenderEngine.Models;

import CoreEngine.DataLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ObjModel {
    public static RawModel loadObjModel(String name) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("src/Assets/Models/" + name + ".obj");
        }catch (FileNotFoundException e) {
            System.err.println("COULDN'T LOAD THIS OBJ MODEL: " + name);
            e.printStackTrace();
        }

        assert fileReader != null;
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int[] indicesArray;
        float[] positionArray;
        float[] normalArray = new float[0];
        float[] texCoordsArray = new float[0];

        try {
            while(true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if(line.startsWith("v ")) {
                    Vector3f pos = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    positions.add(pos);
                }
                else if(line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }
                else if(line.startsWith("vt ")) {
                    Vector2f texCoord = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    texCoords.add(texCoord);
                }
                else if(line.startsWith("f ")) {
                    texCoordsArray = new float[positions.size() * 2];
                    normalArray = new float[positions.size() * 3];
                    break;
                }
            }

            while(line != null) {
                if(!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indices, texCoords, normals, texCoordsArray, normalArray);
                processVertex(vertex2, indices, texCoords, normals, texCoordsArray, normalArray);
                processVertex(vertex3, indices, texCoords, normals, texCoordsArray, normalArray);
                line = reader.readLine(); //read the next line
            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        positionArray = new float[positions.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f pos: positions) {
            positionArray[vertexPointer++] = pos.x;
            positionArray[vertexPointer++] = pos.y;
            positionArray[vertexPointer++] = pos.z;
        }

        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return DataLoader.loadRawModel(positionArray, normalArray, texCoordsArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> texCoords,
                                      List<Vector3f> normals, float[] texCoordsArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTexCoords = texCoords.get(Integer.parseInt(vertexData[1]) - 1);
        texCoordsArray[currentVertexPointer * 2] = currentTexCoords.x;
        texCoordsArray[currentVertexPointer * 2 + 1] = currentTexCoords.y;
        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;
    }
}
