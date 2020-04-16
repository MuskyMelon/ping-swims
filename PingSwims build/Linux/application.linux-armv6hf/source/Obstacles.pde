class Obstacles
{
  PImage obstacle;
  float xPos, yPos, oWidth, oHeight; // x position, y position, object width, object height
  float timer, spawnTimeInSeconds; // timer will be used for the actual timer. spawnTimeInSeconds is the variable to save the actual spawntime.
  String oType, obType;
  void init() {
    //timer = spawnTimeInSeconds;
    if (obType == "Ship") {
      obstacle = loadImage("./Sprites/Objects/shipwreck.png");
      obstacle.resize((int)shipWidth, (int)shipHeight);
    }
    if (obType == "Ice"){
      obstacle = loadImage("./Sprites/Objects/ice.png");
      obstacle.resize((int)iceWidth, (int)iceHeight);
    }
  }
    void draw() {
      updateObstacle();
    }

  void updateObstacle() {
    //Check if the object is fully through the left side of the screen.
    if (xPos < 0 - oWidth) {
      if (spawnObject(spawnTimeInSeconds)) {
        xPos = width;
      }
    }
    //moving
    xPos -= 4; //Change this value to the actual game speed.
   
    image(obstacle, xPos, yPos + watery, oWidth, oHeight);
    

    for (int i =0; i<bullets.length; i++)
    {
      if (CollisionWithObstacleBullet(bullets[i], ice) || CollisionWithObstacleBullet(bullets[i], ship)) // if the bullets collide with obstacles they will dissapear.
      {
        xPos = 0 - oWidth;
      }
    }
    if (collides(thePinguin))
    {
      if (lifecounter<= powerup.invincibilityLife && lifecounter > powerup.shieldLife)//if the player collides with a obtacle while being in invincible mode, the obstacle dissapears
      {
        xPos = 0-oWidth;
      }
    }
  }

  boolean collides(Pinguin ping)
  {
    if (ping.position.x + ping.playerWidth >= xPos && // Ping right edge past Enemy left
      ping.position.x <= xPos + oWidth && // Ping left edge past Enemy right
      ping.position.y + ping.playerHeight >= yPos + watery && // Ping top edge past Enemy bottom
      ping.position.y <= yPos + oHeight + watery)   // Ping bottom edge past Enemy top
    {
      
      return true;
    } else
    {
      return false;
    }
  }

  boolean CollisionWithObstacleBullet(Bullet b, Obstacles o)
  {

    if (b.x + b.size >= xPos && // bullet right edge past Enemy left
      b.x <= xPos + oWidth && // bullet left edge past Enemy right
      b.y + b.size >= yPos && // bullet top edge past Enemy bottom
      b.y <= yPos + oHeight)   // bullet bottom edge past Enemy top
    {
      return true;
    } else
    {
      return false;
    }
  }

  boolean spawnObject(float time) {
    if (timer > 0) {
      timer -= (float)1/60;
    } else {
      timer = time;
      return true;
    }
    return false;
  }

  //oHeight has to be declared before declaring the y. 
  //This is because of the fact that shipwrecks are at the bottom of the screen - the height.
  void spawnObstacle(float x, float y, float w, float h, float spawnTime, String oType) { 
    oWidth = w;                     //println("oWidth: "+ oWidth);
    oHeight = h;                    //println("oHeight: "+ oHeight);
    xPos = x;                       //println("xPos: "+ xPos);
    yPos = y;                       //println("yPos: "+ yPos);
    spawnTimeInSeconds = spawnTime; //println("spawnTimeInSeconds: "+ spawnTimeInSeconds);
    obType = oType;                 //determines the type of the object
  }
}
