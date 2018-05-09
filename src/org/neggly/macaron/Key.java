package org.neggly.macaron;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Key
{
    /**
     * キーの状態の取得先
     */
    private static GameContainer gc;
    /**
     * キーコード
     */
    private static int[] keycode;
    /**
     * キーが押された直後か否かのフラグ
     */
    private static boolean[] keyPressed;
    /**
     * キーが長押し中か否かのフラグ
     */
    private static boolean[] keyDown;

    /**
     * キーの状態を返す際に使用する定数
     */
    public static final int ENTER = 6;
    public static final int BACK = 7;
    public static final int UP = 8;
    public static final int LEFT = 9;
    public static final int RIGHT = 10;
    public static final int DOWN = 11;

    /**
     * 初期化処理を行う.
     *
     * @param gc キーの状態の取得先
     */
    public static void init(GameContainer gc)
    {
        Key.gc = gc;

        keycode = new int[12];
        for (int i = 0; i < Config.keycode.length; i++)
        {
            keycode[i] = Config.keycode[i];
        }
        keycode[ENTER] = Input.KEY_ENTER;
        keycode[BACK] = Input.KEY_BACK;
        keycode[UP] = Input.KEY_UP;
        keycode[LEFT] = Input.KEY_LEFT;
        keycode[RIGHT] = Input.KEY_RIGHT;
        keycode[DOWN] = Input.KEY_DOWN;

        keyPressed = new boolean[keycode.length];
        keyDown = new boolean[keycode.length];
    }

    /**
     * キーの状態を取得する.
     */
    public static void load()
    {
        for (int i = 0; i < keyPressed.length; i++)
        {
            keyPressed[i] = gc.getInput().isKeyPressed(keycode[i]);
            keyDown[i] = gc.getInput().isKeyDown(keycode[i]);
        }

        // Escapeでゲーム終了
        if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE))
        {
            gc.exit();
        }
        // F1でオートプレイ切り替え
        else if (gc.getInput().isKeyPressed(Input.KEY_F1))
        {
            StatePlay.autoplay = !StatePlay.autoplay;
        }
        // F11でフルスクリーン切り替え
        else if (gc.getInput().isKeyPressed(Input.KEY_F11))
        {
            try
            {
                gc.setFullscreen(!gc.isFullscreen());
            }
            catch (SlickException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定されたキーが押された直後か否かを返す.
     *
     * @param keycodeNum キーコード番号
     * @return キーが押された直後か否か
     */
    public static boolean isPressed(int keycodeNum)
    {
        return keyPressed[keycodeNum];
    }

    /**
     * 指定されたキーが長押し中か否かを返す.
     *
     * @param keycodeNum キーコード番号
     * @return キーが長押し中か否か
     */
    public static boolean isDown(int keycodeNum)
    {
        return keyDown[keycodeNum];
    }
}
