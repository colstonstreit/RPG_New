#pragma once

#include "Constants.h"

#include <unordered_map>

#include "Shader.h"
#include "Spritesheet.h"
#include "Texture.h"
#include "Tile.h"

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

struct MapData {
    const char* FilePath;
};

class ResourceManager {
public:
    void LoadResources();

    // Resource fetching methods
    const Shader& GetShader(EShader eshader) const;
    const Texture& GetTexture(ETexture etexture) const;
    const Sprite& GetSprite(ESprite esprite) const;
    const TileMap& GetMap(EMap emap) const;

private:
    const Spritesheet& getSpritesheet(ESpritesheet espritesheet) const;

    void loadShaders() const;
    void loadTextures() const;
    void loadSpritesheets() const;
    void loadSprites() const;
    void loadMaps() const;

private:
    mutable std::unordered_map<EShader, Shader> loadedShaders;
    mutable std::unordered_map<ETexture, Texture> loadedTextures;
    mutable std::unordered_map<ESpritesheet, Spritesheet> loadedSpritesheets;
    mutable std::unordered_map<ESprite, Sprite> loadedSprites;
    mutable std::unordered_map<EMap, TileMap> loadedMaps;

    static const ShaderData s_shaderData[];
    static const TextureData s_textureData[];
    static const SpritesheetData s_spritesheetData[];
    static const SpriteData s_spriteData[];
    static const MapData s_mapData[];
};

