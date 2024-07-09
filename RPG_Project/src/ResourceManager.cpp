#include "ResourceManager.h"

#include "Shader.h"
#include "Texture.h"
#include "Spritesheet.h"

const ShaderData ResourceManager::s_shaderData[] = { SHADER_DATA(SHADER_TO_TABLE) };
const TextureData ResourceManager::s_textureData[] = { TEXTURE_DATA(TEXTURE_TO_TABLE) };
const SpritesheetData ResourceManager::s_spritesheetData[] = { SPRITESHEET_DATA(SPRITESHEET_TO_TABLE) };
const SpriteData ResourceManager::s_spriteData[] = { SPRITE_DATA(SPRITE_TO_TABLE) };
const MapData ResourceManager::s_mapData[] = { MAP_DATA(MAP_TO_TABLE) };

void ResourceManager::LoadResources() {
    loadShaders();
    loadTextures();
    loadSpritesheets();
    loadSprites();
    loadMaps();
}

const Shader& ResourceManager::GetShader(EShader eshader) const {
    auto result = this->loadedShaders.find(eshader);
    if (result == this->loadedShaders.end()) {
        const ShaderData& shaderData = ResourceManager::s_shaderData[static_cast<size_t>(eshader)];
        this->loadedShaders.try_emplace(eshader, shaderData.VertexShaderPath, shaderData.FragmentShaderPath);
        return this->loadedShaders.at(eshader);
    } else {
        return result->second;
    }
}

const Texture& ResourceManager::GetTexture(ETexture etexture) const {
    auto result = this->loadedTextures.find(etexture);
    if (result == this->loadedTextures.end()) {
        const TextureData& textureData = ResourceManager::s_textureData[static_cast<size_t>(etexture)];
        this->loadedTextures.try_emplace(etexture, textureData.FilePath);
        return this->loadedTextures.at(etexture);
    } else {
        return result->second;
    }
}

const Spritesheet& ResourceManager::getSpritesheet(ESpritesheet espritesheet) const {
    auto result = this->loadedSpritesheets.find(espritesheet);
    if (result == this->loadedSpritesheets.end()) {
        const SpritesheetData& spritesheetData = ResourceManager::s_spritesheetData[static_cast<size_t>(espritesheet)];
        this->loadedSpritesheets.try_emplace(espritesheet, spritesheetData.ETexture, spritesheetData.TileWidth, spritesheetData.TileHeight);
        return this->loadedSpritesheets.at(espritesheet);
    } else {
        return result->second;
    }
}

const Sprite& ResourceManager::GetSprite(ESprite esprite) const {
    auto result = this->loadedSprites.find(esprite);
    if (result == this->loadedSprites.end()) {
        const SpriteData& spriteData = ResourceManager::s_spriteData[static_cast<size_t>(esprite)];
        this->loadedSprites.try_emplace(esprite, ResourceManager::getSpritesheet(spriteData.ESpritesheet).Crop(spriteData.TileX, spriteData.TileY));
        return this->loadedSprites.at(esprite);
    } else {
        return result->second;
    }
}

const TileMap& ResourceManager::GetMap(EMap emap) const {
    auto result = this->loadedMaps.find(emap);
    if (result == this->loadedMaps.end()) {
        const MapData& mapData = ResourceManager::s_mapData[static_cast<size_t>(emap)];
        this->loadedMaps.try_emplace(emap, mapData.FilePath);
        return this->loadedMaps.at(emap);
    } else {
        return result->second;
    }
}

void ResourceManager::loadShaders() const {
    for (size_t i = 0; i < static_cast<size_t>(EShader::NUM_SHADERS_OR_INVALID); i++) {
        this->GetShader(static_cast<EShader>(i));
    }
}

void ResourceManager::loadTextures() const {
    for (size_t i = 0; i < static_cast<size_t>(ETexture::NUM_TEXTURES_OR_INVALID); i++) {
        this->GetTexture(static_cast<ETexture>(i));
    }
}

void ResourceManager::loadSpritesheets() const {
    for (size_t i = 0; i < static_cast<size_t>(ESpritesheet::NUM_SPRITESHEETS_OR_INVALID); i++) {
        this->getSpritesheet(static_cast<ESpritesheet>(i));
    }
}

void ResourceManager::loadSprites() const {
    for (size_t i = 0; i < static_cast<size_t>(ESprite::NUM_SPRITES_OR_INVALID); i++) {
        this->GetSprite(static_cast<ESprite>(i));
    }
}

void ResourceManager::loadMaps() const {
    for (size_t i = 0; i < static_cast<size_t>(EMap::NUM_MAPS_OR_INVALID); i++) {
        this->GetMap(static_cast<EMap>(i));
    }
}
