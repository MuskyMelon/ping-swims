class Pinguin {

  int 
    framesPinguin = 18, 
    pinguinFrame = 0, 
    bltCounter = 0, 
    coolDown = 0;
  float vxBullet = 30;
  
  PVector 
    startPosition, //Defines the coordinates of the start position which will be used to reset position upon respawn.
    position, //The x and y coordinates of the player.
    velocity, //The speed that the player is moving.
    acceleration, //Used to calculate the speed.
    gravity; //The direction which the player is moving to

  boolean //Booleans for the arrow key presses
    uPressed, 
    dPressed, 
    lPressed, 
    rPressed, 
    jumping, 
    idle;

  float
    playerWidth = height / 100 * 7, //width of the player
    playerHeight = height / 100 * 4, //height of the player
    moveForce = 5, //Defines the amount of force the player has to move
    moveForceUp = 5;

  final float 
    VELOCITY_DRAG = 0.5, //The drag force the players has.
    SPEED = 2, 
    ACCELL_RATE = 1;
  final int KEY_LIMIT = 1024;

  final PVector NORMAL_GRAVITY;
  final PVector JUMPING_GRAVITY;

  boolean[] keysPressed = new boolean[KEY_LIMIT];

  //The pinguin will be initiated here.
  Pinguin(float x, float y) {
    //playerWidth = pwidth;
    //playerHeight = pheight;
    position = new PVector(x, y);
    startPosition = new PVector(x, y);
    velocity = new PVector(0, 0);
    acceleration = new PVector(0, 0);
    NORMAL_GRAVITY = new PVector(5, 0.001);
    JUMPING_GRAVITY = new PVector(0.0001, 5.05);;
    gravity = NORMAL_GRAVITY;
  }

  //====================================================================================

  //Setting the player in the starting position and determining the size of the hitbox
  void initial() { 

    position = new PVector(startPosition.x, startPosition.y); //We define the start position here due to the resetGame() method in the main script.
  }

  //====================================================================================

  void draw() { 

    if (lifecounter == powerup.defaultLife)
    {
      //rect(position.x, position.y, size, size); //DEBUG, DO NOT REMOVE UNTIL FINAL PHASE.
      drawPingFrames();
    }
  }

  void drawPingFrames() {

    image(pinguinSprite[pinguinFrame], position.x, position.y, playerWidth, playerHeight);
    if (pinguinFrame == framesPinguin-1) {
      pinguinFrame = 0;
    } else {
      pinguinFrame++;
    }
  }

  //====================================================================================

  void update() {
    // pressing spacebar will shoot a bullet
    if (keysPressed[' '] && coolDown == 0) {   
      if (lifecounter == powerup.shootLife) {
        shoot.play();
        shoot.rewind();
      }
      fireBullet(vxBullet, 0);
    }

    if (coolDown > 0) coolDown --;

    if (lifecounter == powerup.defaultLife)
    {
      bltCounter = 0;
    }
    
    applyForce(gravity);
    controls();
    setBoundaries();
    
    limitDrag();
   
    //if no keys are being pressed, stop acceleration.
    
    velocity.add(acceleration);
    velocity.limit(10);
    
    position.add(velocity);
    acceleration.mult(0);

    if (watery < 5) {
      jumping = false;
    }

    if (jumping) { //if the player is jumping increase gravity over time so he eventually falls down.
      gravity.mult(1.10);
    } else {
      gravity = new PVector(0.0001, 0.003);
    }
  }

  void fireBullet(float tx, float ty)
  {
    bullets[bltCounter].fire(tx, ty); // shoot the bullet
    bullets[bltCounter].isFired = true;
    bltCounter ++;
    coolDown = 60;
  }

  //====================================================================================

  void applyForce(PVector force) {
    PVector f = PVector.div(force, 1);
    
    acceleration.add(f);
  }

  //====================================================================================

  void setBoundaries() {

    if (position.x < 0 || position.x > width) {
      velocity.x *= -1;
    }

    if (rPressed && position.x > width - 50) {
      velocity.x = 0;
    }

    if (position.y > height - 50) {
      velocity.y *= -1;
    }
    
    if(position.y > height - playerHeight){
      position.y = height - playerHeight;
    }
  }

  //====================================================================================
  void limitDrag() {//This is to be able to quickly move to the opposite direction.
    if(!rPressed){
      if(velocity.x > 0){
      velocity.mult(0.95);
      }
    }
    
    if(!uPressed){
      if(velocity.y > 0){
       velocity.mult(0.95);
      }
    }
    
    if(!dPressed){
      if(velocity.y < 0){
        velocity.mult(0.95);
      }
    }
  }

  void controls() {
    if (uPressed) {
      if (velocity.y > 0) {
        velocity.y = 0;
      }
      acceleration.y -= SPEED * ACCELL_RATE;
    }
    if (dPressed) {
      if (velocity.y < 0) {
        velocity.y = 0;
      }
      acceleration.y += SPEED * ACCELL_RATE;
    }

    if (lPressed) {
      acceleration.x -= SPEED * ACCELL_RATE;
    }

    if (rPressed) {
      acceleration.x += SPEED * ACCELL_RATE;
    }
   
    
  }
  void keyPressed() {
    println(keyCode);
    switch(keyCode) {
    case 87:
    case UP:
      {
        uPressed = true;
        break;
      }
    case 83:
    case DOWN:
      {
        dPressed = true;
        break;
      }
    case 65: 
    case LEFT:
      {
        lPressed = true;
        break;
      }
    case 68:
    case RIGHT:
      {

        rPressed = true;
        break;
      }
    }

    //=================================================================  
    //safety: if keycode exceeds tha maximum number, break off methhod ('return').
    keysPressed[keyCode] = true;
  }

  void keyReleased() {

    switch(keyCode) {
    case 87:
    case UP:
      {
        uPressed = false;
        break;
      }
    case 83:
    case DOWN:
      {
        dPressed = false;
        break;
      }
    case 65: 
    case LEFT:
      {
        lPressed = false;
        break;
      }
    case 68:
    case RIGHT:
      {
        rPressed = false;
        break;
      }
    }

    //=================================================================    

    keysPressed[keyCode] = false;
  }
}
