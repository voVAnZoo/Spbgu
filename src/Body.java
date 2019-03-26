import java.awt.*;

/**
 * Created by Vova on 24.02.2019.
 */
public class Body {

    static double g = 100;
    static double dt = 0.0015;

    double x;
    double y;

    double speedX;
    double speedY;

    double mass;
    int radius;

    boolean taken;
    boolean pillar;

    public Body(){
        this.x = 0;
        this.y = 0;

        pillar = false;
        taken = false;

        speedX = 0;
        speedY = 0;

        mass = 10;
        radius = 10;
    }

    public Body(int x, int y){
        this.x = x;
        this.y = y;

        pillar = false;
        taken = false;

        speedX = 0;
        speedY = 0;

        mass = 10;
        radius = 10;
    }

    public Body(double x, double y){
        this.x = x;
        this.y = y;

        pillar = false;
        taken = false;

        speedX = 0;
        speedY = 0;

        mass = 10;
        radius = 10;
    }

    public void action(){
        if(!pillar){
            if(!taken){
                x += speedX * dt;
                y += speedY * dt;
            }else {
                speedX = 0;
                speedY = 0;
            }
        }
    }

    public void action(double fx, double fy){
        if (!pillar) {
            this.speedY += (fy / mass) * dt;
            this.speedX += (fx / mass) * dt;
        }
    }

    public void draw(Graphics2D g2){
        if(pillar){
            g2.fillRect((int)x - radius,(int)y - radius,2 * radius,2 * radius);
        }else {
            g2.drawOval((int)x - radius,(int)y - radius,2 * radius,2 * radius);
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setXY(double x,double y){
        this.x = x;
        this.y = y;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public void setPillar(boolean pillar) {
        this.pillar = pillar;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getMass() {
        return mass;
    }

    public double getSpeed2() {
        return (speedX * speedX) + (speedY * speedY);
    }

    public double getKineticEnergy(){
        return getSpeed2() * mass / 2;
    }

    public boolean isTaken() {
        return taken;
    }

    public boolean isPillar() {
        return pillar;
    }
}
