class Menu {
  final int 
    MAX_BUTTON_IMAGES = 3, 
    LOWEST_SELECT_VALUE = 0, //The lowest value that the button select can go
    HIGHEST_SELECT_VALUE = 2, //The highest value that the button select can go
    SELECT_SPACING = 10, //Spacing of the button select with the button
    image_size = 200;

  final float spacing = 100;

  PImage[] buttonImages = new PImage[MAX_BUTTON_IMAGES]; //Saving the button images here
  PImage[] selectImages = new PImage[MAX_BUTTON_IMAGES];
  PVector[] imagePos = new PVector[MAX_BUTTON_IMAGES]; //Saving the button coordinates here

  PImage background, title;

  float 
    margin, 
    selectX, //x position of the button select
    selectY, //y position of the button select
    pingX = 50, 
    timer = 5, 
    bgX, 
    bgY, 
    bgX2, 
    bgY2, 
    buttonMoveSpeed, 
    titleXPos, 
    titleYPos;

  int 
    buttonSelected, 
    waterWidth, 
    waterHeight;

  boolean
    disableKeyPress, 
    sceneTransition;
  //================================================================

  void init() {

    //setting background values and loading image.
    waterWidth = width;
    waterHeight = height;
    bgX = 0;
    bgX2 = waterWidth;

    background = loadImage("./Sprites/water/Fullwater.png", "png");
    background.resize(waterWidth, waterHeight);
    title = loadImage("./Sprites/menu/logo.png", "png");


    loadMenuImages();
  }

  void update() {
    background(0, 191, 255);
    updateBackground(); //updating the background's position
    drawBackground(); //drawing the background

    switch(GameStateManager.gameStateVal) {
    case 0:
      {
        showTitle();
        checkButtonValue();
        showImages();
        pingDraw();
        break;
      }
    }
  }
  //===============background draw and update=====================

  void drawBackground() {
    image(background, bgX, bgY);
    image(background, bgX2, bgY2);
  }

  void updateBackground() { 
    bgX -= 5;
    bgX2 -= 5;
    if (bgX <= 0 - waterWidth) {
      bgX = waterWidth;
    }
    if (bgX2 <= 0 - waterWidth) {
      bgX2 = waterWidth;
    }
  }

  //================================================================

  void loadMenuImages() { //images of the buttons in the menu
    buttonImages[0] = loadImage("./Sprites/menu/playOff.png");
    buttonImages[1] = loadImage("./Sprites/menu/HighscoreOff.png");
    buttonImages[2] = loadImage("./Sprites/menu/ExitOff.png");

    selectImages[0] = loadImage("./Sprites/menu/playOn.png");
    selectImages[1] = loadImage("./Sprites/menu/HighscoreOn.png");
    selectImages[2] = loadImage("./Sprites/menu/ExitOn.png");

    //Initialize position of buttons
    float left_spacing = 3;
    float x_calculation, y_calculation;

    margin = width/left_spacing - (image_size/2); //margin from the left side of the screen to the position of the first image.
    for (int i=0; i<MAX_BUTTON_IMAGES; i++) {
      buttonImages[i].resize(image_size, image_size);

      x_calculation = margin + ((image_size + spacing) * i);
      y_calculation = height/2;

      imagePos[i] = new PVector(x_calculation, y_calculation);
    }
  }

  void showImages() { //Drawing the buttons on the screen
    if (sceneTransition && buttonMoveSpeed < height) buttonMoveSpeed += 20;

    switch(buttonSelected) { //Draw the selected button.
    case 0:
      {
        image(selectImages[0], imagePos[0].x, imagePos[0].y + buttonMoveSpeed);
        break;
      }
    case 1:
      {
        image(selectImages[1], imagePos[1].x, imagePos[1].y + buttonMoveSpeed);
        break;
      }
    case 2:
      {
        image(selectImages[2], imagePos[2].x, imagePos[2].y + buttonMoveSpeed);
        break;
      }
    }

    for (int i=0; i<MAX_BUTTON_IMAGES; i++) { //Draws the buttons which are not selected.
      if (i != buttonSelected) image(buttonImages[i], imagePos[i].x, imagePos[i].y + buttonMoveSpeed);
    }
  }

  void pingDraw() {
    if (sceneTransition && titleYPos < 0 && buttonMoveSpeed >= height) {
      if (pingX < width) {
        pingX += 20;
      } else {
        GameStateManager.changeGameState(1);
      }
    }

    image(pinguinSprite[pinguinFrame], pingX, height/3, thePinguin.playerWidth, thePinguin.playerHeight);
    if (pinguinFrame == framesPinguin-1) {
      pinguinFrame = 0;
    } else {
      pinguinFrame++;
    }
  }

  //================================================================

  void showTitle() { //TITLE TEXT NEEDS TO REPLACED WITH AN ACTUAL TITLE IMAGE
    titleXPos = width/2 - (title.width/2); 
    titleYPos = 150 + (buttonMoveSpeed *-1);
    image(title, titleXPos, titleYPos, title.width, title.height);
  }

  //================================================================

  void checkButtonValue() { //You cannot navigate further than the first or last button
    if (buttonSelected < LOWEST_SELECT_VALUE) buttonSelected = LOWEST_SELECT_VALUE;
    if (buttonSelected > HIGHEST_SELECT_VALUE) buttonSelected = HIGHEST_SELECT_VALUE;
  }

  //================================================================

  void checkSelectedButton() {
    switch(buttonSelected) {
    case 0: //first button selected (play)
      {
        disableKeyPress = true;
        sceneTransition = true;
        break;
      }
    case 1: 
      {
        GameStateManager.changeGameState(2);
        break;
      }
    case 2: 
      {
        exit();
        break;
      }
    }
  }

  //================================================================

  void keyPressed() {
    if (!disableKeyPress) {
      switch(GameStateManager.gameStateVal) {
      case 0:
        {
          switch(keyCode) {
          case 65: //a
          case LEFT:
            {
              buttonSelected --;
              break;
            }
          case 68: //d
          case RIGHT:
            {
              buttonSelected ++;
              break;
            }
          case ' ':
          case ENTER:
            {
              selectSound.play();
              selectSound.rewind();
              checkSelectedButton();
              break;
            }
          }
          break;
        }
      }
    }
  }
}