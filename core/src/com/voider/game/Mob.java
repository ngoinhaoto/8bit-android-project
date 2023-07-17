    package com.voider.game;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.audio.Sound;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.g2d.Animation;
    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.graphics.g2d.BitmapFont;
    import com.badlogic.gdx.graphics.g2d.GlyphLayout;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureAtlas;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.MathUtils;
    import com.badlogic.gdx.math.Rectangle;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.scenes.scene2d.Actor;
    import com.badlogic.gdx.utils.Array;

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

        private boolean isMelee;
        private int damage;
        private float biteCooldown;
        private static final float BITE_COOLDOWN = 1f; // Adjust the value as needed

        private float shootingCooldown;

        private MobDeathListener mobDeathListener;

        private Array<Bullet> bullets;
        private int shootingRadius;
        private boolean isLeft;

        private Sound chortBiteSound;

        private Sound bigDemonBiteSound;
        private BitmapFont damageFont;

        private float damageDisplayTime = 0.25f;

        private int damageTaken;

        private boolean isCrit;

        private final float SHOOTING_COOLDOWN_NECROMANCER = 1.25f;

        private final float SHOOTING_COOLDOWN_PUMPKIN = 1.85f;


        public interface MobDeathListener {
            void onMobDeath();
        }

        //add one more parameter to see whether mob is a melee mob
        public Mob(TileMap tileMap, float x, float y, String mobType, boolean isMelee,float radius, int damage,Character player, MobDeathListener mobDeathListener) {
            this.tileMap = tileMap;

            this.maxHP = calculateMaxHealth(mobType); // cái này thì mỗi mob có maxhealth riêng nghe, hp chỉ là máu tạm thời
            this.setCurrentHP(maxHP);
            this.mobType = mobType;
            this.radius = radius;
            this.player = player;
            this.isMelee = isMelee;
            this.damage = damage;
            this.mobDeathListener = mobDeathListener;

            damageFont = new BitmapFont(); // You can customize the font style and size

            // Set the movement speed based on the isMelee parameter
            if (isMelee) {
                if (mobType == "chort") {
                    movementSpeed = 54;
                }
                if (mobType == "bigDemon") {
                    movementSpeed = 43;
                }
                biteCooldown = 0f;
            } else {
                 // Set the default speed for non-melee mobs
                this.bullets = new Array<>();
                shootingCooldown = 0;

                if (mobType == "necromancer") {
                    shootingRadius = 110;
                    movementSpeed = 24;
                }
                if (mobType == "pumpkin") {
                    shootingRadius = 140;
                    movementSpeed = 35;
                }
            }

            currentState = State.IDLING;
            previousState = State.IDLING;


            if (mobType == "chort") {
                textureAtlas = new TextureAtlas(Gdx.files.internal("mobs/chort/animation_chort.atlas"));
                mIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
                mWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));
                mAttack = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("attack"));
                mDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));
                mIdle.setFrameDuration(FRAME_TIME);
                mWalk.setFrameDuration(FRAME_TIME);
                mAttack.setFrameDuration(FRAME_TIME);
                mDie.setFrameDuration(FRAME_TIME);

                this.chortBiteSound = Gdx.audio.newSound(Gdx.files.internal("music/chortBite.mp3"));
            } else if (mobType == "necromancer") {
                textureAtlas = new TextureAtlas(Gdx.files.internal("mobs/necromancer/necromancer.atlas"));

                mIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
                mWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));
//            mAttack = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("attack"));
                mDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));
                mIdle.setFrameDuration(FRAME_TIME);
                mWalk.setFrameDuration(FRAME_TIME);
