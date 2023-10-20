#pragma once

#include "Window.h"
#include "ResourceManager.h"

#include "Texture.h"
#include "Shader.h"

class Game {

public:
    Game(unsigned int width, unsigned int height, const char* title);
    ~Game();

    void init();
    void run();
    void update();
    void render();
    void stop();

private:
    Window& window;
    ResourceManager resourceManager;
};

