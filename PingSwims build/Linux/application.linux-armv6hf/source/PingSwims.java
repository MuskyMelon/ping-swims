import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PingSwims extends PApplet {


PowerUp powerup;
Pinguin thePinguin;
Obstacles ship;
Obstacles ice;
ScoreScreen score;
Encounter encounter;
GameState GameStateManager;

ScoreSave save;
DeveloperTools tools;
Bullet[] bullets;
Menu menu;
Snow snow;

int index;

//point pickup
int scoreForPoint = 15;
int pickupScore;
int seconde = 60;
int pointcountdown;
float scoreHeightAlt;
boolean isCollected = false;
float pointX;

PFont font;
Level l1;
int lifecounter;
Minim minim;

AudioPlayer 
  song, 
  playSong, 
  damage, 
  point, 
  shoot, 
  bulletHit, 
  star_pickup, 
  star_hit, 
  gun_pickup, 
  bubble_pickup, 
  gameOver_sound, 
  selectSound;


Timer startTimer;
boolean isNotDead = false;

float //Ship spawn values
  shipHeight, 
  shipWidth, 
  shipSpawnTime;
String shipType;

float //ice spawn values
  iceHeight, 
  iceWidth, 
  iceSpawnTime;
String iceType;

float //powerup spawn values
  powerupWidth, 
  powerupHeight, 
  xPos, 
  yPos, 
  powerupSpawnTime;
int powerupKind;

float //pinguin spawn values
  startX, 
  startY;

//Pinguin sprites
int 
  framesPinguin = 18, 
  pinguinFrame = 0;

int EnemyComboBonus;
int perComboBonus = 1;

PImage[] pinguinSprite = new PImage[framesPinguin];

public void setup() {
  //============================================
  font = createFont("./font/8-bit pusab.ttf", 1);
  textFont(font);
  
  //size(1280, 1000);
  initAudioFiles();
  gameOver_sound.play();
  song.play();
  song.loop(); //loop the background music.

  song.setGain(-20.0f); //set volume a bit lower, needs to be handled by controls in menu later.
  playSong.setGain(-20);
  bulletHit.setGain(-20);
  shoot.setGain(-20);
  gun_pickup.setGain(-20);
  bubble_pickup.setGain(-20);
  star_pickup.setGain(-20);
  //Initializing gameStateManager and setting gamestate to 0 (menu).
  GameStateManager = new GameState();
  GameStateManager.changeGameState(0);
}

//===============================================

public void initAudioFiles() {
  minim = new Minim(this); 
  damage = minim.loadFile("./sounds/damage.wav");
  point = minim.loadFile("./sounds/coin.wav");
  selectSound = minim.loadFile("./sounds/Select.wav");
  song = minim.loadFile("./sounds/background_music.wav");
  playSong = minim.loadFile("./sounds/play_bg_music.wav");
  shoot = minim.loadFile("./sounds/shoot.wav");
  bulletHit = minim.loadFile("./sounds/bullet_hit.wav");
  star_pickup = minim.loadFile("./sounds/powerup_pickup.wav");
  star_hit = minim.loadFile("./sounds/star_hit_enemy.wav");
  gun_pickup = minim.loadFile("./sounds/gun_pickup.wav");
  bubble_pickup = minim.loadFile("./sounds/bubble_pickup.wav");
  gameOver_sound = minim.loadFile("./sounds/gameOverSound.wav");
}

public void initMenuElements() {
  startX = 50;
  startY = height/2;
  thePinguin = new Pinguin(startX, startY);
  for (int i = 0; i<framesPinguin; i++) {
    pinguinSprite[i] = loadImage("./Images/Pinguin/pinguin-"+i+".png");
  }

  menu = new Menu();
  menu.init();
}

//===============================================

public void initPlayElements() {
  song.pause();
  playSong.play();
  playSong.loop();

  lifecounter=1;
  index = 0;

  //INIT CLASSSES.
  tools = new DeveloperTools();
  powerup = new PowerUp();
  bullets = new Bullet[6];
  ice = new Obstacles();
  ship = new Obstacles();
  snow = new Snow();

  //Spawn values
  shipHeight = 100;
  shipWidth = 260;
  shipSpawnTime = 5;
  shipType = "Ship";

  iceHeight = 100;
  iceWidth = 100;
  iceSpawnTime = 3;
  iceType = "Ice";

  powerupWidth = width;                   
  powerupHeight =(height/2);                  
  xPos = 10;                     
  yPos = 10;                       
  powerupSpawnTime = random(30, 60); 
  powerupKind = (int)random(1, 4); 

  //audio
  startTimer = new Timer(500);//store the current time in frames
  point.setGain(-20);
}

//===============================================


public void draw() {
  GameStateManager.UpdateGameState();
  noCursor();
}

//===============================================

public void playDrawElements() {
  if (!isNotDead) {

    l1.draw();
    fill(0, 255, 0);

    ship.draw();
    ice.draw();
    tools.draw();

    fill(255, 0, 0);
    encounter.draw();


    for (int i=0; i<bullets.length; i++) {
      bullets[i].update();
      bullets[i].draw();
    }

    powerup.draw();

    fill(255, 0, 0);

    for (int i = 0; i < encounter.maxEnemies; i++) {
      if (encounter.collidesWithEnemy(thePinguin, encounter.enemy[i] ) || 
        ice.collides(thePinguin) || ship.collides(thePinguin)) {

        damage.play();
        damage.rewind();
        lifecounter--; // a live will be taken
        if (lifecounter == 0)
        {
          GameStateManager.changeGameState(4);
          isNotDead = true;
        } else if (lifecounter <= powerup.shieldLife && lifecounter > powerup.shootLife)//no damagesound is heard when you collide with the enemy and have a shield powerup & will activate the shield timer.
        {
          damage.pause();
          powerup.startTime = true;
        }
      }
    } 

    // geef punten als de speler tegen punten aan komt
    if (encounter.collidesWithPoint(thePinguin)) {
      point.play();
      point.rewind();
      if (pointcountdown > 0) {
        EnemyComboBonus += perComboBonus;
        score.score += scoreForPoint + EnemyComboBonus;
        pickupScore += scoreForPoint + EnemyComboBonus;
      } else {
        EnemyComboBonus = 0;
        score.score += scoreForPoint;
        pickupScore = scoreForPoint;
      }
      pointcountdown = seconde * 1;
      scoreHeightAlt = thePinguin.position.y - 20;
      pointX = thePinguin.position.x;
    }

    if (pointcountdown > 0) { 
      //haalt score eraf 
      pointcountdown -= 1;
      textSize(23);
      fill(255, 255, 255);
      text("+" + pickupScore, pointX, scoreHeightAlt);
      scoreHeightAlt -= 1;
    }

    score.draw();
    thePinguin.update();
    thePinguin.draw();


    //text(frameRate, width/2, 100); fps checken
  } else {
  }
}

//===============RESET FUNCTION====================

public void resetGame() {
  isNotDead = false;
  score = new ScoreScreen();
  save = new ScoreSave();

  l1 = new Level();
  l1.init();
  snow.init();
  lifecounter =1;
  encounter = new Encounter();
  thePinguin.initial();
  powerup.init();
  score.init();
  save.setup();

  //spawnObstacle(x, y, w, h, spawnTime);
  ship.spawnObstacle(width, height - shipHeight, shipWidth, shipHeight, shipSpawnTime, shipType); 
  ice.spawnObstacle(width, 0 -(iceHeight/2), iceWidth, iceHeight, iceSpawnTime, iceType);  //0 - (iceHeight/2) will get the upper half of the ice obstacle stick out of the screen.

  ship.init();
  ice.init();

  powerup.spawnPowerUp(width, (height/2), 10, 10, powerupSpawnTime, powerupKind);///
  for (index=0; index<bullets.length; index++) { 
    bullets[index]= new Bullet(); //Display the bullets at the position where the character was when shooting
    bullets[index].init();
  }
}

//===============================================

public void keyPressed() {
  switch(GameStateManager.gameStateVal) {
  case 0: //menu
    {
      menu.keyPressed();
      break;
    }
  case 1: //play
    {
      tools.keyPressed();
      thePinguin.keyPressed();
      break;
    }
  case 2: //help
    {
      score.keyPressed();
      break;
    }
  case 4://score
    {
      score.keyPressed();
      break;
    }
  }
}

public void keyReleased() {
  switch(GameStateManager.gameStateVal) {
  case 1: //play
    {
      thePinguin.keyReleased();
      break;
    }
  case 2: //help
    {
      score.keyReleased();
      break;
    }
  case 4://score
    {
      score.keyReleased();
      break;
    }
  }
}
class Bullet {

  // a position
  float x, y;
  // a velocity (direction)
  float vx, vy;
  // a way to see if it is being fired
  boolean isFired;
  //a way to see if the bullet is visible
  boolean isActive;
  // an indicator for active bullets
  float life;
  //a size
  float size;
  // a variable that makes the bullet larger
  float expansion;
  
  // The init method can be called to set a bullet to it's default state
  public void init() 
  {
    size = 1;
    expansion = 1;
    // Our bullets are energyballs
    reinit();
  }
  public void reinit()
  {
    // the bullet is not being fired right now
    isFired = false;

    // a bullets starts outside the window when it is not being fired 
    x = -1000;    
    y = -1000;

    // The bullet starts out with 0 velocity
    vx = 0;
    vy = 0;
  }

  // Whenever you want to update a bullet, call this method
  public void update() {
   
    // if the bullet is being fired
    if (isFired) {
      if (lifecounter == powerup.shootLife) {    
        
        // bullet is visisble
        isActive =true;
       
        //this will expand the size of the bullet by 1 each update
        size = size + expansion;
        
        // if the bullet runs out of our window, return it to its initial state
        if (x > width) 
        {
          // bullet is not visible
          isActive=false;
          init();
        }

        // use the velocity to calculate the new position
        x += vx;
        y += vy;
        
      } else
      { 
        // if the lifecounter is anything else but equal to the shootpowerup, the bullet will be reset when fired.
        init();
      }
    }
  }

  // Call this method to signify that the bullet has been fired
  public void fire(float tx, float ty) {   
    isFired = true;
    
    // Start the bullet at the player position
    x = thePinguin.position.x+50;
    y = thePinguin.position.y+20;
    
    // the horizontal velocity should be 13
    vx = tx;
    vy = ty;
  }

  // Whenever you want to draw the bullet, call this method
  public void draw() {
    
    fill(255, 165, 0);// orange
    
    //the bullet will stop with expanding if size =30;
    if (size == 30)
    {
      expansion=0;
    }
    
    ellipse(x, y, size, size);
  }
}
class DeveloperTools {
  boolean tPressed = false; // check if t is pressed
  float countdown = 0; // countdown for text 
  boolean numpad1Pressed, numpad2Pressed, numpad3Pressed, numpad4Pressed, numpad5Pressed, numpad6Pressed; // keys with functions 
  String invisibleModeText;
  int textX = 0;


  public void draw() {
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
  public void keyPressed() {
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
class Encounter {
  float secondsToNewSpawn = 3; // seconds before new enemy spawn
  int minute = 60; // a second is 60 frames /* need to fix the name */
  float countDown = (secondsToNewSpawn * minute);
  int numberOfEnemies; // number of enemies used for formation 
  int spawnHeight, spawnWidth; // place where the enemies are spawned
  int chooseFormation; // formation type is decided with this
  int enemyType; // enemyType is decided with this 
  int enemyHeight; // this is where the enemy height is stored
  int enemyWidth; // this is where the enemy width is stored
  int pointHeight; // this is where the points height is stored
  int pointWidth; // this is where the points width is stored
  int maxEnemies = 30; // max number of enemies on screen 
  int maxPoints = 60; // max number of enemies on screen 
  int numberOfRows; // number of rows that points need
  int obstacleHight = 50; // height of the obstacles
  int spaceBetweenEnemies = 10; // space between enemies
  int enemyYSpeed;
  boolean pointFormation = false; // boolean to check if it needs point formations
  PImage pointSprite, sharkSprite, whaleSprite, swordFishSprite; // images
  String typeOfEnemy; // this is where it says its a whale, shark or points
  boolean bulletEnemyCollide = false; // if the enemy hits a bullet this turns to true
  boolean checkEnemyPosition = false;
  boolean checkInterupted = false; 
  boolean isAllLoaded = false;
  int spawnHeightDifference;
  int oneMinute = 60 * 30;
  int oneMinuteCountdown = oneMinute;
  int oneMinuteCountdownTwo = oneMinute;
  int enemySpeed = 6;
  boolean invisible = false;
  int randomSpawn;
  int EnemyComboBonus;
  int scoreForPoint = 30;
  int perComboBonus = 5;
  int enemyHeightAdjustment;
  boolean specialEnemy = false;
  int previousSpawnHeight;

  // make arrays
  Enemy[] enemy = new Enemy[maxEnemies];
  Points[] point = new Points[maxPoints];

  // load images
  Encounter() {
    pointSprite = loadImage("./Sprites/Points/Fish.png");
    pointSprite.resize(30, 20);
    sharkSprite = loadImage("./Sprites/Shark/shark-0.png");
    sharkSprite.resize(height / 100 * 19, height / 100 * 9);
    whaleSprite = loadImage("./Sprites/Killerwhale/killerwhale.png");
    whaleSprite.resize(height / 100 * 21, height / 100 * 15);
    swordFishSprite = loadImage("./Sprites/swordFish/swordFish.png");
    swordFishSprite.resize(height / 100 * 19, height / 100 * 9);
  }  

  // Spawns the enemies in formations
  public void initEnemy() {
    // Randomly selects the enemy or points
    enemyType = (int)(Math.random() * 7 + 1);
    switch(enemyType) {
    case 1: 
    case 2: 
      shark();
      break;
    case 3:
    case 4: 
      whale();
      break;
    case 5:
    case 6:
      points();
      pointFormation = true;
      break;
    case 7:
      swordFish();
      specialEnemy = true;
      break;
    }
    if (pointFormation == false) {
      // Checks what formations it needs as enemies
      if (specialEnemy == true) {
        quickDash();
        specialEnemy = false;
      } else {
        chooseFormation = (int)(Math.random() * 4 + 1);
        switch(chooseFormation) {
        case 1: 
          rowBottom();
          break;
        case 2: 
          rowTop();
          break;
        case 3: 
          followPlayer();
          break;
        case 4: 
          vFormation();
          break;
        }
      }
    } else {
      // Checks what formations it needs as Points
      chooseFormation = (int)(Math.random() * 4 + 1); 
      switch(chooseFormation) {
      case 1: 
        rowPointsBottom();
        break;
      case 2:
        rowPointsTop();
        break;
      case 3: 
        rowPointsRandom();
        break;
      case 4:
        hiFormation();
        break;
      }

      pointFormation = false;
    }
    checkEnemyPosition = true;
  }


  //update the sharks
  public void updateEnemy() {
    if (enemySpeed < 15) {
      oneMinuteCountdownTwo -= 1;
      if (oneMinuteCountdownTwo == 0) {
        enemySpeed += 1;
        oneMinuteCountdownTwo = oneMinute;
      }
    }
    //if (gotHit) return;
    for (int i = 0; i < maxEnemies; i++) {
      // only move the shark if it exists 
      if (enemy[i] != null) {
        if(enemy[i].type == "swordFish"){
          enemy[i].x -= enemySpeed * 2; 
        } else {
          enemy[i].x -= enemySpeed; 
        }
        // folloplayer if it is true
        if (enemy[i].isFolowingPlayer == true) {
          if (enemy[i].y < thePinguin.position.y) { 
            if (!(enemy[i].y + enemy[i].h >= thePinguin.position.y - (thePinguin.playerHeight / 2))) {
              enemy[i].y += enemy[i].yv;
            }
          } else if (enemy[i].y > thePinguin.position.y) {
            if (!(enemy[i].y <= thePinguin.position.y - (thePinguin.playerHeight / 2))) {
              enemy[i].y -= enemy[i].yv;
            }
          }
        }
        // if a shark is out of bounds remove him
        if (enemy[i].x < 0 - enemy[i].w) {
          enemy[i] = null;
        }
        if (collidesWithEnemy(thePinguin, enemy[i])) {
          if (lifecounter <= powerup.invincibilityLife && lifecounter > powerup.shieldLife) {
            enemy[i] = null;    
            bulletHit.play();
            bulletHit.rewind();
            if (pointcountdown > 0) {
              EnemyComboBonus += perComboBonus;
              score.score += scoreForPoint + EnemyComboBonus;
              pickupScore += scoreForPoint + EnemyComboBonus;
            } else {
              EnemyComboBonus = 0;
              score.score += scoreForPoint;
              pickupScore = scoreForPoint;
            }
            pointcountdown = seconde * 1;
            scoreHeightAlt = thePinguin.position.y - 20;
            pointX = thePinguin.position.x;
          }
        }


        // bullets
        for (int n =0; n<bullets.length; n++) {
          bulletEnemyCollide = CollisionWithEnemyBullet(bullets[n], enemy[i]);
          if (bulletEnemyCollide) {
            if (lifecounter == powerup.shootLife)
            {
              enemy[i] = null;//If a shark collide with a bullet he will dissapear.
              bullets[n].init();
              //gotHit = true;
            } else {
              enemy[i] = enemy[i];
            }
          }
        }
      }
    }
  }

  // update the points
  public void updatePoints() {
    for (int i = 0; i < maxPoints; i++) {
      // only move the if it exists 
      if (point[i] != null) {
        point[i].x -= enemySpeed;

        // if a point is out of bounds remove him
        if (point[i].x < 0 - point[i].w) {
          point[i] = null;
        }
      }
    }
  }

  //draw the enemies
  public void drawEnemy() {
    for (int i = 0; i < maxEnemies; i++) {
      // only draw sharks that exist
      if (enemy[i] != null) {
        fill(255, 0, 0);
        if (enemy[i].type == "Shark") {
          image(sharkSprite, enemy[i].x, enemy[i].y + watery, enemy[i].w, enemy[i].h);
        } else if (enemy[i].type == "Whale") {
          image(whaleSprite, enemy[i].x, enemy[i].y + watery, enemy[i].w, enemy[i].h);
        } else if (enemy[i].type == "swordFish") {
          image(swordFishSprite, enemy[i].x, enemy[i].y + watery, enemy[i].w, enemy[i].h);
        }
      }
    }
  } 

  // draw the points
  public void drawPoints() {
    for (int i = 0; i < maxPoints; i++) {
      // only draw sharks that exist
      if (point[i] != null) {
        fill(122, 122, 122);
        image(pointSprite, point[i].x, point[i].y + watery, point[i].w, point[i].h);
      }
    }
  } 

  public void draw() {

    //check to see if all enemies are on screen
    if (checkEnemyPosition == true) {
      checkInterupted = false; 
    loop:
      for (int i = 0; i < maxEnemies; i++) {
        if (enemy[i] != null) {
          if (enemy[i].x > width) {
            checkInterupted = true; 
            break loop;
          }
        }
      }
    }
    if (checkInterupted == false) {
      countDown -= 1;
      checkEnemyPosition = false;
    }
    if (secondsToNewSpawn > 1.5f ) {
      oneMinuteCountdown -= 1;
      if (oneMinuteCountdown == 0) {
        secondsToNewSpawn -= 0.2f;
        oneMinuteCountdown = oneMinute;
      }
    } else {
      secondsToNewSpawn = 1.5f;
    }
    if (countDown <= 0 && checkInterupted == false) {
      //if all enemies are to the left;
      initEnemy();
      countDown = secondsToNewSpawn * minute;
    }
    // update scripts
    updateEnemy();
    updatePoints();
    // draw scripts
    drawEnemy();
    drawPoints();

    if (pointcountdown > 0) { 
      //haalt score eraf 
      pointcountdown -= 1;
      textSize(23);
      fill(255, 255, 255);
      text("+" + pickupScore, pointX, scoreHeightAlt);
      scoreHeightAlt -= 1;
    }
  }


  ////////////////////////////////
  //// BEGIN ENEMY FORMATIONS ////
  ////////////////////////////////

  // an enemy row from the bottom
  public void rowBottom() {
    if (typeOfEnemy == "Shark") {
      numberOfEnemies = 8;
    } else if (typeOfEnemy == "Whale") {
      numberOfEnemies = 5;
    }
    // start position 
    spawnHeight = height - enemyHeight - obstacleHight - spaceBetweenEnemies;
    // code for the formation
    for (int j = 0; j < numberOfEnemies; j++) {
      for (int i = 0; i < maxEnemies; i++) {
        if (enemy[i] == null) {
          enemy[i] = new Enemy();
          enemy[i].type = typeOfEnemy;
          enemy[i].h = enemyHeight;
          enemy[i].w = enemyWidth;
          enemy[i].y = spawnHeight - 20;
          spawnHeight = spawnHeight - enemy[i].h - spaceBetweenEnemies;
          break;
        }
      }
    }
  }


  // an enemy row from the top
  public void rowTop() {
    if (typeOfEnemy == "Shark") {
      numberOfEnemies = 8;
    } else if (typeOfEnemy == "Whale") {
      numberOfEnemies = 5;
    }
    // start position 
    spawnHeight = obstacleHight + spaceBetweenEnemies + watery;
    // code for the formation
    for (int j = 0; j < numberOfEnemies; j++) {
      for (int i = 0; i < maxEnemies; i++) {
        if (enemy[i] == null) {
          enemy[i] = new Enemy();
          enemy[i].type = typeOfEnemy;
          enemy[i].h = enemyHeight;
          enemy[i].w = enemyWidth;
          enemy[i].y = spawnHeight;
          spawnHeight = spawnHeight + enemy[i].h + spaceBetweenEnemies;
          break;
        }
      }
    }
  }

  public void quickDash() {
    numberOfEnemies = 2;
    for (int j = 0; j < numberOfEnemies; j++) {
      for (int i = 0; i < maxEnemies; i++) {
        if (j == 1) {
          spawnHeight = (int)(Math.random() * (height / 100 * 80 + 1));
          while (spawnHeight >= previousSpawnHeight && spawnHeight <= (previousSpawnHeight + enemyHeight)){
            spawnHeight = (int)(Math.random() * (height / 100 * 80 + 1));
          }
          } else {
          spawnHeight = (int)(Math.random() * (height / 100 * 80 + 1));
        }
        if (enemy[i] == null) {
          enemy[i] = new Enemy();
          enemy[i].type = typeOfEnemy;
          enemy[i].h = enemyHeight;
          enemy[i].w = enemyWidth;
          enemy[i].y = spawnHeight;
          if (j == 0) {
            previousSpawnHeight = spawnHeight;
          }
          break;
        }
      }
    }
  }

  // follow the player
  public void followPlayer() {    
    numberOfEnemies = 2;
    // start position 
    spawnHeight = 0 + obstacleHight + watery;
    //set speed for both
    enemyYSpeed = (int)(Math.random() * 2 + 1);
    // code for the formation
    for (int j = 0; j < numberOfEnemies; j++) {
      for (int i = 0; i < maxEnemies; i++) {
        if (enemy[i] == null) {
          enemy[i] = new Enemy();
          enemy[i].type = typeOfEnemy;
          enemy[i].h = enemyHeight;
          enemy[i].w = enemyWidth;
          enemy[i].y = spawnHeight;
          enemy[i].isFolowingPlayer = true;
          spawnHeight = height - enemyHeight - obstacleHight;
          enemy[i].yv = enemyYSpeed; 
          break;
        }
      }
    }
  }

  // v Formation
  public void vFormation() {    
    if (typeOfEnemy == "Shark") {
      numberOfEnemies = 13;
    } else if (typeOfEnemy == "Whale") {
      numberOfEnemies = 9;
    }
    // start position 
    spawnHeight = obstacleHight + spaceBetweenEnemies + watery;
    spawnWidth = width;
    // code for the formation
    for (int j = 0; j < numberOfEnemies; j++) {
      for (int i = 0; i < maxEnemies; i++) {
        if (enemy[i] == null) {
          enemy[i] = new Enemy();
          enemy[i].type = typeOfEnemy;
          enemy[i].h = enemyHeight;
          enemy[i].w = enemyWidth;
          enemy[i].y = spawnHeight;
          enemy[i].x = spawnWidth;
          // to make the V formation it needs to change halfway
          if (j < (int)numberOfEnemies/2) {
            spawnHeight = spawnHeight + enemy[i].h + spaceBetweenEnemies;
          } else { 
            spawnHeight = spawnHeight - enemy[i].h - spaceBetweenEnemies;
          }
          spawnWidth = spawnWidth + (int)enemy[i].w/2;
          break;
        }
      }
    }
  }


  //////////////
  /// Points ///
  //////////////

  // points square from the bottom
  public void rowPointsBottom() {
    numberOfRows = 5;
    numberOfEnemies = 6;
    // code for the formation
    for (int h = 0; h < numberOfRows; h++) {
      spawnHeight = height - enemyHeight - obstacleHight - spaceBetweenEnemies - watery;
      if (h > 0) {
        spawnWidth = spawnWidth + pointWidth + spaceBetweenEnemies;
      } else {
        spawnWidth = width;
      }
      for (int j = 0; j < numberOfEnemies; j++) {
        for (int i = 0; i < maxPoints; i++) {
          if (point[i] == null) {
            point[i] = new Points();
            point[i].h = pointHeight;
            point[i].w = pointWidth;
            point[i].x = spawnWidth;
            point[i].y = spawnHeight;
            spawnHeight = spawnHeight - point[i].h - spaceBetweenEnemies;
            break;
          }
        }
      }
    }
  }

  // points square from the top
  public void rowPointsRandom() {
    numberOfRows = 5;
    numberOfEnemies = 6;
    // code for the formation
    randomSpawn = (int)(Math.random() * (height / 100 * 80 + 1));
    for (int h = 0; h < numberOfRows; h++) {
      spawnHeight = obstacleHight + randomSpawn + spaceBetweenEnemies + watery;
      if (h > 0) {
        spawnWidth = spawnWidth + pointWidth + spaceBetweenEnemies;
      } else {
        spawnWidth = width;
      }
      for (int j = 0; j < numberOfEnemies; j++) {
        for (int i = 0; i < maxPoints; i++) {
          if (point[i] == null) {
            point[i] = new Points();
            point[i].h = pointHeight;
            point[i].w = pointWidth;
            point[i].x = spawnWidth;
            point[i].y = spawnHeight;
            spawnHeight = spawnHeight + point[i].h + spaceBetweenEnemies;
            break;
          }
        }
      }
    }
  }

  // points square from the top
  public void rowPointsTop() {
    numberOfRows = 5;
    numberOfEnemies = 6;
    // code for the formation
    for (int h = 0; h < numberOfRows; h++) {
      spawnHeight = obstacleHight + spaceBetweenEnemies + watery;
      if (h > 0) {
        spawnWidth = spawnWidth + pointWidth + spaceBetweenEnemies;
      } else {
        spawnWidth = width;
      }
      for (int j = 0; j < numberOfEnemies; j++) {
        for (int i = 0; i < maxPoints; i++) {
          if (point[i] == null) {
            point[i] = new Points();
            point[i].h = pointHeight;
            point[i].w = pointWidth;
            point[i].x = spawnWidth;
            point[i].y = spawnHeight;
            spawnHeight = spawnHeight + point[i].h + spaceBetweenEnemies;
            break;
          }
        }
      }
    }
  }

  public void hiFormation() {
    numberOfRows = 5;
    numberOfEnemies = 6;
    // code for the formation
    randomSpawn = (int)(Math.random() * (height / 100 * 80 + 1));
    for (int h = 0; h < numberOfRows; h++) {    
      spawnHeight = obstacleHight + randomSpawn + spaceBetweenEnemies + watery;
      if (h > 0) {
        spawnWidth = spawnWidth + pointWidth + spaceBetweenEnemies;
      } else {
        spawnWidth = width;
      }
      for (int j = 0; j < numberOfEnemies; j++) {
        if ((h == 1 && j == 0) || (h == 1 && j == 1) || (h == 1 && j == 4) ||(h == 1 && j == 5) || (h == 3) || (h == 4 && j == 1)) {
          spawnHeight = spawnHeight + pointHeight + spaceBetweenEnemies;
        } else {
          for (int i = 0; i < maxPoints; i++) {
            if (point[i] == null) {
              point[i] = new Points();
              point[i].h = pointHeight;
              point[i].w = pointWidth;
              point[i].x = spawnWidth;
              point[i].y = spawnHeight;
              spawnHeight = spawnHeight + point[i].h + spaceBetweenEnemies;
              break;
            }
          }
        }
      }
    }
  }


  //////////////////////////////
  //// END ENEMY FORMATIONS ////
  //////////////////////////////

  ////////////////////////////
  //// TYPE OF ENCOUNTERS ////
  ////////////////////////////

  // shark variables
  public void shark() {
    enemyHeight = height / 100 * 9;
    enemyWidth = height / 100 * 19;
    typeOfEnemy = "Shark";
  }

  // whale variables
  public void whale() {
    enemyHeight = height / 100 * 15;
    enemyWidth = height / 100 * 21;
    typeOfEnemy = "Whale";
  }

  public void swordFish() {
    enemyHeight = height / 100 * 9;
    enemyWidth = height / 100 * 19;
    typeOfEnemy = "swordFish";
  }

  // point variables
  public void points() {
    pointHeight = 20;
    pointWidth = 30;
  }

  ////////////////////////////////
  //// END TYPE OF ENCOUNTERS ////
  ////////////////////////////////

  /* -------------- NIET AANPASSEN, COLLISIONS -------------- */

  // enemy collision with the penguin
  public boolean collidesWithEnemy(Pinguin ping, Enemy enemy)
  {
    if (enemy != null && invisible == false) {
      if ((ping.position.x + ping.playerWidth) >= enemy.x && // Ping right edge past Enemy left
        ping.position.x <= (enemy.x + enemy.w )&& // Ping left edge past Enemy right
        (ping.position.y + ping.playerHeight) >= enemy.y + watery && // Ping top edge past Enemy bottom
        ping.position.y <= (enemy.y + enemy.h))   // Ping bottom edge past Enemy top
      {
        return true;
      }
    }
    return false;
  }

  // point collision with the penguin
  public boolean collidesWithPoint(Pinguin ping)
  {
    for (int p = 0; p < maxPoints; p++) {
      if (point[p] != null) {
        if ((ping.position.x + ping.playerWidth) >= point[p].x && // Ping right edge past Enemy left
          ping.position.x <= (point[p].x + point[p].w )&& // Ping left edge past Enemy right
          (ping.position.y + ping.playerHeight) >= point[p].y + watery && // Ping top edge past Enemy bottom
          ping.position.y <= (point[p].y + point[p].h +  watery))   // Ping bottom edge past Enemy top
        {
          point[p] = null;
          return true;
        }
      }
    }
    return false;
  }

  // enemy collision with a bullet
  public boolean CollisionWithEnemyBullet(Bullet b, Enemy enemy)
  {
    if (enemy != null) 
    {
      if (b.x + b.size >= enemy.x && // bullet right edge past Enemy left
        b.x <= enemy.x + enemy.w && // bullet left edge past Enemy right
        b.y + b.size >= enemy.y && // bullet top edge past Enemy bottom
        b.y <= enemy.y + enemy.h)   // bullet bottom edge past Enemy top
      {
        bulletHit.play();  
        bulletHit.rewind();
        if (pointcountdown > 0) {
          EnemyComboBonus += perComboBonus;
          score.score += scoreForPoint + EnemyComboBonus;
          pickupScore += scoreForPoint + EnemyComboBonus;
        } else {
          EnemyComboBonus = 0;
          score.score += scoreForPoint;
          pickupScore = scoreForPoint;
        }
        pointcountdown = seconde * 1;
        scoreHeightAlt = thePinguin.position.y - 20;
        pointX = thePinguin.position.x;
        return true;
      }
    }
    return false;
  }
}
class Enemy {
  float x = width;
  float y;
  float yv = 6;
  float w;
  int h;
  boolean isFolowingPlayer = false;
  String type;
}
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
  public void changeGameState(int state) { 
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
  public void UpdateGameState() { 
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
PImage backgroundImage;

int widthitem;
int heightitem;

int waterx = 0;
int watery = 0;
int waterx2;

boolean movingDown = false;
boolean movingUp = false;


class Level {
  
  float x;
  float y;
  float size;

  Level() {
    backgroundImage = loadImage("./Sprites/water/Fullwater.png", "png");
    backgroundImage.resize(width,height);
  }

  public void init() {
    widthitem = width;
    heightitem = height;
    waterx = 0;
    waterx2 = widthitem;
    watery = 0;
    x=width;
    size = 80;
    y=height-300;
    updateWater();
  }

  public void draw() {
    setLevelBoundaries();
    background(0,191,255);//deep sky blue
    snow.draw();//drawing snoweffect
    
    updateWater();
    drawWater();
   
    movingUp = false;
    movingDown = false;
  
    //JUMPING MECHANICS BELOW
  
    if(thePinguin.position.y < height / 8 && watery < 500){ //move the camera up if above a certain part of screen.
     watery -= thePinguin.velocity.y; 
     movingUp = true;
     thePinguin.jumping = true;
   }
   

   if(thePinguin.position.y > height / 8 && watery > 0){ //move camera down..
      watery -= thePinguin.velocity.y;
     movingDown = true;  
   }
   
  
  }

  public void drawWater() {
    image(backgroundImage, waterx, watery, widthitem, heightitem);
    image(backgroundImage, waterx2, watery, widthitem, heightitem);
  }
  
  public void setLevelBoundaries(){
    
   if(watery > 0 && thePinguin.position.y < 50){
     thePinguin.position.y = 50; 
   }
   
   if(watery < 0){
     watery = 0;
   }
   
  }

  public void updateWater() {
    waterx -= 3;
    waterx2 -= 3;
    if(waterx <= 0 - widthitem){
      waterx = widthitem;
      
    }
    if(waterx2 <= 0 - widthitem){
      waterx2 = widthitem;
    }
  }
}
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

  public void init() {

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

  public void update() {
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

  public void drawBackground() {
    image(background, bgX, bgY);
    image(background, bgX2, bgY2);
  }

  public void updateBackground() { 
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

  public void loadMenuImages() { //images of the buttons in the menu
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

  public void showImages() { //Drawing the buttons on the screen
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

  public void pingDraw() {
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

  public void showTitle() { //TITLE TEXT NEEDS TO REPLACED WITH AN ACTUAL TITLE IMAGE
    titleXPos = width/2 - (title.width/2); 
    titleYPos = 150 + (buttonMoveSpeed *-1);
    image(title, titleXPos, titleYPos, title.width, title.height);
  }

  //================================================================

  public void checkButtonValue() { //You cannot navigate further than the first or last button
    if (buttonSelected < LOWEST_SELECT_VALUE) buttonSelected = LOWEST_SELECT_VALUE;
    if (buttonSelected > HIGHEST_SELECT_VALUE) buttonSelected = HIGHEST_SELECT_VALUE;
  }

  //================================================================

  public void checkSelectedButton() {
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

  public void keyPressed() {
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
class Obstacles
{
  PImage obstacle;
  float xPos, yPos, oWidth, oHeight; // x position, y position, object width, object height
  float timer, spawnTimeInSeconds; // timer will be used for the actual timer. spawnTimeInSeconds is the variable to save the actual spawntime.
  String oType, obType;
  public void init() {
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
    public void draw() {
      updateObstacle();
    }

  public void updateObstacle() {
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

  public boolean collides(Pinguin ping)
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

  public boolean CollisionWithObstacleBullet(Bullet b, Obstacles o)
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

  public boolean spawnObject(float time) {
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
  public void spawnObstacle(float x, float y, float w, float h, float spawnTime, String oType) { 
    oWidth = w;                     //println("oWidth: "+ oWidth);
    oHeight = h;                    //println("oHeight: "+ oHeight);
    xPos = x;                       //println("xPos: "+ xPos);
    yPos = y;                       //println("yPos: "+ yPos);
    spawnTimeInSeconds = spawnTime; //println("spawnTimeInSeconds: "+ spawnTimeInSeconds);
    obType = oType;                 //determines the type of the object
  }
}
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
    VELOCITY_DRAG = 0.5f, //The drag force the players has.
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
    NORMAL_GRAVITY = new PVector(5, 0.001f);
    JUMPING_GRAVITY = new PVector(0.0001f, 5.05f);;
    gravity = NORMAL_GRAVITY;
  }

  //====================================================================================

  //Setting the player in the starting position and determining the size of the hitbox
  public void initial() { 

    position = new PVector(startPosition.x, startPosition.y); //We define the start position here due to the resetGame() method in the main script.
  }

  //====================================================================================

  public void draw() { 

    if (lifecounter == powerup.defaultLife)
    {
      //rect(position.x, position.y, size, size); //DEBUG, DO NOT REMOVE UNTIL FINAL PHASE.
      drawPingFrames();
    }
  }

  public void drawPingFrames() {

    image(pinguinSprite[pinguinFrame], position.x, position.y, playerWidth, playerHeight);
    if (pinguinFrame == framesPinguin-1) {
      pinguinFrame = 0;
    } else {
      pinguinFrame++;
    }
  }

  //====================================================================================

  public void update() {
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
      gravity.mult(1.10f);
    } else {
      gravity = new PVector(0.0001f, 0.003f);
    }
  }

  public void fireBullet(float tx, float ty)
  {
    bullets[bltCounter].fire(tx, ty); // shoot the bullet
    bullets[bltCounter].isFired = true;
    bltCounter ++;
    coolDown = 60;
  }

  //====================================================================================

  public void applyForce(PVector force) {
    PVector f = PVector.div(force, 1);
    
    acceleration.add(f);
  }

  //====================================================================================

  public void setBoundaries() {

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
  public void limitDrag() {//This is to be able to quickly move to the opposite direction.
    if(!rPressed){
      if(velocity.x > 0){
      velocity.mult(0.95f);
      }
    }
    
    if(!uPressed){
      if(velocity.y > 0){
       velocity.mult(0.95f);
      }
    }
    
    if(!dPressed){
      if(velocity.y < 0){
        velocity.mult(0.95f);
      }
    }
  }

  public void controls() {
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
  public void keyPressed() {
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

  public void keyReleased() {

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
class Points {
  float x = width;
  float y;
  float xv = 6;
  float yv = 6;
  int w;
  int h;
}
 class PowerUp
{
  final float yStartMin =1.25f;
  final float yStartMax = 5;
  final int amountOfFrames = 30;
  final int maxKindsOfPowerUps = 3;
  
  final int shield = 1 ;
  final int invincibility = 2;
  final int shoot = 3;
  
  final String shieldName = "shield";
  final String invincibilityName ="invincibility";
  final String shootName ="shoot";
  
  int [] PowerUpNumber = new int [maxKindsOfPowerUps];
  String [] PowerUpNames = new String [maxKindsOfPowerUps];
 
  float xPos, yPos, PuWidth, PuHeight, yStart;
  float timer, spawnTimeInSeconds; // timer will be used for the actual timer. spawnTimeInSeconds is the variable to save the actual spawntime.
  
  float imgSize;
  int marginCorrection;
  int shieldLife,invincibilityLife,shootLife,defaultLife;
  int r;// random variable
  
  boolean shieldIsActive;
  boolean invincibilityIsActive;
  boolean shootIsActive;
  
  int framesPinguin = 18;
  int framesTimer = 5;
  int framesShield = 5;
  int timerFrame = 0;
  int pinguinFrame = 0;
  int shieldFrame = 0;
  Timer shieldTimer; 
  Timer shootTimer;
  boolean startTime;
  
  int ammoCount = 6;
  
  //all images
  
    // powerup items images
    PImage shieldItem = new PImage();
    PImage invincibleItem = new PImage();
    PImage shootItem = new PImage();
  
   //shield opgepakt
    PImage imgShieldActive = new PImage();
  
    //De pinguin
    PImage[] pinguinInvincibleSprite = new PImage[framesPinguin];
    PImage[] pinguinShieldSprite = new PImage[framesPinguin];
    
    //De timer
    PImage[] timerSprite  = new PImage[framesTimer];
   
 //================================================================   
  
  public void init()
  {
    yStart = height/random(yStartMin,yStartMax);
    shieldTimer = new Timer(amountOfFrames);
    shootTimer  = new Timer(55);
    
    for(int index = 0; index < PowerUpNumber.length; index++)
    {
      PowerUpNumber[index] = (index+1);
    }
    
    shieldIsActive = false;
    invincibilityIsActive = false;
    shootIsActive = false;
    
    shieldLife = 10000;
    invincibilityLife= 20000;
    shootLife= 2;
    defaultLife =1;
    
    imgSize= thePinguin.playerWidth*1.6f;
    PuWidth =80;
    PuHeight=80;
    marginCorrection = 15;
    spawnTimeInSeconds = random(30, 60); 
    timer = spawnTimeInSeconds;
    
    invincibleItem =  loadImage("./Sprites/Invincibility/Item/star.png");
    invincibleItem.resize((int)PuWidth, (int)PuHeight);
    shootItem =  loadImage("./Sprites/Shoot/Item/gun.png");
    shootItem.resize((int)PuWidth, (int)PuHeight);
    shieldItem = loadImage("./Sprites/Shield/shield.png"); 
    shieldItem.resize((int)PuWidth, (int)PuHeight);
    imgShieldActive = loadImage("./Sprites/Shield/shield.png"); 
    imgShieldActive.resize((int)imgSize,(int)imgSize); 
    for (int i = 0; i<framesPinguin; i++) {
      pinguinInvincibleSprite[i] = loadImage("./Sprites/Invincibility/invincibility-"+i+".png");
      pinguinInvincibleSprite[i].resize((int)thePinguin.playerWidth, (int)thePinguin.playerHeight);
    }
    for (int i = 0; i<framesPinguin; i++) {
      pinguinShieldSprite[i] = loadImage("./Images/Pinguin/pinguin-"+i+".png");
      pinguinShieldSprite[i].resize((int)thePinguin.playerWidth, (int)thePinguin.playerHeight);
    } 
    for (int i = 0; i<framesTimer; i++) {
      timerSprite[i] = loadImage("./Sprites/Timer/timer"+i+".png");
      timerSprite[i].resize((int)imgSize, (int)imgSize);
    }
  }
  
  public void draw()
  {
    updatePowerUp();
  }
  public void updatePowerUp()
  {
   
    //Check if the object is fully through the left side of the screen.If so, it will randomly generate 1 of the 3 powerupnumbers.
    powerupRandomGenerator();
    
    //moving
    powerupMoving();
   
    //powerup dimensions 
    PuWidth =40; 
    PuHeight=40;
    
    
    //this method will convert the powerupnumbers to an actual image that correspond with that number.
    drawImagePowerup();
    

    if(collidesWithPowerUp(thePinguin))// if ping collides with the powerup.
    {
       //this method will reset the timers 
       resetTimers();
       
       //this method will add soundeffect correspondend to the powerup you picked up
       collisionSoundEffects();
       
       xPos = 0-PuWidth-100;//The object dissapears when touched by ping
     
       //this method will change the default amount of lives to that of the powerups its amount of lives.
       changeLifeCounter();
    }
    
    //this method convert the powerup lives to a boolean term.
    //activatePowerUp();
    
    //these 3 powerups requires a timer because they need to be temporary
    if(lifecounter <= shieldLife && lifecounter > shootLife|| lifecounter <= invincibilityLife && lifecounter > shieldLife || lifecounter == shootLife ){
      
      int tempcounter = PowerUpTimer(lifecounter);// this method will activate the timer
      lifecounter = tempcounter;
    }
  } 
  
  public boolean collidesWithPowerUp(Pinguin ping)
  {
       if ((ping.position.x + ping.playerWidth ) >= xPos && // Ping right edge past powerup left
            ping.position.x  <= (xPos + PuWidth )&& // Ping left edge past powerup right
           (ping.position.y + ping.playerHeight) >= yPos && // Ping top edge past powerup bottom
            ping.position.y  <= (yPos + PuHeight))   // Ping bottom edge past powerup top
       {
          return true;
       }
       else
       {
         return false;
       } 
  }
  
  public boolean spawnObject(float time)
  {
    if(timer > 0)
    {
      timer -= (float)1/60;
    }
    else
    {
      timer = time;
      return true;
    }
    return false;
  }
  
  public void spawnPowerUp(float x, float y, float w, float h, float spawnTime, int random)
  {
    PuWidth = w;                    //println("oWidth: "+ PuWidth);
    PuHeight = h;                   //println("oHeight: "+ PuHeight);
    xPos = x;                       //println("xPos: "+ xPos);
    yPos = y;                       //println("yPos: "+ yPos);
    spawnTimeInSeconds = spawnTime; //println("spawnTimeInSeconds: "+ spawnTimeInSeconds);
    r = random;                     //println("r: " + r);
  }
  public void resetTimers()
  {
     startTimer.setTime(500);//this will reset the timer to 500 frames
     shieldTimer.setTime(30);//this will reset the timer to 30 frames
     shootTimer.setTime(55);//this will reset the timer to 30 frames
  }
  
  public void collisionSoundEffects()
  {
    if(r == shield){
      bubble_pickup.play();
      bubble_pickup.rewind();
    }
    else if(r == invincibility){
      star_pickup.play();
      star_pickup.rewind();
    }
    else{
      gun_pickup.play();
      gun_pickup.rewind();
    }

  }
  
  public void powerupRandomGenerator()
  {
    if(xPos < 0 - PuWidth)
    {
      r=(int)random(1,4);//each time the powerup dissapears from the screen the next on will be randomly generated.
      if(spawnObject(spawnTimeInSeconds))
      {
        xPos = width;
        yStart = height/random(1.25f,5);//the powerup will start at different locations on the Y-axis.
      }
    }  
  }

  public void powerupMoving()
  {
    xPos -= 4; //Change this value to the actual game speed.
    yPos = yStart + 40 * sin(radians(xPos)) + watery;//make the powerup move in a sine wave
  }
 
  public void drawImagePowerup()
  {
    if (r ==shield)
    {
      image(shieldItem,xPos,yPos,PuWidth, PuHeight);//draws the shieldpowerup
    } else if (r==invincibility)
    {
      image(invincibleItem,xPos,yPos,PuWidth, PuHeight);//draws the invincible powerup
    } else if (r==shoot)
    {
      image(shootItem,xPos,yPos,PuWidth, PuHeight);//draws the shoot powerup
    }    
  
  }
  
  public void changeLifeCounter()
  {
     if(r == shield)
     {
       lifecounter = shieldLife;// If the player picks up the powerupshield, ping will get a extra lives
     }
     else if(r == invincibility)
     {   
       lifecounter = invincibilityLife; // if you pick up this powerup you are temporary invulnerable thats why the lifecounter is such a high number 
     }
     else if(r == shoot)
     {
       lifecounter =shootLife;// If you pick up this powerup ping got 3 lives
       ammoCount =6;//this will reset the ammocount to 6;
       thePinguin.bltCounter = 0;// this will reset the bulletCount to 0;
     }
  }
  
 /* void activatePowerUp()
  {
    if(lifecounter <= shieldLife && lifecounter > shootLife )
    {
      shieldIsActive = true;
    }
    else if (lifecounter <= invincibilityLife && lifecounter > shieldLife)
    {
      invincibilityIsActive = true;
    }
    else if(lifecounter == shootLife)
    {
      shootIsActive = true;
    } 
  }*/
  
  public int PowerUpTimer(int counter)// this will activate the timer and change the counter value back to default when the the timer has passed.
  {  
    int newcounter = 0;
    if(counter <= shieldLife && counter > shootLife)
    {
      //this will draw the images of a powered up pinguin
      drawPoweredUpPinguin(counter);
      
      //If you pick up the powerup but have not yet have been collided with the enemy the shield will be drawn
      if(!startTime)
      {
        image(imgShieldActive,(thePinguin.position.x-marginCorrection),(thePinguin.position.y-30),imgSize,imgSize);//draws the shieldpowerup
        fill(255, 255, 255);
      }
      else if(startTime == true)
      {
         
        if(shieldTimer.getTime()>0)
        {
          // the powerup timer will start to countdown from 500 to 0 in frames
          shieldTimer.countDown();  
          
          //this will give a indication that the shield will dissapear
          drawFlickeringImage();  
        }
        else
        {
         // have to do this to stop the timer at zero or else the counter will go under the zero
          shieldTimer.setTime(0);
        }
        if (shieldTimer.getTime() == 0)
        {
          //after time is past life will be back to 1
          counter = defaultLife;
          
          // the starttime will be stopped
          startTime =false;
        }
      }
      newcounter = counter;
     
    }
    else if (counter <= invincibilityLife && counter > shieldLife )//This will give ping the invincibility powerup color and set of the timer for invincibility.
    {
      if(startTimer.getTime()>30)
      {
        //this will draw the images of a poweredup pinguin
        drawPoweredUpPinguin(counter);
      }
      if(startTimer.getTime()>0)
      {
        // the powerup timer will start to countdown from 500 to 0 in frames
        startTimer.countDown();
        
        //Show the countdown on screen
        showCountDown();
        
        // if the countdown is below 30 frames the powerup image must flicker to indicate to the player the powerup will end soon.
        if(startTimer.getTime()<= 30)
        {
          // the image will appear 6 times during countdown 
          if (startTimer.getTime() % 5 == 0)
          {
           thePinguin. drawPingFrames();
          }
        }
      }
      else
      {
       startTimer.setTime(0);
      }
      if (startTimer.getTime() == 0)
      {
        invincibilityIsActive =false;
        counter = defaultLife;//after time is past life will be back to 1
      }
      newcounter = counter;
    }
    else if (counter == shootLife)//This will give ping the shoot powerup color and set timer for invincibility.
    {
       // draw the powerup shoot image
       drawPoweredUpPinguin(counter);
       
        //draw the ammoAmmount image
       drawAmmoAmountImage();
       
        if (thePinguin.bltCounter == bullets.length)
       {
         shootTimer.countDown();
         shootIsActive = false;
         if(shootTimer.getTime()==0)
         {
           counter = defaultLife;//after time is past life will be back to 1
         }
       }  
       newcounter = counter;     
    }
  return newcounter;
 }
 
  public void powerupIsActive()
  {
   
  }
  
  public void showCountDown()
  { 
    if(lifecounter <= invincibilityLife && lifecounter > shootLife || lifecounter == shootLife)
    {
      //fill(255, 255, 0); // the color of the timer will be yellow to indicate that the timer belongs to the yellow powerup: invincibility.
      //text((int)startTimer.getTime(),(thePinguin.position.x-5),(thePinguin.position.y - 10)); // the countdown will be shown on the screen.
      image(timerSprite[timerFrame], thePinguin.position.x, thePinguin.position.y - 50, thePinguin.playerWidth, thePinguin.playerHeight);
      
      /*for (int i =0 ;i<framesTimer;i++) {
        if(startTimer.getTime() <= (500 /(5/(5-i))))
        {
          timerFrame = i;
        } 
      } */
      
      if(startTimer.getTime() <= 500)
      {
       timerFrame = 0;
      }
      if(startTimer.getTime() <= 400)
      {
       timerFrame = 1;
      }
      if(startTimer.getTime() <= 300)
      {
       timerFrame = 2;
      }
      if(startTimer.getTime() <= 200)
      {
       timerFrame = 3;
      }
      if(startTimer.getTime() <= 100)
      {
       timerFrame = 4;
      }
        
    }   
  }
 
  public void drawFlickeringImage()
  {
    if(shieldTimer.getTime() % 5 == 0)
    {
            image(imgShieldActive,(thePinguin.position.x-marginCorrection),(thePinguin.position.y-30),imgSize,imgSize);//draws the shieldpowerup
    }
  }
  
  public void drawAmmoAmountImage()
  { 
    if(thePinguin.keysPressed[' '] && thePinguin.coolDown == 0)
    {
      ammoCount = ammoCount-1;
    }
    textSize(18);
    text("ammo: " + (ammoCount),(thePinguin.position.x+25),(thePinguin.position.y - 10)); // the ammoAmount will be shown on the screen.
  }
  
  public void drawPoweredUpPinguin(int counter)
  {
    
      if (counter <= shieldLife && counter > shootLife)
      {
        image(pinguinShieldSprite[pinguinFrame], thePinguin.position.x, thePinguin.position.y, thePinguin.playerWidth, thePinguin.playerHeight);
        if (pinguinFrame == framesPinguin-1) {
        pinguinFrame = 0;
        } 
        else {
        pinguinFrame++;
        }
      }
      else if (counter <= invincibilityLife && counter > shieldLife)
      {
        image(pinguinInvincibleSprite[pinguinFrame], thePinguin.position.x,thePinguin.position.y, thePinguin.playerWidth, thePinguin.playerHeight);
        if (pinguinFrame == framesPinguin-1) {
          pinguinFrame = 0;
        } 
        else {
        pinguinFrame++;
        }
      }
      else if(counter == shootLife)
      {
        image(pinguinShieldSprite[pinguinFrame], thePinguin.position.x, thePinguin.position.y, thePinguin.playerWidth, thePinguin.playerHeight);
        if (pinguinFrame == framesPinguin-1) {
        pinguinFrame = 0;
        } 
        else {
        pinguinFrame++;
        }
        
        image(shootItem,thePinguin.position.x+ (thePinguin.playerWidth/2-(10)), thePinguin.position.y+(thePinguin.playerHeight/2-(5)), 30,20);//draws the shoot powerup 
      
      }
  }
}
class ScoreSave {
  Table table; // a table fot the data
  final String filename = "saveFile.csv"; // name and path of the file
  boolean isSaved; // check if it is saved

  final int listlength = 10; 
  int [] score = new int [listlength];
  String [] name = new String [listlength];

  int tempHighscore;
  String tempName;
  int check;

  public void setup() {
    isSaved = false;
    File f = new File(dataPath(filename));
    if (f.exists()) {
      print("exist");
    } else {
      table = new Table();
      table.addColumn("id");
      table.addColumn("Name");
      table.addColumn("Highscore");


      table.addRow();
      table.setString(0, "Name", "Mar");
      table.setInt(0, "Highscore", 90);

      table.addRow();
      table.setString(1, "Name", "Jef");
      table.setInt(1, "Highscore", 80);

      table.addRow();
      table. setString(2, "Name", "Max");
      table.setInt(2, "Highscore", 70);

      table.addRow();
      table.setString(3, "Name", "Kel");
      table.setInt(3, "Highscore", 60);

      table.addRow();
      table.setString(4, "Name", "Kev");
      table.setInt(4, "Highscore", 50);

      table.addRow();
      table.setString(5, "Name", "Mar");
      table.setInt(5, "Highscore", 40);

      table.addRow();
      table.setString(6, "Name", "Jef");
      table.setInt(6, "Highscore", 30);
      table.addRow();
      table.setString(7, "Name", "Max");
      table.setInt(7, "Highscore", 20);

      table.addRow();
      table.setString(8, "Name", "Kel");
      table.setInt(8, "Highscore", 10);

      table.addRow();
      table.setString(9, "Name", "Kev");
      table.setInt(9, "Highscore", 5);
      saveTable(table, "./data/" + filename);
    }

    table = loadTable("./data/" + filename, "header");

    for (int i = 0; i < listlength; i++) {
      println(i);
      tempHighscore = table.getInt(i, "Highscore");
      println(i+tempHighscore);
      score[i] = tempHighscore;
      tempName = table.getString(i, "Name");
      name[i] = tempName;
    }
  }
  public void scoreCheck(int newScore, String newName) {
    if (isSaved == false) {
      print("Name:" + newName);
      check = 0; // added this, everything else is fine
      if (newScore > score[0]) { 
        check = 10;
      } else if (newScore > score[1]) { 
        check = 9;
      } else if (newScore > score[2]) { 
        check = 8;
      } else if (newScore > score[3]) { 
        check = 7;
      } else if (newScore > score[4]) { 
        check = 6;
      } else if (newScore > score[5]) { 
        check = 5;
      } else if (newScore > score[6]) { 
        check = 4;
      } else if (newScore > score[7]) { 
        check = 3;
      } else if (newScore > score[8]) { 
        check = 2;
      } else if (newScore > score[9]) { 
        check = 1;
      }

      if (check == 1) {
        score[9] = newScore;

        name[9] = newName;
      } else if (check == 2) {
        score[9]  = score[8];
        score[8] = newScore;

        name[9] = name[8];
        name[8] = newName;
      } else if (check == 3) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = newName;
      } else if (check == 4) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = newName;
      } else if (check == 5) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = newName;
      } else if (check == 6) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = newName;
      } else if (check == 7) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = newName;
      } else if (check == 8) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = newName;
      } else if (check == 9) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = score[1];
        score[1] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = name[1];
        name[1] = newName;
      } else if (check == 10) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = score[1];
        score[1] = score[0];
        score[0] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = name[1];
        name[1] = name[0];
        name[0] = newName;
      }
      table.clearRows();
      for (int i = 0; i < listlength; i++) {
        println (name[i], score[i]);
        table.addRow();
        table.setString(i, "Name", name[i]);
        table.setInt(i, "Highscore", score[i]);
      }

      saveTable(table, "./data/" + filename);
    }
    isSaved = true;
  }

  public void ScoreScreen() {
  }
}
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

  public void init() {
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
  public void draw() {
    score += 0.1f;
    showScore(score);
  }

  // draw the moving background in the score screen
  public void drawBackground() {
    background(0, 191, 255);
    image(background, bgX, bgY);
    image(background, bgX2, bgY2);
  }

  // update the background so it moves
  public void updateBackground() {
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
  public void triggerScore() {
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
  public void displayName() {
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
  public void keyPressed() {
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
  public void keyReleased() {
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


  public void changePosition(int min, int max) {
    // check what letter is selected
    if (leftPressed == true && charPosition != min) {
      charPosition--;
      leftPressed = false;
    } else if (rightPressed == true && charPosition != max) {
      charPosition++;
      rightPressed = false;
    }
  }

  public void changeNameByInput() {
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
  public void showScore(double scoreGetal) {
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
  public void saveAndDislayHighscore() {
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
class Snow
{
  // amount of snowflakes
  int snowCount = 875;
  
  // snow got a maxsize
  int snowSize = 3;
  
  // snow got a maxspeed
  int snowSpeed = 4;
  
  // different array's
  float[]xspeed = new float[snowCount];
  float[]yspeed= new float[snowCount];
  float[]xpos = new float[width];
  float[]ypos = new float[height];
  float[]wdth = new float[snowCount];
  float[]ht = new float[snowCount];
 
  //initialize sketch
  public void init() {
   
    //initialize values for all snowflakes
    for (int i=0; i<snowCount; i++) {
      // set varied snow speed
      xspeed[i] = random(1, snowSpeed);
      yspeed[i] = random(-snowSpeed, snowSpeed);
      // ball varied ball sizes
      wdth[i]= random(1, snowSize);
      ht[i]= wdth[i];
      // set initial ball placement
      xpos[i] = width/2+random(-width/3, width/3);
      ypos[i] = height/12+random(-height/100, height/100);
    }
    // turn off shape stroke rendering
    noStroke();
    //set the animation loop speed
    frameRate(60);
  }
  
  // begin animation loop
  public void draw() {
    //updates background
    
    for (int i=0; i<snowCount; i++) {
      
      fill(255, 250, 250);//snow white
      
      //draw flakes
      ellipse(xpos[i], ypos[i], wdth[i], ht[i]);
      
      //upgrade position values
      xpos[i]+=xspeed[i];
      ypos[i]+=yspeed[i];
      
      //detects ball collision with sketch window edges accounting for ball thickness.
      if (xpos[i]+wdth[i]/2>=width || xpos[i]<=wdth[i]/2) {
        xspeed[i]*=-1;
      }
      if (ypos[i]+ht[i]/2>=(height/1.5f) || ypos[i]<=ht[i]/2) {
        yspeed[i]*=-1;
      }
    }
  }
}
class Timer/// used for temporary powerups
{
 float time;
 
 Timer(float set)//Contructor when you use a new timer
 {
   time = set;
 }
 public float getTime()//return the current time
 {
  return(time);
 }
 public void setTime(float set)
 {
   time = set;
 }
 public void countUp()
 {
  time += 1;//frameRate
 }
 public void countDown()
 {
   time -=1;//frameRate
 }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PingSwims" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
