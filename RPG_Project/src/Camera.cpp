#include "Camera.h"

#include <glm/gtc/matrix_transform.hpp>

Camera::Camera(glm::vec3 position, glm::vec3 up, double yaw, double pitch) : movementSpeed(CAM_DEFAULT_SPEED), mouseSensitivity(CAM_DEFAULT_SENSITIVITY), fov(CAM_DEFAULT_FOV) {
    this->position = position;
    this->worldUp = up;
    this->yaw = yaw;
    this->pitch = pitch;
    this->updateCameraVectors();
}

glm::mat4 Camera::getViewMatrix() {
    return glm::lookAt(this->position, this->position + this->front, this->worldUp);
}

glm::mat4 Camera::getPerspectiveProjectionMatrix(double aspectRatio, double zNear, double zFar) {
    return glm::perspective(glm::radians(this->fov), aspectRatio, zNear, zFar);
}

void Camera::processKeyboardInput(CameraDirection direction, double deltaTime) {
    float velocity = (float) (this->movementSpeed * deltaTime);
    if (direction == CameraDirection::FORWARD)  this->position += velocity * this->front;
    if (direction == CameraDirection::BACKWARD) this->position -= velocity * this->front;
    if (direction == CameraDirection::RIGHT)    this->position += velocity * this->right;
    if (direction == CameraDirection::LEFT)     this->position -= velocity * this->right;
    if (direction == CameraDirection::UP)       this->position += velocity * this->worldUp;
    if (direction == CameraDirection::DOWN)     this->position -= velocity * this->worldUp;
}

void Camera::processMouseMovement(double xOffset, double yOffset) {
    xOffset *= this->mouseSensitivity;
    yOffset *= this->mouseSensitivity;

    this->yaw = glm::mod(yaw + xOffset, 360.0);
    this->pitch = glm::clamp(this->pitch + yOffset, -CAM_MAX_PITCH, CAM_MAX_PITCH);

    this->updateCameraVectors();
}

void Camera::processMouseScroll(double yOffset) {
    this->fov = glm::clamp(this->fov - yOffset, CAM_MIN_FOV, CAM_MAX_FOV);
}

void Camera::updateCameraVectors() {
    // Recalculate front from angles
    this->front.x = (float) (cos(glm::radians(this->yaw)) * cos(glm::radians(this->pitch)));
    this->front.y = (float) (sin(glm::radians(this->pitch)));
    this->front.z = (float) (sin(glm::radians(this->yaw)) * cos(glm::radians(this->pitch)));

    // Do cross products to get other directions
    this->right = glm::cross(this->front, this->worldUp);
    this->up = glm::cross(this->right, this->front);
}
