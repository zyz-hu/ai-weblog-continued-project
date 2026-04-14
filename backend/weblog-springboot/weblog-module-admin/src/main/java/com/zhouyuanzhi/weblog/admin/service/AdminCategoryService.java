package com.zhouyuanzhi.weblog.admin.service;

import com.zhouyuanzhi.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.zhouyuanzhi.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.zhouyuanzhi.weblog.admin.model.vo.category.FindCategoryPageListReqVO;
import com.zhouyuanzhi.weblog.common.utils.PageResponse;
import com.zhouyuanzhi.weblog.common.utils.Response;

/**
 * @author: zyz
 * @description: TODO
 **/
public interface AdminCategoryService {
    /**
     * 添加分类
     * @param addCategoryReqVO
     * @return
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据查询
     * @param findCategoryPageListReqVO
     * @return
     */
    PageResponse findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     * @param deleteCategoryReqVO
     * @return
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的 Select 列表数据
     * @return
     */
    Response findCategorySelectList();

}
