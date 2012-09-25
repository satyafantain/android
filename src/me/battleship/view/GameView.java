package me.battleship.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.battleship.Orientation;
import me.battleship.PlaceableShip;
import me.battleship.Playground;
import me.battleship.PlaygroundField;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.manager.BitmapManager;
import me.battleship.ui.Button;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * A thread rendering the game
 * 
 * @author Manuel Vögele
 */
public class GameView extends SurfaceView implements Runnable, OnTouchListener, SurfaceHolder.Callback
{
	/** The log tag **/
	public final static String LOG_TAG = GameView.class.getSimpleName();

	/** Indicates how many px are needed for 1 dp **/
	private final float dp = getContext().getResources().getDisplayMetrics().density;

	/** Indicates whether the thread can draw on the surface or not **/
	private volatile boolean canDraw = false;

	/** Indicates whether the view was initialized - it may not be drawn before */
	private volatile boolean initialized = false;

	/** The screen to draw in **/
	private volatile Rect screen;

	/** The pos for the large playground **/
	private volatile Rect playgroundLarge;

	/** The pos for the small playground **/
	private volatile Rect playgroundSmall;

	/** The black area in the bottom of the screen **/
	private volatile Rect bottomArea;

	/** The own playground **/
	private volatile Playground ownPlayground;

	/** The opponents playground **/
	private volatile Playground opponentPlayground;

	/** The own ships **/
	private volatile List<Ship> ownShips;

	/** The ships of the opponent **/
	private volatile List<Ship> opponentShips;

	/** The accept button */
	private volatile Button acceptButton;

	/**
	 * The x position on which the ship was grabbed relative to the ships x
	 * position
	 **/
	private volatile int grabX;

	/**
	 * The y position on which the ship was grabbed relative to the ships y
	 * position
	 **/
	private volatile int grabY;

	/**
	 * The ship which is actually moved across the screen by the player - this is
	 * only needed while placing a ship
	 **/
	private volatile PlaceableShip grabbedShip;

	@SuppressWarnings("javadoc")
	public GameView(Context context)
	{
		super(context);
		getHolder().addCallback(this);
		setOnTouchListener(this);
	}

