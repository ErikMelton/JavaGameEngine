package tk.kovu.fontrendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import tk.kovu.shaders.ShaderProgram;

public class FontShader extends ShaderProgram
{

	private static final String VERTEX_FILE = "src/tk/kovu/fontrendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/tk/kovu/fontrendering/fontFragment.txt";

	private int location_color;
	private int location_translation;
	private int location_width;
	private int location_edge;
	private int location_borderWidth;
	private int location_borderEdge;
	private int location_offset;
	private int location_outlineColor;

	public FontShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations()
	{
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
		location_width = super.getUniformLocation("width");
		location_edge = super.getUniformLocation("edge");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdge = super.getUniformLocation("borderEdge");
		location_offset = super.getUniformLocation("offset");
		location_outlineColor = super.getUniformLocation("outlineColor");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadWidth(float width)
	{
		super.loadFloat(location_width, width);
	}
	
	protected void loadEdge(float edge)
	{
		super.loadFloat(location_borderEdge, edge);
	}
	
	protected void loadBoarderWidth(float borderWidth)
	{
		super.loadFloat(location_borderWidth, borderWidth);
	}
	
	protected void loadBorderEdge(float borderEdge)
	{
		super.loadFloat(location_borderEdge, borderEdge);
	}
	
	protected void loadOffset(Vector2f offset)
	{
		super.load2DVector(location_offset, offset);
	}
	
	protected void loadOutlineColor(Vector3f outlineColor)
	{
		super.loadVector(location_outlineColor, outlineColor);
	}
	
	protected void loadColour(Vector3f colour)
	{
		super.loadVector(location_color, colour);
	}

	protected void loadTranslation(Vector2f translation)
	{
		super.load2DVector(location_translation, translation);
	}
}
