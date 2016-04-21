/**
 * @FileName: BinaryTree.java
 * @Package:test
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 上午11:26:01
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-12       y.nie        1.0         1.0 Version
 */
package test.binarytree;

/**
 * @ClassName: BinaryTree
 * @Description:
 * @author: tanghui
 * @date:2015-3-12 上午11:26:01
 */
public class BinaryTree {

	 int data;      //根节点数据
	 BinaryTree left;    //左子树
	 BinaryTree right;   //右子树

	 public BinaryTree(int data)    //实例化二叉树类
	 {
	  this.data = data;
	  left = null;
	  right = null;
	 }

	 public void insert(BinaryTree root,int data){     //向二叉树中插入子节点
	  if(data>root.data)                               //二叉树的左节点都比根节点小
	  {
	   if(root.right==null){
	    root.right = new BinaryTree(data);
	   }else{
	    this.insert(root.right, data);
	   }
	  }else{                                          //二叉树的右节点都比根节点大
	   if(root.left==null){
	    root.left = new BinaryTree(data);
	   }else{
	    this.insert(root.left, data);
	   }
	  }
	 }
	}
