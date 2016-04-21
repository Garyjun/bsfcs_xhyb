/**
 * @FileName: Widget.java
 * @Package:test.bfbc
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午3:52:10
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-12       y.nie        1.0         1.0 Version
 */
package test.bfbc;

import org.jruby.runtime.callsite.SuperCallSite;

/**
 * @ClassName: Widget
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午3:52:10
 */
public class Widget {

	public synchronized void doSomething(){
		System.out.println("Super method!");
	}
}
