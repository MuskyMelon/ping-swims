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
  void initEnemy() {
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
  void updateEnemy() {
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
  void updatePoints() {
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
  void drawEnemy() {
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
  void drawPoints() {
    for (int i = 0; i < maxPoints; i++) {
      // only draw sharks that exist
      if (point[i] != null) {
        fill(122, 122, 122);
        image(pointSprite, point[i].x, point[i].y + watery, point[i].w, point[i].h);
      }
    }
  } 

  void draw() {

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
    if (secondsToNewSpawn > 1.5 ) {
      oneMinuteCountdown -= 1;
      if (oneMinuteCountdown == 0) {
        secondsToNewSpawn -= 0.2;
        oneMinuteCountdown = oneMinute;
      }
    } else {
      secondsToNewSpawn = 1.5;
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
  void rowBottom() {
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
  void rowTop() {
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

  void quickDash() {
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
  void followPlayer() {    
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
  void vFormation() {    
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
  void rowPointsBottom() {
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
  void rowPointsRandom() {
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
  void rowPointsTop() {
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

  void hiFormation() {
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
  void shark() {
    enemyHeight = height / 100 * 9;
    enemyWidth = height / 100 * 19;
    typeOfEnemy = "Shark";
  }

  // whale variables
  void whale() {
    enemyHeight = height / 100 * 15;
    enemyWidth = height / 100 * 21;
    typeOfEnemy = "Whale";
  }

  void swordFish() {
    enemyHeight = height / 100 * 9;
    enemyWidth = height / 100 * 19;
    typeOfEnemy = "swordFish";
  }

  // point variables
  void points() {
    pointHeight = 20;
    pointWidth = 30;
  }

  ////////////////////////////////
  //// END TYPE OF ENCOUNTERS ////
  ////////////////////////////////

  /* -------------- NIET AANPASSEN, COLLISIONS -------------- */

  // enemy collision with the penguin
  boolean collidesWithEnemy(Pinguin ping, Enemy enemy)
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
  boolean collidesWithPoint(Pinguin ping)
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
  boolean CollisionWithEnemyBullet(Bullet b, Enemy enemy)
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