package processing.kinect;

//Modified from "doodle 3" by bitcraft
//licensed under Creative Commons Attribution-Share Alike 3.0 and GNU GPL license.
//Work: http://openprocessing.org/visuals/?visualID= 6742	
//License: 
//http://creativecommons.org/licenses/by-sa/3.0/
//http://creativecommons.org/licenses/GPL/2.0/

import java.util.*;

import processing.core.*;

class Point {

	PApplet p;
	public PVector pos = new PVector();
	public PVector perlin = new PVector();
	public int life = 0;

	

	Point(PApplet p) {
		this.p = p;
	}

	public void Position(float x, float y) {

		Random randomGenerator = new Random();
		pos.x = x + randomGenerator.nextInt(25)
				* PApplet.cos(randomGenerator.nextFloat() * PApplet.TWO_PI);
		pos.y = y + randomGenerator.nextInt(25)
				* PApplet.sin(randomGenerator.nextFloat() * PApplet.TWO_PI);
		life = 0;
	}

	public PVector perlin(int seed) {
		return new PVector(
				p.noise(seed + 1, pos.x * 0.01f, pos.y * 0.01f) - 0.5f,
				p.noise(seed - 1, pos.x * 0.01f, pos.y * 0.01f) - 0.5f);

	}

}

public class Effect1 {

	PApplet p;
	PImage img;

	// color scheme
	int Mode;
	// 0 -- blace and white
	// 1 -- HSB color
	// >2 -- random color

	int n = 500;
	int timer = 20;
	int index = 0;

	 //Perlin seed
	 int seed;
	
	Point[] Points;

	int leftColor;
	int rightColor;
	int clr;
	float rr, gg, bb;

	Effect1(PApplet p) {

		this.p = p;

	}

	public void init() {

		Random randomGenerator = new Random();
		Mode = randomGenerator.nextInt(10);

		seed = randomGenerator.nextInt(100);
		if (Mode == 0 || Mode == 1) {
			p.colorMode(PApplet.HSB, PApplet.TWO_PI, 2, 1);
		} else if (Mode >= 2) {
			leftColor = p.color(p.random(255), p.random(255), p.random(255));
			rightColor = p.color(p.random(255), p.random(255), p.random(255));
		}

		if (Mode != 0)
			p.background(0);
		else {
			p.colorMode(PApplet.RGB, 255, 255, 255, 255);
			p.background(255);
		}

		Points = new Point[n];
		for (int i = 0; i < n; i++) {
			Points[i] = new Point(this.p);
		}
		// System.out.println("init");

		p.smooth();

	}

	public void draw(float x, float y) {

		for (int i = 0; i * timer < n & index < n; i++, index++) {
			Points[index].Position(x, y);
		}

		// Draw line between points with perlin noise positions
		for (int i = 0; i < index; i++) {

			Points[i].life++;
			if (Points[i].life > timer) {
				Points[i].Position(x, y);
			} else {
				PVector per = Points[i].perlin(seed);
				float opacity = PApplet.mag(per.x, per.y) * 160;

				// color scheme

				if (Mode == 0) {
					float h = PApplet.atan2(per.x, per.y) + PApplet.PI;
					this.p.stroke(h, 0, 0, opacity);
				} else if (Mode == 1) {
					float h = PApplet.atan2(per.x, per.y) + PApplet.PI;
					this.p.stroke(h, 1, 1, opacity);
				} else if (Mode >= 2) {
					clr = this.p.lerpColor(leftColor, rightColor,
							((float) Points[i].pos.x + this.p.width/2) / this.p.width);
					
					rr = this.p.red(clr);
					gg = this.p.green(clr);
					bb = this.p.blue(clr);
					this.p.stroke(rr, gg, bb, opacity);
				}

				// draw

				this.p.line(Points[i].pos.x, Points[i].pos.y,
						Points[i].pos.x += 60 * per.x,
						Points[i].pos.y += 60 * per.y);
			}
		}
	}

	public void finishDrawing() {
		img = p.get();
		p.save(p.millis() + ".tif");
		p.colorMode(PApplet.RGB, 255, 255, 255, 255);
	}

}
