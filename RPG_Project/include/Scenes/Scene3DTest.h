#pragma once

#include "Scene.h"
#include "Camera3D.h"

class Scene3DTest : public Scene {

public:
    Scene3DTest();
    ~Scene3DTest();
    void Update(double deltaTime) override;
    void Render() override;

private:
    unsigned int VAO = 0;
    unsigned int VBO = 0;
    unsigned int EBO = 0;

    Camera3D camera;
};

