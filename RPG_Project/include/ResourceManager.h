#pragma once

#include "Constants.h"

class Shader;
class Texture;
class Spritesheet;

class ResourceManager {
public:
    ResourceManager();
    ~ResourceManager();
    void LoadResources();

    // Resource fetching methods
    const Shader& GetShader(EShader eshader) const;
    const Texture& GetTexture(ETexture etexture) const;
    const Spritesheet& GetSpritesheet(ESpritesheet espritesheet) const;

private:
    void loadShaders();
    void loadTextures();
    void loadSpritesheets();

private:
    Shader* shaderArray;
    Texture* textureArray;
    Spritesheet* spritesheetArray;
};

