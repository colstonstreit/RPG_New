#include "Game.h"
#include "Scenes/Scene3DTest.h"
#include "Scenes/Scene2D.h"

#include <iostream>
#include <string>

int main(int argc, char** argv) {
    Game game = Game(900, 900, "RPG Version 2");
    game.Init();
    game.ChangeScene(new Scene2D(game));
    game.Run();
    return 0;
}