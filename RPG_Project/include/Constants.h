#pragma once

#define STRINGIZE(A) #A

/*********************** Shaders *********************/

#define SHADER_TO_ENUM(ENUM_NAME, FILE_NAME) ENUM_NAME,
#define SHADER_TO_TABLE(ENUM_NAME, FILE_NAME) { STRINGIZE(res/shaders/ ## FILE_NAME ## .vert), STRINGIZE(res/shaders/ ## FILE_NAME ## .frag) },

#define SHADER_DATA(MACRO) \
    MACRO(DEFAULT, Default) \
    MACRO(TEST_2D, Test2D) \
    MACRO(QUAD_BATCH, QuadBatchShader) \

enum class EShader : unsigned char {
    SHADER_DATA(SHADER_TO_ENUM)
    NUM_SHADERS_OR_INVALID
};

/*********************** Maps *********************/

#define MAP_TO_ENUM(ENUM_NAME, FILE_NAME) ENUM_NAME,
#define MAP_TO_TABLE(ENUM_NAME, FILE_NAME) { STRINGIZE(res/maps/ ## FILE_NAME ## .map) },

#define MAP_DATA(MACRO) \
    MACRO(BROOKIE_SHOW_AND_TELL, BrookieShowAndTell) \
    MACRO(COOL_ISLAND, CoolIsland) \
    MACRO(FLOWERS, Flowers) \
    MACRO(INSIDE_HOUSE, InsideHouse) \
    MACRO(ISLAND, Island) \
    MACRO(LAND_BRIDGE, LandBridge) \
    MACRO(LINKS_AWAKENING_MAP_SIZE, LinksAwakeningMapSize) \
    MACRO(LOL, Lol) \
    MACRO(MARIO_SUN_LEVEL, MarioSunLevel) \
    MACRO(SQUARE_PONDS, SquarePonds) \
    MACRO(SUNS, Suns) \
    MACRO(UNNECESSARILY_LARGE_ISLAND, UnnecessarilyLargeIsland) \

enum class EMap : unsigned char {
    MAP_DATA(MAP_TO_ENUM)
    NUM_MAPS_OR_INVALID
};

/*********************** Textures *********************/

#define TEXTURE_TO_ENUM(ENUM_NAME, FILE_NAME) ENUM_NAME,
#define TEXTURE_TO_TABLE(ENUM_NAME, FILE_NAME) { STRINGIZE(res/textures/ ## FILE_NAME ## .png) },

#define TEXTURE_DATA(MACRO) \
    MACRO(AWESOME_FACE, AwesomeFace) \
    MACRO(CONTAINER, Container) \
    MACRO(TILE_SHEET, TileSheet) \
    MACRO(ITEM_SHEET, ItemSheet) \
    MACRO(CHARACTER_SHEET, CharacterSheet) \

enum class ETexture : unsigned char {
    TEXTURE_DATA(TEXTURE_TO_ENUM)
    NUM_TEXTURES_OR_INVALID
};

/*********************** Spritesheets *********************/

#define SPRITESHEET_TO_ENUM(ENUM_NAME, ETEXTURE, TILE_WIDTH, TILE_HEIGHT) ENUM_NAME,
#define SPRITESHEET_TO_TABLE(ENUM_NAME, ETEXTURE, TILE_WIDTH, TILE_HEIGHT) { ETEXTURE, TILE_WIDTH, TILE_HEIGHT },

#define SPRITESHEET_DATA(MACRO) \
    MACRO(TILES, ETexture::TILE_SHEET, 16, 16) \
    MACRO(ITEMS, ETexture::ITEM_SHEET, 16, 16) \
    MACRO(CHARACTERS, ETexture::CHARACTER_SHEET, 16, 16)

enum class ESpritesheet : unsigned char {
    SPRITESHEET_DATA(SPRITESHEET_TO_ENUM)
    NUM_SPRITESHEETS_OR_INVALID
};

/*********************** Sprites *********************/

#define SPRITE_TO_ENUM(ESPRITESHEET, TILE_X, TILE_Y, ENUM_NAME) ENUM_NAME,
#define SPRITE_TO_TABLE(ESPRITESHEET, TILE_X, TILE_Y, ENUM_NAME) { ESPRITESHEET, TILE_X, TILE_Y },

