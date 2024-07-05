#pragma once

#include "Scene.h"
#include "Camera3D.h"

class Scene3DTest : public Scene {

public:
    void Init() override;
    void Update(double deltaTime) override;
    void Render() override;
    void Teardown() override;

private:
    unsigned int VAO = 0;
    unsigned int VBO = 0;
    unsigned int EBO = 0;

    Camera3D camera;
};

