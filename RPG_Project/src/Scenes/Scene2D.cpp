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

Scene2D::Scene2D() : tilemap("res/maps/LOL.map") {}

void Scene2D::Init() {
    tilemap.Init();

    const ResourceManager& resourceManager = Game::GetResourceManager();

    const Sprite* grassSprite = &resourceManager.GetSprite(ESprite::TILE_GRASS);
    Sprite* emptySprite = new Sprite(ETexture::NUM_TEXTURES_OR_INVALID);

    for (int i = 0; i < 10000; i++) {
        float x = (float) (i % 100);
        float y = (float) (i / 100);
        renderer.AddQuad(new VisibleEntity("Entity", { x, y }, { 0.5f, 1.0f }, const_cast<Sprite*> (grassSprite), { 1.0f, x / 100.0f, y / 100.0f }));
        renderer.AddQuad(new VisibleEntity("Entity", { x + 0.5f, y }, { 0.5f, 1.0f }, emptySprite, { 1.0f, x / 100.0f, y / 100.0f }));
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
    renderer.Render(projection);
    tilemap.Render(projection);
}

void Scene2D::Teardown() {
    // Free resources
    tilemap.Teardown();
}
