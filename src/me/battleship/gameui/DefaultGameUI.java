package me.battleship.gameui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.battleship.Orientation;
import me.battleship.PlaceableShip;
import me.battleship.Playground;
import me.battleship.PlaygroundField;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.manager.BitmapManager;
import me.battleship.services.interfaces.GameServiceConnectedListener;
import me.battleship.ui.Button;
import me.battleship.utils.RectUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
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
public class DefaultGameUI extends GameUI implements Runnable, OnTouchListener, SurfaceHolder.Callback
{
	/** Indicates how many px are needed for 1 dp */
	private float dp;

	/** Indicates whether the thread can draw on the surface or not */
	private boolean canDraw = false;

	/** The screen to draw in */
	private Rect screen;

	/** The pos for the large playground */
	private Rect playgroundLarge;

	/** The pos for the small playground */
	private Rect playgroundSmall;

	/** The black area in the bottom of the screen */
	private Rect bottomArea;

	/** The own playground */
	private Playground ownPlayground;

	/** The opponents playground */
	private Playground opponentPlayground;

	/** The own ships */
	private List<Ship> ownShips;

	/** The ships of the opponent */
	private List<Ship> opponentShips;

	/** The accept button */
	private Button acceptButton;

	/** The view to render the surface to */
	private SurfaceView surfaceView;

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

	/**
	 * Indicates whether the grabbed ship has been moved since it has been grabbed
	 */
	private volatile boolean grabbedShipMoved;
	
	/**
	 * Indicates whether the grabbed ship was on the playground before grabbing
	 */
	private volatile boolean grabbedShipWasOnPlayground;
	
	/**
	 * The Thread responsible for drawing
	 */
	private Thread drawThread;

	@SuppressWarnings("javadoc")
	public DefaultGameUI(Context context, GameServiceConnectedListener listener)
	{
		super(context, listener);
		dp = context.getResources().getDisplayMetrics().density;
		surfaceView = new SurfaceView(getContext());
		surfaceView.getHolder().addCallback(this);
	}

	@Override
	public void onStart()
	{
		drawThread = new Thread(this);
		if (gameService != null)
			drawThread.start();
	}

	@Override
	public void onStop()
	{
		drawThread.interrupt();
		drawThread = null;
	}

	@Override
	public View getView()
	{
		return surfaceView;
	}

	@Override
	public void onGameServiceConnected()
	{
		ownShips = gameService.getOwnShips();
		if (gameService.isInPlacementPhase())
		{
			List<Ship> tmpShips = new ArrayList<Ship>(ownShips.size());
			for (Ship ship : ownShips)
			{
				tmpShips.add(new PlaceableShip(ship));
			}
			ownShips.clear();
			ownShips.addAll(tmpShips);
		}
		opponentShips = gameService.getOpponentShips();
		ownPlayground = gameService.getOwnPlayground();
		opponentPlayground = gameService.getOpponentPlayground();
		surfaceView.setOnTouchListener(this);
		if (drawThread != null && !drawThread.isAlive())
			drawThread.start();
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
		if (!canDraw)
		{
			return;
		}
		SurfaceHolder holder = surfaceView.getHolder();
		Canvas canvas = holder.lockCanvas(screen);
		Bitmap water = BitmapManager.getBitmap(getContext().getResources(), R.drawable.background);
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
		drawFieldMarks(canvas, pos, playground, ships, context);
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
		double fieldsize = getFieldsize(playgroundPos);
		Rect pos = getShipDrawRectangle(ship, playgroundPos);
		if (ship.getOrientation() == Orientation.VERTICAL)
		{
			canvas.drawBitmap(image, null, pos, null);
		}
		else
		{
			canvas.save();
			canvas.rotate(90, (int)(pos.left + fieldsize / 2), (int) (pos.top + fieldsize / 2));
			canvas.translate(0, (int) (-(ship.getSize() - 1) * fieldsize));
			canvas.drawBitmap(image, null, pos, null);
			canvas.restore();
		}
	}

