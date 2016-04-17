package com.negset.macaron;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * プレイ画面の動作・描画を行うクラス.
 *
 * @author negset
 */
public class StatePlay extends BasicGameState
{
	/** ゲーム画面のID */
	private int id;
	/** ゲーム画面移行用 */
	private StateBasedGame game;
	/** 前のループにかかった時間(単位:ミリ秒) */
	private int delta;

	/** オブジェクトを管理する */
	private ObjectPool objectpool;
	/** 背景ライン画像 */
	private Image line;
	/** 上部バー背景画像 */
	private Image bar;
	/** ポーズメニュー背景画像 */
	private Image pause;
	/** ポーズメニューカーソル画像 */
	private Image cursor;
	/** コンボ数表示用の数字画像 */
	private Image[] comboNum = new Image[10];
	/** オートプレイ表記用の画像 */
	private Image[] autoplayIcon = new Image[2];
	/** 再生する曲 */
	private Music track;

	/** シーン管理 */
	private int scene;
	/** プレイ中 */
	private static final int PLAYING = 0;
	/** ポーズ中 */
	private static final int PAUSING = 1;
	/** プレイ終了後 */
	private static final int AFTER_PLAY = 2;

	/** プレイ開始からの経過時間 */
	private static long passTime;
	/** プレイ開始時刻 */
	private long startTime;
	/** 小節開始時刻 */
	private float msrStartTime;
	/** 曲再生開始時刻 */
	private long trackStartTime;
	/** プレイ終了時刻 */
	private long endTime;

	/** 曲のBPM */
	private float bpm;
	/** ノーツを生成してから判定枠に来るまでの時間(単位:ミリ秒) */
	private int scroll;
	private final int defaultScroll = 1200;

	/** 小節数のカウント */
	private int bmDataCnt;
	/** 曲が再生されたか否かのフラグ */
	private boolean trackStarted;
	/** オートプレイであるか否かのフラグ */
	public static boolean autoplay;
	/** オートプレイを使用したか否かのフラグ */
	public static boolean useAutoplay;

	/** コンボ数のカウント */
	private static int comboCnt;
	/** 最大コンボ数 */
	private static int maxCombo;
	/** ノーツ判定結果のカウント */
	private static int[] judgementCnt;
	/** 称号 */
	private int badge;
	/** ポーズ時のカーソルの位置 */
	private int cursorPos;

	/**
	 * コンストラクタ
	 *
	 * @param id ゲーム画面のID
	 */
	StatePlay(int id)
	{
		this.id = id;
	}

	/**
	 * ゲーム画面の初期化.
	 * リソースファイルの読み込み等を行う.
	 * 起動時に一度だけ呼ばれる.
	 */
	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException
	{
		this.game = game;

		line = new Image("res\\play\\line.png");
		bar = new Image("res\\play\\bar.png");
		pause = new Image("res\\play\\pause_menu.png");
		cursor = new Image("res\\play\\pause_cursor.png");
		Image img = new Image("res\\play\\combo_num.png");
		SpriteSheet ss =
				new SpriteSheet(img, img.getWidth()/10, img.getHeight());
		for (int i = 0; i < 10; i++)
		{
			comboNum[i] = ss.getSubImage(i, 0);
		}
		Image img2 = new Image("res\\play\\autoplay_icon.png");
		SpriteSheet ss2 =
				new SpriteSheet(img2, img2.getWidth(), img2.getHeight()/2);
		for (int i = 0; i < 2; i++)
		{
			autoplayIcon[i] = ss2.getSubImage(0, i);
		}

		autoplay = false;
		judgementCnt = new int[6];
	}

	/**
	 * 描画処理を行う.
	 * 1ループにつき1回呼ばれる.
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException
	{
		if (Loading.isFinished())
		{
			line.draw();

			if (autoplay)
			{
				autoplayIcon[0].drawCentered(400, 175);
			}
			else if (useAutoplay)
			{
				autoplayIcon[1].drawCentered(400, 175);
			}

			// シーンに応じて処理を分岐する.
			switch (scene)
			{
				case PLAYING:
					drawCombo();
					break;
				case AFTER_PLAY:
					renderAfterPlay(g);
					break;
			}

			// ゲームオブジェクトの動作・描画を行う.
			objectpool.doAllObject(g, delta);

			bar.draw();
			g.setColor(Color.white);
			g.setFont(Drawer.fontS);
			g.drawString(String.valueOf(judgementCnt[0]), 110, 1);
			g.drawString(String.valueOf(judgementCnt[1]), 310, 1);
			g.drawString(String.valueOf(judgementCnt[2]), 510, 1);
			g.drawString(String.valueOf(judgementCnt[3]), 710, 1);

			// ポーズ中は描画する.
			if (scene == PAUSING)
			{
				renderPausing();
			}

			// FPSを描画する.
			Drawer.drawFps(gc.getFPS(), g);
		}
		else
		{
			Loading.draw(g);
		}
	}

	/**
	 * ポーズ中に行う描画処理.
	 */
	private void renderPausing()
	{
		pause.drawCentered(400, 300);
		cursor.drawCentered(400, 268 + 60*cursorPos);
	}

