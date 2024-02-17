#pragma once

#include "Spritesheet.h"

#include <vector>

class Tile {

public:
    enum class TileType { GRASS, SAND, BRICK, WATER, LAVA, TREE, SUN, FLOWER, HOUSE_DOOR, HOUSE_WINDOW, HOUSE_OUTER_WALL, HOUSE_FLOOR, HOUSE_INNER_WALL };

    static void initializeTiles();

    Tile(TileType tileType);
    virtual void update(double deltaTime) = 0;
    virtual const Sprite& getCurrentSprite() = 0;

protected:
    TileType tileType;

private:
    static std::vector<Tile*> tiles;
};

class TileStatic : public Tile {

public:
    TileStatic(TileType tileType, Sprite* sprite);
    ~TileStatic();
    void update(double deltaTime) override;
    const Sprite& getCurrentSprite() override;

private:
    Sprite* sprite;
};

class TileAnimated : public Tile {

public:
    TileAnimated(TileType tileType, Sprite* animationFrames, unsigned int numberOfFrames, unsigned int msPerFrame);
    ~TileAnimated();
    void update(double deltaTime);
    const Sprite& getCurrentSprite() override;

private:
    double elapsedTime = 0.0;
    unsigned int currentFrameIndex = 0;
    unsigned int msPerFrame;
    unsigned int numberOfFrames;
    Sprite* animationFrames;

};

