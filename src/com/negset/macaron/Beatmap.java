package com.negset.macaron;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * 定義ファイル・譜面ファイルの読み込みを行うクラス.
 *
 * @author negset
 */
public class Beatmap
{
	/** 定義ファイルから取得する曲名 */
	private static String title;
	/** 定義ファイルから取得するアーティスト名 */
	private static String artist;
	/** 定義ファイルから取得するBPM */
	private static float bpm;
	/** 定義ファイルから取得する譜面難易度 */
	private static int level;
	/** 定義ファイルから取得するオフセット */
	private static int offset;
	/** 定義ファイルから取得する作譜者名 */
	private static String mapper;

	/** 譜面ファイルから取得する小節データ及び命令  */
	private static String[] bmdata = new String[999];
	/** 譜面ファイルから取得した小節データ及び命令のカウント */
	private static int bmdataCnt;

	/**
	 * 定義ファイルを読み込みを行う.
	 *
	 * @param path 定義ファイルのパス
	 */
	public static void readDefine(String path)
	{
		title = "no data";
		artist = "no data";
		bpm = 0;
		level = 0;
		offset = 0;
		mapper = "no data";

		BufferedReader br = null;
		try
		{
			FileReader fr = new FileReader(path);
			br = new BufferedReader(fr);
			String line;
			while((line=br.readLine()) != null)
			{
				if (line.startsWith("#TITLE:"))
				{
					title = line.substring(7);
				}
				else if (line.startsWith("#ARTIST:"))
				{
					artist = line.substring(8);
				}
				else if (line.startsWith("#BPM:"))
				{
					bpm = Float.parseFloat(line.substring(5));
				}
				else if (line.startsWith("#LEVEL:"))
				{
					level = Integer.parseInt(line.substring(7));
				}
				else if (line.startsWith("#OFFSET:"))
				{
					offset = Integer.parseInt(line.substring(8));
				}
				else if (line.startsWith("#MAPPER:"))
				{
					mapper = line.substring(8);
				}
			}

			br.close();
			fr.close();
		}
		catch(Exception e)
		{
			System.out.println("定義ファイル読み込みエラー");
		}
	}

	/**
	 * 譜面ファイルの読み込みを行う.
	 *
	 * @param path 譜面ファイルのパス
	 */
	public static void readBeatmap(String path)
	{
		bmdataCnt = 0;

		BufferedReader br = null;
		try
		{
			FileReader fr = new FileReader(path);
			br = new BufferedReader(fr);
			String line;

			boolean start = false;
			loadLoop:
				while((line = br.readLine()) != null)
				{
					if (!start)
					{
						if (line.equals("#START"))
						{
							start = true;
						}
					}
					else
					{
						// 読み込み行が空行でなく,かつコメント行でもない時
						if (!line.equals("") && !line.startsWith("//"))
						{
							bmdata[bmdataCnt] = line;
							bmdataCnt++;

							if (bmdataCnt == bmdata.length)
							{
								break loadLoop;
							}
						}
						if (line.equals("#END"))
						{
							break loadLoop;
						}
					}
				}

			br.close();
			fr.close();
		}
		catch(Exception e)
		{
			System.out.println("譜面ファイル読み込みエラー");
		}
	}


	/**
	 * 定義ファイルから取得した曲名を返す.
	 *
	 * @return 曲名
	 */
	public static String getTitle()
	{
		return title;
	}

	/**
	 * 定義ファイルから取得したアーティスト名を返す.
	 *
	 * @return アーティスト名
	 */
	public static String getArtist()
	{
		return artist;
	}

	/**
	 * 定義ファイルから取得したBPMを返す.
	 *
	 * @return BPM
	 */
	public static float getBpm()
	{
		return bpm;
	}

	/**
	 * 定義ファイルから取得した譜面難易度を返す.
	 *
	 * @return 譜面難易度
	 */
	public static int getLevel()
	{
		return level;
	}

	/**
	 * 定義ファイルから取得したオフセットを返す.
	 *
	 * @return オフセット
	 */
	public static int getOffset()
	{
		return offset;
	}

	/**
	 * 定義ファイルから取得した作譜者名を返す.
	 *
	 * @return 作譜者名
	 */
	public static String getMapper()
	{
		return mapper;
	}

	/**
	 * 譜面ファイルから取得した小節データを返す.
	 *
	 * @param bmdataNum 小節データの番号
	 * @return 小節データ
	 */
	public static String getBmdata(int bmdataNum)
	{
		if (bmdataNum < bmdataCnt)
		{
			return bmdata[bmdataNum];
		}
		else if (bmdataNum == bmdataCnt)
		{
			return "just_end";
		}
		return "end";
	}
}