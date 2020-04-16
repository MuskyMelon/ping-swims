class Timer/// used for temporary powerups
{
 float time;
 
 Timer(float set)//Contructor when you use a new timer
 {
   time = set;
 }
 float getTime()//return the current time
 {
  return(time);
 }
 void setTime(float set)
 {
   time = set;
 }
 void countUp()
 {
  time += 1;//frameRate
 }
 void countDown()
 {
   time -=1;//frameRate
 }
}