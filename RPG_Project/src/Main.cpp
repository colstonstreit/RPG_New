#include "Dialogue.h"
#include "Game.h"
#include "Scenes/Scene3DTest.h"
#include "Scenes/Scene2D.h"

#include <iostream>
#include <string>

int oldMain(int argc, char** argv) {

    DialogueManager::parse("res/dialogue/Dialogue_Test.dlog");
    DialogueController* dc = DialogueManager::get("introNpc");
    const std::unordered_map<std::string, bool> gameFlags = {
        { "worldSaved", true },
        { "dogMissing", false }
    };
    dc->chooseDialogue(gameFlags);

    return 0;
}

int main(int argc, char** argv) {
    Game game = Game(1920, 1080, "RPG Version 2");
    game.init();
    game.changeScene(new Scene2D(game));
    game.run();
    return 0;
}