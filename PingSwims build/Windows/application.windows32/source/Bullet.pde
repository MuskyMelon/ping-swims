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
  void init() 
  {
    size = 1;
    expansion = 1;
    // Our bullets are energyballs
    reinit();
  }
  void reinit()
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
  void update() {
   
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
  void fire(float tx, float ty) {   
    isFired = true;
    
    // Start the bullet at the player position
    x = thePinguin.position.x+50;
    y = thePinguin.position.y+20;
    
    // the horizontal velocity should be 13
    vx = tx;
    vy = ty;
  }

  // Whenever you want to draw the bullet, call this method
  void draw() {
    
    fill(255, 165, 0);// orange
    
    //the bullet will stop with expanding if size =30;
    if (size == 30)
    {
      expansion=0;
    }
    
    ellipse(x, y, size, size);
  }
}
