#pragma once

#include "Constants.h"

#include <unordered_map>

#include "Shader.h"
#include "Spritesheet.h"
#include "Texture.h"

struct ShaderData {
    const char* VertexShaderPath;
    const char* FragmentShaderPath;
};

struct TextureData {
    const char* FilePath;
};

struct SpritesheetData {
    const ETexture ETexture;
    const unsigned int TileWidth;
    const unsigned int TileHeight;
};

struct SpriteData {
    const ESpritesheet ESpritesheet;
    const unsigned int TileX;
    const unsigned int TileY;
};

class ResourceManager {
public:
    void LoadResources();

    // Resource fetching methods
    const Shader& GetShader(EShader eshader) const;
    const Texture& GetTexture(ETexture etexture) const;
    const Sprite& GetSprite(ESprite esprite) const;

private:
    const Spritesheet& getSpritesheet(ESpritesheet espritesheet) const;

    void loadShaders() const;
    void loadTextures() const;
    void loadSpritesheets() const;
    void loadSprites() const;

private:
    mutable std::unordered_map<EShader, Shader> loadedShaders;
    mutable std::unordered_map<ETexture, Texture> loadedTextures;
    mutable std::unordered_map<ESpritesheet, Spritesheet> loadedSpritesheets;
    mutable std::unordered_map<ESprite, Sprite> loadedSprites;

    static const ShaderData s_shaderData[];
    static const TextureData s_textureData[];
    static const SpritesheetData s_spritesheetData[];
    static const SpriteData s_spriteData[];
};

