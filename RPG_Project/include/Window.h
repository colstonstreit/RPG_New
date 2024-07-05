#pragma once

#include <glm/glm.hpp>

#include <string>
#include <unordered_map>
#include <vector>

struct GLFWwindow;

class Window {

public:
    // Keys
    enum class Input {
        FORWARD, BACKWARD, LEFT, RIGHT, CLICK, TOGGLE_DEBUG, QUIT, UP, DOWN,
        NUM_KEYS
    };

    Window(unsigned int width, unsigned int height, const char* title);
    ~Window();

    void InitGLFW();
    void Update();
    void SwapBuffers() const;
    void Close() const;

    bool IsKeyPressed(Window::Input input) const;
    bool WasKeyClicked(Window::Input input) const;
    glm::vec2 GetMousePos() const;

    bool ShouldClose() const;
    unsigned int GetWidth() const;
    unsigned int GetHeight() const;
    glm::vec2 GetLastMousePos() const;
    glm::vec2 GetMouseScroll() const;

private:
    struct RawInput {
        RawInput(int inputCode, bool typeIsKey = true);
        int inputCode;
        bool typeIsKey; // true if key, false if mouse
    };

    static void handleMouseScroll(GLFWwindow* window, double xOffset, double yOffset);
    static void handleWindowResize(GLFWwindow* window, int newWidth, int newHeight);

private:
    GLFWwindow* window;

    // Key management
    std::unordered_map<Window::Input, std::vector<Window::RawInput>> inputMap;
    bool previousKeyStates[static_cast<int>(Input::NUM_KEYS)];
    bool currentKeyStates[static_cast<int>(Input::NUM_KEYS)];

    // Mouse management
    glm::vec2 lastMousePos;
    glm::vec2 mouseScroll;

    // Window properties
    unsigned int width;
    unsigned int height;
    const char* title;
};

