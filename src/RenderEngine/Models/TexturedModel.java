package RenderEngine.Models;

public class TexturedModel {

    private final RawModel rawModel;
    private final ModelTexture modelTexture;

    public TexturedModel(RawModel rawModel, ModelTexture modelTexture) {
        this.rawModel = rawModel;
        this.modelTexture = modelTexture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getModelTexture() {
        return modelTexture;
    }
}
