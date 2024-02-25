#include "Scenes/Scene2D.h"

#include "Camera2D.h"
#include "Game.h"
#include "ResourceManager.h"
#include "Shader.h"
#include "Texture.h"
#include "Window.h"
#include "Spritesheet.h"
#include "Tile.h"

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <iostream>

Scene2D::Scene2D(Game& game) : Scene(game), camera(game), tilemap(game, "res/maps/LOL.map") {}

void Scene2D::init() {
    tilemap.init();
}

void Scene2D::update(double deltaTime) {

    this->timeElapsed += (float) deltaTime;
    this->camera.update(deltaTime);

    Tile::sUpdateTiles(deltaTime);

    tilemap.update(deltaTime);
}

void Scene2D::render() {
    glm::mat4 projection = this->camera.getWorldToScreenMatrix();
    tilemap.render(projection);
}

void Scene2D::teardown() {
    // Free resources
    tilemap.teardown();
}
