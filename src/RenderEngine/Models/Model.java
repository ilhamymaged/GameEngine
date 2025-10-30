package RenderEngine.Models;

import CoreEngine.DataLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public final class Model {

    /** Vertex/index bundle + texture that goes with it. */
    public record MeshAsset(RawModel rawModel, ModelTexture texture) {}

    /** One imported model may contain several sub-meshes/materials. */
    public record ModelAsset(List<MeshAsset> meshes) {}

    private static final int ASSIMP_FLAGS =
            aiProcess_Triangulate |
                    aiProcess_JoinIdenticalVertices |
                    aiProcess_GenSmoothNormals |
                    aiProcess_FlipUVs |
                    aiProcess_ImproveCacheLocality;

    private static final Map<String, ModelTexture> textureCache = new HashMap<>();

    private Model() {
    }

    public static ModelAsset loadModel(String fileName) {
        String modelPath = "src/Assets/Models/" + fileName;
        Path modelDir = Paths.get(modelPath).getParent();

        try (AIScene scene = aiImportFile(modelPath, ASSIMP_FLAGS)) {
            if (scene == null || scene.mNumMeshes() == 0) {
                throw new IllegalStateException("Assimp failed: " + aiGetErrorString());
            }

            PointerBuffer meshBuffer = Objects.requireNonNull(scene.mMeshes());
            PointerBuffer materialBuffer = Objects.requireNonNull(scene.mMaterials());

            List<MeshAsset> meshes = new ArrayList<>();

            for (int meshIdx = 0; meshIdx < scene.mNumMeshes(); meshIdx++) {
                AIMesh mesh = AIMesh.create(meshBuffer.get(meshIdx));
                RawModel rawModel = toRawModel(mesh);

                ModelTexture texture = resolveDiffuseTexture(
                        mesh.mMaterialIndex(),
                        materialBuffer,
                        modelDir
                );

                meshes.add(new MeshAsset(rawModel, texture));
            }

            return new ModelAsset(meshes);
        }
    }

    private static RawModel toRawModel(AIMesh mesh) {
        int vertexCount = mesh.mNumVertices();
        int faceCount = mesh.mNumFaces();

        float[] positions = new float[vertexCount * 3];
        float[] normals   = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        int[]   indices   = new int[faceCount * 3];

        AIVector3D.Buffer posBuf = Objects.requireNonNull(mesh.mVertices(), "Mesh has no positions");
        AIVector3D.Buffer normBuf = Objects.requireNonNull(mesh.mNormals(), "Mesh has no normals");
        AIVector3D.Buffer texBuf  = Objects.requireNonNull(mesh.mTextureCoords(0), "Mesh has no texCoords"); // 0 -> first UV set

        //Getting positions, normals, texCoordinates
        for (int i = 0; i < vertexCount; i++) {
            AIVector3D pos = posBuf.get(i);
            positions[i * 3]     = pos.x();
            positions[i * 3 + 1] = pos.y();
            positions[i * 3 + 2] = pos.z();

            AIVector3D n = normBuf.get(i);
            normals[i * 3]     = n.x();
            normals[i * 3 + 1] = n.y();
            normals[i * 3 + 2] = n.z();

            AIVector3D t = texBuf.get(i);
            texCoords[i * 2]     = t.x();
            texCoords[i * 2 + 1] = t.y();
        }

        //Getting indices
        AIFace.Buffer faceBuf = Objects.requireNonNull(mesh.mFaces(), "Mesh has no faces");
        for (int i = 0; i < faceCount; i++) {
            AIFace face = faceBuf.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("Non-triangulated face encountered; expected 3 indices.");
            }
            indices[i * 3]     = face.mIndices().get(0);
            indices[i * 3 + 1] = face.mIndices().get(1);
            indices[i * 3 + 2] = face.mIndices().get(2);
        }

        return DataLoader.loadRawModel(positions, normals, texCoords, indices);
    }

    private static ModelTexture resolveDiffuseTexture(int materialIndex,
                                                      PointerBuffer materialBuffer,
                                                      Path modelDirectory) {
        if (materialIndex < 0 || materialIndex >= materialBuffer.capacity()) {
            return defaultWhiteTexture();
        }

        AIMaterial mat = AIMaterial.create(materialBuffer.get(materialIndex));

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIString path = AIString.malloc(stack);
            IntBuffer texFlags = stack.mallocInt(1);

            int result = aiGetMaterialTexture(mat, aiTextureType_DIFFUSE, 0,
                    path, null, null, null, null, texFlags, null);

            if (result == aiReturn_SUCCESS) {
                String texName = path.dataString().replace("\\", "/");
                Path resolved = resolveTexturePath(modelDirectory, texName);
                return textureCache.computeIfAbsent(resolved.toString(), Model::loadTexture);
            }
        }

        return defaultWhiteTexture();
    }

    private static Path resolveTexturePath(Path modelDirectory, String textureFromMtl) {
        Path texPath = Paths.get(textureFromMtl);
        if (texPath.isAbsolute()) {
            return texPath;
        }
        Path resolved = modelDirectory.resolve(texPath).normalize();
        if (Files.exists(resolved)) {
            return resolved;
        }
        // fallback: look inside src/Assets/Textures as before
        Path fallback = Paths.get("src/Assets/Textures").resolve(texPath.getFileName()).normalize();
        return Files.exists(fallback) ? fallback : resolved;
    }

    private static ModelTexture loadTexture(String absolutePath) {
        // DataLoader expects only the file name; extend it to take absolute paths.
        int textureId = DataLoader.loadTextureFromAbsolutePath(absolutePath);
        return new ModelTexture(textureId);
    }

    private static ModelTexture defaultWhiteTexture() {
        return textureCache.computeIfAbsent("__white__", key -> {
            int texId = DataLoader.create1x1Texture(0xFFFFFFFF);
            return new ModelTexture(texId);
        });
    }
}