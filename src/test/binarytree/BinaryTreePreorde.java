/**
 * @FileName: BinaryTreePreorde.java
 * @Package:test
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 上午11:27:00
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-12       y.nie        1.0         1.0 Version
 */
package test.binarytree;

/**
 * @ClassName: BinaryTreePreorde
 * @Description:二叉树遍历（主要采用的事递归调用）
 * @author: tanghui
 * @date:2015-3-12 上午11:27:00
 */
public class BinaryTreePreorde {
	public static void preOrder(BinaryTree root) { // 先根遍历
		if (root != null) {
			System.out.print(root.data + "-");
			preOrder(root.left);
			preOrder(root.right);
		}
	}

	public static void inOrder(BinaryTree root) { // 中根遍历

		if (root != null) {
			inOrder(root.left);
			System.out.print(root.data + "--");
			inOrder(root.right);
		}
	}

	public static void postOrder(BinaryTree root) { // 后根遍历

		if (root != null) {
			postOrder(root.left);
			postOrder(root.right);
			System.out.print(root.data + "---");
		}
	}

	public static void main(String[] str) {
		int[] array = { 12, 76, 35, 22, 16, 48, 90, 46, 9, 40 };
		BinaryTree root = new BinaryTree(array[0]); // 创建二叉树
		for (int i = 1; i < array.length; i++) {
			root.insert(root, array[i]); // 向二叉树中插入数据
		}
		System.out.println("先根遍历：");
		preOrder(root);
		System.out.println();
		System.out.println("中根遍历：");
		inOrder(root);
		System.out.println();
		System.out.println("后根遍历：");
		postOrder(root);
	}
}