#define SPRITE_DATA(MACRO) \
    MACRO(ESpritesheet::TILES,   0,   0,   TILE_GRASS) \
    MACRO(ESpritesheet::TILES,   1,   0,   TILE_SAND) \
    MACRO(ESpritesheet::TILES,   2,   0,   TILE_ROAD) \
    MACRO(ESpritesheet::TILES,   3,   0,   TILE_WATER_ANIM_0) \
    MACRO(ESpritesheet::TILES,   4,   0,   TILE_WATER_ANIM_1) \
    MACRO(ESpritesheet::TILES,   5,   0,   TILE_WATER_ANIM_2) \
    MACRO(ESpritesheet::TILES,   6,   0,   TILE_LAVA_ANIM_0) \
    MACRO(ESpritesheet::TILES,   7,   0,   TILE_LAVA_ANIM_1) \
    MACRO(ESpritesheet::TILES,   8,   0,   TILE_LAVA_ANIM_2) \
    MACRO(ESpritesheet::TILES,   9,   0,   TILE_TREE) \
    MACRO(ESpritesheet::TILES,   10,  0,   TILE_SUN) \
    MACRO(ESpritesheet::TILES,   11,  0,   TILE_FLOWER) \
    MACRO(ESpritesheet::TILES,   12,  0,   TILE_HOUSE_DOOR) \
    MACRO(ESpritesheet::TILES,   13,  0,   TILE_HOUSE_WINDOW) \
    MACRO(ESpritesheet::TILES,   14,  0,   TILE_HOUSE_WALL) \
    MACRO(ESpritesheet::TILES,   12,  1,   TILE_BLUE_HOUSE_ROOF_UPPER_LEFT) \
    MACRO(ESpritesheet::TILES,   13,  1,   TILE_BLUE_HOUSE_ROOF_UPPER_MID) \
    MACRO(ESpritesheet::TILES,   14,  1,   TILE_BLUE_HOUSE_ROOF_UPPER_RIGHT) \
    MACRO(ESpritesheet::TILES,   12,  2,   TILE_BLUE_HOUSE_ROOF_LOWER_LEFT) \
    MACRO(ESpritesheet::TILES,   13,  2,   TILE_BLUE_HOUSE_ROOF_LOWER_MID) \
    MACRO(ESpritesheet::TILES,   14,  2,   TILE_BLUE_HOUSE_ROOF_LOWER_RIGHT) \
    MACRO(ESpritesheet::TILES,   15,  0,   TILE_WOOD_FLOORBOARD) \
    MACRO(ESpritesheet::TILES,   15,  1,   TILE_STONE_BRICK) \
    \
    MACRO(ESpritesheet::ITEMS,   0,   0,   ITEM_APPLE) \
    MACRO(ESpritesheet::ITEMS,   1,   0,   ITEM_ORANGE) \
    \
    MACRO(ESpritesheet::CHARACTERS,  0,   0,   CHAR_PLAYER_DOWN_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  0,   1,   CHAR_PLAYER_DOWN_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  0,   2,   CHAR_PLAYER_DOWN_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  1,   0,   CHAR_PLAYER_UP_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  1,   1,   CHAR_PLAYER_UP_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  1,   2,   CHAR_PLAYER_UP_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  2,   0,   CHAR_PLAYER_RIGHT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  2,   1,   CHAR_PLAYER_RIGHT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  2,   2,   CHAR_PLAYER_RIGHT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  3,   0,   CHAR_PLAYER_LEFT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  3,   1,   CHAR_PLAYER_LEFT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  3,   2,   CHAR_PLAYER_LEFT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  4,   0,   CHAR_BULBASAUR_DOWN_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  4,   1,   CHAR_BULBASAUR_DOWN_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  4,   2,   CHAR_BULBASAUR_DOWN_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  5,   0,   CHAR_BULBASAUR_UP_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  5,   1,   CHAR_BULBASAUR_UP_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  5,   2,   CHAR_BULBASAUR_UP_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  6,   0,   CHAR_BULBASAUR_RIGHT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  6,   1,   CHAR_BULBASAUR_RIGHT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  6,   2,   CHAR_BULBASAUR_RIGHT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  7,   0,   CHAR_BULBASAUR_LEFT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  7,   1,   CHAR_BULBASAUR_LEFT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  7,   2,   CHAR_BULBASAUR_LEFT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  8,   0,   CHAR_PIKACHU_DOWN_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  8,   1,   CHAR_PIKACHU_DOWN_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  8,   2,   CHAR_PIKACHU_DOWN_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  9,   0,   CHAR_PIKACHU_UP_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  9,   1,   CHAR_PIKACHU_UP_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  9,   2,   CHAR_PIKACHU_UP_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  10,  0,   CHAR_PIKACHU_RIGHT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  10,  1,   CHAR_PIKACHU_RIGHT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  10,  2,   CHAR_PIKACHU_RIGHT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  11,  0,   CHAR_PIKACHU_LEFT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  11,  1,   CHAR_PIKACHU_LEFT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  11,  2,   CHAR_PIKACHU_LEFT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  12,  0,   CHAR_SQUIRTLE_DOWN_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  12,  1,   CHAR_SQUIRTLE_DOWN_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  12,  2,   CHAR_SQUIRTLE_DOWN_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  13,  0,   CHAR_SQUIRTLE_UP_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  13,  1,   CHAR_SQUIRTLE_UP_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  13,  2,   CHAR_SQUIRTLE_UP_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  14,  0,   CHAR_SQUIRTLE_RIGHT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  14,  1,   CHAR_SQUIRTLE_RIGHT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  14,  2,   CHAR_SQUIRTLE_RIGHT_WALK_ANIM_1) \
    MACRO(ESpritesheet::CHARACTERS,  15,  0,   CHAR_SQUIRTLE_LEFT_IDLE) \
    MACRO(ESpritesheet::CHARACTERS,  15,  1,   CHAR_SQUIRTLE_LEFT_WALK_ANIM_0) \
    MACRO(ESpritesheet::CHARACTERS,  15,  2,   CHAR_SQUIRTLE_LEFT_WALK_ANIM_1) \

