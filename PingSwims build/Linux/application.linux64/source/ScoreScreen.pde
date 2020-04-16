class ScoreScreen {
  double score; // score 
  int backgroundColor = 0; // height of the background color
  int scoreTitleHeight; // height of the score title
  int scoreHeight; // height of the score
  char character1, character2, character3; // characters where the name is shown
  int lettercount1, lettercount2, lettercount3; // see the lettercount 
  int charPosition; // see what character is selected
  boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed; // booleans to see if a button is pushed
  String name; // the result of the 3 characters
  boolean isSaved; // check if the name is saved
  float bgX, bgY, bgX2, bgY2; // coordinates for the background
  PImage background; // background image
  PImage saveImage, saveSelectedImage;
  PImage playImage, selectedPlayImage, homeImage, selectedHomeImage;
  String addPoints;
  Table table;
  int txtHeight;
  int tempHighscore;
  String tempName;
  int scoreCountdown;
  boolean scoreFound;
  final String filename = "saveFile.csv"; 
  final int save_image_size = 90;
  final int highscoreImageSize = 150;

  ScoreScreen() {
    background = loadImage("./Sprites/water/Fullwater.png", "png");
    background.resize(width, height);
    saveImage = loadImage("./Sprites/menu/saveOff.png", "png");
    saveSelectedImage = loadImage("./Sprites/menu/saveOn.png", "png");
    playImage = loadImage("./Sprites/menu/playOff.png", "png");
    selectedPlayImage = loadImage("./Sprites/menu/playOn.png", "png");
    homeImage = loadImage("./Sprites/menu/homeOff.png", "png");
    selectedHomeImage = loadImage("./Sprites/menu/homeOn.png", "png");
    saveImage.resize(save_image_size, save_image_size);
    saveSelectedImage.resize(save_image_size, save_image_size);
    playImage.resize(highscoreImageSize, highscoreImageSize);
    selectedPlayImage.resize(highscoreImageSize, highscoreImageSize);
    homeImage.resize(highscoreImageSize, highscoreImageSize);
    selectedHomeImage.resize(highscoreImageSize, highscoreImageSize);
  }

  void init() {
    // ready the variables for the score screen  
    bgX = 0;
    bgX2 = width;
    isSaved = false;
    scoreTitleHeight = height / 100 * 20;
    scoreHeight = height / 100 * 40;
    score = 0;
    // set the name characters
    character1 = 'a';
    character2 = 'a';
    character3 = 'a';
    // set the positions for reset so that it will show a a a when you die again 
    charPosition = 1;
    lettercount1 = 1;
    lettercount2 = 1;
    lettercount3 = 1;
    scoreFound = false;
  }

  // add points if the game progresses
  void draw() {
    score += 0.1;
    showScore(score);
  }

  // draw the moving background in the score screen
  void drawBackground() {
    background(0, 191, 255);
    image(background, bgX, bgY);
    image(background, bgX2, bgY2);
  }

  // update the background so it moves
  void updateBackground() {
    bgX -= 5;
    bgX2 -= 5;
    if (bgX <= 0 - width) {
      bgX = width;
    }
    if (bgX2 <= 0 - width) {
      bgX2 = width;
    }
  }
  // draw the score screen
  void triggerScore() {
    if (isSaved != true) { 
      fill(255, 255, 255);
      updateBackground();
      drawBackground();
      // draw score title
      textSize(50);
      textAlign(CENTER);
      text("Game over", width/2, scoreTitleHeight);
      // draw score
      textSize(50);
      textAlign(CENTER);
      text("Score:", width/2, scoreHeight - 80); 
      textSize(40);
      textAlign(CENTER);
      text((int)score, width/2, scoreHeight);
      // calls the script that checks input
      changeNameByInput();
      //draws the name after change
      displayName();
    } else {
      saveAndDislayHighscore();
    }
  }
  // makes the 3 letter input and save button 
  void displayName() {
    textSize(50);
    textAlign(CENTER);
    text("enter your name", width/2, scoreHeight + 80);
    // draws the first character
    fill(255, 255, 255);
    if (charPosition == 1) {
      // gives a different collor if selected
      fill(102, 153, 255);
    }
    text(character1, width/2 - 70, scoreHeight + 170);

    // draws the second character
    fill(255, 255, 255);
    if (charPosition == 2) {
      // gives a different collor if selected
      fill(102, 153, 255);
    }
    text(character2, width/2, scoreHeight + 170);

    // draws the third character
    fill(255, 255, 255);
    if (charPosition == 3) {
      // gives a different collor if selected
      fill(102, 153, 255);
    }
    text(character3, width/2 + 70, scoreHeight + 170);

    // draws the save button
    fill(255, 0, 0);
    if (charPosition == 4) {
      image(saveSelectedImage, width/2 + 120, scoreHeight + 100);
    } else {
      image(saveImage, width/2 + 120, scoreHeight + 100);
    }
    textSize(23);
    fill(255, 255, 255);
  }

  // check the arrow keys and enter if pressed
  void keyPressed() {
    switch(keyCode) {
    case 87:
    case UP:
      {
        upPressed = true;
        break;
      }
    case 83:
    case DOWN:
      {
        downPressed = true;
        break;
      }
    case 65: 
    case LEFT:
      {
        leftPressed = true;
        break;
      }
    case 68:
    case RIGHT:
      {
        rightPressed = true;
        break;
      }
    case ' ': 
    case ENTER:
      {
        enterPressed = true;
      }
    }
  }
  // check the arrow keys and enter if released
  void keyReleased() {
    switch(keyCode) {
    case 87:
    case UP:
      {
        upPressed = false;
        break;
      }
    case 83:
    case DOWN:
      {
        downPressed = false;
        break;
      }
    case 65: 
    case LEFT:
      {
        leftPressed = false;
        break;
      }
    case 68:
    case RIGHT:
      {
        rightPressed = false;
        break;
      }
    case ' ': 
    case ENTER:
      {
        enterPressed = false;
      }
    }
  }


  void changePosition(int min, int max) {
    // check what letter is selected
    if (leftPressed == true && charPosition != min) {
      charPosition--;
      leftPressed = false;
    } else if (rightPressed == true && charPosition != max) {
      charPosition++;
      rightPressed = false;
    }
  }

  void changeNameByInput() {
    changePosition(1, 4);
    // change letters on up and down keys for each letter
    if (charPosition == 1) {
      if (upPressed == true && lettercount1 != 26) {
        character1++;
        lettercount1++;
        upPressed = false;
      } else if (downPressed == true && lettercount1 != 1) {
        character1--;
        lettercount1--;
        downPressed = false;
      }
    } else if (charPosition == 2) {
      if (upPressed == true && lettercount2 != 26) {
        character2++;
        lettercount2++;
        upPressed = false;
      } else if (downPressed == true && lettercount2 != 1) {
        character2--;
        lettercount2--;
        downPressed = false;
      }
    } else if (charPosition == 3) {
      if (upPressed == true && lettercount3 != 26) {
        character3++;
        lettercount3++;
        upPressed = false;
      } else if (downPressed == true && lettercount3 != 1) {
        character3--;
        lettercount3--;
        downPressed = false;
      }
    } else if (charPosition == 4 && enterPressed == true) {
      // check if the submit button was pushed
      name = String.valueOf(character1) + String.valueOf(character2) + String.valueOf(character3);
      selectSound.play();
      selectSound.rewind();
      enterPressed = false;
      isSaved = true;
    }
  }

  // show score in game on the top left
  void showScore(double scoreGetal) {
    textSize(40);
    fill(255, 255, 255);
    textAlign(LEFT);
    if (pointcountdown > 0) {
      addPoints = " + " + pickupScore;
    } else {
      addPoints = "";
    }
    text((int)scoreGetal + addPoints, 70, 120);
  }

  // access the save and reset game 
  void saveAndDislayHighscore() {
    if (name != null) {
      save.scoreCheck((int)score, name);
    }
    fill(255, 255, 255);
    updateBackground();
    drawBackground();
    txtHeight = 0;
    // draws scores
    textSize(50);
    textAlign(CENTER);
    text("HighScore", width/2, scoreTitleHeight - 40);

    table = loadTable("./data/" + filename, "header");
    scoreCountdown = 10;
    for (TableRow row : table.rows()) {
      txtHeight += 60;
      tempHighscore = row.getInt("Highscore");
      tempName = row.getString("Name");

      textSize(35);
      textAlign(CENTER);
      fill(255, 255, 255);
      if (scoreCountdown == save.check) {
        fill(255, 0, 0);
        scoreFound = true;
      } 
      scoreCountdown -= 1;
      text(tempName + "  " + tempHighscore, width/2, scoreTitleHeight + txtHeight);
    }
    txtHeight += 60;
    if (charPosition > 2) {
      charPosition = 1;
    }
    changePosition(1, 2);
    if (charPosition == 1) {
      image(selectedPlayImage, width/2 - 240, height - 250);
      image(homeImage, width/2 + 120, height - 250);
      if (enterPressed == true) {
        GameStateManager.changeGameState(1);
      }
    } else if (charPosition == 2) {
      image(playImage, width/2 - 240, height - 250);
      image(selectedHomeImage, width/2 + 120, height - 250);
      if (enterPressed == true) {
        GameStateManager.changeGameState(0);
      }
    }
    /*
     if (enterPressed == true && name != null) {
      GameStateManager.changeGameState(1);
      selectSound.play();
      selectSound.rewind();
    } else if (enterPressed == true) {
      GameStateManager.changeGameState(0);
    }*/
  }
}
