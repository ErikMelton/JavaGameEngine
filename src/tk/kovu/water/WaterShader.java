package tk.kovu.water;

import org.lwjgl.util.vector.Matrix4f;

import tk.kovu.entities.Camera;
import tk.kovu.shaders.ShaderProgram;
import tk.kovu.utils.MathUtils;

public class WaterShader extends ShaderProgram
{
	private final static String VERTEX_FILE = "src/tk/kovu/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "src/tk/kovu/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;

	public WaterShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes()
	{
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations()
	{
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
	}

	public void loadProjectionMatrix(Matrix4f projection)
	{
		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = MathUtils.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadModelMatrix(Matrix4f modelMatrix)
	{
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
