#pragma once

#include "Constants.h"

#include <glm/glm.hpp>

#include <vector>

struct Sprite {
    const ETexture texture;
    const glm::vec2 topLeftUV;
    const glm::vec2 topRightUV;
    const glm::vec2 bottomLeftUV;
    const glm::vec2 bottomRightUV;

    Sprite(ETexture texture);
    Sprite(ETexture texture, glm::vec2 topLeftUV, glm::vec2 topRightUV, glm::vec2 bottomLeftUV, glm::vec2 bottomRightUV);
};

class Spritesheet {

public:
    Spritesheet(ETexture texture, unsigned int tileWidth, unsigned int tileHeight);
    Sprite Crop(unsigned int tileX, unsigned int tileY) const;

private:
    const ETexture texture;
    const unsigned int tileWidth;
    const unsigned int tileHeight;
};

