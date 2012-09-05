package me.battleship.view;

import me.battleship.Playground;
import me.battleship.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A thread rendering the game
 * 
 * @author Manuel Vögele
 */
public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
	/** Indicates how many px are needed for 1 dp **/
	private final float dp = getContext().getResources().getDisplayMetrics().density;

	/** Indicates whether the thread can draw on the surface or not **/
	private volatile boolean canDraw = false;

	/** Indicates whether the view was initialized - it may not be drawn before */
	private volatile boolean initialized = false;

	/** The screen to draw in **/
	private volatile Rect screen;

	/** The area the gird should be drawn in **/
	private volatile Rect drawArea;

	/** The pos for the large playground **/
	private volatile Rect playgroundLarge;

	/** The pos for the small playground **/
	private volatile Rect playgroundSmall;

	/** The own playground **/
	private volatile Playground ownPlayground;

	/** The opponents playground **/
	private volatile Playground opponentPlayground;

	@SuppressWarnings("javadoc")
	public GameView(Context context)
	{
		super(context);
		getHolder().addCallback(this);
	}

	@SuppressWarnings("javadoc")
	public GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
	}

	@SuppressWarnings("javadoc")
	public GameView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
	}

	/**
	 * Initializes the {@link GameView}
	 * 
	 * @param ownPlayground
	 *           the own playground
	 * @param opponentPlayground
	 *           the opponents playground
	 */
	@SuppressWarnings("hiding")
	public void initialize(Playground ownPlayground, Playground opponentPlayground)
	{
		this.ownPlayground = ownPlayground;
		this.opponentPlayground = opponentPlayground;
		initialized = true;
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (Thread.interrupted())
				return;
			draw();
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				// Nothing to do
			}
		}
	}

	/**
	 * Draws the view
	 * 
	 * @throws IllegalStateException
	 *            if the view is not initialized
	 */
	public synchronized void draw() throws IllegalStateException
	{
		if (!initialized)
		{
			throw new IllegalStateException("The view is not initialized");
		}
		if (!canDraw)
		{
			return;
		}
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas(screen);
		Bitmap water = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.water);
		for (int y = 0;y < screen.height();y += water.getHeight())
		{
			for (int x = 0;x < screen.width();x += water.getWidth())
			{
				canvas.drawBitmap(water, x, y, null);
			}
		}
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		drawGrid(canvas, playgroundLarge, Playground.SIZE, Playground.SIZE, paint);
		drawGrid(canvas, playgroundSmall, Playground.SIZE, Playground.SIZE, paint);
		holder.unlockCanvasAndPost(canvas);
	}

	/**
	 * Draws a grid
	 * 
	 * @param canvas
	 *           the canvas to draw on
	 * @param grid
	 *           the grid to draw
	 * @param horizontalCells
	 *           no of horizontal cells
	 * @param verticalCells
	 *           no of vertical cells
	 * @param paint
	 *           the paint
	 */
	private static void drawGrid(Canvas canvas, Rect grid, int horizontalCells, int verticalCells, Paint paint)
	{
		if (horizontalCells <= 0 || verticalCells <= 0)
		{
			throw new IllegalArgumentException("horizontalCells and verticalCells must not be null");
		}
		for (int i = 0;i <= horizontalCells;i++)
		{
			float x = grid.left + grid.width() / (float) horizontalCells * i;
			canvas.drawLine(x, grid.top, x, grid.bottom, paint);
		}
		for (int i = 0;i <= horizontalCells;i++)
		{
			float y = grid.top + grid.height() / (float) verticalCells * i;
			canvas.drawLine(grid.left, y, grid.right, y, paint);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		screen = new Rect(0, 0, width, height);
		int border = dpToPx(8);
		drawArea = new Rect(border, border, width - border, height - border);
		final int smallFieldSize = dpToPx(5);
		int oneDP = dpToPx(1);
		int size = (smallFieldSize + oneDP) * Playground.SIZE + oneDP + border;
		playgroundSmall = new Rect(border, border, size, size);
		final int largeFieldMarginTop = dpToPx(5);
		final int largeFieldSize = width - 2 * border;
		playgroundLarge = new Rect(border, size + largeFieldMarginTop, border + largeFieldSize, size + largeFieldMarginTop + largeFieldSize);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		canDraw = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		canDraw = false;
	}

	/**
	 * Converts a dp value to a px value
	 * 
	 * @param tDP
	 *           the dp value
	 * @return the px value
	 */
	private int dpToPx(float tDP)
	{
		return Math.round(tDP * dp);
	}
}
