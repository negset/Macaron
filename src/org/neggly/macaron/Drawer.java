package org.neggly.macaron;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;

/**
 * リソースファイルを管理するクラス.
 *
 * @author negset
 */
public class Drawer
{
    /**
     * タップ画像
     */
    public static Image tap_a, tap_b;
    /**
     * ホールド画像
     */
    public static Image[] hold_a, hold_b;
    /**
     * 文字列描画用フォント
     */
    public static TrueTypeFont fontS, fontR, fontM;
    /**
     * 決定音
     */
    private static Sound enterSE;
    /**
     * カーソル移動音
     */
    private static Sound cursorSE;
    /**
     * ノーツ判定音
     */
    private static Sound[] judgeSE;
    /**
     * サウンド用定数
     */
    public static final int SE_ENTER = 3;
    public static final int SE_CURSOR = 4;

    /**
     * リソースファイルの読み込みを行う.
     *
     * @throws SlickException
     */
    public static void load() throws SlickException
    {
        Image notes = new Image("res\\play\\notes.png");
        SpriteSheet ss
                = new SpriteSheet(notes, notes.getWidth() / 4, notes.getHeight() / 2);
        tap_a = ss.getSubImage(0, 0);
        tap_b = ss.getSubImage(0, 1);
        hold_a = new Image[3];
        hold_b = new Image[3];
        for (int i = 0; i < 3; i++)
        {
            hold_a[i] = ss.getSubImage(i + 1, 0);
            hold_b[i] = ss.getSubImage(i + 1, 1);
        }

        Font font = null;
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("res\\system\\default.ttf"));
        }
        catch (FontFormatException | IOException e)
        {
            System.out.println("フォント読み込みエラー");
        }
        fontS = new TrueTypeFont(font.deriveFont(16.0f), true);
        fontR = new TrueTypeFont(font.deriveFont(24.0f), true);
        fontM = new TrueTypeFont(font.deriveFont(50.0f), true);

        judgeSE = new Sound[3];
        for (int i = 0; i < 3; i++)
        {
            judgeSE[i] = new Sound("res\\play\\judge_se_" + i + ".wav");
        }
        enterSE = new Sound("res\\system\\enter_se.wav");
        cursorSE = new Sound("res\\system\\cursor_se.wav");
    }

    /**
     * FPSの描画を行う.
     * コンフィグでShowFPSがONになっているときのみ.
     *
     * @param fps 描画するFPS
     */
    public static void drawFps(int fps, Graphics g)
    {
        if (Config.showFps)
        {
            String s = "FPS: " + fps;
            int w = fontS.getWidth(s);
            g.setColor(Color.black);
            g.fillRect(800 - (w + 20), 575, w + 20, 25);
            g.setColor(Color.white);
            g.setFont(fontS);
            g.drawString(s, 800 - (w + 10), 575);
        }
    }

    /**
     * SEを再生する.
     *
     * @param type SEの種類
     */
    public static void playSE(int type)
    {
        switch (type)
        {
            case 0:
                judgeSE[0].play();
                break;

            case 1:
                judgeSE[1].play();
                break;

            case 2:
                judgeSE[2].play();
                break;

            case SE_ENTER:
                enterSE.play();
                break;

            case SE_CURSOR:
                cursorSE.play();
                break;
        }
    }
}
