package kaps;

import kaps.level.*;
import kaps.level.board.Board;
import kaps.level.board.Pops;
import kaps.level.board.Tile;
import kaps.level.board.TileColor;
import kaps.level.board.caps.Capsule;
import kaps.level.board.caps.Gelule;
import kaps.level.board.caps.Look;
import kaps.level.board.germ.Germ;
import kaps.level.board.germ.Type;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Window extends JPanel {
	private static int sidePanelMargin, sidePanelWidth;
  private static int windowWidth, windowHeight;
  private static int boardWidth, boardHeight;
	private static int topMargin, sideMargin;
	private static int bottomPanelHeight;
  private static int tileSize;
  private static int fontSize;
	private static int gelBox;
	private static Font font;
	private int animFrame = 0;

	private final JFrame frame;
  private final Germ template;
  private final TileColor mainColor;
	private static Controller ctrl;
	private static Level level;
	private static Board board;

  Window(int height, Level level) {
  	if (height < 200) throw new IllegalArgumentException("   Invalid height: " + height + "\n Window's height must be higher than 200 px.");

  	Window.level = level;
  	board = level.board();
  	ctrl = new Controller(level);
  	mainColor = board.randomColor();
  	template = new Germ(mainColor, Type.Basic);

  	// dimensions
		windowHeight = height*3/4;
		windowWidth = windowHeight*3/4;

		bottomPanelHeight = windowHeight/10;
	  tileSize = (windowHeight - bottomPanelHeight) / (level.boardHeight() + 2);
	  fontSize = tileSize;
	  topMargin = tileSize/2;
	  sideMargin = tileSize/2;
	  boardWidth = level.boardWidth() * tileSize;
	  boardHeight = level.boardHeight() * tileSize;
	  
	  sidePanelMargin = boardWidth + 2*sideMargin;
	  sidePanelWidth = windowWidth - sidePanelMargin;
	  gelBox = windowHeight/8;

	  // font
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("data/fonts/Gotham.ttf"));
		} catch (IOException |FontFormatException e) { e.printStackTrace(); }

		// jPanel
		frame = new JFrame("KAPS");
		frame.setSize(windowWidth, windowHeight + 35);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.addKeyListener(ctrl);

		frame.add(this);
		frame.revalidate();
		frame.setVisible(true);
  }

	private boolean isPaused() {
  	return ctrl.isPaused();
	}
	private int shake() {
  	return (animFrame == 0 ? 0 : 1) * (animFrame%2 == 0 ? -1 : 1);
	}

	private void updateLevel() {
		level.update(this);
	}
	private void endLevel() {
		frame.removeKeyListener(ctrl);
		paintImmediately();
		waitMilliseconds(2000);
	}
	private void playSound(String name) {
		level.playSound(name);
	}
	private void resetAnim() {
		animFrame = 0;
	}
	void run() {
		do {
			if (isPaused()) continue;

			updateLevel();
			repaint();
		} while (!level.isOver());

		playSound(level.remainingGerms() == 0 ? "cleared" : "game_over");
		endLevel();
	}

	public static void waitMilliseconds(int ms) {
		Timer timer = new Timer();
		while ((timer.value())*1000 < ms) {
			continue;
		}
	}
	public void paintImmediately() {
		super.paintImmediately(0, 0, windowWidth, windowHeight);
	}

	// anim
	public void dropAllAnim() { dropAllAnim(100); }
	public void dropAllAnim(int speed) {
		boolean dropped = false;
		for (int x = 0; x < level.boardWidth(); x++) {
			for (int y = 1; y < level.boardHeight(); y++) {
				if (board.get(y,x) != null && board.get(y,x) instanceof Capsule && board.get(y-1,x) == null) {
					if (board.get(y,x).getLook() == Look.Left && board.get(y,x+1).getLook() == Look.Right) {    // side niggas
						if (board.get(y-1,x+1) != null) continue;
						board.dropOne(y, x+1);
					}
					else if (board.get(y,x).getLook() == Look.Right && board.get(y,x-1).getLook() == Look.Left) {
						if (board.get(y-1,x-1) != null) continue;
						board.dropOne(y, x-1);
					}
					board.dropOne(y, x);
					dropped = true;
				}
			}
		}
		paintImmediately();
		waitMilliseconds(speed);
		if (dropped) dropAllAnim(speed*3/4);
	}
	public Pops popTilesAnim() {
  	paintImmediately();
		ctrl.playSound("plop" + Math.min(level.combo(), 4));
		Pops pops = new Pops(board.colors());

		for (int frame = 0; frame < 8; frame++) {
			for (Coord co : board.tiles()) {
				Tile tile = board.get(co);
				if (tile.popping) {
					tile.timer = frame;
					paintImmediately();

					if (frame == 7) {
						pops.add(tile.color);
						level.popTile(co);
					}
				}
			}
			waitMilliseconds(50);
		}
		return pops;
	}
	public void germAttacksAnim() {
		paintImmediately();
		ctrl.playSound("virus");

		for (int frame = 0; frame < 8; frame++) {
			for (Coord tile : board.germs()) {
				Germ germ = (Germ) board.get(tile);
				if (!germ.isAttacking()) continue;

				germ.timer = frame;
				paintImmediately();

				if (frame == 7) {
					germ.stopsAttacking();
					germ.resetTimer();
				}
			}
			waitMilliseconds(25);
		}
	}
	public void paintCapsAnim(Coord tile, TileColor color) {
		paintImmediately();
		playSound("paint" + Op.randInt(0, 2));

		board.get(tile).attack(Attack.Paint);
		board.get(tile).color = color;

		for (int frame = 0; frame < 8; frame++) {
			animFrame = frame;
			board.get(tile).timer = frame;

			paintImmediately();
			waitMilliseconds(50);
		}
		board.get(tile).resetTimer();
	}
	public void sliceTileAnim(Coord tile, Sidekick sdk) {
		sliceTileAnim( new ArrayList<>(Collections.singletonList(tile)), sdk );
	}
	public void sliceTileAnim(ArrayList<Coord> tiles, Sidekick sdk) {
  	sliceTileAnim(tiles, sdk.atkType(), sdk.dmg());
	}
	public void sliceTileAnim(ArrayList<Coord> tiles) {
  	sliceTileAnim(tiles, Attack.Slice, 1);
	}
	public void sliceTileAnim(ArrayList<Coord> tiles, Attack atk, int damage) {
  	if (tiles.size() == 0) return;
		paintImmediately();
		playSound(atk.toString() + Op.randInt(0, 1));
		ArrayList<Coord> toPop = new ArrayList<>();

		// init
		for (Coord tile: tiles) {
			if (board.get(tile) == null) continue;

			board.get(tile).attack = atk;
			board.startsPopping(tile);
			toPop.add(tile);
		}
		// anim
		for (int frame = 0; frame < 8; frame++) {
			for (Coord tile : toPop) {
				animFrame = frame;
				board.get(tile).timer = frame;
			}
			paintImmediately();
			waitMilliseconds(frame == 0 ? 200 : 50);
		}
		// supp
		for (Coord tile: toPop) {
			board.get(tile).resetTimer();
			for (int i = 0; i < damage; i++) level.popTile(tile);
		}
		resetAnim();
	}

	// draw all
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawRect(g, 0, 0, windowWidth, windowHeight, new Color(50, 50, 50));
		drawSidePanel(g);
		drawBottomPanel(g);

		// board
		drawBoard(g);
		drawTimer(g);
		drawEffects(g);
		// gelule
		if (level.gelule() != null) {
			drawPreview(g);
			drawGelule(g, level.gelule());
		}
		// override
		drawPause(g);
		drawMute(g);
	}

	// parts
	private void drawRemainingGerms(Graphics g) {
		drawRect(g, 0, windowHeight - bottomPanelHeight, windowWidth, bottomPanelHeight, new Color(90, 90, 90));
		drawGerm(g, template, 10, windowHeight - bottomPanelHeight +10, bottomPanelHeight - 20);
		drawText(g, level.remainingGerms() + "", bottomPanelHeight -5 , windowHeight - 15, fontSize, new Color(255, 255, 255), template.color.rgba());
	}
	private void drawBottomPanel(Graphics g) {
		drawRemainingGerms(g);
		drawScore(g);
		drawText(g, level.name(), windowWidth - 90, windowHeight - 20, fontSize*3/4, -2, new Color(120, 120, 120), new Color(75, 75, 75));
  }
	private void drawSidekicks(Graphics g) {
  	for (int i = 0; i < level.nbSidekicks(); i++) {
			drawSidekick(g, level.getSidekick(i), i);
		}
	}
	private void drawSidePanel(Graphics g) {
		drawRect(g, sidePanelMargin, 0, sidePanelWidth, windowHeight + 10, mainColor.rgba(100));
		drawRect(g, sidePanelMargin, 260, sidePanelWidth, 120, new Color(0, 0, 0, 50));

		drawNext(g);
		drawHold(g);
		drawSidekicks(g);
		for (int i = 0; i < level.nbSidekicks(); i++) {
			if (level.getSidekick(i).isTriggered()) {
				drawRect(g, 0, 0, windowWidth, windowHeight, new Color(0, 0, 0, 100));
				drawSidekick(g, level.getSidekick(i), i);
				break;
			}
		}
		drawCombo(g);
	}
	private void drawPreview(Graphics g) {
		level.updatePreview();
		((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		drawGelule(g, level.preview());
		((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	private void drawEffects(Graphics g) {
		for (Sidekick sdk: level.getSidekicks()) {
			if (!sdk.isTriggered() || sdk.target() == null) continue;
			switch (sdk) {
				case Red:
					drawSlice(g, sdk.targetX(), -1, sdk.targetX(), board.height());
					break;
				case Jim:
					drawSlice(g, -1, sdk.targetY(), board.width(), sdk.targetY());
					break;
				case Xereth:
					drawSlice(g, sdk.targetX() - 8, sdk.targetY() - 8, sdk.targetX() - animFrame, sdk.targetY() - animFrame);
					drawSlice(g, sdk.targetX() - 8, sdk.targetY() + 8, sdk.targetX() - animFrame, sdk.targetY() + animFrame);
					drawSlice(g, sdk.targetX() + animFrame, sdk.targetY() + animFrame, sdk.targetX() + 8, sdk.targetY() + 8);
					drawSlice(g, sdk.targetX() + animFrame, sdk.targetY() - animFrame, sdk.targetX() + 8, sdk.targetY() - 8);
					break;
			}
		}
	}
	private void drawTimer(Graphics g) {
  	drawGauge(g, new Rectangle(sideMargin, topMargin + boardHeight + 10, boardWidth, 10), level.cooldown(), new Color(150, 150, 150));
  }
	private void drawScore(Graphics g) {
		drawText(g, "score", new Rectangle(windowWidth/ 2 - 100, windowHeight - bottomPanelHeight + 5, 200, 15), fontSize/2, new Color(70, 70, 70));
		drawText(g, level.score() +  "", new Rectangle(windowWidth/ 2 - 100, windowHeight - 35, 200, 30),
				fontSize*2/3, 3, new Color(255, 255, 255), mainColor.rgba());
	}
	private void drawBoard(Graphics g) {
		for (int y = 0; y < level.boardHeight(); y++) {
			for (int x = 0; x < level.boardWidth(); x++) {
				drawTile(g, y, x);
			}
		}
		for (Coord co : board.germs()) {
			drawGerm(g, co);
			Germ germ = (Germ) board.get(co);
			if (germ.hasCooldown() && !germ.popping) {
				Rectangle rect = new Rectangle(sideMargin + co.x()*tileSize + tileSize -10,
						topMargin + (level.boardHeight()-1 - co.y())*tileSize + tileSize -10, 15, 15);
				drawCooldown(g, rect, germ.cooldownGauge(), germ.color.rgba());
			}
		}
		for (Coord co : board.caps()) {
			drawCaps(g, co);
		}
	}
	private void drawCombo(Graphics g) {
  	if (level.combo() <= 1) return;
		drawText(g, level.combo() + "x COMBO!!", sidePanelMargin+15 + level.combo()*((int) (level.chrono()*100) %2), 400,
				fontSize/3, 2, new Color(255, 255, 255), new Color(199, 242, 76, 255));
	}
	private void drawPause(Graphics g) {
  	if (!isPaused()) return;
		drawRect(g, sideMargin, topMargin, boardWidth, boardHeight, new Color(0, 0, 0, 100));
		drawRect(g, sideMargin + boardWidth/2 - 20, topMargin + boardHeight/3 - 30, 15, 50, new Color(255, 255, 255));
		drawRect(g, sideMargin + boardWidth/2 + 5,  topMargin + boardHeight/3 - 30, 15, 50, new Color(255, 255, 255));
		drawText(g, "bah ?", new Rectangle(sideMargin, topMargin, boardWidth, boardHeight/3), fontSize/2, new Color(255, 255, 255));

		for (int i = 0; i < level.getSidekicks().size(); i++) {
				Sidekick sdk = level.getSidekicks().get(i);
				drawRect(g, sideMargin + tileSize/4, topMargin + boardHeight/2 + i*2*tileSize,
						boardWidth - tileSize/2, tileSize*2, sdk.getColor().rgba().darker().darker());
				g.drawImage(img(sdk.toPath((int) (level.chrono()*5) %4), tileSize),
						sideMargin + tileSize*3/4, topMargin + boardHeight/2 + tileSize/2 + i*2*tileSize, null);
				drawText(g, sdk.name(), sideMargin + tileSize*2, topMargin + boardHeight/2 + i*2*tileSize + fontSize/3, fontSize/3, sdk.getColor().rgba().brighter().brighter());

				String[] lines = sdk.getInfo().split("\\.");
				for (int l = 0; l < lines.length; l++) {
					drawText(g, lines[l], sideMargin + tileSize*2, topMargin + boardHeight/2 + i*2*tileSize + l*(fontSize/3) + tileSize, fontSize/3, new Color(255, 255, 255));
				}
			}

	}
	private void drawNext(Graphics g) {
		drawRect(g, sidePanelMargin +15, 15, gelBox, gelBox, new Color(75, 75, 75));
		g.setFont(font.deriveFont(Font.PLAIN, (float) fontSize/2));
		drawText(g, "NEXT", sidePanelMargin+15, 15 + gelBox + fontSize/2, fontSize/2, new Color(255, 255, 255), new Color(50, 50, 50, 50));
		drawGelule(g, level.next(), sidePanelMargin + 15 + gelBox/6, 15 + gelBox/3, gelBox/3);
	}
	private void drawHold(Graphics g) {
		drawRect(g, sidePanelMargin + 15, 30 + gelBox + fontSize/2, gelBox, gelBox, new Color(65, 65, 65));
		drawText(g, "HOLD", sidePanelMargin+15, (15 + gelBox + fontSize/2)*2, fontSize/2, new Color(255, 255, 255), new Color(50, 50, 50, 50));
		if (level.hold() != null) {
			drawGelule(g, level.hold(), sidePanelMargin + 15 + gelBox/6, 30 + gelBox/3 + gelBox + fontSize/2, gelBox/3);
		}
	}
	private void drawMute(Graphics g) {
	  g.drawImage(img("img/icons/" + (level.isMuted() ? "mute" : "sound") + ".png", sideMargin, topMargin), 0, 0, null);
	}

	// shapes
	private void drawCooldown(Graphics g, Rectangle rect, Gauge gauge, Color color) {
		g.setColor(new Color(255, 255, 255, ((gauge.value() <= 2 && (int) (level.cooldown().value()*10) %4 == 0) ? 220 : 150)));
		((Graphics2D) g).fill(new Ellipse2D.Double(rect.x, rect.y -1, rect.width, rect.height));
		drawText(g, (int) gauge.value() + "", rect, rect.height*3/4, new Color(255, 255, 255));
		g.setColor(color);
		for (int i = 0; i < 3; i++) {
			((Graphics2D) g).draw(new Arc2D.Double(rect.x +i, rect.y -1 +i, rect.width -2*i, rect.height -2*i, 90, 360*((gauge.value()/gauge.max()) -1), Arc2D.OPEN));
		}
	}
	private void drawGauge(Graphics g, Rectangle rect, Gauge gauge, Color color) {
		drawRect(g, rect.x, rect.y, rect.width, rect.height, new Color(255, 255, 255, 25));
		((Graphics2D) g).fill(new Arc2D.Double(rect.x + rect.width - rect.height/2.0, rect.y, rect.height, rect.height, 90, -180, Arc2D.OPEN));
		drawRect(g, rect.x, rect.y, (int) (rect.width*gauge.value()/gauge.max()) +1, rect.height, color);
		((Graphics2D) g).fill(new Arc2D.Double(rect.x - rect.height/2.0, rect.y, rect.height, rect.height, 90, 180, Arc2D.OPEN));
		((Graphics2D) g).fill(new Arc2D.Double(rect.x + (rect.width*gauge.value()/gauge.max()) - rect.height/2.0, rect.y, rect.height, rect.height, 90, -180, Arc2D.OPEN));
	}
	private void drawRect(Graphics g, int x, int y, int width, int height, Color color) {
  	g.setColor(color);
		g.fillRect(x, y, width, height);
	}
	private void drawText(Graphics g, String txt, int x, int y, int size, Color color) {
		g.setFont(font.deriveFont(Font.PLAIN, (float) size));
		g.setColor(color);
		g.drawString(txt, x, y);
	}
	private void drawText(Graphics g, String txt, int x, int y, int size, Color color, Color shade) {       // shade
		drawText(g, txt, x, y, size, 5, color, shade);
	}
	private void drawText(Graphics g, String txt, int x, int y, int size, int shift, Color color, Color shade) {       // sized shade
		drawText(g, txt, x, y+shift, size, shade);
		drawText(g, txt, x, y, size, color);
	}
	private void drawText(Graphics g, String txt, Rectangle rect, int size, int shift, Color color, Color shade) {
		drawText(g, txt, new Rectangle(rect.x, rect.y +shift, rect.width, rect.height), size, shade);
		drawText(g, txt, rect, size, color);
	}
	private void drawText(Graphics g, String txt, Rectangle rect, int size, Color color) {			// centered
		g.setFont(font.deriveFont(Font.PLAIN, (float) size));
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = rect.x + (rect.width - metrics.stringWidth(txt)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

		g.setColor(color);
		g.drawString(txt, x, y);
	}
	Image img(String path, int w) {
		return img(path, w, w);
	}
	Image img(String path, int w, int h) {
		Image srcImg = new ImageIcon(path).getImage();
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gg = resizedImg.createGraphics();

		gg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		gg.drawImage(srcImg, 0, 0, w, h, null);
		gg.dispose();

		return resizedImg;
	}

	// effects
	private void drawSlice(Graphics g, int x, int y, int xDest, int yDest) {
  	g.setColor(new Color(255, 255, 255, 150));
		for (int i = -2; i < 2; i++) {
			g.drawLine(sideMargin + x*tileSize + tileSize/2 +i + shake(), topMargin + (board.height()-1 -y)*tileSize + tileSize/2 + shake(),
					sideMargin + xDest*tileSize + tileSize/2 +i + shake(), topMargin + (board.height()-1 -yDest)*tileSize + tileSize/2 + shake());
			g.drawLine(sideMargin + x*tileSize + tileSize/2 + shake(), topMargin + (board.height()-1 -y)*tileSize + tileSize/2 +i + shake(),
					sideMargin + xDest*tileSize + tileSize/2 + shake(), topMargin + (board.height()-1 -yDest)*tileSize + tileSize/2 +i + shake());
		}
	}
	private void drawPoints(Graphics g, Tile tile, int x, int y) {
		drawText(g, "+" + tile.points, x + tileSize/3 - 2*((int) (level.chrono()*50) %2), y + tileSize/2,
				10 + tile.points /4, 2, new Color(255, 255, 255), (int) (level.chrono()*50) %2 == 0 ? tile.color.rgba() : tile.color.rgba().brighter());
	}
	private void drawEffect(Graphics g, Coord coords) {
		int x = sideMargin + coords.x()*tileSize,
				y = topMargin + (board.height()-1 -coords.y())*tileSize;
		g.drawImage(img("img/fx/" + board.get(coords).attack + "_" + board.get(coords).timer + ".png", tileSize),
			x - tileSize/4 + shake(), y - tileSize/4 + shake(), null);
	}

	// units
	private void drawGelule(Graphics g, Gelule gelule, int x, int y, int width) {
		drawCaps(g, gelule.main(), x, y, width);
		drawCaps(g, gelule.co(), x+width, y, width);
	}
	private void drawGelule(Graphics g, Gelule gelule) {
  	if (gelule == null) return;
		drawCaps(g, gelule.main(), sideMargin + gelule.mainX()*tileSize, topMargin + ((level.boardHeight()-1) - gelule.mainY()) *tileSize, tileSize);
		drawCaps(g, gelule.co(),   sideMargin + gelule.coX()*tileSize,   topMargin + ((level.boardHeight()-1) - gelule.coY())   *tileSize, tileSize);
  }
  /* fluid
	private void drawGelule(Graphics g) {
  	if (level.gelule() != null) {
			drawCaps(g, level.gelule().main(), sideMargin + level.gelule().mainX()*tileSize, topMargin + (int) ((level.timer()/level.cooldown())*tileSize) + (level.boardHeight()-2 - level.gelule().mainY()) *tileSize, tileSize);
			drawCaps(g, level.gelule().co(),   sideMargin + level.gelule().coX()*tileSize,   topMargin + (int) ((level.timer()/level.cooldown())*tileSize) + (level.boardHeight()-2 - level.gelule().coY())   *tileSize, tileSize);
		}
  }
  */

	private void drawTile(Graphics g, int y, int x) {
  	drawRect(g, sideMargin + x*tileSize + shake(), topMargin + (board.height()-1 -y)*tileSize + shake(), tileSize, tileSize,
				mainColor.rgba(y%2 == x%2 ? 35 : 50).brighter());
	}

	private void drawCaps(Graphics g, Capsule caps, int x, int y, int width) {
		g.drawImage(img(caps.toPath(), width), x, y, null);
		if (caps.popping) drawPoints(g, caps, x, y);
	}
	private void drawCaps(Graphics g, Coord coords) {
		drawCaps(g, (Capsule) board.get(coords), sideMargin + coords.x()*tileSize,
				topMargin + (int) ((level.boardHeight()-1 - coords.y())*tileSize), tileSize);
		if (board.get(coords).isAttacked()) drawEffect(g, coords);
	}

	private void drawGerm(Graphics g, Germ germ, int x, int y, int width) {
		int frame;

		if (germ.hasHP()) {
			frame = (int) (level.chrono() * (20 - 3 * germ.HP().max())) % (germ.HP().value() > 2 ? 4 : 8);
		} else {
			frame = (int) (level.chrono() * 10) % 8;
		}
		g.drawImage(img(germ.toPath(frame), width), x, y, null);

		if (germ.hasHP()) drawGauge(g, new Rectangle(x+5, y, width-10, 5), germ.HP(), new Color(255, 255, 255, 150));
		if (germ.popping) drawPoints(g, germ, x, y);
	}
	private void drawGerm(Graphics g, Coord coords) {
		drawGerm(g, (Germ) board.get(coords), sideMargin + coords.x()*tileSize, topMargin + (level.boardHeight()-1 - coords.y())*tileSize, tileSize);
		if (board.get(coords).isAttacked()) drawEffect(g, coords);
	}

	private void drawSidekick(Graphics g, Sidekick sdk, int index) {
		int x = sidePanelMargin + 5,
				y = (15 + gelBox + fontSize/2)*2 + tileSize + index*tileSize;
		if (sdk.hasCooldown()) {
			Rectangle rect = new Rectangle(x + tileSize + 10, y + tileSize/8, tileSize*3/4, tileSize*3/4);
			drawCooldown(g, rect, sdk.cooldown(), sdk.getColor().rgba().brighter());
		}
		else {
			drawText(g, "" + (int) sdk.gauge().value(), x + tileSize + 10, y + tileSize/4, tileSize/3, new Color(255, 255, 255, 200));
			drawText(g, "/" + (int) sdk.gauge().max(), x + tileSize*3/2 + 10, y + tileSize/4, tileSize/4, sdk.getColor().rgba());
			drawGauge(g, new Rectangle(x + tileSize + 10, y + tileSize/2 -5, sidePanelWidth*3/8, 10), sdk.gauge(), sdk.getColor().rgba());
		}
		g.drawImage(img(sdk.toPath((int) (level.chrono()*5) %4), tileSize), x, y, null);
	}
}
