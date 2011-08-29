package jp.yom.yglib;

import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FSurface;

/********************************
 * 
 * 
 * 当たり判定付きノード
 * 
 * @author Yomusu
 *
 */
public class AtariModel {
	
	
	
	/** 当たり面(World座標) */
	public FSurface[]	surfaces;
	/** 当たり辺(World座標) */
	public FLine[]		lines;
	
	
	
	public void transform( FMatrix mat ) {
		
		for( FSurface s : surfaces )
			s.transform( mat );
		
		for( FLine l : lines )
			l.transform( mat );
		
	}
	

}
