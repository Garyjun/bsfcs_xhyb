/**
 * @FileName: LoggerWidget.java
 * @Package:test.bfbc
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午3:54:20
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-12       y.nie        1.0         1.0 Version
 */
package test.bfbc;

/**
 * @ClassName: LoggerWidget
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午3:54:20
 */
public class LoggerWidget extends Widget {

	public synchronized void doSomething(){
		System.out.println(toString() + ": calling doSomething method!");
		super.doSomething();
	}


	/**
	 * @Title: main
	 * @Description:
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) {
		// tanghui Auto-generated method stub
		new LoggerWidget().doSomething();
	}

}
