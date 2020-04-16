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
  void init() {
   
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
  void draw() {
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
      if (ypos[i]+ht[i]/2>=(height/1.5) || ypos[i]<=ht[i]/2) {
        yspeed[i]*=-1;
      }
    }
  }
}
