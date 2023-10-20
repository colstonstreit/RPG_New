#pragma once

#include <unordered_map>

class Texture;
class Shader;

class ResourceManager {
public:

    // Enums for shaders and textures in application
    enum class EShader { DEFAULT };
    enum class ETexture { FACE, BOX };

    void loadResources();

    // Resource fetching methods
    Shader& getShader(EShader eshader);
    Texture& getTexture(ETexture texture);

private:
    void loadShaders();
    void loadTextures();

private:
    std::unordered_map<EShader, Shader> shaderMap;
    std::unordered_map<ETexture, Texture> textureMap;


};

