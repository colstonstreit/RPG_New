#pragma once

#include <glm/glm.hpp>

class Camera2D {

public:
    Camera2D(double offsetX = 0, double offsetY = 0, double unitsPerWindowHeight = 10);

    void Update(double deltaTime);
    glm::vec2 GetScreenCenter();
    glm::mat4 GetWorldToScreenMatrix();

private:
    double offsetX;
    double offsetY;
    double unitsPerWindowHeight;

};
