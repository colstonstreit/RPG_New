#pragma once

#include "Constants.h"

#include <vector>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

struct Vertex {
    glm::vec2 position;
    glm::vec3 color;
    glm::vec2 uv;
    float textureID;
};

class VisibleEntity;

class QuadBatch {

public:
    const unsigned int maxQuads = 1000;
    const unsigned int maxTextures = 16;

    QuadBatch(unsigned int maxQuads);
    QuadBatch(QuadBatch&& other) noexcept;
    ~QuadBatch();

    bool AddQuad(VisibleEntity* quad);
    void Render(const glm::mat4& projectionMatrix);

private:

    void regenerateVertices();
    bool isDirty() const;

    unsigned int VAO = 0;
    unsigned int VBO = 0;
    unsigned int EBO = 0;

    float* vertices = nullptr;
    unsigned int* indices = nullptr;

    std::vector<ETexture> usedTextures;
    std::vector<VisibleEntity*> quads;
};

class Renderer {

public:

    void AddQuad(VisibleEntity* quad);
    void Render(const glm::mat4& projectionMatrix);

    static const unsigned int BATCH_SIZE = 1000;

private:
    std::vector<QuadBatch> quadBatches;

};

