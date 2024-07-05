#pragma once

#include "Constants.h"

#include "Scene.h"
#include "Camera2D.h"
#include "Renderer.h"
#include "Tile.h"

class Scene2D : public Scene {

public:
    Scene2D();

    void Init() override;
    void Update(double deltaTime) override;
    void Render() override;
    void Teardown() override;

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
    unsigned int VBO = 0;
    unsigned int EBO = 0;
    float timeElapsed = 0.0f;

    float* vertices = nullptr;
    unsigned int* indices = nullptr;

    const int width = 500;
    const int height = 500;
    ETile* s_Tiles = nullptr;

    Camera2D camera;
    TileMap tilemap;
    Renderer renderer;
};


