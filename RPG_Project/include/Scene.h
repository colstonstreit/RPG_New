#pragma once

class Game;

class Scene {

public:
    virtual void Init();
    virtual void Update(double deltaTime) = 0;
    virtual void Render() = 0;
    virtual void Teardown();
};

