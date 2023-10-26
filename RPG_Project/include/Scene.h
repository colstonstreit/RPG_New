#pragma once

class Game;

class Scene {

public:
    Scene(Game& game);

    virtual void init();
    virtual void update(double deltaTime) = 0;
    virtual void render() = 0;
    virtual void teardown();

protected:
    Game& game;
};

