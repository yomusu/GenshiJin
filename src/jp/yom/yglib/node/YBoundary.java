package jp.yom.yglib.node;

import java.util.Iterator;

import jp.yom.yglib.vector.FSurface;


/******************************************
 * 
 * 
 * 当たり判定の境界線を持っているYNode
 * 
 * @author Yomusu
 *
 */
public interface YBoundary {

	/** 境界線情報のイテレーター */
	public Iterator<FSurface> getBoundsIter();
}
