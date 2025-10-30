package RenderEngine.Models;

public class RawModel {
    int VAO;
    int indicesCount;

    public RawModel(int VAO, int indicesCount) {
        this.VAO = VAO;
        this.indicesCount = indicesCount;
    }

    public int getVAO() {
        return VAO;
    }

    public void setVAO(int VAO) {
        this.VAO = VAO;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void setIndicesCount(int indicesCount) {
        this.indicesCount = indicesCount;
    }
}
