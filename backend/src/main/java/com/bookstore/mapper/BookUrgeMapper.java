package com.bookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookstore.entity.BookUrge;
import com.bookstore.vo.UrgeStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookUrgeMapper extends BaseMapper<BookUrge> {

    @Select("""
            SELECT u.book_id AS bookId, b.title AS bookTitle, COUNT(*) AS count, MAX(u.create_time) AS latestTime
            FROM book_urge u
            LEFT JOIN book b ON b.id = u.book_id
            GROUP BY book_id
            ORDER BY latestTime DESC
            """)
    List<UrgeStatVO> listStats();
}
