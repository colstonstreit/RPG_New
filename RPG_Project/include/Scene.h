#pragma once

class Game;

class Scene {

public:
    Scene(Game& game);

    virtual void Init();
    virtual void Update(double deltaTime) = 0;
    virtual void Render() = 0;
    virtual void Teardown();

protected:
    Game& game;
};

