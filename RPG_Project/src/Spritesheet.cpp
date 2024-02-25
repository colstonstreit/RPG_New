#include "Spritesheet.h"

#include "Texture.h"

Sprite::Sprite(const Texture& texture)
    : texture(texture), topLeftUV({ 0.0f, 1.0f }), topRightUV({ 1.0f, 1.0f }), bottomLeftUV({ 0.0f, 0.0f }), bottomRightUV({ 1.0f, 0.0f }) {}

Sprite::Sprite(const Texture& texture, glm::vec2 topLeftUV, glm::vec2 topRightUV, glm::vec2 bottomLeftUV, glm::vec2 bottomRightUV)
    : texture(texture), topLeftUV(topLeftUV), topRightUV(topRightUV), bottomLeftUV(bottomLeftUV), bottomRightUV(bottomRightUV) {}

Spritesheet::Spritesheet(const Texture& texture, unsigned int tileWidth, unsigned int tileHeight)
    : texture(texture), tileWidth(tileWidth), tileHeight(tileHeight) {}

Sprite Spritesheet::crop(unsigned int tileX, unsigned int tileY) const {
    const float uvPerTileX = (float) this->tileWidth / this->texture.getWidth();
    const float uvPerTileY = (float) this->tileHeight / this->texture.getHeight();

    const glm::vec2 topLeftUV = { tileX * uvPerTileX, 1.0f - tileY * uvPerTileY };
    const glm::vec2 topRightUV = glm::vec2(topLeftUV.x + uvPerTileX, topLeftUV.y);
    const glm::vec2 bottomLeftUV = glm::vec2(topLeftUV.x, topLeftUV.y - uvPerTileY);
    const glm::vec2 bottomRightUV = glm::vec2(topLeftUV.x + uvPerTileX, topLeftUV.y - uvPerTileY);

    return Sprite(this->texture, topLeftUV, topRightUV, bottomLeftUV, bottomRightUV);
}
