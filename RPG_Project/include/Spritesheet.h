#pragma once

#include <glm/glm.hpp>

#include <vector>

class Texture;

struct Sprite {
    const Texture& texture;
    const glm::vec2 topLeftUV;
    const glm::vec2 topRightUV;
    const glm::vec2 bottomLeftUV;
    const glm::vec2 bottomRightUV;

    Sprite(const Texture& texture);
    Sprite(const Texture& texture, glm::vec2 topLeftUV, glm::vec2 topRightUV, glm::vec2 bottomLeftUV, glm::vec2 bottomRightUV);
};

class Spritesheet {

public:
    Spritesheet(const Texture& texture, unsigned int tileWidth, unsigned int tileHeight);
    Sprite crop(unsigned int tileX, unsigned int tileY) const;

private:
    const Texture& texture;
    const unsigned int tileWidth;
    const unsigned int tileHeight;
};

