package com.negset.macaron;

import org.newdawn.slick.Graphics;

/**
 * ゲームオブジェクトのスーパークラス.
 * ノーツや判定エフェクトが継承する.
 *
 * @author negset
 */
public abstract class AbstractObject
{
	/** インスタンス格納配列におけるインデックス */
	public int index;

	/** 座標のx成分 */
	public float x;

	/** 座標のy成分 */
	public float y;

	/**
	 * 動作を規定する.
	 * 1ループにつき1回呼ばれる.
	 *
	 * @param delta 前のループにかかった時間(単位:ミリ秒)
	 */
	public abstract void move(int delta);

	/**
	 * 描画処理を行う.
	 * 1ループにつき1回呼ばれる.
	 *
	 * @param g 描画先
	 */
	public abstract void draw(Graphics g);
}