package ewing.common;

import ewing.application.AppAsserts;
import ewing.application.common.TreeUtils;
import ewing.application.exception.AppRunException;
import ewing.application.query.Paging;
import ewing.application.query.QueryUtils;
import ewing.common.dao.DictionaryDao;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.query.entity.Dictionary;
import ewing.query.entity.DictionaryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据字典服务实现。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;

    @Override
    public Paging<Dictionary> findWithSubDictionary(
            FindDictionaryParam findDictionaryParam) {
        AppAsserts.notNull(findDictionaryParam, "查询参数不能为空！");
        findDictionaryParam.setName(QueryUtils.likeContains(findDictionaryParam.getName()));
        findDictionaryParam.setValue(QueryUtils.likeContains(findDictionaryParam.getValue()));
        long total = dictionaryDao.countDictionary(findDictionaryParam);
        List<Dictionary> dictionaries = dictionaryDao.findWithSubDictionary(findDictionaryParam);
        return new Paging<>(total, dictionaries);
    }

    @Override
    public void addDictionary(Dictionary dictionary) {
        AppAsserts.notNull(dictionary, "字典项不能为空！");
        AppAsserts.hasText(dictionary.getName(), "字典名不能为空！");
        AppAsserts.hasText(dictionary.getValue(), "字典值不能为空！");

        // 处理父字典和根字典的关系
        if (dictionary.getParentId() != null) {
            DictionaryExample example = new DictionaryExample();
            example.createCriteria().andDictionaryIdEqualTo(dictionary.getParentId());
            Dictionary parent = QueryUtils.getMaxOne(dictionaryDao.selectByExample(example));
            if (parent == null) {
                throw new AppRunException("父字典项不存在！");
            } else {
                // 父字典存在则根字典继承自父字典
                dictionary.setRootId(parent.getRootId());
            }
        }

        // 相同位置下的字典名称或值不能重复
        AppAsserts.yes(dictionaryDao.countSames(null,
                dictionary.getParentId(), dictionary.getName(), dictionary.getValue()) < 1,
                "相同位置下的字典名或值不能重复！");

        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        dictionary.setCreateTime(new Date());
        dictionaryDao.insertSelective(dictionary);

        // 更新字典值的根字典ID
        if (dictionary.getParentId() == null) {
            dictionary.setRootId(dictionary.getDictionaryId());
            dictionaryDao.updateByPrimaryKeySelective(dictionary);
        }
    }

    @Override
    public void updateDictionary(Dictionary dictionary) {
        AppAsserts.notNull(dictionary, "字典项不能为空！");
        AppAsserts.notNull(dictionary.getDictionaryId(), "字典ID不能为空！");
        AppAsserts.hasText(dictionary.getName(), "字典名不能为空！");
        AppAsserts.hasText(dictionary.getValue(), "字典值不能为空！");

        // 相同位置下的字典名称或值不能重复
        AppAsserts.yes(dictionaryDao.countSames(dictionary.getDictionaryId(),
                dictionary.getParentId(), dictionary.getName(), dictionary.getValue()) < 1,
                "相同位置下的字典名或值不能重复！");

        // 不能修改父字典和根字典
        dictionary.setRootId(null);
        dictionary.setParentId(null);
        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        dictionaryDao.updateByPrimaryKeySelective(dictionary);
    }

    @Override
    public void deleteDictionary(Long dictionaryId) {
        AppAsserts.notNull(dictionaryId, "字典ID不能为空！");
        AppAsserts.notNull(dictionaryDao.selectByPrimaryKey(dictionaryId),
                "该字典不存在或已删除！");
        DictionaryExample example = new DictionaryExample();
        example.createCriteria().andParentIdEqualTo(dictionaryId);
        AppAsserts.yes(dictionaryDao.countByExample(example) < 1,
                "请先删除该字典的所有子项！");

        dictionaryDao.deleteByPrimaryKey(dictionaryId);
    }

    @Override
    public List<DictionaryNode> findDictionaryTrees(String[] rootValues) {
        AppAsserts.notNull(rootValues, "查询参数不能为空！");
        List<DictionaryNode> dictionaries = dictionaryDao.findRootSubDictionaries(rootValues);
        return TreeUtils.toTree(dictionaries,
                ArrayList::new,
                DictionaryNode::getDictionaryId,
                DictionaryNode::getParentId,
                DictionaryNode::getChildren,
                DictionaryNode::setChildren);
    }

}
