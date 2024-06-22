#pragma once

#include "ResourceManager.h"

class Scene;
class Window;

class Game {

public:
    Game(unsigned int width, unsigned int height, const char* title);
    ~Game();

    void Init();
    void Run();
    void Stop();

    void ChangeScene(Scene* newScene);

    int GetWidth() const;
    int GetHeight() const;

    const Window& GetWindow() const;
    const Scene& GetCurrentScene() const;

private:
    void update(double deltaTime);
    void render();

private:
    Window& window;
    Scene* currentScene = nullptr;
};

