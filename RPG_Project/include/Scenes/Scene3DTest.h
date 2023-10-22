#pragma once

#include "Scene.h"

class Game;

class Scene3DTest : public Scene {

public:
    Scene3DTest(Game& game);

    void init() override;
    void update(double deltaTime) override;
    void render() override;
    void teardown() override;

private:
    unsigned int VAO = 0;
    unsigned int VBO = 0;
    unsigned int EBO = 0;

};