	/**
	 * Draws the marks (such as hit, missed, invalid position for ship) on the
	 * playground
	 * 
	 * @param canvas
	 *           the canvas to draw on
	 * @param playgroundPos
	 *           the position of the playground
	 * @param playground
	 *           the playground
	 * @param ships
	 *           the ships on the playground
	 * @param context
	 *           the context
	 */
	private static void drawFieldMarks(Canvas canvas, Rect playgroundPos, Playground playground, Collection<Ship> ships, Context context)
	{
		double fieldsize = getFieldsize(playgroundPos);
		Set<Point> invalidFields = getInvalidFields(ships);
		for (Point point : invalidFields)
		{
			int left = (int) (point.x * fieldsize) + playgroundPos.left;
			int top = (int) (point.y * fieldsize) + playgroundPos.top;
			int right = left + (int) fieldsize;
			int bottom = top + (int) fieldsize;
			canvas.save();
			canvas.clipRect(left, top, right, bottom);
			canvas.drawARGB(150, 255, 0, 0);
			canvas.restore();
		}

		for (int y = 0;y < Playground.SIZE;y++)
		{
			for (int x = 0;x < Playground.SIZE;x++)
			{
				PlaygroundField field = playground.getField(x, y);
				if (field.isHit())
				{
					int resource = (field.isShip() ? R.drawable.hit : R.drawable.water);
					int left = (int) (x * fieldsize) + playgroundPos.left + 1;
					int top = (int) (y * fieldsize) + playgroundPos.top + 1;
					int right = left + (int) fieldsize;
					int bottom = top + (int) fieldsize;
					Rect rect = new Rect(left, top, right, bottom);
					Bitmap image = BitmapManager.getBitmap(context.getResources(), resource);
					canvas.drawBitmap(image, null, rect, null);
				}
			}
		}
	}

