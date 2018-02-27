package ewing.common;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.query.BaseBeanDao;
import ewing.application.query.Page;
import ewing.application.query.QueryUtils;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.entity.Dictionary;
import ewing.query.QDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典数据访问实现。
 */
@Repository
public class DictionaryDaoImpl extends BaseBeanDao<QDictionary, Dictionary> implements DictionaryDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    private QDictionary qSubDictionary = new QDictionary("sub");

    @Override
    public Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam) {
        // 结果条数以根字典为准
        SQLQuery<Dictionary> rootQuery = queryFactory.selectDistinct(qDictionary)
                .from(qDictionary)
                .leftJoin(qSubDictionary)
                .on(qDictionary.dictionaryId.eq(qSubDictionary.rootId))
                .where(qDictionary.rootId.isNull())
                // 搜索条件，支持子字典名称搜索
                .where(StringUtils.hasText(findDictionaryParam.getName()) ?
                        qDictionary.name.contains(findDictionaryParam.getName()).or(
                                qSubDictionary.name.contains(findDictionaryParam.getName())) : null)
                .where(StringUtils.hasText(findDictionaryParam.getValue()) ?
                        qDictionary.value.contains(findDictionaryParam.getValue()).or(
                                qSubDictionary.value.contains(findDictionaryParam.getValue())) : null);
        // 先查询根字典的总数
        long total = rootQuery.fetchCount();

        // 关联查根字典下的所有子字典项
        rootQuery.limit(findDictionaryParam.getLimit()).offset(findDictionaryParam.getOffset());
        List<Tuple> tuples = queryFactory.select(qDictionary, qSubDictionary)
                .from(rootQuery.as(qDictionary))
                .leftJoin(qSubDictionary)
                .on(qDictionary.dictionaryId.eq(qSubDictionary.rootId))
                .fetch();

        // 字典元组转换成字典列表
        List<Dictionary> dictionaries = new ArrayList<>();
        List<Long> dictionaryIds = new ArrayList<>();
        for (Tuple tuple : tuples) {
            for (Object object : tuple.toArray()) {
                Dictionary dictionary = (Dictionary) object;
                if (dictionary != null && dictionary.getDictionaryId() != null
                        && !dictionaryIds.contains(dictionary.getDictionaryId())) {
                    dictionaries.add(dictionary);
                    dictionaryIds.add(dictionary.getDictionaryId());
                }
            }
        }

        return new Page<>(total, dictionaries);
    }

    @Override
    public List<DictionaryNode> findRootSubDictionaries(String[] rootValues) {
        List<Tuple> tuples = queryFactory.select(
                QueryUtils.fitBean(DictionaryNode.class, qDictionary),
                QueryUtils.fitBean(DictionaryNode.class, qSubDictionary))
                .from(qDictionary)
                .leftJoin(qSubDictionary)
                .on(qDictionary.dictionaryId.eq(qSubDictionary.rootId))
                .where(qDictionary.value.in(rootValues))
                .fetch();
        // 字典元组转换成字典列表
        List<DictionaryNode> dictionaries = new ArrayList<>();
        List<Long> dictionaryIds = new ArrayList<>();
        for (Tuple tuple : tuples) {
            for (Object object : tuple.toArray()) {
                DictionaryNode dictionary = (DictionaryNode) object;
                if (dictionary != null && dictionary.getDictionaryId() != null
                        && !dictionaryIds.contains(dictionary.getDictionaryId())) {
                    dictionaries.add(dictionary);
                    dictionaryIds.add(dictionary.getDictionaryId());
                }
            }
        }
        return dictionaries;
    }
}
