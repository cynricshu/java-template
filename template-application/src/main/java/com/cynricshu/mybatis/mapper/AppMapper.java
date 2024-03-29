package com.cynricshu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.cynricshu.model.app.App;
import com.cynricshu.model.dataobject.AppDo;
import com.cynricshu.model.request.AppListRequest;

/**
 * AppMapper
 *
 * @author Cynric Shu
 */
@Mapper
public interface AppMapper {
    @Insert({
            "INSERT INTO app (name, create_time)",
            "values (#{name}, #{createTime})"
    })
    @Options(useGeneratedKeys = true)
    int insert(AppDo dbObject);

    @Select({"<script>",
            "SELECT * FROM app",
            "<where>",
            "<if test='createTime != null'>create_time = #{createTime}</if>",
            "</where>",
            "</script>"})
    List<App> select(AppListRequest request);
}
