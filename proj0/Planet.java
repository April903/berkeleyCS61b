import java.awt.Image;
import javax.swing.ImageIcon;

public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;
	public double r;
	public static double G = 6.67E-11;

	public Planet (double xP, double yP, double xV, double yV, double m, String img){
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
		ImageIcon icon = new ImageIcon("images/" + imgFileName);
		Image image = icon.getImage();
		r = image.getWidth(null) / 2;
	}

	public Planet (Planet b) {
		xxPos = b.xxPos;
		yyPos = b.yyPos;
		xxVel = b.xxVel;
		yyVel = b.yyVel;
		mass = b.mass;
		imgFileName = b.imgFileName;
	}

	public double calcDistance (Planet b) {
		double distance = Math.sqrt(Math.pow(b.xxPos - xxPos, 2) + Math.pow(b.yyPos - yyPos, 2));
		return distance;
	}

	public double calcForceExertedBy (Planet p) {
		double distance = calcDistance (p);
		double force = G * mass * p.mass / Math.pow(distance, 2);
		return force;
	}

	public double calcForceExertedByX (Planet p) {
		double f = calcForceExertedBy(p);
		double dx = p.xxPos - xxPos;
		double r = calcDistance(p);

		return f * dx / r;
	}

	public double calcForceExertedByY (Planet p) {
		double f = calcForceExertedBy(p);
		double dy = p.yyPos - yyPos;
		double r = calcDistance(p);

		return f * dy / r;
	}

	public double calcNetForceExertedByX (Planet[] ps) {
		double netForceX = 0;
		for (int i = 0; i < ps.length; i++) {
			if (!ps[i].equals(this)) {
				netForceX += calcForceExertedByX(ps[i]);
			}
		}

		return netForceX;
	}

	public double calcNetForceExertedByY (Planet[] ps) {
		double netForceY = 0;
		for (int i = 0; i < ps.length; i++) {
			if (!ps[i].equals(this)) {
				netForceY += calcForceExertedByY(ps[i]);
			}
		}

		return netForceY;
	}


	public boolean collide (Planet p) {
		if (calcDistance(p) <= r + p.r) {
			return true;
		}
		else {
			return false;
		}
	}

	public void handleCollide (Planet p) {


		double dx = p.xxPos - xxPos;
		double dy = p.yyPos - yyPos;

		double drx = dx / calcDistance(p);
		double dry = dy / calcDistance(p);

		double v1x = xxVel;
		double v1y = yyVel;

		double v2x = p.xxVel;
		double v2y = p.yyVel;

		double v1nx = (v1x * drx + v1y * dry) * drx;
		double v1ny = (v1x * drx + v1y * dry) * dry;
		double v1tx = v1x - v1nx;
		double v1ty = v1y - v1ny;

		double v2nx = (v2x * drx + v2y * dry) * drx;
		double v2ny = (v2x * drx + v2y * dry) * dry;
		double v2tx = v2x - v2nx;
		double v2ty = v2y - v2ny;

		double totalMass = mass + p.mass;

		double v1nNewX = ((mass - p.mass) * v1nx + 2 * p.mass * v2nx) / totalMass;
		double v1nNewY = ((mass - p.mass) * v1ny + 2 * p.mass * v2ny) / totalMass;

		double v2nNewX = ((p.mass - mass) * v2nx + 2 * p.mass * v1nx) / totalMass;
		double v2nNewY = ((p.mass - mass) * v2ny + 2 * p.mass * v1ny) / totalMass;

		xxVel = v1nNewX + v1tx;
		yyVel = v1nNewY + v1ty;

		p.xxVel = v2nNewX + v2tx;
		p.yyVel = v2nNewY + v2ty;


	}

	public void update (double timeStep, double netForceX, double netForceY) {
		double accX = netForceX / mass;
		double accY = netForceY / mass;
		xxVel += timeStep * accX;
		yyVel += timeStep * accY;
		xxPos += timeStep * xxVel;
		yyPos += timeStep * yyVel;
	}

	public void draw() {
		StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
	}
}