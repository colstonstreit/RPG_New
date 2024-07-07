#include "Tile.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>

#include <fstream>
#include <iostream>

#include "Game.h"
#include "ResourceManager.h"
#include "Shader.h"
#include "Spritesheet.h"
#include "Texture.h"

/* ======================================== Tile ======================================== */

Tile Tile::s_tiles[] = {};
TileData Tile::s_tileData[] = { TILE_DATA(TILE_TO_TABLE) };

bool Tile::s_isDirty = true;

void Tile::sInitializeTiles() {
    for (size_t i = 0; i < static_cast<size_t>(ETile::NUM_TILES_OR_EMPTY); i++) {
        const TileData& tileData = Tile::s_tileData[static_cast<size_t>(i)];
        Tile::s_tiles[i] = Tile(tileData.Sprites, tileData.SecondsPerFrame);
    }
}

void Tile::sUpdateTiles(double deltaTime) {
    for (Tile& tile : Tile::s_tiles) {
        bool frameChanged = tile.update(deltaTime);
        if (frameChanged) {
            Tile::s_isDirty = true;
        }
    }
}

const Tile& Tile::sGetTile(ETile tileType) {
    return Tile::s_tiles[static_cast<int>(tileType)];
}

bool Tile::sTileIsDirty() {
    return Tile::s_isDirty;
}

void Tile::sCleanDirtyTile() {
    Tile::s_isDirty = false;
}

Tile::Tile() {}

Tile::Tile(const std::vector<ESprite>& esprites, double secondsPerFrame) : secondsPerFrame(secondsPerFrame) {
    const ResourceManager& resourceManager = Game::GetResourceManager();
    for (auto esprite : esprites) {
        this->sprites.push_back(&resourceManager.GetSprite(esprite));
    }
}

const Sprite& Tile::GetCurrentSprite() const {
    return *this->sprites[this->currentFrameIndex];
}

bool Tile::update(double deltaTime) {
    size_t numberOfFrames = this->sprites.size();

    if (numberOfFrames == 1)
        return false;

    this->elapsedTime += deltaTime;
    if (this->elapsedTime >= secondsPerFrame) {
        this->elapsedTime -= secondsPerFrame;
        this->currentFrameIndex = (this->currentFrameIndex + 1) % numberOfFrames;
        return true;
    }
    return false;
}

/* ======================================== TileLayer ======================================== */

TileLayer::TileLayer(unsigned int width, unsigned int height, ETile* tileData) : width(width), height(height), tiles(tileData) {}

TileLayer::~TileLayer() {
    if (this->tiles) {
        delete[] this->tiles;
    }
}

