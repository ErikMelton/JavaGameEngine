
package tk.kovu.utils;

/*
 * OpenSimplex Noise sample class.
 */

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OpenSimplexNoiseGenerator
{
	private int width = 512;
	private int height = 512;
	private double featureSize = 24;
	private long seed = 52964135465l;

	public OpenSimplexNoiseGenerator(int width, int height, double featureSize, long seed)
	{
		this.width = width;
		this.height = height;
		this.featureSize = featureSize;
		this.seed = seed;
	}

	public BufferedImage generateHeightMapOpenSimplex() throws IOException
	{
		List<Integer> map1 = new ArrayList<Integer>();
		List<Integer> map2 = new ArrayList<Integer>();
		List<Integer> map3 = new ArrayList<Integer>();
		
		for (int i = 0; i < 2; i++)
		{
			OpenSimplexNoise noise = new OpenSimplexNoise(seed);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					double value = noise.eval(x / featureSize, y / featureSize, 0.0);
					int rgb = 0x010101 * (int) ((value + 1) * 135.5);
					rgb *= .5;

					image.setRGB(x, y, rgb);
					
					if(i == 0)
					{
						map1.add(rgb);
					}
					else
					{
						map2.add(rgb);
					}
				}
			}
			ImageIO.write(image, "png", new File("res/heightmap" + i + ".png"));
		}
		for(int i = 0; i < 512 * 512; i++)
		{
			map3.add(map1.get(i) + map2.get(i));
		}
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				i.setRGB(x, y, map3.get(x * 512 + y));
			}
		}
		ImageIO.write(i, "PNG", new File("res/heightmap3.png"));
		return i;
	}
}