#pragma once

#include "Constants.h"
#include "Spritesheet.h"

#include <string>
#include <vector>

struct TileData {
    const std::vector<ESprite> Sprites;
    const double SecondsPerFrame;
};

struct TileMapData {

    TileMapData(size_t width = 1, size_t height = 1, size_t numLayers = 1);
    TileMapData(const std::string& filepath);
    TileMapData(const TileMapData& other);
    ~TileMapData();

    size_t Width;
    size_t Height;
    std::vector<ETile*> TileData;
    bool* CollisionData;
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
    TileLayer(size_t width, size_t height, ETile* tileData);
    TileLayer(TileLayer&& rhs) noexcept;
    ~TileLayer();
    void Update(double deltaTime);
    void Render(const glm::mat4& projectionMatrix);

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

    size_t width = 0;
    size_t height = 0;
    ETile* tiles = nullptr;

};

class TileMap {
public:
    TileMap(EMap emap);
    void Update(double deltaTime);
    void Render(const glm::mat4& projectionMatrix);

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

    TileMapData mapData;
    std::vector<TileLayer> tileLayers;
};
