#include "../include/Camera.h"
#include <glm/gtc/matrix_transform.hpp>

Camera::Camera(glm::vec3 position, glm::vec3 up, float yaw, float pitch) : movementSpeed(CAM_DEFAULT_SPEED), mouseSensitivity(CAM_DEFAULT_SENSITIVITY), fov(CAM_DEFAULT_FOV) {
    this->position = position;
    this->worldUp = up;
    this->yaw = yaw;
    this->pitch = pitch;
    this->updateCameraVectors();
}

glm::mat4 Camera::getViewMatrix() {
    return glm::lookAt(this->position, this->position + this->front, this->worldUp);
}

glm::mat4 Camera::getPerspectiveProjectionMatrix(float aspectRatio, float zNear, float zFar) {
    return glm::perspective(glm::radians(this->fov), aspectRatio, zNear, zFar);
}

void Camera::processKeyboardInput(CameraDirection direction, float deltaTime) {
    float velocity = this->movementSpeed * deltaTime;
    if (direction == CameraDirection::FORWARD)  this->position += velocity * this->front;
    if (direction == CameraDirection::BACKWARD) this->position -= velocity * this->front;
    if (direction == CameraDirection::RIGHT)    this->position += velocity * this->right;
    if (direction == CameraDirection::LEFT)     this->position -= velocity * this->right;
    if (direction == CameraDirection::UP)       this->position += velocity * this->worldUp;
    if (direction == CameraDirection::DOWN)     this->position -= velocity * this->worldUp;
}

void Camera::processMouseMovement(float xOffset, float yOffset) {
    xOffset *= this->mouseSensitivity;
    yOffset *= this->mouseSensitivity;

    this->yaw = glm::mod(yaw + xOffset, 360.0f);
    this->pitch = glm::clamp(this->pitch + yOffset, -CAM_MAX_PITCH, CAM_MAX_PITCH);

    this->updateCameraVectors();
}

void Camera::processMouseScroll(float yOffset) {
    this->fov = glm::clamp(this->fov - yOffset, CAM_MIN_FOV, CAM_MAX_FOV);
}

void Camera::updateCameraVectors() {
    // Recalculate front from angles
    this->front.x = cos(glm::radians(this->yaw)) * cos(glm::radians(this->pitch));
    this->front.y = sin(glm::radians(this->pitch));
    this->front.z = sin(glm::radians(this->yaw)) * cos(glm::radians(this->pitch));

    // Do cross products to get other directions
    this->right = glm::cross(this->front, this->worldUp);
    this->up = glm::cross(this->right, this->front);
}
