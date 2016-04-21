/**
 * @FileName: FinalTest.java
 * @Package:test.bfbc
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午5:27:44
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-12       y.nie        1.0         1.0 Version
 */
package test.bfbc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: FinalTest
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 下午5:27:44
 */
public class FinalTest {
	public final Map<String,String> map = new HashMap<String,String>();

	public synchronized Map<String,String> putV(String a,String b){
//		String result = map.get(a);
//		if(StringUtils.isBlank(result)){
			map.put(a, b);
		//}
		return map;
	}


	public static void main(String[] args){
		FinalTest s = new FinalTest();
		s.putV("a", "b");
		s.putV("a1", "b1");
		s.putV("a2", "b2");
		s.putV("a3", "b3");
		s.putV("a1", "b11");
		Map<String,String> map1 = s.putV("a", "b");
		System.out.println(map1.get("a1"));
	}
}
