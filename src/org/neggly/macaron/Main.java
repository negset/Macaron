package org.neggly.macaron;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;

/**
 * メインクラス.
 * ウィンドウの生成及びゲーム画面の管理を行う.
 *
 * @author negset
 */
public class Main extends StateBasedGame
{
    /**
     * バージョン情報
     */
    public static final String version = "Beta 0.6";

    /**
     * ゲーム画面管理用定数
     */
    public static final int STATE_SELECT = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_RESULT = 2;

    /**
     * コンストラクタ
     *
     * @param title ゲーム名
     */
    public Main(String title)
    {
        super(title);
        this.addState(new StateSelect(STATE_SELECT));
        this.addState(new StatePlay(STATE_PLAY));
        this.addState(new StateResult(STATE_RESULT));
    }

    /**
     * 各ゲーム画面を初期化する.
     */
    @Override
    public void initStatesList(GameContainer gc) throws SlickException
    {
        LoadingList.setDeferredLoading(true);
        this.getState(STATE_SELECT).init(gc, this);
        this.getState(STATE_PLAY).init(gc, this);
        this.getState(STATE_RESULT).init(gc, this);
        Drawer.load();
        Key.init(gc);
    }

    /**
     * ウィンドウの生成を行う.
     */
    public static void main(String[] args)
    {
        Main game = new Main("Macaron " + version);
        AppGameContainer app = null;
        Config.read();
        try
        {
            app = new AppGameContainer(game);
            app.setDisplayMode(800, 600, Config.fullscreen);
            app.setTargetFrameRate(Config.framerate);
            app.setShowFPS(false);
            app.setAlwaysRender(true);
            app.setIcon("res\\system\\icon.png");
            app.start();
        }
        catch (SlickException e)
        {
            System.err.println("起動エラー");
        }
        finally
        {
            app.destroy();
        }
    }
}
