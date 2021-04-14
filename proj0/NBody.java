public class NBody {

	public static double readRadius (String file) {
		In in = new In(file);

		in.readLine();
		double radius = in.readDouble();

		return radius;
	}

	public static Planet[] readPlanets (String file) {
		In in = new In(file);

		int count = in.readInt();
		Planet[] ps = new Planet[count];

		in.readLine();
		in.readLine();

		for (int i = 0; i < count; i++) {
			double xP = in.readDouble();
			double yP = in.readDouble();
			double xV = in.readDouble();
			double yV = in.readDouble();
			double m = in.readDouble();
			String img = in.readString();

			ps[i] = new Planet(xP, yP, xV, yV, m, img);
		}

		return ps;

	}

	public static void checkCollision (Planet[] ps, double radius) {
		for (int i = 0; i < ps.length; i++) {
			for (int j = i + 1; j < ps.length; j++) {
				if (ps[i].calcDistance(ps[j]) <= ps[i].r * radius / 256 + ps[j].r * radius / 256) {
				   	ps[i].handleCollide(ps[j]);
				}
			}
		}
	}

	public static void main (String args[]) {

		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];

		double radius = readRadius(filename);
		Planet[] planets = readPlanets(filename);

		StdDraw.setScale(- radius, radius);
		StdDraw.clear();

		StdDraw.enableDoubleBuffering();

		double time = 0.0;

		StdAudio.play("audio/2001.mid");

		long last = System.nanoTime();
		while (time < T) {
			long now = System.nanoTime();
			double delta = now - last;
			last = now;

			//System.out.println(delta);

			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];

			checkCollision(planets, radius);

			for (int i = 0; i < planets.length; i++) {

				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);

				planets[i].update(dt, xForces[i], yForces[i]);

			}

			StdDraw.picture(0, 0, "images/starfield.jpg");

			for (Planet p : planets) {
			    p.draw();
		    }

		    StdDraw.show();
		    StdDraw.pause(10);

		    time += dt;
		}

		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                  planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                  planets[i].yyVel, planets[i].mass, planets[i].imgFileName); 
		}
	}
}