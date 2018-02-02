package tk.kovu.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import tk.kovu.models.TexturedModel;
import tk.kovu.renderengine.DisplayManager;
import tk.kovu.terrain.Terrain;

public class PlayerCamera extends Entity
{
	private static final float RUN_SPEED = 40;
	private static final float LR_SPEED = 40;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
		
	private boolean isInAir = false;
	
	private float currentSpeed = 0;
	private float currentLRSpeed = 0;
	private float upwardSpeed = 0;
	
	public PlayerCamera(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale)
	{
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(Terrain terrain)
	{
		checkInputs();
		
		float distanceUD = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float distanceLR = currentLRSpeed * DisplayManager.getFrameTimeSeconds();
		
		super.increasePosition(distanceLR, 0, distanceUD);
	
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		if(super.getPosition().y < terrainHeight) 
		{
			upwardSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	private void jump()
	{
		if(!isInAir) 
		{
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}

	}
	
	private void checkInputs()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			this.currentSpeed = RUN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			this.currentSpeed = -RUN_SPEED;
		}
		else
		{
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			this.currentLRSpeed = -LR_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			this.currentLRSpeed = LR_SPEED;
		}
		else
		{
			this.currentLRSpeed = 0;
		}
	}
}