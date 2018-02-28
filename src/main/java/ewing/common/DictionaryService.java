package ewing.common;

import ewing.application.query.Paging;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.query.entity.Dictionary;

import java.util.List;

/**
 * 数据字典服务接口。
 **/
public interface DictionaryService {

    Paging<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    void addDictionary(Dictionary dictionary);

    void updateDictionary(Dictionary dictionary);

    void deleteDictionary(Long dictionaryId);

    List<DictionaryNode> findDictionaryTrees(String[] rootValues);
}
