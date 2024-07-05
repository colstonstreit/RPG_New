#include "Game.h"
#include "Scenes/Scene3DTest.h"
#include "Scenes/Scene2D.h"

#include <iostream>
#include <string>

int main(int argc, char** argv) {
    Game::Initialize(900, 900, "RPG Project V2");
    Game::ChangeScene(new Scene2D());
    Game::Run();
    return 0;
}