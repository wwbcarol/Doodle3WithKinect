package processing.kinect;

import java.util.*;

//import ddf.minim.AudioPlayer;
//import ddf.minim.Minim;
import SimpleOpenNI.*;
import processing.core.*;

public class Main extends PApplet {

	// Debug?
	boolean debugMode = false;
	// Kinect
	SimpleOpenNI context;
	
	// Hands
	float zoomF = 1f;
	float rotX = radians(180);
	float rotY = radians(0);
	boolean handsTrackFlag = false;
	PVector handVec = new PVector();
	ArrayList<PVector> handVecList = new ArrayList<PVector>();
	int handVecListSize = 30;
	String lastGesture = "";

	// Effect
	Effect1 e1;

	// MainLoop
	boolean isDrawing = true;
	boolean isImgSave = false;
	float lastDestroyTime = -1;
	float lastCreateTime = -1;
	float startTime = 0;

	public void setup() {
		size(1024, 768, P3D);

		/***** Kinect *******/
		context = new SimpleOpenNI(this);
		context.setMirror(true);
		if (context.enableDepth() == false) {
			println("Can't open the depthMap, maybe the camera is not connected!");
			exit();
			return;
		}
		context.enableGesture();
		context.enableHands();
		context.addGesture("Wave");
		context.addGesture("RaiseHand");

		/***** Effect *******/
		e1 = new Effect1(this);
		e1.init();
	}

	public void reset() {
		e1 = new Effect1(this);
		e1.init();
		startTime = millis();

	}

	public void draw() {

		/***** Kinect *******/
		context.update();
		// set the scene pos

			translate(width / 2, height / 2, 0);
			rotateX(rotX);
			rotateY(rotY);
			scale(zoomF);

			/***** Debug Mode!!! *******/
			if (debugMode == true) {
				pushStyle();
				colorMode(RGB, 255, 255, 255, 255);
				stroke(255, 0, 0);
				strokeWeight(5);
				point(handVec.x, handVec.y);
				popStyle();
			}

			/***** Effect *******/
			
			if (isDrawing == true) {
//				e1.draw(mouseX, mouseY);
				if (handsTrackFlag) {
//					if(player1.isPlaying() == false){
//						if(isMusicOn == true)
//							player2.play();
//						else
//							player2.mute();
//						}
					e1.draw(handVec.x, handVec.y);
				}
			} else {
				e1.finishDrawing();
				reset();
				isDrawing = true;
//				player1.pause();
//				player1.rewind();
			}

		if ((lastCreateTime > startTime)&&(lastDestroyTime > lastCreateTime)
				&& (millis() - lastDestroyTime > 2000) && (isDrawing == true)) {
				isDrawing = false;
		} 

	}

	// -----------------------------------------------------------------
	// hand events

	public void onCreateHands(int handId, PVector pos, float time) {

		if (debugMode == true)
			println("onCreateHands - handId: " + handId + ", pos: " + pos
					+ ", time:" + time);

		lastCreateTime = millis();
		handsTrackFlag = true;
		handVec = pos;

		handVecList.clear();
		handVecList.add(pos);
	}

	public void onUpdateHands(int handId, PVector pos, float time) {
		handVec = pos;

		handVecList.add(0, pos);
		if (handVecList.size() >= handVecListSize) { // remove the last point
			handVecList.remove(handVecList.size() - 1);
		}
	}

	public void onDestroyHands(int handId, float time) {
		if (debugMode == true)
			println("onDestroyHandsCb - handId: " + handId + ", time:" + time);

		lastDestroyTime = millis();
		handsTrackFlag = false;
		context.addGesture(lastGesture);
	}

	// -----------------------------------------------------------------
	// gesture events

	public void onRecognizeGesture(String strGesture, PVector idPosition,
			PVector endPosition) {

		if (debugMode == true)
			println("onRecognizeGesture - strGesture: " + strGesture
					+ ", idPosition: " + idPosition + ", endPosition:"
					+ endPosition);

		lastGesture = strGesture;
		context.removeGesture(strGesture);
		context.startTrackingHands(endPosition);

	}

	public void onProgressGesture(String strGesture, PVector position,
			float progress) {
		if (debugMode == true)
			println("onProgressGesture - strGesture: " + strGesture
					+ ", position: " + position + ", progress:" + progress);
	}

	// -----------------------------------------------------------------
	// Keyboard event
	public void keyPressed() {
//		if(key==' ')
//			isMusicOn = !isMusicOn;
		if (debugMode == true)
			System.out.println("Key Pressed!!");
	}
	
//	public void stop(){
//		player1.close();
//		player2.close();
//		minim.stop();
//		super.stop();
//	}


	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "processing.kinect.Main" });
	}
}