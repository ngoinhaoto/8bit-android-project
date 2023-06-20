    package com.voider.game;

    import com.badlogic.gdx.graphics.g2d.Batch;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.MathUtils;
    import com.badlogic.gdx.scenes.scene2d.Actor;

    public class Mob extends Actor {
        private TileMap tileMap;
        private TextureRegion[] sprites;
        private String mobType;
        private float radius; // if player enters their radius, they will follow player
        private Character player;
        private float movementSpeed;

        private float distanceToMove;
        private boolean movingRight;

        public Mob(TileMap tileMap, TextureRegion[] sprites, String mobType, float radius, Character player) {
            this.tileMap = tileMap;
            this.sprites = sprites;
            this.mobType = mobType;
            this.radius = radius;
            this.player = player;
            setSize(sprites[0].getRegionWidth(), sprites[0].getRegionHeight());

            movementSpeed = 30;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            // Update the mob's behavior and attributes
            update(delta);
        }

        public void update(float delta) {
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
        }


        public void draw(SpriteBatch batch, float parentAlpha) {
            TextureRegion sprite = sprites[0];  // Default sprite

            // Retrieve the appropriate sprite based on mobType
            if (mobType.equals("Type1")) {
                sprite = sprites[1];
            } else if (mobType.equals("Type2")) {
                sprite = sprites[2];
            }

            batch.setColor(1, 1, 1, parentAlpha);  // Set the batch color with parent alpha
            batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
        }

        public boolean isColliding(float x, float y) {
            int tileXStart = (int) (x / tileMap.getTileWidth());
            int tileXEnd = (int) ((x + getWidth()) / tileMap.getTileWidth());
            int tileYStart = (int) (y / tileMap.getTileHeight());
            int tileYEnd = (int) ((y + getHeight()) / tileMap.getTileHeight());

            // Check collisions for each corner of the character
            boolean topLeft = tileMap.isBoundary(tileXStart, tileYEnd);
            boolean topRight = tileMap.isBoundary(tileXEnd, tileYEnd);
            boolean bottomLeft = tileMap.isBoundary(tileXStart, tileYStart);
            boolean bottomRight = tileMap.isBoundary(tileXEnd, tileYStart);

            // Return true if any corner collides with a boundary tile
            return topLeft || topRight || bottomLeft || bottomRight;
        }
    }