package me.battleship.manager;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * A class wrapping the {@link BitmapFactory} that caches the created
 * {@link Bitmap Bitmaps}.
 * 
 * @author Manuel Vögele
 */
public class BitmapManager
{
	/** The cached bitmaps **/
	private static Map<Resource, Bitmap> bitmaps = new HashMap<Resource, Bitmap>();

	/**
	 * Returns the bitmap for the specified resource and id
	 * 
	 * @param resources
	 *           the resource
	 * @param id
	 *           the id
	 * @return the bitmap
	 */
	public static Bitmap getBitmap(Resources resources, int id)
	{
		Resource resource = new Resource(resources, id);
		if (bitmaps.containsKey(resource))
		{
			return bitmaps.get(resource);
		}
		Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
		bitmaps.put(resource, bitmap);
		return bitmap;
	}

	/**
	 * A container storing the android Resources and a resource id
	 * 
	 * @author Manuel Vögele
	 */
	private static class Resource
	{
		/** The android resources **/
		private Resources resources;

		/** The resource id **/
		private int id;

		/**
		 * Instantiates a new {@link Resource}
		 * 
		 * @param resources
		 *           the android resources
		 * @param id
		 *           the resource id
		 */
		public Resource(Resources resources, int id)
		{
			this.resources = resources;
			this.id = id;
		}

		/**
		 * Returns the android resources
		 * 
		 * @return the android resources
		 */
		public Resources getResources()
		{
			return resources;
		}

		/**
		 * Returns the resource id
		 * 
		 * @return the resource id
		 */
		public int getId()
		{
			return id;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			if (!(o instanceof Resource))
				return false;
			Resource res = (Resource) o;
			return res.getId() == this.id && res.getResources() == this.resources;
		}

		@Override
		public int hashCode()
		{
			return resources.hashCode() + id;
		}
	}
}