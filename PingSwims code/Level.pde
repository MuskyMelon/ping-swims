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

  void init() {
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

  void draw() {
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

  void drawWater() {
    image(backgroundImage, waterx, watery, widthitem, heightitem);
    image(backgroundImage, waterx2, watery, widthitem, heightitem);
  }
  
  void setLevelBoundaries(){
    
   if(watery > 0 && thePinguin.position.y < 50){
     thePinguin.position.y = 50; 
   }
   
   if(watery < 0){
     watery = 0;
   }
   
  }

  void updateWater() {
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