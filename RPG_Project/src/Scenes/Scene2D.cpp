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

#include "Entity.h"

Scene2D::Scene2D(Game& game) : Scene(game), camera(game), tilemap(game, "res/maps/LOL.map") {}

void Scene2D::Init() {
    tilemap.Init();

    for (int i = 0; i < 10000; i++) {
        float x = i % 100;
        float y = i / 100;
        renderer.AddQuad(new VisibleEntity("Entity", { x, y }, { 0.5f, 1.0f }, new Sprite(ResourceManager::GetSpritesheet(ESpritesheet::TILE_SHEET).Crop(0, 0)), { 1.0f, x / 100.0f, y / 100.0f }));
        renderer.AddQuad(new VisibleEntity("Entity", { x + 0.5f, y }, { 0.5f, 1.0f }, new Sprite(ETexture::NUM_TEXTURES), { 1.0f, x / 100.0f, y / 100.0f }));
    }

}

void Scene2D::Update(double deltaTime) {

    this->timeElapsed += (float) deltaTime;
    this->camera.Update(deltaTime);

    Tile::sUpdateTiles(deltaTime);

    tilemap.Update(deltaTime);
}

void Scene2D::Render() {
    glm::mat4 projection = this->camera.GetWorldToScreenMatrix();
    //tilemap.Render(projection);
    renderer.Render(projection);
}

void Scene2D::Teardown() {
    // Free resources
    tilemap.Teardown();
}
