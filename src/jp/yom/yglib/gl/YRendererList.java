package jp.yom.yglib.gl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;



/********************************************
 * 
 * 
 * レンダラ専用のリスト
 * 
 * 
 * @author Yomusu
 *
 */
public class YRendererList {
	
	class Item {
		
		/** Zつまり優先順 */
		float	z;
		/** レンダラ */
		YRenderer	renderer;
		
		public Item( float z, YRenderer renderer ) {
			this.z = z;
			this.renderer = renderer;
		}
	}
	
	/** 本物のツリーセット */
	private final TreeSet<Item>	renderList = new TreeSet<Item>( new Comparator<Item>() {

		@Override
		public int compare(Item o1, Item o2 ) {
			
			// 逆順
			if( o1.z > o2.z )
				return -1;
			
			// 同値の場合、変更なし
			if( o1.z == o2.z )
				return 1;
			
			return 1;
		}

	});
	
	
	
	/*************************************************
	 * 
	 * レンダラを追加する
	 * 
	 * @param z
	 * @param render
	 */
	public void add( float z, YRenderer render ) {
		renderList.add( new Item( z, render ) );
	}
	
	
	/*************************************************
	 * 
	 * リストのイテレーターを返す
	 * 
	 * @return
	 */
	public Iterator<YRenderer> iterator() {
		
		return new Iterator<YRenderer>() {

			Iterator<Item>	it = renderList.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public YRenderer next() {
				Item	item = it.next();
				if( item !=null )
					return item.renderer;
				return null;
			}

			@Override
			public void remove() {}
		};
	}
}
