package com.hxht.autodeploy.utils

class NodeUtil {
    NodeUtil() { }
    NodeUtil(long id, String str, String icons, List<NodeUtil> NodeUtil)
    {
        Id = id
        text = str
        icon = icons
        nodes = NodeUtil
        
    }
    long Id //树的节点Id，区别于数据库中保存的数据Id。若要存储数据库数据的Id，添加新的Id属性；若想为节点设置路径，类中添加Path属性
    String text //节点名称
    String icon //节点图标
    List<NodeUtil> nodes //子节点，可以用递归的方法读取
}