void TileLayer::Init() {

    size_t numTiles = (size_t) this->width * this->height;

    if (tiles == nullptr) {
        tiles = new ETile[numTiles];
        for (size_t i = 0; i < numTiles; i++) {
            int remainder = i % static_cast<int>(ETile::NUM_TILES_OR_EMPTY);
            tiles[i] = static_cast<ETile>(remainder);
        }
    }

    // Generate VAO
    glGenVertexArrays(1, &this->VAO);
    glBindVertexArray(this->VAO);

    // Generate buffer data
    float* staticData = new float[numTiles * 5 * 4];
    float* dynamicData = new float[numTiles * 2 * 4];

    size_t staticIndex = 0;
    size_t dynamicIndex = 0;
    for (size_t y = 0; y < this->height; y++) {
        for (size_t x = 0; x < this->width; x++) {

            ETile type = tiles[y * this->width + x];

            // Fill position and color data for each corner
            staticData[staticIndex++] = (float) x;
            staticData[staticIndex++] = (float) y;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;

            staticData[staticIndex++] = (float) x + 1.0f;
            staticData[staticIndex++] = (float) y;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;

            staticData[staticIndex++] = (float) x;
            staticData[staticIndex++] = (float) y + 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;

            staticData[staticIndex++] = (float) x + 1.0f;
            staticData[staticIndex++] = (float) y + 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;
            staticData[staticIndex++] = 1.0f;

            // Fill UVs with current sprite data or invalid UVs that will be discarded by shader if empty
            if (type == ETile::NUM_TILES_OR_EMPTY) {
                const glm::vec2 invalidUV = { -1.0f, -1.0f };
                for (size_t i = 0; i < 4; i++) {
                    dynamicData[dynamicIndex++] = invalidUV.s;
                    dynamicData[dynamicIndex++] = invalidUV.t;
                }
            } else {
                const Sprite& currentSprite = Tile::sGetTile(type).GetCurrentSprite();
                dynamicData[dynamicIndex++] = currentSprite.topLeftUV.s;
                dynamicData[dynamicIndex++] = currentSprite.topLeftUV.t;
                dynamicData[dynamicIndex++] = currentSprite.topRightUV.s;
                dynamicData[dynamicIndex++] = currentSprite.topRightUV.t;
                dynamicData[dynamicIndex++] = currentSprite.bottomLeftUV.s;
                dynamicData[dynamicIndex++] = currentSprite.bottomLeftUV.t;
                dynamicData[dynamicIndex++] = currentSprite.bottomRightUV.s;
                dynamicData[dynamicIndex++] = currentSprite.bottomRightUV.t;
            }
        }
    }

    // Construct indices
    size_t indicesIndex = 0;
    unsigned int* indices = new unsigned int[6 * this->width * this->height];
    for (size_t y = 0; y < this->height; y++) {
        for (size_t x = 0; x < this->width; x++) {
            unsigned int tileBase = (unsigned int) (4 * (y * this->width + x));
            indices[indicesIndex++] = tileBase;
            indices[indicesIndex++] = tileBase + 1;
            indices[indicesIndex++] = tileBase + 2;

            indices[indicesIndex++] = tileBase + 1;
            indices[indicesIndex++] = tileBase + 2;
            indices[indicesIndex++] = tileBase + 3;
        }
    }
    glGenBuffers(1, &this->EBO);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this->EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * numTiles * sizeof(unsigned int), indices, GL_STATIC_DRAW);
    delete[] indices;

    // Build out static attributes (position, color?)
    glGenBuffers(1, &this->VBOStatic);
    glBindBuffer(GL_ARRAY_BUFFER, this->VBOStatic);
    glBufferData(GL_ARRAY_BUFFER, numTiles * 5 * 4 * sizeof(float), staticData, GL_STATIC_DRAW);
    delete[] staticData;

    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*) 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void*) (sizeof(float) * 2));
    glEnableVertexAttribArray(1);

    // Build out dynamic attributes (UVs, maybe texture ID later?)
    glGenBuffers(1, &this->VBODynamic);
    glBindBuffer(GL_ARRAY_BUFFER, this->VBODynamic);
    glBufferData(GL_ARRAY_BUFFER, numTiles * 2 * 4 * sizeof(float), dynamicData, GL_STATIC_DRAW);
    delete[] dynamicData;

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, 0, (void*) 0);
    glEnableVertexAttribArray(2);

    // Bind texture and shader
    const ResourceManager& resourceManager = Game::GetResourceManager();

    const Texture& tileTexture = resourceManager.GetTexture(ETexture::TILE_SHEET);
    glActiveTexture(GL_TEXTURE0);
    tileTexture.Bind();

    const Shader& shaderProgram = resourceManager.GetShader(EShader::TEST_2D);
    shaderProgram.Use();

}

