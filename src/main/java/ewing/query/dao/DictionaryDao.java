package ewing.query.dao;

import ewing.query.mapper.DictionaryMapper;
import org.apache.ibatis.annotations.Param;

public interface DictionaryDao extends DictionaryMapper {

    long countSame(@Param("parentId") Long parentId, @Param("name") String name, @Param("value") String value);

}