#pragma once

#include "Constants.h"

class Shader;
class Texture;
class Spritesheet;

class ResourceManager {
public:

    // Singleton setup
    static ResourceManager& sGet();
    ResourceManager(ResourceManager& other) = delete;
    void operator=(const ResourceManager&) = delete;

    static void LoadResources();

    // Resource fetching methods
    static const Shader& GetShader(EShader eshader);
    static const Texture& GetTexture(ETexture etexture);
    static const Spritesheet& GetSpritesheet(ESpritesheet espritesheet);

private:
    ResourceManager();
    ~ResourceManager();

    static void loadShaders();
    static void loadTextures();
    static void loadSpritesheets();

private:
    static ResourceManager s_instance;

    Shader* shaderArray;
    Texture* textureArray;
    Spritesheet* spritesheetArray;
};

