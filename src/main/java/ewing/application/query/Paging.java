package ewing.application.query;

import com.github.pagehelper.Page;

import java.util.List;
import java.util.function.Consumer;

/**
 * 分页数据。
 *
 * @author Ewing
 **/
public class Paging<T> {
    private long total;

    private List<T> rows;

    public Paging() {
    }

    public Paging(List<T> rows) {
        if (rows == null) {
            return;
        }
        this.rows = rows;
        this.total = rows.size();
    }

    public Paging(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Paging(Page<T> page) {
        this.total = page.isCount() ? page.getTotal() : page.size();
        this.rows = page;
    }

    public long getTotal() {
        return total;
    }

    public Paging<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getRows() {
        return rows;
    }

    public Paging<T> setRows(List<T> rows) {
        this.rows = rows;
        return this;
    }

    public Paging<T> forEach(Consumer<T> consumer) {
        if (rows != null && rows.size() > 0) {
            this.rows.forEach(consumer);
        }
        return this;
    }
}
