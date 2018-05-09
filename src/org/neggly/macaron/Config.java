package org.neggly.macaron;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * コンフィグファイルの読み込みを行うクラス.
 *
 * @author negset
 */
public class Config
{
    /**
     * 描画のフレームレート
     */
    public static int framerate;
    /**
     * FPSを画面上に表記するか否かのフラグ
     */
    public static boolean showFps;
    /**
     * フルスクリーンにするか否かのフラグ
     */
    public static boolean fullscreen;
    /**
     * 自動ポーズ機能を有効化するか否かのフラグ
     */
    public static boolean autoPause;

    /**
     * ノーツの判定幅
     * 単位はミリ秒.
     */
    public static long[] tapRange = {40, 80, 120};
    public static long[] holdRange = {60, 100, 120};

    /**
     * ノーツの判定に使用するキーのコード
     */
    static int[] keycode = new int[6];

    /**
     * コンフィグの読み込みを行う.
     */
    public static void read()
    {
        // フィールドの初期化
        framerate = 60;
        showFps = false;
        fullscreen = false;
        autoPause = true;
        keycode[0] = 0x1F;
        keycode[1] = 0x20;
        keycode[2] = 0x21;
        keycode[3] = 0x24;
        keycode[4] = 0x25;
        keycode[5] = 0x26;

        BufferedReader br = null;
        try
        {
            FileReader fr = new FileReader("config.ini");
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("FrameRate="))
                {
                    framerate = Integer.parseInt(line.substring(10));
                }
                else if (line.equals("ShowFPS=1"))
                {
                    showFps = true;
                }
                else if (line.equals("FullScreen=1"))
                {
                    fullscreen = true;
                }
                else if (line.equals("AutoPause=0"))
                {
                    autoPause = false;
                }

                else if (line.startsWith("TapPerfect="))
                {
                    tapRange[0] = Long.parseLong(line.substring(11));
                }
                else if (line.startsWith("TapGreat="))
                {
                    tapRange[1] = Long.parseLong(line.substring(9));
                }
                else if (line.startsWith("TapGood="))
                {
                    tapRange[2] = Long.parseLong(line.substring(8));
                }
                else if (line.startsWith("HoldPerfect="))
                {
                    holdRange[0] = Long.parseLong(line.substring(12));
                }
                else if (line.startsWith("HoldGreat="))
                {
                    holdRange[1] = Long.parseLong(line.substring(10));
                }
                else if (line.startsWith("HoldGood="))
                {
                    holdRange[2] = Long.parseLong(line.substring(9));
                }

                else if (line.startsWith("Key1="))
                {
                    keycode[0] = Integer.decode(line.substring(5));
                }
                else if (line.startsWith("Key2="))
                {
                    keycode[1] = Integer.decode(line.substring(5));
                }
                else if (line.startsWith("Key3="))
                {
                    keycode[2] = Integer.decode(line.substring(5));
                }
                else if (line.startsWith("Key4="))
                {
                    keycode[3] = Integer.decode(line.substring(5));
                }
                else if (line.startsWith("Key5="))
                {
                    keycode[4] = Integer.decode(line.substring(5));
                }
                else if (line.startsWith("Key6="))
                {
                    keycode[5] = Integer.decode(line.substring(5));
                }
            }

            br.close();
            fr.close();
        }
        catch (Exception e)
        {
            System.out.println("コンフィグ読み込みエラー");
        }
    }
}
