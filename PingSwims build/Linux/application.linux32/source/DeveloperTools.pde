class DeveloperTools {
  boolean tPressed = false; // check if t is pressed
  float countdown = 0; // countdown for text 
  boolean numpad1Pressed, numpad2Pressed, numpad3Pressed, numpad4Pressed, numpad5Pressed, numpad6Pressed; // keys with functions 
  String invisibleModeText;
  int textX = 0;


  void draw() {
    if (tPressed == true && countdown > 0) {
      // if developer mode is enabled
      countdown -= 1;
      textSize(40);
      fill(255, 255, 255);
      textAlign(CENTER);
      text("Developer tools enabled", width/2, 70);
    } else if (tPressed == false && countdown > 0) {
      // if developer mode is disabled
      countdown -= 1;
      textSize(40);
      fill(255, 255, 255);
      textAlign(CENTER);
      text("Developer tools disabled", width/2, 70);
    }

    //all usable functions if developer mode is enabled
    if (tPressed == true) {
      // frame rate counter - num pad 1
      if (numpad1Pressed == true) {
        textSize(30);
        fill(255, 255, 255);
        textAlign(CENTER);
        text(frameRate, 100, 200);
      }
      if (numpad2Pressed == true) {
        textSize(10);
        textX = width - 50;
        for (int i = 0; i < encounter.maxPoints; i++) {
          if (encounter.point[i] != null) {  
            fill(0 + 1, 255, 0);
            text(i, textX, 30);
            text("o", textX, 50);
            textX -= 30;
          } else {
            fill(255, 0, 0);
            text(i + 1, textX, 30);
            text("x", textX, 50);
            textX -= 30;
          }
        }
        textX -= 25;
        text("Points:", textX, 50);
      }
      if (numpad3Pressed == true) {
        textSize(10);
        textX = width - 50;
        for (int i = 0; i < encounter.maxEnemies; i++) {
          if (encounter.enemy[i] != null) {      
            fill(0, 255, 0);
            text(i + 1, textX, 80);
            text("o", textX, 100);
            textX -= 30;
          } else {
            fill(255, 0, 0);
            text(i + 1, textX, 80);
            text("x", textX, 100);
            textX -= 30;
          }
        }
        textX -= 25;
        text("Enemies:", textX, 100);
      }
      if (numpad4Pressed == true) {
        textSize(20);
        fill(255, 255, 255);
        textAlign(LEFT);
        text("enemy stats ", 10, 250);
        text("speed: " + encounter.enemySpeed, 10, 300);
        text("seconds to spawn: " + encounter.secondsToNewSpawn, 10, 350);
        text("difficulty times: " + encounter.oneMinuteCountdown + " + " + encounter.oneMinuteCountdownTwo, 10, 400);
      }
      if (numpad5Pressed == true) {
        encounter.invisible = true;
      } else {
        encounter.invisible = false;
      }
      if (numpad6Pressed == true) {
        powerupKind = (int)random(1, 4); 
        powerup.spawnPowerUp(width, (height/2), 10, 10, 0, powerupKind);///
        numpad6Pressed = false;
      }
      // show tool tips
      textSize(20);
      fill(255, 255, 255);
      textAlign(LEFT);
      if (encounter.invisible == true) {
        invisibleModeText = "on";
      } else {
        invisibleModeText = "off";
      } 
      text("Numpad \n 1 - see fps \n 2 - see point array \n 3 - see enemy array \n 4 - see enemy spawnStats \n 5 - invisible mode " + invisibleModeText + "\n 6 - spawn Powerup" , 50, height - 300);
    }
  }

  // check the pressed keys
  void keyPressed() {
    // if T is pressed enable or disable devmenu
    if (keyCode == 84 && tPressed == false) {
      tPressed = true;
      countdown = frameRate * 3;
    } else if (keyCode == 84 && tPressed == true) {
      tPressed = false;
      countdown = frameRate * 3;
    }
    // if numpad 1 is pressed enable framerate
    switch (keyCode) {
    case 129:
    case 49:
      if (numpad1Pressed == false) {
        numpad1Pressed = true;
      } else {
        numpad1Pressed = false;
      }
      break;
    case 50:
      if (numpad2Pressed == false) {
        numpad2Pressed = true;
      } else {
        numpad2Pressed = false;
      }
      break;
    case 51:
      if (numpad3Pressed == false) {
        numpad3Pressed = true;
      } else {
        numpad3Pressed = false;
      }
      break;
    case 52:
      if (numpad4Pressed == false) {
        numpad4Pressed = true;
      } else {
        numpad4Pressed = false;
      }
      break;
    case 53:
      if (numpad5Pressed == false) {
        numpad5Pressed = true;
      } else {
        numpad5Pressed = false;
      }
      break;
    case 54:
      if (numpad6Pressed == false) {
        numpad6Pressed = true;
      }
      break;
    }
  }
}
