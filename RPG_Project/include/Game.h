#pragma once

#include "ResourceManager.h"

class Scene;
class Window;

class Game {

public:
    Game(unsigned int width, unsigned int height, const char* title);
    ~Game();

    void init();
    void run();
    void stop();

    void changeScene(Scene* newScene);

    int getWidth() const;
    int getHeight() const;

    const Window& getWindow() const;
    const ResourceManager& getResourceManager() const;
    const Scene& getCurrentScene() const;

private:
    void update(double deltaTime);
    void render();

private:
    Window& window;
    ResourceManager resourceManager;
    Scene* currentScene = nullptr;
};

