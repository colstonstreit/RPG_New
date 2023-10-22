#pragma once

#include <unordered_map>

#define NUM_SHADERS (1)
#define NUM_TEXTURES (2)

class Shader;
class Texture;

class ResourceManager {
public:

    // Enums for shaders and textures in application
    enum class EShader { DEFAULT };
    enum class ETexture { FACE, BOX };

    static_assert(static_cast<int>(EShader::DEFAULT) == NUM_SHADERS - 1, "NUM_SHADERS does not match");
    static_assert(static_cast<int>(ETexture::BOX) == NUM_TEXTURES - 1, "NUM_TEXTURES does not match");

    ResourceManager();
    ~ResourceManager();

    void loadResources();

    // Resource fetching methods
    const Shader& getShader(EShader eshader) const;
    const Texture& getTexture(ETexture texture) const;

private:
    void loadShaders();
    void loadTextures();

private:
    Shader* shaderArray;
    Texture* textureArray;
};

