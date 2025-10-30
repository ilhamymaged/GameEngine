package RenderEngine.Models;

import CoreEngine.DataLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;

public final class Model {

    private static final int ASSIMP_FLAGS =
            aiProcess_Triangulate |
                    aiProcess_JoinIdenticalVertices |
                    aiProcess_GenSmoothNormals |
                    aiProcess_FlipUVs |
                    aiProcess_ImproveCacheLocality;

    private Model() {
    }

    public static RawModel loadModel(String fileName) {
        String absolutePath = "src/Assets/Models/" + fileName;

        try (AIScene scene = aiImportFile(absolutePath, ASSIMP_FLAGS)) {
            if (scene == null || scene.mNumMeshes() == 0) {
                throw new IllegalStateException("Assimp failed: " + aiGetErrorString());
            }

            PointerBuffer meshBuffer = Objects.requireNonNull(scene.mMeshes(), "Scene has no mesh buffer");
            AIMesh mesh = AIMesh.create(meshBuffer.get(0)); // first mesh; extend if you need them all

            int vertexCount = mesh.mNumVertices();
            int faceCount = mesh.mNumFaces();

            float[] positions = new float[vertexCount * 3];
            float[] normals   = new float[vertexCount * 3];
            float[] texCoords = new float[vertexCount * 2];
            int[]   indices   = new int[faceCount * 3];

            // --- Vertex attributes ---
            AIVector3D.Buffer positionBuf = Objects.requireNonNull(mesh.mVertices(), "Mesh has no positions");
            AIVector3D.Buffer normalBuf   = mesh.mNormals();
            AIVector3D.Buffer texBuf      = mesh.mTextureCoords(0); // second channel if you ever need it

            for (int i = 0; i < vertexCount; i++) {
                AIVector3D pos = positionBuf.get(i);
                positions[i * 3]     = pos.x();
                positions[i * 3 + 1] = pos.y();
                positions[i * 3 + 2] = pos.z();

                if (normalBuf != null) {
                    AIVector3D normal = normalBuf.get(i);
                    normals[i * 3]     = normal.x();
                    normals[i * 3 + 1] = normal.y();
                    normals[i * 3 + 2] = normal.z();
                } else {
                    // keep array in sync; Assimp should have generated them but fall back to zeroed vectors
                    normals[i * 3]     = 0f;
                    normals[i * 3 + 1] = 0f;
                    normals[i * 3 + 2] = 0f;
                }

                if (texBuf != null) {
                    AIVector3D tex = texBuf.get(i);
                    texCoords[i * 2]     = tex.x();  // z is unused for standard UVs
                    texCoords[i * 2 + 1] = tex.y();
                } else {
                    texCoords[i * 2]     = 0f;
                    texCoords[i * 2 + 1] = 0f;
                }
            }

            // --- Indices ---
            AIFace.Buffer faceBuf = Objects.requireNonNull(mesh.mFaces(), "Mesh has no faces");
            for (int i = 0; i < faceCount; i++) {
                AIFace face = faceBuf.get(i);
                if (face.mNumIndices() != 3) {
                    throw new IllegalStateException("Non-triangulated face encountered (index count = " + face.mNumIndices() + ")");
                }
                indices[i * 3]     = face.mIndices().get(0);
                indices[i * 3 + 1] = face.mIndices().get(1);
                indices[i * 3 + 2] = face.mIndices().get(2);
            }

            return DataLoader.loadRawModel(positions, normals, texCoords, indices);
        }
    }
}