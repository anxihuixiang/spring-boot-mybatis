package ewing.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ewing.application.common.TreeNode;

import java.util.List;

public class DictionaryNode implements TreeNode<DictionaryNode> {

    private Long dictionaryId;

    private Long parentId;

    private Long rootId;

    private String name;

    private String value;

    private List<DictionaryNode> children;

    public Long getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return dictionaryId;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @JsonIgnore
    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public List<DictionaryNode> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<DictionaryNode> children) {
        this.children = children;
    }
}