	/**
	 * プレイ終了後に行う描画処理.
	 * @param g 描画先
	 */
	private void renderAfterPlay(Graphics g)
	{
		// プレイ終了後2秒間はコンボ表示のまま
		if (System.currentTimeMillis() < endTime + 2000)
		{
			drawCombo();
		}
		// プレイ終了2秒後以降はAP・FCの表示を行う.
		else
		{
			String s;
			switch (badge)
			{
				case Score.BADGE_AP:
					s = "ALL PERFECT";
					g.setColor(Color.orange);
					g.setFont(Drawer.fontM);
					int w1 = g.getFont().getWidth(s);
					g.drawString(s, 400 - w1/2, 250);
					break;

				case Score.BADGE_FC:
					s = "FULL COMBO";
					g.setColor(Color.orange);
					g.setFont(Drawer.fontM);
					int w2 = g.getFont().getWidth(s);
					g.drawString(s, 400 - w2/2, 250);
					break;

				default:
					drawCombo();
			}
		}
	}

	/**
	 * コンボ数の描画を行う.
	 */
	private void drawCombo()
	{
		if (comboCnt == 0) return;

		int n = String.valueOf(comboCnt).length();
		int x = 400 - (n-1)*comboNum[0].getWidth()/2;
		for (int i = 0; i < n; i++)
		{
			String s = String.valueOf(comboCnt).substring(i, i+1);
			comboNum[Integer.parseInt(s)].drawCentered(x, 300);
			x += comboNum[0].getWidth();
		}
	}