void TileLayer::Update(double deltaTime) {
    bool needsToRefresh = Tile::sTileIsDirty();
    if (needsToRefresh) {
        Tile::sCleanDirtyTile();

        size_t numTiles = (size_t) this->width * this->height;

        float* dynamicData = new float[numTiles * 2 * 4];
        size_t dynamicIndex = 0;
        for (size_t y = 0; y < this->height; y++) {
            for (size_t x = 0; x < this->width; x++) {

                ETile type = tiles[y * this->width + x];

                // Fill UVs with current sprite data or invalid UVs that will be discarded by shader if empty
                if (type == ETile::NUM_TILES_OR_EMPTY) {
                    const glm::vec2 invalidUV = { -1.0f, -1.0f };
                    for (size_t i = 0; i < 4; i++) {
                        dynamicData[dynamicIndex++] = invalidUV.s;
                        dynamicData[dynamicIndex++] = invalidUV.t;
                    }
                } else {
                    const Sprite& currentSprite = Tile::sGetTile(type).GetCurrentSprite();
                    dynamicData[dynamicIndex++] = currentSprite.topLeftUV.s;
                    dynamicData[dynamicIndex++] = currentSprite.topLeftUV.t;
                    dynamicData[dynamicIndex++] = currentSprite.topRightUV.s;
                    dynamicData[dynamicIndex++] = currentSprite.topRightUV.t;
                    dynamicData[dynamicIndex++] = currentSprite.bottomLeftUV.s;
                    dynamicData[dynamicIndex++] = currentSprite.bottomLeftUV.t;
                    dynamicData[dynamicIndex++] = currentSprite.bottomRightUV.s;
                    dynamicData[dynamicIndex++] = currentSprite.bottomRightUV.t;
                }

            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, this->VBODynamic);
        glBufferSubData(GL_ARRAY_BUFFER, 0, numTiles * 2 * 4 * sizeof(float), dynamicData);
        delete[] dynamicData;
    }
}

void TileLayer::Render(const glm::mat4& projectionMatrix) {
    const Shader& shaderProgram = Game::GetResourceManager().GetShader(EShader::TEST_2D);

    shaderProgram.SetUniformMat4("projection", projectionMatrix);

    glBindVertexArray(this->VAO);
    glDrawElements(GL_TRIANGLES, 6 * this->width * this->height, GL_UNSIGNED_INT, 0);
}

void TileLayer::Teardown() const {
    // Free resources
    glDeleteVertexArrays(1, &this->VAO);
    glDeleteBuffers(1, &this->EBO);
    glDeleteBuffers(1, &this->VBOStatic);
    glDeleteBuffers(1, &this->VBODynamic);
}

/* ======================================== TileMap ======================================== */

TileMap::TileMap(unsigned int width, unsigned int height, unsigned int numLayers) : width(width), height(height) {
    this->tileLayers.reserve(numLayers);
    for (size_t i = 0; i < numLayers; i++) {
        this->tileLayers.emplace_back(width, height);
    }
    this->collisions = new bool[width * height] { false };
}

TileMap::TileMap(const std::string& path) {
    std::fstream fileStream = std::fstream(path);
    unsigned int numLayers;
    std::string breakToIgnore;

    // Read in width, height, and number of layers
    fileStream >> width >> height >> numLayers;
    std::getline(fileStream, breakToIgnore);
    std::getline(fileStream, breakToIgnore);
    size_t numTiles = (size_t) width * height;

    // Read in data for each layer
    this->tileLayers.reserve(numLayers);
    for (size_t i = 0; i < numLayers; i++) {
        // Read in actual data
        int tileID;
        ETile* tileLayerData = new ETile[numTiles];
        for (size_t tileDataIndex = 0; tileDataIndex < numTiles; tileDataIndex++) {
            fileStream >> tileID;
            if (tileID < 0) {
                tileLayerData[tileDataIndex] = ETile::NUM_TILES_OR_EMPTY;
            } else {
                tileLayerData[tileDataIndex] = static_cast<ETile>(tileID);
            }
        }

        // Push new tile layer
        this->tileLayers.emplace_back(width, height, tileLayerData);

        // Ignore break
        std::getline(fileStream, breakToIgnore);
        std::getline(fileStream, breakToIgnore);
    }

    // Read in collision data (TODO: could make this more space-efficient by packing in 8 bools per byte)
    this->collisions = new bool[numTiles];
    int collisionValue;
    for (size_t tileDataIndex = 0; tileDataIndex < numTiles; tileDataIndex++) {
        fileStream >> collisionValue;
        collisions[tileDataIndex] = (collisionValue > 0);
    }
}

TileMap::~TileMap() {
    if (this->collisions) {
        delete[] this->collisions;
    }
}

void TileMap::Init() {
    for (auto& layer : this->tileLayers) {
        layer.Init();
    }
}

void TileMap::Update(double deltaTime) {
    for (auto& layer : this->tileLayers) {
        layer.Update(deltaTime);
    }
}

void TileMap::Render(const glm::mat4& projectionMatrix) {
    for (auto& layer : this->tileLayers) {
        layer.Render(projectionMatrix);
    }
}

void TileMap::Teardown() {
    for (auto& layer : this->tileLayers) {
        layer.Teardown();
    }
}