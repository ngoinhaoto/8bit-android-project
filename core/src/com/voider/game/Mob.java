    package com.voider.game;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.g2d.Animation;
    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureAtlas;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.MathUtils;
    import com.badlogic.gdx.math.Rectangle;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.scenes.scene2d.Actor;

    import org.w3c.dom.Text;


    public class Mob extends Actor {
        enum State { IDLING, WALKING_LEFT, WALKING_RIGHT, ATTACKING, DEAD };
        private TileMap tileMap;
        private String mobType;

        private int currentHP;
        private int maxHP;
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
        private Animation<TextureRegion> mDie;
        private static final float FRAME_TIME = 0.15f;
        private float stateTime;
        private boolean isBeingAttacked;
        private float attackTimer;
        private static final float ATTACK_DURATION = 0.2f;
        public Mob(TileMap tileMap, float x, float y, String mobType, float radius, Character player) {
            this.tileMap = tileMap;

            this.maxHP = calculateMaxHealth(mobType); // cái này thì mỗi mob có maxhealth riêng nghe, hp chỉ là máu tạm thời
            this.setCurrentHP(maxHP);
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
            mDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));

            mIdle.setFrameDuration(FRAME_TIME);
            mWalk.setFrameDuration(FRAME_TIME);
//            mAttack.setFrameDuration(FRAME_TIME);
            mDie.setFrameDuration(FRAME_TIME);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            // Update the mob's behavior and attributes
            update(delta);
        }

        public void takeDamage(int damage) {
            if (currentState == State.DEAD) {
                return; // If mob is already dead, ignore further damage
            }
            this.setCurrentHP(this.getCurrentHP()-damage);
            // Set Mob as being attacked and start the attack timer
            isBeingAttacked = true;
            attackTimer = ATTACK_DURATION;
            // Check State of Mob when HP is 0
            if (this.getCurrentHP() <= 0) {
                // Mob is dead, remove it from the stage
                this.currentState = State.DEAD;
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
            // Check if the mob is
            // If Mod dead, no movement
            if (this.currentState != State.DEAD) {
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
        }

        public void randomMovement(float delta) {
            State previousState = currentState; // Store the previous state before updating

            // Check if the mob is currently moving right
            float distanceMoved = Math.min(movementSpeed * delta, distanceToMove);
            if (movingRight) {
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
            // Check if the Mob is being attacked and modify the color accordingly
            if (isBeingAttacked) {
                // Set the color to red
                spriteBatch.setColor(Color.RED);
            }
            // Render the character at its current position
            float textureWidth = 32; // Set the desired texture width
            float textureHeight = 32; // Set the desired texture height
            if (!movingRight) {
                spriteBatch.draw(currentFrame, getX() + textureWidth, getY(),
                        -textureWidth, textureHeight);
            } else {
                spriteBatch.draw(currentFrame, getX(), getY(), textureWidth, textureHeight);
            }
            // Reset the color back to normal
            spriteBatch.setColor(Color.WHITE);

            // Update the attack timer and check if it's expired
            if (isBeingAttacked) {
                attackTimer -= Gdx.graphics.getDeltaTime();
                if (attackTimer <= 0) {
                    isBeingAttacked = false;
                }
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
                case DEAD:
                    region = mDie.getKeyFrame(stateTime, true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentState);
            }
            return region;
        }

        public State getState() {
            return this.currentState;
        }

        public Vector2 getPosition() {
            return new Vector2(getX(), getY());
        }
        public void setCurrentHP(int currentHP) {
            this.currentHP = currentHP;
        }
        public int getCurrentHP() {return currentHP;}
        public Rectangle getBoundingRectangle() {
            float x = getX();
            float y = getY();
            float width = getWidth();
            float height = getHeight();

            return new Rectangle(x + 4, y, width + 23, height + 23);
        }
    }