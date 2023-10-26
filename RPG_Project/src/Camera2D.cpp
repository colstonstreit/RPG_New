#include "Camera2D.h"
#include "Window.h"
#include "Game.h"

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

Camera2D::Camera2D(Game& game, double offsetX, double offsetY, double unitsPerWindowHeight)
    : game(game), offsetX(offsetX), offsetY(offsetY), unitsPerWindowHeight(unitsPerWindowHeight) {}

void Camera2D::update(double deltaTime) {

    // Process input
    const Window& window = this->game.getWindow();

    const double velocity = this->unitsPerWindowHeight / 2.0f;
    const double aspectRatio = (float) this->game.getWidth() / this->game.getHeight();

    if (window.isKeyPressed(Window::Input::FORWARD)) this->offsetY -= velocity * deltaTime;
    if (window.isKeyPressed(Window::Input::BACKWARD)) this->offsetY += velocity * deltaTime;
    if (window.isKeyPressed(Window::Input::LEFT)) this->offsetX -= velocity * deltaTime;
    if (window.isKeyPressed(Window::Input::RIGHT)) this->offsetX += velocity * deltaTime;

    if (window.isKeyPressed(Window::Input::DOWN) || window.isKeyPressed(Window::Input::UP)) {
        const double zoomRatio = 0.5f;
        double deltaY = 0;
        if (window.isKeyPressed(Window::Input::DOWN)) deltaY = -zoomRatio * this->unitsPerWindowHeight * deltaTime;
        if (window.isKeyPressed(Window::Input::UP)) deltaY = zoomRatio * this->unitsPerWindowHeight * deltaTime;
        deltaY = glm::clamp<double>(deltaY, 1.0f - this->unitsPerWindowHeight, 2000.0f - this->unitsPerWindowHeight);
        this->unitsPerWindowHeight += deltaY;
        this->offsetY -= deltaY / 2.0f;
        this->offsetX -= deltaY * aspectRatio / 2.0f;
    }

}

glm::vec2 Camera2D::getScreenCenter() {
    const float aspectRatio = (float) this->game.getWidth() / this->game.getHeight();
    float centerX = this->offsetX + aspectRatio * this->unitsPerWindowHeight / 2.0f;
    float centerY = this->offsetY + this->unitsPerWindowHeight / 2.0f;
    return glm::vec2(centerX, centerY);
}

glm::mat4 Camera2D::getWorldToScreenMatrix() {
    const float aspectRatio = (float) this->game.getWidth() / this->game.getHeight();
    return glm::ortho(this->offsetX, this->offsetX + this->unitsPerWindowHeight * aspectRatio, this->offsetY + this->unitsPerWindowHeight, this->offsetY);
}
