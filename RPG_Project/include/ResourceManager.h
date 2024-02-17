#pragma once

#define NUM_SHADERS (2)
#define NUM_TEXTURES (5)
#define NUM_SPRITESHEETS (3)

class Shader;
class Texture;
class Spritesheet;

class ResourceManager {
public:

    // Enums for shaders and textures in application
    enum class EShader { DEFAULT, TEST_2D };
    enum class ETexture { FACE, BOX, TILE_SHEET, ITEM_SHEET, CHARACTER_SHEET };
    enum class ESpritesheet { TILE_SHEET, ITEM_SHEET, CHARACTER_SHEET };

    static_assert(static_cast<int>(EShader::TEST_2D) == NUM_SHADERS - 1, "NUM_SHADERS does not match");
    static_assert(static_cast<int>(ETexture::CHARACTER_SHEET) == NUM_TEXTURES - 1, "NUM_TEXTURES does not match");
    static_assert(static_cast<int>(ESpritesheet::CHARACTER_SHEET) == NUM_SPRITESHEETS - 1, "NUM_SPRITESHEETS does not match");

    ResourceManager();
    ~ResourceManager();

    void loadResources();

    // Resource fetching methods
    const Shader& getShader(EShader eshader) const;
    const Texture& getTexture(ETexture etexture) const;
    const Spritesheet& getSpritesheet(ESpritesheet espritesheet) const;

private:
    void loadShaders();
    void loadTextures();
    void loadSpritesheets();

private:
    Shader* shaderArray;
    Texture* textureArray;
    Spritesheet* spritesheetArray;
};

