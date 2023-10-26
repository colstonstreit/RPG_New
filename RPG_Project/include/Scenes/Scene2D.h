#pragma once

#include "Scene.h"
#include "Camera2D.h"

class Game;

class Scene2D : public Scene {

public:
    Scene2D(Game& game);

    void init() override;
    void update(double deltaTime) override;
    void render() override;
    void teardown() override;

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

    float* vertices = nullptr;
    unsigned int* indices = nullptr;

    const int width = 500, height = 500;

    Camera2D camera;
};


