#include "Camera2D.h"
#include "Window.h"
#include "Game.h"

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

Camera2D::Camera2D(double offsetX, double offsetY, double unitsPerWindowHeight)
    : offsetX(offsetX), offsetY(offsetY), unitsPerWindowHeight(unitsPerWindowHeight) {}

void Camera2D::Update(double deltaTime) {
    const Window& window = Game::GetWindow();
    const double velocity = this->unitsPerWindowHeight / 2.0;
    const double aspectRatio = (float) Game::GetWidth() / Game::GetHeight();

    if (window.IsKeyPressed(Window::Input::FORWARD)) this->offsetY -= velocity * deltaTime;
    if (window.IsKeyPressed(Window::Input::BACKWARD)) this->offsetY += velocity * deltaTime;
    if (window.IsKeyPressed(Window::Input::LEFT)) this->offsetX -= velocity * deltaTime;
    if (window.IsKeyPressed(Window::Input::RIGHT)) this->offsetX += velocity * deltaTime;

    if (window.IsKeyPressed(Window::Input::DOWN) || window.IsKeyPressed(Window::Input::UP)) {
        const double zoomRatio = 0.5;
        double deltaY = 0;
        if (window.IsKeyPressed(Window::Input::DOWN)) deltaY = -zoomRatio * this->unitsPerWindowHeight * deltaTime;
        if (window.IsKeyPressed(Window::Input::UP)) deltaY = zoomRatio * this->unitsPerWindowHeight * deltaTime;
        deltaY = glm::clamp<double>(deltaY, 1.0 - this->unitsPerWindowHeight, 2000.0 - this->unitsPerWindowHeight);
        this->unitsPerWindowHeight += deltaY;
        this->offsetY -= deltaY / 2.0;
        this->offsetX -= deltaY * aspectRatio / 2.0;
    }
}

glm::vec2 Camera2D::GetScreenCenter() {
    const float aspectRatio = (float) Game::GetWidth() / Game::GetHeight();
    float centerX = this->offsetX + aspectRatio * this->unitsPerWindowHeight / 2.0;
    float centerY = this->offsetY + this->unitsPerWindowHeight / 2.0;
    return glm::vec2(centerX, centerY);
}

glm::mat4 Camera2D::GetWorldToScreenMatrix() {
    const float aspectRatio = (float) Game::GetWidth() / Game::GetHeight();
    return glm::ortho(this->offsetX, this->offsetX + this->unitsPerWindowHeight * aspectRatio, this->offsetY + this->unitsPerWindowHeight, this->offsetY);
}