//            mAttack.setFrameDuration(FRAME_TIME);
                mDie.setFrameDuration(FRAME_TIME);
            } else if (mobType == "bigDemon") {
                textureAtlas = new TextureAtlas(Gdx.files.internal("mobs/big_demon/big_demon.atlas"));

                mIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
                mWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));
                mAttack = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("attack"));
                mDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));
                mIdle.setFrameDuration(FRAME_TIME);
                mWalk.setFrameDuration(FRAME_TIME);
                mAttack.setFrameDuration(FRAME_TIME);
                mDie.setFrameDuration(FRAME_TIME);

                this.bigDemonBiteSound = Gdx.audio.newSound(Gdx.files.internal("music/bigdemon.mp3"));


            } else if (mobType == "pumpkin") {
                textureAtlas = new TextureAtlas(Gdx.files.internal("mobs/pumpkin/pumpkin.atlas"));

                mIdle = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("idle"));
                mWalk = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("walk"));
                mDie = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("die"));

                mIdle.setFrameDuration(FRAME_TIME);
                mWalk.setFrameDuration(FRAME_TIME);
                mDie.setFrameDuration(FRAME_TIME);

            }
        }



        @Override
        public void act(float delta) {
            super.act(delta);

            // Update the mob's behavior and attributes
            update(delta);

            // Update the damage display time
            if (damageDisplayTime > 0) {
                damageDisplayTime -= delta;
            }
        }


        public void takeDamage(int damage) {
            if (currentState == State.DEAD) {
                return; // If mob is already dead, ignore further damage
            }

            // Check if the mob will receive a critical hit
            boolean isCriticalHit = MathUtils.randomBoolean(0.4f); // 40% chance for a critical hit

            // Calculate the damage to be inflicted
            int inflictedDamage = isCriticalHit ? damage * 2 : damage;

            isCrit = isCriticalHit ? true : false;

            damageTaken = inflictedDamage;

            this.setCurrentHP(this.getCurrentHP() - inflictedDamage);

            // Set Mob as being attacked and start the attack timer
            isBeingAttacked = true;
            attackTimer = ATTACK_DURATION;

            // Set the damage display time
            damageDisplayTime = 0.15f; // Adjust the duration as needed

            // Check State of Mob when HP is 0
            if (this.getCurrentHP() <= 0) {
                // Mob is dead, invoke the callback listener
                if (mobDeathListener != null) {
                    mobDeathListener.onMobDeath();
                }
                this.currentState = State.DEAD;
            }
        }



        public void shoot(Character character) {
            if (!isMelee) {
                if (currentState == State.DEAD) {
                    return;
                }
                float bulletSpeed = 180;

                float velocityX;
                float velocityY = 0;

                if (!isLeft) {
                    velocityX = bulletSpeed;
                } else {
                    velocityX = -bulletSpeed;
                }

                if (mobType == "necromancer") {
                    Bullet bullet = new Bullet(getX(), getY(), velocityX, velocityY, movingRight, 0, damage, "bullet/shadow_ball.png", bulletSpeed);
                    // Calculate the angle towards the character
                    float angle = MathUtils.atan2(character.getPosition().y - getY(), character.getPosition().x - getX()) * MathUtils.radiansToDegrees;
                    bullet.setAngle(angle);
                    bullets.add(bullet);
                } else if (mobType == "pumpkin") {
                    Bullet bullet = new Bullet(getX(), getY(), velocityX, velocityY, movingRight, 0, damage, "bullet/pumpkin beam.png", bulletSpeed);
                    // Calculate the angle towards the character
                    float angle = MathUtils.atan2(character.getPosition().y - getY(), character.getPosition().x - getX()) * MathUtils.radiansToDegrees;
                    bullet.setAngle(angle);
                    bullets.add(bullet);
                }
            }
        }

        private int calculateMaxHealth(String mobType) {
            int maxHealth = 0;
            switch (mobType) {
                case "chort":
                    maxHealth = 10;
                    break;
                case "necromancer":
                    maxHealth = 7;
                    break;
                case "bigDemon":
                    maxHealth = 25;
                    break;
                case "pumpkin":
                    maxHealth = 12;
                    break;
            }
            return maxHealth;
        }

        public void update(float delta) {
            Gdx.app.log("STATE", this.getState().toString());
            stateTime += delta;

            if (currentState == State.DEAD) {
                if (!isMelee) {
                    bullets.clear();
                }
                return; // If mob is dead, no movement or actions are needed
            }

            // Check if the mob is colliding with any obstacles
            boolean isColliding = isColliding(getX(), getY());

            // Check the distance between the mob and the player
            float distanceToPlayerX = player.getPosition().x - getX();
            float distanceToPlayerY = player.getPosition().y - getY();
            float totalDistanceToPlayer = (float) Math.sqrt(distanceToPlayerX * distanceToPlayerX + distanceToPlayerY * distanceToPlayerY);


            // Calculate the distance threshold

            float biteRange = 28;

            if (isMelee && Math.abs(totalDistanceToPlayer) <= biteRange && !isColliding && biteCooldown <= 0.0f
            && player.getState() != Character.State.DEAD) {
                // Inflict damage to the character
                // Add any additional behavior here, such as playing a sound effect or triggering an animation
                bite(player);
                biteCooldown = BITE_COOLDOWN; // Reset the biteCooldown to the cooldown duration
            }

            float distanceThreshold = 16;

            // Check if the player is within the mob's radius and not colliding
            if (totalDistanceToPlayer <= radius && !isColliding && totalDistanceToPlayer > distanceThreshold) {
                // Move towards the player
                float directionX = distanceToPlayerX / totalDistanceToPlayer;
                float directionY = distanceToPlayerY / totalDistanceToPlayer;

                float distanceMovedX = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerX));
                float distanceMovedY = Math.min(movementSpeed * delta, Math.abs(distanceToPlayerY));

                // Check if moving towards the player would result in a collision
                if (!isColliding(getX() + distanceMovedX * directionX, getY() + distanceMovedY * directionY)
                        && !getBoundingRectangle().overlaps(player.getBeingAttackedRectangle())) {
                    setX(getX() + distanceMovedX * directionX);
                    setY(getY() + distanceMovedY * directionY);

                    // Perform the bite action when in range
                    if (isMelee && biteCooldown <= 0.0f && player.getState() != Character.State.DEAD) {
                        bite(player);
                        biteCooldown = BITE_COOLDOWN; // Reset the biteCooldown to the cooldown duration
                    }
                } else {
                    // Mob is about to hit a wall, stop moving towards the player
                    // You can add any necessary behavior here, such as changing direction or stopping completely
                }
                // Update movingRight based on the distance to the character
                movingRight = distanceToPlayerX > 0;
            } else {
                // Perform random left and right movement
                randomMovement(delta);
            }



            // shooting mob shoots at character if the character is within the shootingradius
            if (totalDistanceToPlayer <= shootingRadius && !isColliding && totalDistanceToPlayer > distanceThreshold && !isMelee) {
                if (!isMelee && shootingCooldown <= 0.0f && player.getState() != Character.State.DEAD) {
                    shoot(player);

                    if (mobType == "necromancer") {
                        shootingCooldown = SHOOTING_COOLDOWN_NECROMANCER;
                    }
                    if (mobType == "pumpkin") {
                        shootingCooldown = SHOOTING_COOLDOWN_PUMPKIN;
                    }
                }
            }
