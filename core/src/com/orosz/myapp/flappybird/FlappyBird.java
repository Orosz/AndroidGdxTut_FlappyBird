package com.orosz.myapp.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	//Collision
    Circle birdCircle;


	int gameState = 0;
	float gravity = 2;

	Texture topTube;
	Texture bottomTube;
	float gap = 400;
    float maxTubeOffet;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    //For collision
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    //Score
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;

    //GameOver
    Texture gameover;



	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

        //For collision
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        //DisplayScore
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(4);

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffet = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();

		//real value was half of width screen - Gdx.graphics.getWidth() / 2
		distanceBetweenTubes = Gdx.graphics.getWidth() + 50;

        startGame();

        //gameover
        gameover = new Texture("gameover.png");
	}

	public void startGame() {

        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 50);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2
                    + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

    }

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                Gdx.app.log("Score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }

            }

            if (Gdx.input.justTouched()) {

                velocity = -20;

            }

            for (int i = 0; i < numberOfTubes; i++) {

                if ( tubeX[i] < - topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 50);

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
                        topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
                        bottomTube.getWidth(), bottomTube.getHeight());

            }


			if (birdY > 0) {

				velocity += gravity;
				birdY -= velocity;

			} else {

                gameState = 2;

            }


		} else if (gameState == 0 ){

            if (Gdx.input.justTouched()) {

                    //Gdx.app.log("Touched", "Yes");
                    gameState = 1;

            }

        } else if (gameState == 2){

            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

            if (Gdx.input.justTouched()) {

                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = -20;

            }

        }

        if (flapState == 0) {

            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[flapState],
                Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2,
                birdY);


        font.draw(batch, String.valueOf(score), 30, 70);

        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2,  birds[flapState].getWidth() / 2);


//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {

//            shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
//                    topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
//                    bottomTube.getWidth(), bottomTube.getHeight());

            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
                    Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                //Gdx.app.log("Collision", "Yes!");
                gameState = 2;

            }

        }

        // shapeRenderer.end();






	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();

	}
}
