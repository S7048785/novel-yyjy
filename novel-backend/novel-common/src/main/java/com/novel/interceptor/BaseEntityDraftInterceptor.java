package com.novel.interceptor;

import com.novel.po.BaseEntity;
import com.novel.po.BaseEntityDraft;
import com.novel.po.BaseEntityProps;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 保存前拦截器
 */
@Component
public class BaseEntityDraftInterceptor 
implements DraftInterceptor<BaseEntity, BaseEntityDraft> {
    
    @Override
    public void beforeSave(BaseEntityDraft draft, @Null BaseEntity original) {
        // 更新时间
        if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.UPDATE_TIME)) {
            draft.setUpdateTime(LocalDateTime.now());
        }
        // 若没有原型，表示新增，则设置创建时间
        if (original == null) {
            if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATE_TIME)) {
                draft.setCreateTime(LocalDateTime.now());
            }
        }
    }
    
    
    
}