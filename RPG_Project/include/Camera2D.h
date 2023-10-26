#pragma once

#include <glm/glm.hpp>

class Game;

class Camera2D {

public:
    Camera2D(Game& game, double offsetX = 0, double offsetY = 0, double unitsPerWindowHeight = 10);

    void update(double deltaTime);
    glm::vec2 getScreenCenter();
    glm::mat4 getWorldToScreenMatrix();

private:
    Game& game;

    double offsetX;
    double offsetY;
    double unitsPerWindowHeight;

};
