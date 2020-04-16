 class PowerUp
{
  final float yStartMin =1.25;
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
  
  void init()
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
    
    imgSize= thePinguin.playerWidth*1.6;
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
  
  void draw()
  {
    updatePowerUp();
  }
  void updatePowerUp()
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
  
  boolean collidesWithPowerUp(Pinguin ping)
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
  
  boolean spawnObject(float time)
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
  
  void spawnPowerUp(float x, float y, float w, float h, float spawnTime, int random)
  {
    PuWidth = w;                    //println("oWidth: "+ PuWidth);
    PuHeight = h;                   //println("oHeight: "+ PuHeight);
    xPos = x;                       //println("xPos: "+ xPos);
    yPos = y;                       //println("yPos: "+ yPos);
    spawnTimeInSeconds = spawnTime; //println("spawnTimeInSeconds: "+ spawnTimeInSeconds);
    r = random;                     //println("r: " + r);
  }
  void resetTimers()
  {
     startTimer.setTime(500);//this will reset the timer to 500 frames
     shieldTimer.setTime(30);//this will reset the timer to 30 frames
     shootTimer.setTime(55);//this will reset the timer to 30 frames
  }
  
  void collisionSoundEffects()
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
  
  void powerupRandomGenerator()
  {
    if(xPos < 0 - PuWidth)
    {
      r=(int)random(1,4);//each time the powerup dissapears from the screen the next on will be randomly generated.
      if(spawnObject(spawnTimeInSeconds))
      {
        xPos = width;
        yStart = height/random(1.25,5);//the powerup will start at different locations on the Y-axis.
      }
    }  
  }

  void powerupMoving()
  {
    xPos -= 4; //Change this value to the actual game speed.
    yPos = yStart + 40 * sin(radians(xPos)) + watery;//make the powerup move in a sine wave
  }
 
  void drawImagePowerup()
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
  
  void changeLifeCounter()
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
  
  int PowerUpTimer(int counter)// this will activate the timer and change the counter value back to default when the the timer has passed.
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
 
  void powerupIsActive()
  {
   
  }
  
  void showCountDown()
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
 
  void drawFlickeringImage()
  {
    if(shieldTimer.getTime() % 5 == 0)
    {
            image(imgShieldActive,(thePinguin.position.x-marginCorrection),(thePinguin.position.y-30),imgSize,imgSize);//draws the shieldpowerup
    }
  }
  
  void drawAmmoAmountImage()
  { 
    if(thePinguin.keysPressed[' '] && thePinguin.coolDown == 0)
    {
      ammoCount = ammoCount-1;
    }
    textSize(18);
    text("ammo: " + (ammoCount),(thePinguin.position.x+25),(thePinguin.position.y - 10)); // the ammoAmount will be shown on the screen.
  }
  
  void drawPoweredUpPinguin(int counter)
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