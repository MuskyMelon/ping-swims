/*Gamestate values:
 * 0: menu
 * 1: play
 * 3: exit
 * 4: score
 
 * It is important to check the gameState in every method where elements of different scenes are controlled
 * For example: void keyPressed contains the controls for the menu and the game, 
 so this method should have a switch statement to enable the correct controls in their respective scene.
 */


class GameState {

  int gameStateVal = 0;

  //This function is to be called whenever the screen actually changes. (Main menu, play scene, highscore scene etc)
  void changeGameState(int state) { 
    gameStateVal = state;
    switch(state) {
    case 0: //menu
      {
        initMenuElements();
        break;
      }
    case 1: //play
      {
        initPlayElements();
        resetGame();
        break;
      }
    case 2:
      {
        score = new ScoreScreen();
        score.init();
        save = new ScoreSave();
        save.setup();
        break;
      }
    case 4: //score
      {
        playSong.pause();

        gameOver_sound.play();
        gameOver_sound.rewind();
        print("TESTING ");
      }
    }
  }

  //Check what needs to be updated during what state.
  void UpdateGameState() { 
    switch(gameStateVal) {
    case 0: //menu
      {
        menu.update();
        break;  
      }
    case 1: //play
      {

        playDrawElements();
        break;
      }
    case 2:
      {
        score.saveAndDislayHighscore();
        break;
      }
    case 4:
      {
        score.triggerScore();
        /* moet weg en  gefixt worden */

        thePinguin.uPressed = false;
        thePinguin.rPressed = false;
        thePinguin.lPressed = false;
        thePinguin.dPressed = false;
        break;
      }
    }
  }
}