package com.negset.macaron;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MusicCard
{
	/** 背景画像 */
	private static Image bg;
	/** 難易度カード */
	private static Image[] difCard;
	/** 曲サムネイル画像 */
	private Image thumb;
	/** 曲名画像 */
	private Image title;
	/** アーティスト名画像 */
	private Image artist;
	/** 各難易度のレベル */
	private int[] level;
	/** x座標 */
	private float x;
	/** 描画の縮小率 */
	private float scale;

	private boolean isOpen;
	private static int animeCnt;

	static
	{
		try
		{
			bg = new Image("res\\select\\musicCard.png");
			difCard = new Image[4];
			difCard[0] = new Image("res\\select\\difficulty0.png");
			difCard[1] = new Image("res\\select\\difficulty1.png");
			difCard[2] = new Image("res\\select\\difficulty2.png");
			difCard[3] = new Image("res\\select\\difficulty3.png");
		}
		catch (SlickException e)
		{
			System.out.println("カード生成失敗");
		}
	}

	MusicCard(String mbpPath, float x)
	{
		try
		{
			thumb = new Image(mbpPath + "\\thumbnail.png");
			title = new Image(mbpPath + "\\title.png");
			artist = new Image(mbpPath + "\\artist.png");
		}
		catch (SlickException e) {}

		Beatmap.readDefine(mbpPath + "\\define.ini");
		level = Beatmap.getLevel();

		this.x = x;
		scale = 0.7f;
		isOpen = false;
	}

	public void move(float cx, boolean focus, int delta)
	{
		if (animeCnt > 0)
		{
			animeCnt -= delta;
			if (animeCnt < 0)
			{
				animeCnt = 0;
			}
		}
		else if (isOpen)
		{
			isOpen = false;
		}

		// カードを移動する.
		if (x < cx)
		{
			x += 1.5 * delta;
			if (x > cx)
			{
				x = cx;
			}
		}
		else if (x > cx)
		{
			x -= 1.5 * delta;
			if (x < cx)
			{
				x = cx;
			}
		}
		// 縮小率を変える.
		if (focus)
		{
			if (scale < 1)
			{
				scale += 0.002 * delta;
				if (scale > 1)
				{
					scale = 1;
				}
			}
		}
		else
		{
			if (scale > 0.7)
			{
				scale -= 0.002 * delta;
				if (scale < 0.7)
				{
					scale = 0.7f;
				}
			}
		}
	}

	public void move(int dIndex, int difCsr, int delta)
	{
		// 選択中のカードより左のカード
		if (dIndex < 0)
		{
			if (x > -bg.getWidth())
			{
				x -= 1.5 * delta;
			}
		}
		// 選択中のカード
		else if (dIndex == 0)
		{
			if (!isOpen)
			{
				isOpen = true;
			}
			if (animeCnt < 100)
			{
				animeCnt += delta;
				if (animeCnt > 100)
				{
					animeCnt = 100;
				}
			}

			if (x > 180)
			{
				x -= 1.5 * delta;
				if (x < 185)
				{
					x = 185;
				}
			}
		}
		// 選択中のカードより右のカード
		else
		{
			if (x < 800 + bg.getWidth())
			{
				x += 1.5 * delta;
			}
		}
	}

	public void draw(Graphics g)
	{
		// 難易度カードを描画する.
		if (isOpen)
		{
			for (int i = 0; i < 4; i++)
			{
				int x = 345;
				difCard[i].setAlpha(animeCnt / 50);
				difCard[i].draw(x, 213 + 93 * i - animeCnt * 0.5f);
			}
		}

		float w1 = bg.getWidth() * scale;
		float h1 = bg.getHeight() * scale;
		bg.draw(x - w1/2, 300 - h1/2, scale);

		float w2 = thumb.getWidth() * scale;
		thumb.draw(x - w2/2, 300 - h1/2 + 12*scale, scale);

		float w3 = title.getWidth() * scale;
		title.draw(x - w3/2, 300 - h1/2 + 300*scale, scale);

		float w4 = artist.getWidth() * scale;
		artist.draw(x - w4/2, 300 - h1/2 + 330*scale, scale);
	}
}