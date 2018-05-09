package org.neggly.macaron;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * スコアデータの保存・読み込みを行う.
 *
 * @author negset
 */
public class Score
{
    /**
     * 新スコア
     */
    private static int newScore;
    /**
     * 新達成率
     */
    private static float newAchieve;
    /**
     * 新クリア判定
     */
    private static boolean newCleared;
    /**
     * 新称号
     */
    private static int newBadge;

    /**
     * 取得したスコア
     */
    private static int loadedScore;
    /**
     * 取得した達成率
     */
    private static float loadedAchieve;
    /**
     * 取得したクリア判定
     */
    private static boolean loadedCleared;
    /**
     * 取得した称号
     */
    private static int loadedBadge;

    /**
     * 称号なしを表す称号定数
     */
    public static final int BADGE_NONE = 0;
    /**
     * フルコンボを表す称号定数
     */
    public static final int BADGE_FC = 1;
    /**
     * オールパーフェクトを表す称号定数
     */
    public static final int BADGE_AP = 2;

    /**
     * ノーツの判定結果ごとの配点
     */
    private static final int[] MARKS = {500, 400, 250, 0};

    /**
     * スコアデータの存在を確認する.
     * 選曲画面から呼ばれる.
     *
     * @param name データファイル名(曲名)
     * @return スコアデータが存在するかどうか
     */
    public static boolean existsData(String name)
    {
        load(name);
        if (getLoadedScore() != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 判定結果のカウントから新スコアを計算する.
     * プレイ画面から呼ばれる.
     *
     * @param judgementCnt 判定結果数
     */
    public static void setNewScore(int[] judgementCnt)
    {
        newScore = 0;
        int mxscr = 0;
        for (int i = 0; i < 4; i++)
        {
            newScore += judgementCnt[i] * MARKS[i];
            mxscr += judgementCnt[i];
        }
        mxscr *= MARKS[0];
        // プレイヤーのスコアとスコア最大値から,達成率を計算する.
        newAchieve = newScore / (float) mxscr * 101;
        // 80%以上ならクリア
        newCleared = (newAchieve >= 80);
        // 称号をセットする.
        if (newAchieve == 101)
        {
            newBadge = BADGE_AP;
        }
        else if (judgementCnt[3] == 0)
        {
            newBadge = BADGE_FC;
        }
        else
        {
            newBadge = BADGE_NONE;
        }
    }

    /**
     * 新記録を更新したかを調べ,
     * 更新していたらスコアを保存する.
     * 記録がない場合も保存する.
     * オートプレイ使用時は保存しない.
     * リザルト画面から呼ばれる.
     *
     * @param name データファイル名(曲名)
     * @return 新記録を更新したかどうか
     */
    public static boolean isNewRecord(String name)
    {
        if (!StatePlay.useAutoplay)
        {
            load(name);
            if (loadedScore < newScore)
            {
                save(name);
                return true;
            }
        }
        return false;
    }

    /**
     * スコアの保存を行う.
     *
     * @param name データファイル名(曲名)
     */
    private static void save(String name)
    {
        try
        {
            File dir = new File("data\\score");
            // スコア保存ディレクトリの存在を確認する.
            if (!dir.exists())
            {
                // 無ければ生成する.
                dir.mkdirs();
            }

            File file = new File("data\\score\\" + name + ".bin");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(newScore);
            dos.writeFloat(newAchieve);
            dos.writeBoolean(newCleared);
            dos.writeInt(newBadge);

            dos.flush();
            dos.close();
        }
        catch (Exception e)
        {
            System.out.println("スコア保存エラー");
        }
    }

    /**
     * スコアの読み込みを行う.
     *
     * @param name データファイル名(曲名)
     */
    private static void load(String name)
    {
        loadedScore = -1;
        try
        {
            File file = new File("data\\score\\" + name + ".bin");
            // ファイルが存在したら,読み込みを行う.
            if (file.exists())
            {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);

                loadedScore = dis.readInt();
                loadedAchieve = dis.readFloat();
                loadedCleared = dis.readBoolean();
                loadedBadge = dis.readInt();

                dis.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("スコア読み込みエラー");
        }
    }

    /**
     * 達成率に応じた評価ランクを返す.
     *
     * @return 評価ランク
     */
    public static String getRank(float achieve)
    {
        if (achieve >= 100)
        {
            return "SSS";
        }
        if (achieve >= 99)
        {
            return "SS";
        }
        if (achieve >= 97)
        {
            return "S";
        }
        if (achieve >= 94)
        {
            return "AAA";
        }
        if (achieve >= 90)
        {
            return "AA";
        }
        if (achieve >= 80)
        {
            return "A";
        }
        if (achieve >= 60)
        {
            return "B";
        }
        if (achieve >= 40)
        {
            return "C";
        }
        if (achieve >= 20)
        {
            return "D";
        }
        if (achieve >= 10)
        {
            return "E";
        }
        return "F";
    }

    /**
     * 新スコアを返す.
     *
     * @return 新スコア
     */
    public static int getNewScore()
    {
        return newScore;
    }

    /**
     * 新達成率を返す.
     *
     * @return 新達成率
     */
    public static float getNewAchieve()
    {
        return newAchieve;
    }

    /**
     * 新クリア判定を返す.
     *
     * @return 新クリア判定
     */
    public static boolean getNewCleared()
    {
        return newCleared;
    }

    /**
     * 新称号を返す.
     *
     * @return 新称号
     */
    public static int getNewBadge()
    {
        return newBadge;
    }

    /**
     * 取得したスコアを返す.
     *
     * @return 取得したスコア
     */
    public static int getLoadedScore()
    {
        return loadedScore;
    }

    /**
     * 取得した達成率を返す.
     *
     * @return 取得した達成率
     */
    public static float getLoadedAchieve()
    {
        return loadedAchieve;
    }

    /**
     * 取得したクリア判定を返す.
     *
     * @return 取得したクリア判定
     */
    public static boolean getLoadedCleared()
    {
        return loadedCleared;
    }

    /**
     * 取得した称号を返す.
     *
     * @return 取得した称号
     */
    public static int getLoadedBadge()
    {
        return loadedBadge;
    }
}