	/**
	 * 動作を規定する.
	 * 1ループにつき1回呼ばれる.
	 */
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		if (Loading.isFinished())
		{
			this.delta = delta;

			// キーの状態を取得する.
			Key.load();

			// シーンに応じて分岐
			switch (scene)
			{
				case PLAYING:
					updatePlaying(gc);
					break;
				case PAUSING:
					updatePausing();
					break;
				case AFTER_PLAY:
					updateAfterPlay();
					break;
			}

			// ノーツの判定を行う.
			ObjectPool.judgeNotes();
		}
		else
		{
			Loading.loadResorce();
		}
	}

	/**
	 * プレイ中の動作を規定する.
	 *
	 * @param gc ウィンドウのフォーカス判定取得先
	 */
	private void updatePlaying(GameContainer gc)
	{
		// 開始時刻が未設定なら設定する.
		if (startTime == -1)
		{
			startTime = System.currentTimeMillis();
		}

		// 経過時間を更新する.
		passTime = System.currentTimeMillis() - startTime;

		// 曲を再生する.
		if (!trackStarted)
		{
			if (passTime >= trackStartTime)
			{
				track.play();
				trackStarted = true;
			}
		}

		// 小節を生成する.
		long t = (long) (msrStartTime - scroll);
		if (passTime >= t)
		{
			if (Beatmap.getBmdata(bmDataCnt).equals("end"))
			{
				scene = AFTER_PLAY;
				track.fade(3000, 0, false);
				endTime = System.currentTimeMillis();
			}
			else
			{
				ObjectPool.createMeasure(t, bpm,
						scroll, Beatmap.getBmdata(bmDataCnt));
				msrStartTime += 240000 / bpm;
				bmDataCnt++;

				checkCommand();
			}
		}

		if (autoplay && !useAutoplay)
		{
			useAutoplay = true;
		}

		/*
		 *  Enterが押される,
		 *  または自動ポーズ機能がONの状態でウィンドウのフォーカスが外れると,
		 *  ポーズ画面に移行する.
		 */
		if (Key.isPressed(Key.ENTER) ||
				(Config.autoPause && !gc.hasFocus()))
		{
			scene = PAUSING;
			if (trackStarted)
			{
				track.pause();
			}
		}
	}

	/**
	 * ポーズ中の動作を規定する.
	 */
	private void updatePausing()
	{
		if (Key.isPressed(Key.UP))
		{
			if (cursorPos > 0)
			{
				cursorPos--;
			}
		}
		else if (Key.isPressed(Key.DOWN))
		{
			if (cursorPos < 2)
			{
				cursorPos++;
			}
		}
		else if (Key.isPressed(Key.ENTER))
		{
			switch (cursorPos)
			{
				// プレイを続ける.
				case 0:
					scene = PLAYING;
					startTime = System.currentTimeMillis() - passTime;
					if (trackStarted)
					{
						track.setPosition(passTime / 1000);
						track.resume();
					}
					break;
				// 初めからやり直す.
				case 1:
					game.enterState(Main.STATE_PLAY,
							new FadeOutTransition(), new FadeInTransition());
					break;
				// 選曲画面に戻る.
				case 2:
					game.enterState(Main.STATE_SELECT,
							new FadeOutTransition(), new FadeInTransition());
					break;
			}
		}
	}

	/**
	 * プレイ終了後の動作を規定する.
	 */
	private void updateAfterPlay()
	{
		// 経過時間を更新する.
		passTime = System.currentTimeMillis() - startTime;

		// 称号が未設定なら設定する.
		if (badge == -1)
		{
			Score.setNewScore(judgementCnt);
			badge = Score.getNewBadge();
		}

		// プレイ終了6秒後にリザルト画面に移行する.
		if (System.currentTimeMillis() > endTime + 6000)
		{
			track.stop();
			game.enterState(Main.STATE_RESULT,
					new FadeOutTransition(), new FadeInTransition());
		}
	}

	/**
	 * フィールドの初期化等を行う.
	 * プレイ画面に移行した時に1度だけ呼ばれる.
	 */
	@Override
	public void enter(GameContainer gc, StateBasedGame game)
			throws SlickException
	{
		objectpool = new ObjectPool();
		track = new Music(StateSelect.mbpPath + "\\track.ogg");

		Beatmap.readDefine(StateSelect.mbpPath + "\\define.ini");
		Beatmap.readBeatmap(StateSelect.mbpPath + "\\beatmap.txt");

		scene = PLAYING;
		scroll = defaultScroll;
		bpm = Beatmap.getBpm();
		bmDataCnt = 0;
		trackStarted = false;
		useAutoplay = false;
		comboCnt = 0;
		maxCombo = 0;
		for (int i = 0; i < judgementCnt.length; i++)
		{
			judgementCnt[i] = 0;
		}
		badge = -1;
		cursorPos = 0;

		checkCommand();

		startTime = -1;
		int offset = Beatmap.getOffset();
		// オフセットが正の値なら小節の再生開始を遅らせる.
		if (offset > 0)
		{
			msrStartTime = offset + scroll;
			trackStartTime = scroll;
		}
		// オフセットが負の値なら曲の再生開始を遅らせる.
		else
		{
			msrStartTime = scroll;
			trackStartTime = -offset + scroll;
		}
	}

	/**
	 * 譜面内の命令を読み取る.
	 */
	private void checkCommand()
	{
		String bmdata;
		while ((bmdata=Beatmap.getBmdata(bmDataCnt)).startsWith("#"))
		{
			if (bmdata.startsWith("#BPM:"))
			{
				bpm = Float.parseFloat(bmdata.substring(5));
			}
			else if (bmdata.startsWith("#SCROLL:"))
			{
				float f = Float.parseFloat(bmdata.substring(8));
				scroll = (int)(defaultScroll / f);
			}

			bmDataCnt++;
		}
	}

	/**
	 * コンボ数をカウントする.
	 *
	 * @param judgement ノーツの判定結果
	 */
	public static void countCombo(int judgement)
	{
		if (judgement == 3)
		{
			comboCnt = 0;
		}
		else
		{
			comboCnt++;
			if (maxCombo < comboCnt)
			{
				maxCombo = comboCnt;
			}
		}
		judgementCnt[judgement]++;
	}

	/**
	 * プレイ開始からの経過時間を返す.
	 *
	 * @return 経過時間
	 */
	public static long getPassTime()
	{
		return passTime;
	}

	/**
	 * ゲーム画面のIDを返す.
	 *
	 * @return ゲーム画面のID
	 */
	@Override
	public int getID()
	{
		return id;
	}
}