//
            // Update bullets
            if (!isMelee) {
                for (int i = bullets.size - 1; i >= 0; i--) {
                    Bullet bullet = bullets.get(i);
                    bullet.update(delta);

                    // Check for collisions with obstacles or player
                    float bulletRadius = bullet.getWidth() / 2; // Assuming the bullet object has a circular shape

                    // Calculate the center position of the bullet
                    float bulletCenterX = bullet.getPosition().x + bulletRadius;
                    float bulletCenterY = bullet.getPosition().y + bulletRadius;

                    // Check for collision with the player
                    float playerRadius = player.getWidth() / 2; // Assuming the player object has a circular shape
                    float playerCenterX = player.getPosition().x + playerRadius;
                    float playerCenterY = player.getPosition().y + playerRadius;

                    // Calculate the distance between the centers of the bullet and player
                    float distance = Vector2.dst(bulletCenterX, bulletCenterY, playerCenterX, playerCenterY) - 10;

                    if (distance < bulletRadius + playerRadius) {
                        player.takeDamage(bullet.getDamage());
                        bullets.removeIndex(i);
                        continue;
                    }

                    // Check for collision with obstacles
                    if (isColliding_b(bulletCenterX, bulletCenterY - 4) || isBulletOffScreen(bullet)) {
                        bullets.removeIndex(i);
                    }
                }

                if (shootingCooldown > 0.0f) {
                    shootingCooldown -= delta;
                }
            }

            // Update the bite cooldown
            if (isMelee) {
                if (biteCooldown > 0.0f) {
                    biteCooldown -= delta;
                } else {
                    currentState = movingRight ? State.WALKING_RIGHT : State.WALKING_LEFT;
                }
            }
        }

        private boolean isBulletOffScreen(Bullet bullet) {
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            float bulletWidth = bullet.getBulletTexture().getWidth();
            float bulletHeight = bullet.getBulletTexture().getHeight();

            float bulletX = bullet.getPosition().x;
            float bulletY = bullet.getPosition().y;

            return bulletX < -bulletWidth || bulletX > screenWidth || bulletY < -bulletHeight || bulletY > screenHeight;
        }

        public void bite(Character character) {
            // mob has to be a melee mob to bite people
            if (currentState != State.DEAD || character.getState() != Character.State.DEAD || isMelee) {
                float distanceToCharacterX = character.getPosition().x - getX();
                float distanceToCharacterY = character.getPosition().y - getY();
                float totalDistanceToCharacter = (float) Math.sqrt(distanceToCharacterX * distanceToCharacterX + distanceToCharacterY * distanceToCharacterY);

                float biteRange = 28;
                if (Math.abs(totalDistanceToCharacter) <= biteRange) {
                    // Inflict damage to the character
                    character.takeDamage(damage);

                    if (mobType == "chort") {
                        chortBiteSound.play();
                    }

                    if (mobType == "bigDemon") {
                        bigDemonBiteSound.play();
                    }
                    //  add any additional behavior here, such as playing a sound effect or triggering an animation
                    this.setState(State.ATTACKING);
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
            if (isBeingAttacked && getState() != State.DEAD) {
                // Set the color to red
                spriteBatch.setColor(Color.RED);
            }
            // Render the character at its current position
            float textureWidth = 32; // Set the desired texture width
            float textureHeight = 32; // Set the desired texture height

            if (mobType == "bigDemon") {
                textureWidth = 46;
                textureHeight = 49;
            }

            if (!movingRight) {
                spriteBatch.draw(currentFrame, getX() + textureWidth, getY(),
                        -textureWidth, textureHeight);
            } else {
                spriteBatch.draw(currentFrame, getX(), getY(), textureWidth, textureHeight);
            }

            // Render the damage text if the damage display time is positive
            if (damageDisplayTime > 0) {
                // Set the font scale for the damage text
                float fontScale = isCrit ? 0.85f : 0.65f; // Adjust the scale as needed
                // Set the color and scale of the font
                if (isCrit) {
                    damageFont.setColor(Color.YELLOW); // if crit then yellow
                } else {
                    damageFont.setColor(Color.RED);
                }
                damageFont.getData().setScale(fontScale);

                // Get the damage text dimensions
                GlyphLayout glyphLayout = new GlyphLayout(damageFont, "-" + damageTaken);

                float textWidth = glyphLayout.width;
                float textHeight = glyphLayout.height;

                // Calculate the position of the damage text
                float textX = getX() + (getWidth() - textWidth) / 2 + 16;
                float textY = getY() + getHeight() + textHeight + 32;

                // Draw the damage text using the font
                damageFont.draw(spriteBatch, "-" + damageTaken, textX, textY);


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

            // Render bullets
            if (!isMelee) {
                for (Bullet bullet : bullets) {
                    bullet.render(spriteBatch);
                }
            }
        }



        public boolean isColliding_b(float x, float y) {
            int tileXStart = (int) (x / tileMap.getTileWidth());
            int tileXEnd = (int) ((x) / tileMap.getTileWidth());
            int tileYStart = (int) (y / tileMap.getTileHeight());
            int tileYEnd = (int) ((y) / tileMap.getTileHeight());

            // Check collisions for each corner of the character
            boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
            boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
            boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
            boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

            // Return true if any corner collides with a boundary tile
            return topLeft || topRight || bottomLeft || bottomRight;
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

            // Return true if any corner collides with a boundary tile or if there is a collision with the character
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
        private void setState(State state) {
            this.previousState = this.currentState;
            this.currentState = state;
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

        public void dispose() {
            textureAtlas.dispose();
            damageFont.dispose();
            if (mobType == "chort") {
                chortBiteSound.dispose();
            }
            if (mobType == "bigDemon") {
                bigDemonBiteSound.dispose();
            }

        }
    }