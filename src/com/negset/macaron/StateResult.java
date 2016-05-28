package com.negset.macaron;

import java.text.DecimalFormat;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * リザルト画面の動作・描画を行うクラス.
 *
 * @author negset
 */
public class StateResult extends BasicGameState
{
	/** ゲーム画面のID */
	private int id;

	/** 背景画像 */
	private Image bg;
	/** 曲サムネイル画像 */
	private Image thumb;
	/** 曲名表示用の画像 */
	private Image title;
	/** アーティスト名表示用の画像 */
	private Image artist;
	/** オートプレイ使用時の警告画像 */
	private Image autoplayNotice;
	/** BGM */
	private Music bgm;

	/** 達成率表示の四捨五入 */
	private final DecimalFormat df = new DecimalFormat("##0.00");

	/** 描画用達成率(四捨五入あり) */
	private String achieve;
	/** 結果を表すコメント */
	private String comment;
	/** 結果を表すランク */
	private String rank;
	/** 新記録を更新したかどうか */
	private boolean isNewRecord;

	/**
	 * コンストラクタ
	 *
	 * @param id ゲーム画面のID
	 */
	StateResult(int id)
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
		bg = new Image("res\\result\\bg.png");
		autoplayNotice = new Image("res\\result\\autoplay_notice.png");
		bgm = new Music("res\\result\\bgm.ogg");
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
			bg.draw();

			title.draw(50, 60 - title.getHeight() / 2);
			artist.draw(70 + title.getWidth(), 60 - artist.getHeight() / 2);
			if (StatePlay.useAutoplay)
			{
				int w = autoplayNotice.getWidth();
				int h = autoplayNotice.getHeight();
				autoplayNotice.draw(725 - w, 60 - h / 2);
			}
			else if (isNewRecord)
			{
				// NewRecord描画
			}

			// 区切り線を描画する.
			g.setColor(Color.darkGray);
			g.drawLine(50, 90, 730, 90);

			// サムネイルを描画する.
			g.fillRect(75, 210, 280, 280);
			thumb.draw(80, 215);

			g.setFont(Drawer.fontR);
			g.drawString("SCORE", 460, 200);
			g.drawString("ACHIEVE", 460, 300);
			g.drawString("RANK", 460, 400);

			g.setFont(Drawer.fontM);
			g.setColor(Color.orange);
			int w = g.getFont().getWidth(comment);
			g.drawString(comment, 400 - w / 2, 110);
			g.drawString(String.valueOf(Score.getNewScore()), 480, 230);
			g.drawString(achieve + "%", 480, 330);
			g.drawString(rank, 480, 430);

			// FPSを描画する.
			Drawer.drawFps(gc.getFPS(), g);
		}
		else
		{
			Loading.draw(g);
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
			// キーの状態を取得する.
			Key.load();
			// Enterで選曲画面に移行する.
			if (Key.isPressed(Key.ENTER))
			{
				Drawer.playSE(Drawer.SE_ENTER);
				bgm.stop();
				game.enterState(Main.STATE_SELECT,
						new FadeOutTransition(), new FadeInTransition());
			}
		}
		else
		{
			Loading.loadResorce();
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
		thumb = new Image(StateSelect.getMbpPath() + "\\thumbnail.png");
		title = new Image(StateSelect.getMbpPath() + "\\title.png");
		artist = new Image(StateSelect.getMbpPath() + "\\artist.png");
		bgm.loop();

		isNewRecord = Score.isNewRecord(Beatmap.getTitle());
		achieve = df.format(Score.getNewAchieve());
		if (Score.getNewCleared())
		{
			comment = "CLEAR!!";
		}
		else
		{
			comment = "FAILED...";
		}
		rank = Score.getRank(Score.getNewAchieve());
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