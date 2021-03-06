package org.neggly.macaron;

/**
 * 難易度を表す列挙
 *
 * @author negset
 */
public enum Difficulty
{
    EASY("#EASY"),
    NORMAL("#NORMAL"),
    HARD("#HARD"),
    LUNATIC("#LUNATIC");

    /**
     * 譜面内での開始命令
     */
    private String command;

    /**
     * コンストラクタ
     *
     * @param command 譜面内での開始命令
     */
    Difficulty(String command)
    {
        this.command = command;
    }

    /**
     * 譜面内での開始命令を返す.
     *
     * @return 開始命令
     */
    public String getCommand()
    {
        return command;
    }
}
