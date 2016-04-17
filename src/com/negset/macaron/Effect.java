package com.negset.macaron;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * 判定エフェクトの動作・描画を行うクラス.
 * AbstractObjectを継承する.
 *
 * @author negset
 */
public class Effect extends AbstractObject
{
	/** エフェクトの描画色 */
	private Color color;
	/** 判定結果のコメント */
	private String comment;

	/**
	 * コンストラクタ
	 */
	Effect(int index)
	{
		this.index = index;
	}

	/**
	 * 動作を規定する.
	 * 1ループにつき1回呼ばれる.
	 */
	@Override
	public void move(int delta)
	{
		y -= 0.05 * delta;
		if (y < 450)
		{
			ObjectPool.destroyEffect(index);
		}
	}

	/**
	 * 描画処理を行う.
	 * 1ループにつき1回呼ばれる.
	 */
	@Override
	public void draw(Graphics g)
	{
		g.setColor(color);
		g.setFont(Drawer.fontS);
		int w = g.getFont().getWidth(comment);
		g.drawString(comment, x - w / 2, y);
	}

	/**
	 * インスタンスの有効化を行う.
	 * インスタンスの使いまわしをしているので,初期化処理もここで行う.
	 *
	 * @param judgement ノーツ判定結果
	 * @param pos エフェクトを描画する位置
	 */
	public void activate(int judgement, int pos)
	{
		switch (judgement)
		{
			case 0:
				color = Color.yellow;
				comment = "Perfect";
				break;
			case 1:
				color = Color.magenta;
				comment = "Great";
				break;
			case 2:
				color = Color.green;
				comment = "Good";
				break;
			case 3:
				color = Color.gray;
				comment = "Miss";
				break;
		}

		switch (pos)
		{
			case 0:
				x = 100;
				break;
			case 1:
				x = 200;
				break;
			case 2:
				x = 300;
				break;
			case 3:
				x = 500;
				break;
			case 4:
				x = 600;
				break;
			case 5:
				x = 700;
				break;
		}
		y = 470;
	}
}