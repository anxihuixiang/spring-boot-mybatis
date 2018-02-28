package ewing.query.dao;

import ewing.common.vo.FindDictionaryParam;
import ewing.query.entity.Dictionary;
import ewing.query.mapper.DictionaryMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictionaryDao extends DictionaryMapper {

    long countDictionary(FindDictionaryParam findDictionaryParam);

    List<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    long countSame(@Param("parentId") Long parentId, @Param("name") String name, @Param("value") String value);

}