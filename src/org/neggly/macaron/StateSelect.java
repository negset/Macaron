package org.neggly.macaron;

import java.io.File;
import java.io.FilenameFilter;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 * 選曲画面の動作・描画を行うクラス.
 *
 * @author negset
 */
public class StateSelect extends BasicGameState
{
    /**
     * ゲーム画面のID
     */
    private int id;

    /**
     * シーン管理
     */
    private enum Scene
    {
        MUSIC, DIFFICULTY, SELECTED
    }

    private Scene scene;

    /**
     * 背景画像
     */
    private Image bg;
    /**
     * フレーム画像
     */
    private Image frame;
    /**
     * オートプレイ表記用の画像
     */
    private Image autoplayIcon;

    /**
     * mbpディレクトリ
     */
    private File mbpDir;
    /**
     * 譜面ディレクトリ
     */
    private File[] mbp;
    /**
     * 曲カード
     */
    private MusicCard[] musicCard;
    /**
     * 曲選択カーソルの位置
     */
    private int musicCsr;
    /**
     * 難易度選択カーソルの位置
     */
    private int difficultyCsr;
    /**
     * 選択された譜面ディレクトリのパス
     */
    private static String mbpPath;
    /**
     * 選択された譜面の難易度
     */
    private static Difficulty difficulty;

    /**
     * 背景アニメーション用のx座標
     */
    private float bgX1, bgX2;
    /**
     * カード選択アニメーション用のカウント
     */
    private float animeCnt;

    /**
     * コンストラクタ
     *
     * @param id ゲーム画面のID
     */
    StateSelect(int id)
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
        bg = new Image("res\\select\\bg.png");
        frame = new Image("res\\select\\frame.png");
        autoplayIcon = new Image("res\\select\\autoplay_icon.png");
        musicCsr = 0;
        difficultyCsr = 0;
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
            bg.draw(bgX1, 0);
            bg.draw(bgX2, 0);

            // カードを描画する.
            for (int i = 0; i < mbp.length; i++)
            {
                if (i != musicCsr)
                {
                    musicCard[i].draw(g);
                }
            }
            // カーソル位置のカードは最前面に描画する.
            if (mbp.length != 0)
            {
                musicCard[musicCsr].draw(g);
            }

            frame.draw();
            if (StatePlay.autoplay)
            {
                int w = autoplayIcon.getWidth();
                int h = autoplayIcon.getHeight();
                autoplayIcon.draw(720 - w / 2, 540 - h / 2);
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
     * 動作を規定する.
     * 1ループにつき1回呼ばれる.
     */
    @Override
    public void update(GameContainer gc, StateBasedGame game, int delta)
            throws SlickException
    {
        if (Loading.isFinished())
        {
            //背景画像をアニメーションさせる.
            bgX1 += 0.035 * delta;
            if (bgX1 >= 800)
            {
                bgX1 -= 800;
            }
            else if (bgX1 > 0)
            {
                bgX2 = bgX1 - 800;
            }
            else
            {
                bgX2 = bgX1 + 800;
            }

            switch (scene)
            {
                case MUSIC:
                    updateSelectMusic(delta);
                    break;

                case DIFFICULTY:
                    updateSelectLevel(delta);
                    break;

                case SELECTED:
                    game.enterState(Main.STATE_PLAY,
                            new FadeOutTransition(), new FadeInTransition());
            }
        }
        else
        {
            Loading.loadResorce();
        }
    }

    /**
     * 曲選択中の動作を規定する.
     */
    private void updateSelectMusic(int delta)
    {
        keySelectMusic();

        if (animeCnt > 0)
        {
            animeCnt -= delta;
            if (animeCnt < 0)
            {
                animeCnt = 0;
            }
        }

        // カードを移動させる.
        float cx = 400 - musicCsr * 210;
        for (int i = 0; i < mbp.length; i++)
        {
            musicCard[i].move(cx, (i == musicCsr), delta);
            cx += 210;
        }
    }

    /**
     * 難易度選択中の動作を規定する.
     */
    private void updateSelectLevel(int delta)
    {
        keySelectLevel();

        if (animeCnt < 100)
        {
            animeCnt += delta;
            if (animeCnt > 100)
            {
                animeCnt = 100;
            }
        }

        // カードを移動させる.
        for (int i = 0; i < mbp.length; i++)
        {
            musicCard[i].move(i - musicCsr, difficultyCsr, delta);
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
        scene = Scene.MUSIC;
        mbpDir = new File("mbp");
        if (!mbpDir.exists())
        {
            mbpDir.mkdir();
        }
        mbp = mbpDir.listFiles(new Filter());
        musicCard = new MusicCard[mbp.length];
        float cx = 400 - musicCsr * 210;
        for (int i = 0; i < mbp.length; i++)
        {
            musicCard[i] = new MusicCard(mbp[i].getPath(), cx);
            cx += 210;
        }
        animeCnt = 0;
    }

    /**
     * 曲選択中のキーの状態に応じた処理を行う.
     */
    private void keySelectMusic()
    {
        Key.load();
        if (Key.isPressed(Key.ENTER))
        {
            Drawer.playSE(Drawer.SE_ENTER);
            scene = Scene.DIFFICULTY;
        }
        else if (Key.isPressed(Key.LEFT))
        {
            if (musicCsr > 0)
            {
                Drawer.playSE(Drawer.SE_CURSOR);
                musicCsr--;
            }
        }
        else if (Key.isPressed(Key.RIGHT))
        {
            if (musicCsr < mbp.length - 1)
            {
                Drawer.playSE(Drawer.SE_CURSOR);
                musicCsr++;
            }
        }
    }

    /**
     * 難易度選択中のキーの状態に応じた処理を行う.
     */
    private void keySelectLevel()
    {
        Key.load();
        if (Key.isPressed(Key.ENTER))
        {
            Drawer.playSE(Drawer.SE_ENTER);
            mbpPath = mbp[musicCsr].getPath();
            switch (difficultyCsr)
            {
                case 0:
                    difficulty = Difficulty.EASY;
                    break;
                case 1:
                    difficulty = Difficulty.NORMAL;
                    break;
                case 2:
                    difficulty = Difficulty.HARD;
                    break;
                case 3:
                    difficulty = Difficulty.LUNATIC;
                    break;
            }
            scene = Scene.SELECTED;
        }
        else if (Key.isPressed(Key.BACK))
        {
            scene = Scene.MUSIC;
        }
        else if (Key.isPressed(Key.UP))
        {
            if (difficultyCsr > 0)
            {
                Drawer.playSE(Drawer.SE_CURSOR);
                difficultyCsr--;
            }
        }
        else if (Key.isPressed(Key.DOWN))
        {
            if (difficultyCsr < 3)
            {
                Drawer.playSE(Drawer.SE_CURSOR);
                difficultyCsr++;
            }
        }
    }

    /**
     * 選択されたmbpのパスを返す
     *
     * @return mbpのパス
     */
    public static String getMbpPath()
    {
        return mbpPath;
    }

    /**
     * 選択された難易度を返す
     *
     * @return 難易度
     */
    public static Difficulty getDifficulty()
    {
        return difficulty;
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

/**
 * 譜面ディレクトリを選別するクラス.
 *
 * @author negset
 */
class Filter implements FilenameFilter
{
    /**
     * ディレクトリが与えられた場合のみtrueを返す.
     */
    @Override
    public boolean accept(File file, String name)
    {
        if (file.isDirectory())
        {
            return true;
        }
        return false;
    }
}
