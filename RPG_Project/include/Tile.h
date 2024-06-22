#pragma once

#include "Constants.h"
#include "Spritesheet.h"

#include <string>
#include <vector>

class Game;
class ResourceManager;

class Tile {

public:

    static void sInitializeTiles();
    static void sUpdateTiles(double deltaTime);
    static const Tile* sGetTile(ETile tileType);
    static bool sTileIsDirty();
    static void sCleanDirtyTile();

    Tile(ETile tileType);
    virtual const Sprite& GetCurrentSprite() const = 0;

protected:
    ETile tileType;

private:
    virtual bool update(double deltaTime) = 0;
    static std::vector<Tile*> s_Tiles;
    static bool s_IsDirty;
};

class TileStatic : public Tile {

public:
    TileStatic(ETile tileType, Sprite* sprite);
    ~TileStatic();
    const Sprite& GetCurrentSprite() const override;

private:
    bool update(double deltaTime) override;
    Sprite* sprite;
};

class TileAnimated : public Tile {

public:
    TileAnimated(ETile tileType, Sprite* animationFrames, unsigned int numberOfFrames, float secondsPerFrame);
    ~TileAnimated();
    const Sprite& GetCurrentSprite() const override;

private:
    bool update(double deltaTime) override;

    double elapsedTime = 0.0;
    unsigned int currentFrameIndex = 0;
    float secondsPerFrame;
    unsigned int numberOfFrames;
    Sprite* animationFrames;
};


class TileLayer {
public:
    TileLayer(Game& game, unsigned int width, unsigned int height, ETile* tileData = nullptr);
    ~TileLayer();
    void Init();
    void Update(double deltaTime);
    void Render(const glm::mat4& projectionMatrix);
    void Teardown();

private:
    struct Vertex {
        float x;
        float y;
        float r;
        float g;
        float b;
        float u;
        float v;
    };

    const Game& game;

    unsigned int VAO = 0;
    unsigned int VBOStatic = 0;
    unsigned int VBODynamic = 0;
    unsigned int EBO = 0;

    unsigned int width = 0;
    unsigned int height = 0;
    ETile* tiles = nullptr;

};

class TileMap {
public:
    TileMap(Game& game, unsigned int width, unsigned int height, unsigned int numLayers);
    TileMap(Game& game, const std::string& path);
    ~TileMap();

    void Init();
    void Update(double deltaTime);
    void Render(const glm::mat4& projectionMatrix);
    void Teardown();

private:
    struct Vertex {
        float x;
        float y;
        float r;
        float g;
        float b;
        float u;
        float v;
    };

    const Game& game;
    std::vector<TileLayer> tileLayers;

    unsigned int width = 0;
    unsigned int height = 0;
    bool* collisions = nullptr;

};
