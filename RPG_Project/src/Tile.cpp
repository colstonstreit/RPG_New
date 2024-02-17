#include "Tile.h"

#include "Spritesheet.h"
#include "ResourceManager.h"

void Tile::initializeTiles() {

}

Tile::Tile(TileType tileType) : tileType(tileType) {}

TileStatic::TileStatic(TileType tileType, Sprite* sprite) : Tile(tileType), sprite(sprite) {}

TileStatic::~TileStatic() {
    delete this->sprite;
}

void TileStatic::update(double deltaTime) {}

const Sprite& TileStatic::getCurrentSprite() {
    return *(this->sprite);
}

TileAnimated::TileAnimated(TileType tileType, Sprite* animationFrames, unsigned int numberOfFrames, unsigned int msPerFrame)
    : Tile(tileType), animationFrames(animationFrames), numberOfFrames(numberOfFrames), msPerFrame(msPerFrame) {}

TileAnimated::~TileAnimated() {
    delete[] this->animationFrames;
}

void TileAnimated::update(double deltaTime) {
    this->elapsedTime += deltaTime;
    if (this->elapsedTime >= msPerFrame) {
        this->elapsedTime -= msPerFrame;
        this->currentFrameIndex = (this->currentFrameIndex + 1) % this->numberOfFrames;
    }
}

const Sprite& TileAnimated::getCurrentSprite() {
    return this->animationFrames[this->currentFrameIndex];
}