	/**
	 * Returns the fields on which the specified ships overlap
	 * 
	 * @param ships
	 *           the ships
	 * @return the fields on which the specified ships overlap
	 */
	private static Set<Point> getInvalidFields(Collection<Ship> ships)
	{
		// TODO Move to GameService
		Set<Point> fields = new HashSet<Point>();
		for (Ship ship : ships)
		{
			if (ship instanceof PlaceableShip && !((PlaceableShip) ship).isOnPlayground())
			{
				continue;
			}
			for (Ship ship2 : ships)
			{
				if (ship == ship2)
					continue;
				if (ship2 instanceof PlaceableShip && !((PlaceableShip) ship2).isOnPlayground())
					continue;
				Rect rect = RectUtils.getIntersection(ship.getRect(), ship2.getRect());
				if (rect != null)
				{
					if (ship.getOrientation() == Orientation.HORIZONTAL)
					{
						for (int x = rect.left;x <= rect.right;x++)
						{
							fields.add(new Point(x, rect.top));
						}
					}
					else
					{
						for (int y = rect.top;y <= rect.bottom;y++)
						{
							fields.add(new Point(rect.left, y));
						}
					}
				}
			}
		}
		return fields;
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
	private static Rect getShipDrawRectangle(Ship ship, Rect playgroundPos)
	{
		double fieldsize = getFieldsize(playgroundPos);
		int left, top, right, bottom;
		if (ship instanceof PlaceableShip && !((PlaceableShip) ship).isOnPlayground())
		{
			PlaceableShip pship = (PlaceableShip) ship;
			left = pship.getDrawX() + 2;
			top = pship.getDrawY() + 1;
		}
		else
		{
			left = (int) (ship.getX() * fieldsize + playgroundPos.left + 1);
			top = (int) (ship.getY() * fieldsize + playgroundPos.top + 1);
		}
		right = (int) (left + fieldsize);
		bottom = (int) (top + ship.getSize() * fieldsize - 1);
		return new Rect(left, top, right, bottom - 1);
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
	 * @param startOrientation
	 *           the orientation
	 */
	private void rearangePlaceableShip(PlaceableShip ship, int startX, int startY, Orientation startOrientation)
	{
		ship.setStartOrientation(startOrientation);
		ship.setStartPos(startX, startY);
		if (ship.isOnPlayground())
		{
			Rect rect = getShipDrawRectangle(ship, playgroundLarge);
			ship.setDrawPos(rect.left, rect.top);
		}
		else
		{
			ship.setOrientation(startOrientation);
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

		if (gameService.isInPlacementPhase())
		{
			int halfFieldsize = Math.round(fieldsize / 2);
			int oneAndAHalfFieldsize = Math.round(fieldsize * 1.5f);
			int threeAndAHalfFieldsize = Math.round(fieldsize * 3.5f);
			int aircraftCarrierLeft = bottomArea.left + halfFieldsize;
			int aircraftCarrierRight = aircraftCarrierLeft + Math.round(5 * fieldsize);
			int submarineLeft = bottomArea.right - threeAndAHalfFieldsize;
			Iterator<Ship> iterator = ownShips.iterator();
			Ship ship = iterator.next();
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
		double fieldsize = getFieldsize(playgroundLarge);
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
							right = left + (int) (ship.getSize() * fieldsize);
							bottom = top + (int) fieldsize;
						}
						else
						{
							right = left + (int) fieldsize;
							bottom = top + (int) (ship.getSize() * fieldsize);
						}
						Rect rect = new Rect(left, top, right, bottom);
						if (rect.contains(x, y))
						{
							grabbedShipWasOnPlayground = pShip.isOnPlayground();
							pShip.setOnPlayground(false);
							grabbedShip = pShip;
							grabX = x - left;
							grabY = y - top;
							grabbedShipMoved = false;
							return true;
						}
					}
				}
			break;
			case MotionEvent.ACTION_MOVE:
				if (grabbedShip != null)
				{
					grabbedShip.setDrawPos(Math.round(event.getX() - grabX), Math.round(event.getY() - grabY));
					grabbedShipMoved = true;
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (grabbedShip != null)
				{
					if (!grabbedShipMoved && grabbedShipWasOnPlayground)
					{
						int grabOffsetX = (int) ((float) grabX / playgroundLarge.width() * Playground.SIZE);
						int grabOffsetY = (int) ((float) grabY / playgroundLarge.height() * Playground.SIZE);
						int xpos = grabbedShip.getLastX();
						int ypos = grabbedShip.getLastY();
						
						// This will rotate the ship around the touched field
						xpos += grabOffsetX - grabOffsetY;
						ypos += grabOffsetY - grabOffsetX;
						
						Orientation oldOrientation = grabbedShip.getOrientation();
						grabbedShip.setOrientation(oldOrientation == Orientation.HORIZONTAL ? Orientation.VERTICAL : Orientation.HORIZONTAL);

						if (xpos < 0)
							xpos = 0;
						else if (grabbedShip.getOrientation() == Orientation.HORIZONTAL)
						{
							if (xpos + grabbedShip.getSize() - 1 > Playground.SIZE)
								xpos = Playground.SIZE - grabbedShip.getSize();
						}
						else if (xpos > Playground.SIZE - 1)
							xpos = Playground.SIZE - 1;

						if (ypos < 0)
							ypos = 0;
						else if (grabbedShip.getOrientation() == Orientation.VERTICAL)
						{
							if (ypos + grabbedShip.getSize() - 1 > Playground.SIZE)
								ypos = Playground.SIZE - grabbedShip.getSize();
						}
						else if (ypos > Playground.SIZE - 1)
							ypos = Playground.SIZE - 1;

						grabbedShip.setPos(xpos, ypos);
						grabbedShip.setOnPlayground(true);
						Rect pos = getShipDrawRectangle(grabbedShip, playgroundLarge);
						grabbedShip.setDrawPos(pos.left, pos.top);
					}
					else
					{
						Rect snappingRect = new Rect(playgroundLarge.left - (int) fieldsize / 2, playgroundLarge.top - (int) fieldsize / 2, playgroundLarge.right, playgroundLarge.bottom);
						if (snappingRect.contains(grabbedShip.getDrawX(), grabbedShip.getDrawY()))
						{
							int xpos = Math.round(((float) grabbedShip.getDrawX() - playgroundLarge.left) / playgroundLarge.width() * Playground.SIZE);
							int ypos = Math.round(((float) grabbedShip.getDrawY() - playgroundLarge.top) / playgroundLarge.height() * Playground.SIZE);
							grabbedShip.setPos(xpos, ypos);
							Rect rect = grabbedShip.getRect();
							if (rect.left < 0 || rect.top < 0 || rect.right >= Playground.SIZE || rect.bottom >= Playground.SIZE)
							{
								grabbedShip.setOrientation(grabbedShip.getStartOrientation());
								grabbedShip.setDrawPos(grabbedShip.getStartX(), grabbedShip.getStartY());
								grabbedShip.setOnPlayground(false);
							}
							else
							{
								Rect pos = getShipDrawRectangle(new Ship(grabbedShip), playgroundLarge);
								grabbedShip.setDrawPos(pos.left, pos.top);
								grabbedShip.setOnPlayground(true);
							}
						}
						else
						{
							grabbedShip.setOrientation(grabbedShip.getStartOrientation());
							grabbedShip.setDrawPos(grabbedShip.getStartX(), grabbedShip.getStartY());
							grabbedShip.setOnPlayground(false);
						}
					}
					grabbedShip = null;
					for (Ship ship : ownShips)
					{
						PlaceableShip pShip = (PlaceableShip) ship;
						if (!pShip.isOnPlayground())
						{
							setAcceptButtonVisible(false);
							return true;
						}
					}
					if (getInvalidFields(ownShips).size() > 0)
					{
						setAcceptButtonVisible(false);
						return true;
					}
					setAcceptButtonVisible(true);
					return true;
				}
			break;
		}
		return false;
	}
	
	/**
	 * Calculates the field size for the specified playground
	 * 
	 * @param playground
	 *           the playground
	 * @return the field size
	 */
	private static double getFieldsize(Rect playground)
	{
		return (playground.right - playground.left) / Playground.SIZE + 0.5;
	}
}
