package ewing.application.common;

import java.io.Serializable;
import java.util.List;

/**
 * 树节点接口。
 */
public interface TreeNode<CHILD extends TreeNode> {

    Serializable getId();

    Serializable getParentId();

    List<CHILD> getChildren();

    void setChildren(List<CHILD> nodes);

}
