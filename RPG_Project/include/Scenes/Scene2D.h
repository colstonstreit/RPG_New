#pragma once

#include "Constants.h"

#include "Scene.h"
#include "Camera2D.h"
#include "Renderer.h"
#include "Tile.h"

class Scene2D : public Scene {

public:
    Scene2D();
    ~Scene2D();
    void Update(double deltaTime) override;
    void Render() override;

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

    float timeElapsed = 0.0f;

    Camera2D camera;
    TileMap tilemap;
    Renderer renderer;
};


