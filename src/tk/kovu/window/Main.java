package tk.kovu.window;

import tk.kovu.entities.Camera;
import tk.kovu.entities.Entity;
import tk.kovu.entities.Light;
import tk.kovu.entities.PlayerCamera;
import tk.kovu.fontmeshcreator.FontType;
import tk.kovu.fontmeshcreator.GUIText;
import tk.kovu.fontrendering.TextMaster;
import tk.kovu.gui.GuiRenderer;
import tk.kovu.gui.GuiTexture;
import tk.kovu.models.TexturedModel;
import tk.kovu.renderengine.DisplayManager;
import tk.kovu.renderengine.Loader;
import tk.kovu.renderengine.MasterRenderer;
import tk.kovu.renderengine.ModelData;
import tk.kovu.renderengine.OBJLoader;
import tk.kovu.renderengine.RawModel;
import tk.kovu.renderengine.EntityRenderer;
import tk.kovu.shaders.StaticShader;
import tk.kovu.shaders.TerrainShader;
import tk.kovu.terrain.Terrain;
import tk.kovu.textures.ModelTexture;
import tk.kovu.textures.TerrainTexture;
import tk.kovu.textures.TerrainTexturePack;
import tk.kovu.utils.OpenSimplexNoiseGenerator;
import tk.kovu.water.WaterFrameBuffers;
import tk.kovu.water.WaterRenderer;
import tk.kovu.water.WaterShader;
import tk.kovu.water.WaterTile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Main 
{			
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private static List<WaterTile> waters = new ArrayList<WaterTile>();
	private static List<Light> lights = new ArrayList<Light>();
	private static List<Terrain> terrains = new ArrayList<Terrain>();
	
	private static Loader loader;
	
	private static MasterRenderer renderer;
	private static GuiRenderer guiRenderer;
	private static WaterRenderer waterRenderer;
	private static WaterShader waterShader;
	
	private static Terrain terrain;
	private static PlayerCamera player;	
	private static WaterFrameBuffers fbos;
	private static Camera camera;
	private static MousePicker picker;
	
	private static Entity lampTest;
	private static Light lightTest;
	
	public static void main(String[] args)
	{
		DisplayManager.createDisplay();
		
		loader = new Loader();	
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadFontTextureAtlas("ferdana"), new File("res/ferdana.fnt"));
		GUIText text = new GUIText("This is a test text!", 1, font, new Vector2f(0.5f, 0.5f), 0.5f, true);
		text.setColour(0, 0, 0);
		text.setWidth(0.5f);
		text.setEdge(0.1f);
		text.setBorderEdge(0.4f);
		text.setBorderWidth(0.5f);
		text.setOffset(new Vector2f(0.006f, 0.006f));
		text.setOutlineColor(new Vector3f(0.0f, 1.0f, 0.0f));
		renderer = new MasterRenderer(loader);

		loadTerrain();
		loadNonPlayerEntities();
		loadLights();		
		loadPlayerAndCam();
		loadGuis();						
		loadWater();
		
		picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		while(!Display.isCloseRequested())
		{	
			player.move(terrain);
			camera.move();
			
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if(terrainPoint != null)
			{
				lampTest.setPosition(terrainPoint);
				lightTest.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}
			
			renderer.renderScene(entities, terrains, lights, camera);
			picker.update();

			waterRenderer.render(waters, camera);
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
			
		}
		
		fbos.cleanUp();
		guiRenderer.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		TextMaster.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	private static void loadTerrain()
	{
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadGameTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadGameTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadGameTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadGameTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadGameTexture("blendMap"));

		Random r = new Random();
		OpenSimplexNoiseGenerator mGen = new OpenSimplexNoiseGenerator(512, 512, 60, r.nextLong());
		
		try
		{
			mGen.generateHeightMapOpenSimplex();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap3");
		terrains.add(terrain);
	}
	
	private static void loadGuis()
	{
		guiRenderer = new GuiRenderer(loader);

		//GuiTexture gui = new GuiTexture(loader.loadGameTexture("mf"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//GuiTexture gui2 = new GuiTexture(loader.loadGameTexture("mf"), new Vector2f(0.3f, 0.5f), new Vector2f(0.25f, 0.25f));
		// guis.add(gui);
	}
		
	private static void loadWater()
	{
		waterShader = new WaterShader();
		waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix());
		waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(75, -75, 0));
		fbos = new WaterFrameBuffers();
	}
	
	private static void loadNonPlayerEntities()
	{
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadGameTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		// TexturedModel fern = new TexturedModel(RAWMODEL, fernTextureAtlas);
		
		ModelData lampData = OBJLoader.loadOBJ("lamp");
		RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getIndices(), lampData.getNormals());
		TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadGameTexture("lamp")));
 
		entities.add(new Entity(lamp, new Vector3f(185, terrain.getHeightOfTerrain(185, -293), -293), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(370, terrain.getHeightOfTerrain(370, -300), -300), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(293, terrain.getHeightOfTerrain(293, -305), -305), 0, 0, 0, 1));
		
		lampTest = (new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));
		entities.add(lampTest);
	}	
	
	private static void loadLights()
	{
		// Maybe sort by distance to scene to remove lights that are too far away for render speed?
		lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.8f, 0.8f, 0.8f)));
		lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
		lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
		
		lightTest = (new Light(new Vector3f(293, 7, -305), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(lightTest);

	}
	
	private static void loadPlayerAndCam()
	{
		ModelData data = OBJLoader.loadOBJ("person");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getIndices(), data.getNormals());
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadGameTexture("playerTexture")));			
		player = new PlayerCamera(staticModel, new Vector3f(100, 10, -150), 0, 0, 0, 1);
		entities.add(player);
		
		camera = new Camera(player);
	}
}