#pragma once

// Enums for shaders and textures in application
enum class EShader : unsigned char {
    DEFAULT,
    TEST_2D,
    QUAD_BATCH,

    NUM_SHADERS
};

enum class ETexture : unsigned char {
    FACE,
    BOX,
    TILE_SHEET,
    ITEM_SHEET,
    CHARACTER_SHEET,

    NUM_TEXTURES
};

enum class ESpritesheet : unsigned char {
    TILE_SHEET,
    ITEM_SHEET,
    CHARACTER_SHEET,

    NUM_SPRITESHEETS
};

// Maps
enum class EMap : unsigned char {
    BROOKIE_SHOW_AND_TELL,
    COOL_ISLAND,
    FLOWERS,
    INSIDE_HOUSE,
    ISLAND,
    LAND_BRIDGE,
    LINKS_AWAKENING_MAP_SIZE,
    LOL,
    MARIO_SUN_LEVEL,
    SQUARE_PONDS,
    SUNS,
    UNNECESSARILY_LARGE_ISLAND,

    NUM_MAPS
};

// Tile types
enum class ETile : unsigned char {
    GRASS,
    SAND,
    BRICK,
    WATER,
    LAVA,
    TREE,
    SUN,
    FLOWER,
    HOUSE_DOOR,
    HOUSE_WINDOW,
    HOUSE_WALL,
    BLUE_HOUSE_UPPER_LEFT,
    BLUE_HOUSE_UPPER_MID,
    BLUE_HOUSE_UPPER_RIGHT,
    BLUE_HOUSE_LOWER_LEFT,
    BLUE_HOUSE_LOWER_MID,
    BLUE_HOUSE_LOWER_RIGHT,
    WOOD_FLOORBOARD,
    STONE_BRICK,

    NULL_EMPTY, NUM_TILES
};