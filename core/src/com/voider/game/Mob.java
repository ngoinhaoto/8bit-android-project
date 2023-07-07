    package com.voider.game;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.g2d.Animation;
    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureAtlas;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.MathUtils;
    import com.badlogic.gdx.math.Rectangle;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.scenes.scene2d.Actor;


    public class Mob extends Actor {
        private enum State { IDLING, WALKING_LEFT, WALKING_RIGHT, ATTACKING };
        private TileMap tileMap;
        private String mobType;

        private int HP;
        private int maxHealth;
        private float radius; // if player enters their radius, they will follow player
        private Character player;
        private float movementSpeed;
        private float position_x;
        private float position_y;

        private float distanceToMove;
        private boolean movingRight;
        private State currentState;

        private State previousState;
        public TextureAtlas textureAtlas;
        private Animation<TextureRegion> mIdle;
        private Animation<TextureRegion> mWalk;
        private Animation<TextureRegion> mAttack;
        private static final float FRAME_TIME = 0.15f;
        private float stateTime;

        public Mob(TileMap tileMap, float x, float y, String mobType, float radius, Character player) {
            this.tileMap = tileMap;

            this.maxHealth = calculateMaxHealth(mobType); // cái này thì mỗi mob có maxhealth riêng nghe, hp chỉ là máu tạm thời
            this.HP = maxHealth;
            this.mobType = mobType;
            this.radius = radius;
            this.player = player;
            movementSpeed = 40;

            currentState = State.IDLING;
            previousState = State.IDLING;

            textureAtlas = new TextureAtlas(Gdx.files.internal("mobs/chort/animation_chort.atlas"));

            mIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
            mWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));
//            mAttack = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("attack"));

            mIdle.setFrameDuration(FRAME_TIME);
            mWalk.setFrameDuration(FRAME_TIME);
//            mAttack.setFrameDuration(FRAME_TIME);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            // Update the mob's behavior and attributes
            update(delta);
        }

        public void takeDamage(int damage) {
            HP -= damage;

            // Check if the mob is still alive
            if (HP <= 0) {
                // Mob is dead, remove it from the stage
                remove();
            }
        }


        private int calculateMaxHealth(String mobType) {
            int maxHealth = 0;
            switch (mobType) {
                case "chort":
                    maxHealth = 10;
                    break;
                case "goblin":
                    maxHealth = 8;
                    break;

            }
            return maxHealth;
        }

        public void update(float delta) {
            stateTime += delta;
            // Check if the mob is colliding
            boolean isColliding = isColliding(getX(), getY());

            // Check the distance between the mob and the player
            float distanceToPlayerX = player.getPosition().x - getX();
            float distanceToPlayerY = player.getPosition().y - getY();
            float totalDistanceToPlayer = (float) Math.sqrt(distanceToPlayerX * distanceToPlayerX + distanceToPlayerY * distanceToPlayerY);

            // Check if the player is within the mob's radius and not colliding
            if (totalDistanceToPlayer <= radius && !isColliding) {
                // Move towards the player
                float directionX = distanceToPlayerX / totalDistanceToPlayer;
                float directionY = distanceToPlayerY / totalDistanceToPlayer;

                float distanceMovedX = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerX));
                float distanceMovedY = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerY));

                // Check if moving towards the player would result in a collision
                if (!isColliding(getX() + distanceMovedX * directionX, getY() + distanceMovedY * directionY)) {
                    setX(getX() + distanceMovedX * directionX);
                    setY(getY() + distanceMovedY * directionY);
                } else {
                    // Mob is about to hit a wall, stop moving towards the player
                    // You can add any necessary behavior here, such as changing direction or stopping completely
                }
            } else {
                // Perform random left and right movement
                randomMovement(delta);
            }
        }


        public void randomMovement(float delta) {
            State previousState = currentState; // Store the previous state before updating
            // Check if the mob is currently moving right
            if (movingRight) {
                // Move right
                float distanceMoved = Math.min(movementSpeed * delta, distanceToMove);

                // Check if moving right would result in a collision
                if (!isColliding(getX() + distanceMoved, getY())) {
                    setX(getX() + distanceMoved);
                    distanceToMove -= distanceMoved;
                } else {
                    // Mob is about to hit a wall, stop moving
                    distanceToMove = 0;
                }

                // Check if the distance has been covered
                if (distanceToMove <= 0) {
                    // Change direction to left
                    movingRight = false;
                    distanceToMove = MathUtils.random(50, 200);
                }
            } else {
                // Move left
                float distanceMoved = Math.min(movementSpeed * delta, distanceToMove);

                // Check if moving left would result in a collision
                if (!isColliding(getX() - distanceMoved, getY())) {
                    setX(getX() - distanceMoved);
                    distanceToMove -= distanceMoved;
                } else {
                    // Mob is about to hit a wall, stop moving
                    distanceToMove = 0;
                }

                // Check if the distance has been covered
                if (distanceToMove <= 0) {
                    // Change direction to right
                    movingRight = true;
                    distanceToMove = MathUtils.random(50, 200);
                }
            }

            // Set the current state based on movement direction
            if (movingRight) {
                currentState = State.WALKING_RIGHT;
            } else {
                currentState = State.WALKING_LEFT;
            }

            // Check if the state has changed
            if (currentState != previousState) {
                stateTime = 0; // Reset the state time when the state changes
            }
        }

        public void render(SpriteBatch spriteBatch) {
            TextureRegion currentFrame = getFrame(Gdx.graphics.getDeltaTime());
            // Render the character at its current position
            float textureWidth = 32; // Set the desired texture width
            float textureHeight = 32; // Set the desired texture height
            if (!movingRight) {
                spriteBatch.draw(currentFrame, getX() + textureWidth, getY(),
                        -textureWidth, textureHeight);
            } else {
                spriteBatch.draw(currentFrame, getX(), getY(), textureWidth, textureHeight);
            }
        }

        public boolean isColliding(float x, float y) {
            int tileXStart = (int) (x / tileMap.getTileWidth());
            int tileXEnd = (int) ((x + 16 + getWidth()) / tileMap.getTileWidth());
            int tileYStart = (int) (y / tileMap.getTileHeight());
            int tileYEnd = (int) ((y + 16 + getHeight()) / tileMap.getTileHeight());

            // Check collisions for each corner of the character
            boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
            boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
            boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
            boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

            // Return true if any corner collides with a boundary tile
            return topLeft || topRight || bottomLeft || bottomRight;
        }

        public TextureRegion getFrame(float deltaTime) {
            currentState = getState();
            TextureRegion region;
            switch (currentState) {
                case IDLING:
                    region = mIdle.getKeyFrame(stateTime, true);
                    break;
                case WALKING_LEFT:
                    region = mWalk.getKeyFrame(stateTime, true);
                    this.movingRight = false;
                    break;
                case WALKING_RIGHT:
                    region = mWalk.getKeyFrame(stateTime, true);
                    this.movingRight = true;
                    break;
                case ATTACKING:
                    region = mAttack.getKeyFrame(stateTime, true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentState);
            }
            return region;
        }

        private State getState() {
            return this.currentState;
        }

        public Vector2 getPosition() {
            return new Vector2(getX(), getY());
        }

        public int getHP () {return HP;}

        public Rectangle getBoundingRectangle() {
            float x = getX();
            float y = getY();
            float width = getWidth();
            float height = getHeight();

            return new Rectangle(x + 4, y, width + 23, height + 23);
        }


    }