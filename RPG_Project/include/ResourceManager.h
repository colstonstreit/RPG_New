#pragma once

#include "Constants.h"

class Shader;
class Texture;
class Spritesheet;

class ResourceManager {
public:

    ResourceManager();
    ~ResourceManager();

    void loadResources();

    // Resource fetching methods
    const Shader& getShader(EShader eshader) const;
    const Texture& getTexture(ETexture etexture) const;
    const Spritesheet& getSpritesheet(ESpritesheet espritesheet) const;

private:
    void loadShaders();
    void loadTextures();
    void loadSpritesheets();

private:
    Shader* shaderArray;
    Texture* textureArray;
    Spritesheet* spritesheetArray;
};

