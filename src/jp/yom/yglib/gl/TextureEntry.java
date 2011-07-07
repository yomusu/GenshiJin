package jp.yom.yglib.gl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

/***********************************************
 * 
 * 
 * テクスチャの準備を行うクラス
 * 
 * @author matsumoto
 *
 */
public class TextureEntry {
	
	public final String	key;
	public	Bitmap	bmp = null;
	public final int	resID;
	
	public TextureEntry( String key, int resID ) {
		
		this.key = key;
		this.resID = resID;
	}
	
	
	/***************************************************
	 * 
	 * ビットマップデータを読み込みます
	 * 
	 * @param res
	 * @param resID
	 */
	public void loadBitmap( Resources res ) {
		
		BitmapFactory.Options	options = new BitmapFactory.Options();
		options.inScaled = false;
		options.inPreferredConfig = Config.ARGB_8888;
		
		bmp = BitmapFactory.decodeResource( res, resID, options );

	}
	
	
	/***************************************************
	 * 
	 * 読み込んだビットマップデータを解放します
	 * 
	 */
	public void disposeBitmap() {
		if( bmp!=null ) {
			bmp.recycle();
			bmp = null;
		}
	}
}