enum class ESprite : unsigned char {
    SPRITE_DATA(SPRITE_TO_ENUM)
    NUM_SPRITES_OR_INVALID
};

/*********************** Tiles *********************/

#define TILE_TO_ENUM(ENUM_NAME, ESPRITE_CSL, SECONDS_PER_FRAME) ENUM_NAME,
#define TILE_TO_TABLE(ENUM_NAME, ESPRITE_CSL, SECONDS_PER_FRAME) { std::vector<ESprite> { ESPRITE_CSL }, SECONDS_PER_FRAME },

#define CSL(...) __VA_ARGS__ // Use this to cleanly wrap list items in parentheses and get syntax highlighting

#define TILE_DATA(MACRO) \
    MACRO(GRASS,CSL(ESprite::TILE_GRASS), 0) \
    MACRO(SAND, CSL(ESprite::TILE_SAND), 0) \
    MACRO(ROAD, CSL(ESprite::TILE_ROAD), 0) \
    MACRO(WATER, CSL(ESprite::TILE_WATER_ANIM_0, ESprite::TILE_WATER_ANIM_1, ESprite::TILE_WATER_ANIM_2, ESprite::TILE_WATER_ANIM_1), 0.75) \
    MACRO(LAVA, CSL(ESprite::TILE_LAVA_ANIM_0, ESprite::TILE_LAVA_ANIM_1, ESprite::TILE_LAVA_ANIM_2, ESprite::TILE_LAVA_ANIM_1), 0.75) \
    MACRO(TREE, CSL(ESprite::TILE_TREE), 0) \
    MACRO(SUN, CSL(ESprite::TILE_SUN), 0) \
    MACRO(FLOWER, CSL(ESprite::TILE_FLOWER), 0) \
    MACRO(HOUSE_DOOR, CSL(ESprite::TILE_HOUSE_DOOR), 0) \
    MACRO(HOUSE_WINDOW, CSL(ESprite::TILE_HOUSE_WINDOW), 0) \
    MACRO(HOUSE_WALL, CSL(ESprite::TILE_HOUSE_WALL), 0) \
    MACRO(BLUE_HOUSE_ROOF_UPPER_LEFT, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_UPPER_LEFT), 0) \
    MACRO(BLUE_HOUSE_ROOF_UPPER_MID, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_UPPER_MID), 0) \
    MACRO(BLUE_HOUSE_ROOF_UPPER_RIGHT, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_UPPER_RIGHT), 0) \
    MACRO(BLUE_HOUSE_ROOF_LOWER_LEFT, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_LOWER_LEFT), 0) \
    MACRO(BLUE_HOUSE_ROOF_LOWER_MID, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_LOWER_MID), 0) \
    MACRO(BLUE_HOUSE_ROOF_LOWER_RIGHT, CSL(ESprite::TILE_BLUE_HOUSE_ROOF_LOWER_RIGHT), 0) \
    MACRO(WOOD_FLOORBOARD, CSL(ESprite::TILE_WOOD_FLOORBOARD), 0) \
    MACRO(STONE_BRICK, CSL(ESprite::TILE_STONE_BRICK), 0) \

enum class ETile : unsigned char {
    TILE_DATA(TILE_TO_ENUM)
    NUM_TILES_OR_EMPTY
};