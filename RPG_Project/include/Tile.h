#pragma once

#include "Constants.h"
#include "Spritesheet.h"

#include <string>
#include <vector>

struct TileData {
    const std::vector<ESprite> Sprites;
    const double SecondsPerFrame;
};

class Tile {

public:
    static void sInitializeTiles();
    static void sUpdateTiles(double deltaTime);
    static const Tile& sGetTile(ETile tileType);
    static bool sTileIsDirty();
    static void sCleanDirtyTile();

    Tile();
    Tile(const std::vector<ESprite>& esprites, double secondsPerFrame);

    const Sprite& GetCurrentSprite() const;

private:
    static TileData s_tileData[];
    static Tile s_tiles[static_cast<size_t>(ETile::NUM_TILES_OR_EMPTY)];
    static bool s_isDirty;

    bool update(double deltaTime);

    std::vector<const Sprite*> sprites;
    size_t currentFrameIndex = 0;
    double secondsPerFrame = 0;
    double elapsedTime = 0.0;

};

class TileLayer {
public:
    TileLayer(unsigned int width, unsigned int height, ETile* tileData = nullptr);
    ~TileLayer();
    void Init();
    void Update(double deltaTime);
    void Render(const glm::mat4& projectionMatrix);
    void Teardown() const;

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
    TileMap(unsigned int width, unsigned int height, unsigned int numLayers);
    TileMap(const std::string& path);
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

    std::vector<TileLayer> tileLayers;

    unsigned int width = 0;
    unsigned int height = 0;
    bool* collisions = nullptr;
};