	@SuppressWarnings("javadoc")
	public GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
		setOnTouchListener(this);
	}

	@SuppressWarnings("javadoc")
	public GameView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
		setOnTouchListener(this);
	}

	/**
	 * Initializes the {@link GameView}
	 * 
	 * @param ownPlayground
	 *           the own playground
	 * @param opponentPlayground
	 *           the opponents playground
	 * @param ships
	 *           the own ships
	 */
	@SuppressWarnings("hiding")
	public void initialize(Playground ownPlayground, Playground opponentPlayground, List<Ship> ships)
	{
		this.ownPlayground = ownPlayground;
		this.opponentPlayground = opponentPlayground;
		ownShips = ships;
		// TODO Make real assignment
		opponentShips = new LinkedList<Ship>();
		initialized = true;
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (Thread.interrupted())
				return;
			try
			{
				draw();
			}
			catch (NullPointerException e)
			{
				if (!canDraw)
				{
					Log.i(LOG_TAG, "A null pointer exception was caught - was the surface destroyed?", e);
				}
				else
				{
					throw e;
				}
			}
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
		Bitmap water = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background);
		for (int y = 0;y < screen.height();y += water.getHeight())
		{
			for (int x = 0;x < screen.width();x += water.getWidth())
			{
				canvas.drawBitmap(water, x, y, null);
			}
		}
		Paint paint = new Paint();
		paint.setARGB(100, 0, 0, 0);
		canvas.drawRect(bottomArea, paint);
		drawButton(canvas, acceptButton, getContext());
		drawPlayground(canvas, opponentPlayground, playgroundSmall, opponentShips, getContext());
		drawPlayground(canvas, ownPlayground, playgroundLarge, ownShips, getContext());
		holder.unlockCanvasAndPost(canvas);
	}

	/**
	 * Draws the specified playground to the specified canvas at the position
	 * specified by pos
	 * 
	 * @param canvas
	 *           the canvas to draw on
	 * @param playground
	 *           the playground to draw
	 * @param pos
	 *           the position to draw the playground on
	 * @param ships
	 *           the ships to draw
	 * @param context
	 *           the context
	 */
	private static void drawPlayground(Canvas canvas, Playground playground, Rect pos, Collection<Ship> ships, Context context)
	{
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		drawGrid(canvas, pos, Playground.SIZE, Playground.SIZE, paint);
		for (Ship ship : ships)
		{
			drawShip(canvas, ship, pos, context);
		}
		for (int y = 0;y < Playground.SIZE;y++)
		{
			for (int x = 0;x < Playground.SIZE;x++)
			{
				PlaygroundField field = playground.getField(x, y);
				if (field.isHit())
				{
					int resource = (field.isShip() ? R.drawable.hit : R.drawable.water);
					int fieldsize = (pos.right - pos.left) / Playground.SIZE;
					int left = x * fieldsize + pos.left + 1;
					int top = y * fieldsize + pos.top + 1;
					int right = left + fieldsize;
					int bottom = top + fieldsize;
					Rect rect = new Rect(left, top, right, bottom);
					Bitmap image = BitmapFactory.decodeResource(context.getResources(), resource);
					canvas.drawBitmap(image, null, rect, null);
				}
			}
		}
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

	/**
	 * Draws a ship to the playground
	 * 
	 * @param canvas
	 *           the canvas to draw on
	 * @param ship
	 *           the ship to draw
	 * @param playgroundPos
	 *           the position of the playground
	 * @param context
	 *           the context
	 */
	private static void drawShip(Canvas canvas, Ship ship, Rect playgroundPos, Context context)
	{
		Bitmap image = BitmapManager.getBitmap(context.getResources(), ship.getDrawable());
		int fieldsize = (playgroundPos.right - playgroundPos.left) / Playground.SIZE;
		Rect pos = getShipRectangle(ship, playgroundPos);
		if (ship.getOrientation() == Orientation.VERTICAL)
		{
			canvas.drawBitmap(image, null, pos, null);
		}
		else
		{
			canvas.save();
			canvas.rotate(90, pos.left + fieldsize / 2, pos.top + fieldsize / 2);
			canvas.translate(0, -(ship.getSize() - 1) * fieldsize);
			canvas.drawBitmap(image, null, pos, null);
			canvas.restore();
		}
	}

	/**
	 * Draws a button. Does nothing if <code>null</code> is passed
	 * 
	 * @param canvas
	 *           the canvas to draw on
	 * 
	 * @param button
	 *           the button
	 * @param context
	 *           the context
	 */
	private static void drawButton(Canvas canvas, Button button, Context context)
	{
		if (button != null)
		{
			Bitmap image = BitmapManager.getBitmap(context.getResources(), button.getDrawable());
			canvas.drawBitmap(image, null, button.getLocation(), null);
		}
	}

	/**
	 * Returns the rectangle for the specified ship
	 * 
	 * @param ship
	 *           the ship
	 * @param playgroundPos
	 *           the position of the playground the ship is on
	 * @return the rectangle
	 */
	private static Rect getShipRectangle(Ship ship, Rect playgroundPos)
	{
		int fieldsize = (playgroundPos.right - playgroundPos.left) / Playground.SIZE;
		int left, top, right, bottom;
		if (ship instanceof PlaceableShip && !((PlaceableShip) ship).isOnPlayground())
		{
			PlaceableShip pship = (PlaceableShip) ship;
			left = pship.getDrawX();
			top = pship.getDrawY();
		}
		else
		{
			left = ship.getX() * fieldsize + playgroundPos.left + 1;
			top = ship.getY() * fieldsize + playgroundPos.top + 1;
		}
		right = left + fieldsize - 2;
		bottom = top + ship.getSize() * fieldsize - 2;
		return new Rect(left, top, right, bottom);
	}

	/**
	 * Moves the ship to its new position after a surface change if required
	 * 
	 * @param ship
	 *           the ship
	 * @param startX
	 *           the x position where the ship should start
	 * @param startY
	 *           the y position where the ship should start
	 * @param orientation
	 *           the orientation
	 */
	private static void rearangePlaceableShip(PlaceableShip ship, int startX, int startY, Orientation orientation)
	{
		ship.setStartPos(startX, startY);
		if (!ship.isOnPlayground())
		{
			ship.setOrientation(orientation);
			ship.setDrawPos(startX, startY);
		}
	}

	/**
	 * Sets the visibility of the accept button
	 * 
	 * @param visible
	 *           the visibility
	 */
	private void setAcceptButtonVisible(boolean visible)
	{
		if (visible)
		{
			float fieldsize = playgroundLarge.width() / Playground.SIZE;
			float halfFieldsize = fieldsize / 2;
			float doubleFieldsize = fieldsize * 2;
			int right = Math.round(bottomArea.right - halfFieldsize);
			int top = Math.round(bottomArea.top + halfFieldsize);
			int left = Math.round(right - doubleFieldsize);
			int bottom = Math.round(top + doubleFieldsize);
			acceptButton = new Button(new Rect(left, top, right, bottom), R.drawable.accept);
		}
		else
		{
			acceptButton = null;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		screen = new Rect(0, 0, width, height);
		int border = dpToPx(8);
		final int smallFieldSize = dpToPx(5);
		int oneDP = dpToPx(1);
		int size = (smallFieldSize + oneDP) * Playground.SIZE + oneDP + border;
		playgroundSmall = new Rect(border, border, size, size);

		float fieldsize = (height - playgroundSmall.bottom - 2 * border) / (Playground.SIZE + 3);
		int largeFieldSize = Math.round(fieldsize * Playground.SIZE);
		int spaceW = width - 2 * border;
		if (largeFieldSize > spaceW)
		{
			largeFieldSize = spaceW;
			fieldsize = largeFieldSize / Playground.SIZE;
		}
		playgroundLarge = new Rect(border, size + border, border + largeFieldSize, size + border + largeFieldSize);
		bottomArea = new Rect(0, playgroundLarge.bottom + border, width, height);

		Iterator<Ship> iterator = ownShips.iterator();
		Ship ship = iterator.next();
		if (ship instanceof PlaceableShip)
		{
			int halfFieldsize = Math.round(fieldsize / 2);
			int oneAndAHalfFieldsize = Math.round(fieldsize * 1.5f);
			int threeAndAHalfFieldsize = Math.round(fieldsize * 3.5f);
			int aircraftCarrierLeft = bottomArea.left + halfFieldsize;
			int aircraftCarrierRight = aircraftCarrierLeft + Math.round(5 * fieldsize);
			int submarineLeft = bottomArea.right - threeAndAHalfFieldsize;
			rearangePlaceableShip((PlaceableShip) ship, aircraftCarrierLeft, bottomArea.top + halfFieldsize, Orientation.HORIZONTAL);
			ship = iterator.next();
			rearangePlaceableShip((PlaceableShip) ship, bottomArea.left + Math.round(fieldsize), bottomArea.bottom - oneAndAHalfFieldsize, Orientation.HORIZONTAL);
			ship = iterator.next();
			rearangePlaceableShip((PlaceableShip) ship, submarineLeft, bottomArea.top + halfFieldsize, Orientation.HORIZONTAL);
			ship = iterator.next();
			rearangePlaceableShip((PlaceableShip) ship, submarineLeft, bottomArea.bottom - oneAndAHalfFieldsize, Orientation.HORIZONTAL);
			ship = iterator.next();
			rearangePlaceableShip((PlaceableShip) ship, Math.round(aircraftCarrierRight + (submarineLeft - aircraftCarrierRight) / 2 - fieldsize / 2), bottomArea.top + bottomArea.height() / 2 - Math.round(fieldsize), Orientation.VERTICAL);
		}
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

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int fieldsize = Math.round(playgroundLarge.width() / Playground.SIZE);
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				int x = Math.round(event.getX());
				int y = Math.round(event.getY());
				for (Ship ship : ownShips)
				{
					if (ship instanceof PlaceableShip)
					{
						PlaceableShip pShip = (PlaceableShip) ship;
						int left = pShip.getDrawX();
						int top = pShip.getDrawY();
						int right, bottom;
						if (ship.getOrientation() == Orientation.HORIZONTAL)
						{
							right = left + ship.getSize() * fieldsize;
							bottom = top + fieldsize;
						}
						else
						{
							right = left + fieldsize;
							bottom = top + ship.getSize() * fieldsize;
						}
						Rect rect = new Rect(left, top, right, bottom);
						if (rect.contains(x, y))
						{
							pShip.setOnPlayground(false);
							grabbedShip = pShip;
							grabX = x - left;
							grabY = y - top;
							return true;
						}
					}
				}
			break;
			case MotionEvent.ACTION_MOVE:
				if (grabbedShip != null)
				{
					grabbedShip.setDrawPos(Math.round(event.getX() - grabX), Math.round(event.getY() - grabY));
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (grabbedShip != null)
				{
					Rect snappingRect = new Rect(playgroundLarge.left - fieldsize / 2, playgroundLarge.top - fieldsize / 2, playgroundLarge.right, playgroundLarge.bottom);
					if (snappingRect.contains(grabbedShip.getDrawX(), grabbedShip.getDrawY()))
					{
						int xpos = Math.round(((float) grabbedShip.getDrawX() - playgroundLarge.left) / playgroundLarge.width() * Playground.SIZE);
						int ypos = Math.round(((float) grabbedShip.getDrawY() - playgroundLarge.top) / playgroundLarge.height() * Playground.SIZE);
						grabbedShip.setPos(xpos, ypos);
						Rect pos = getShipRectangle(new Ship(grabbedShip), playgroundLarge);
						grabbedShip.setDrawPos(pos.left, pos.top);
						grabbedShip.setOnPlayground(true);
					}
					else
					{
						grabbedShip.setDrawPos(grabbedShip.getStartX(), grabbedShip.getStartY());
						grabbedShip.setOnPlayground(false);
					}
					grabbedShip = null;
					for (Ship ship : ownShips)
					{
						PlaceableShip pShip = (PlaceableShip) ship;
						if (!pShip.isOnPlayground())
						{
							setAcceptButtonVisible(false);
							return false;
						}
					}
					setAcceptButtonVisible(true);
					return false;
				}
		}
		return false;
	}
}
