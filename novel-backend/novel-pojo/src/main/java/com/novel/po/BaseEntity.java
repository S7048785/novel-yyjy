package com.novel.po;

import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.babyfish.jimmer.sql.OnDissociate;

import java.time.LocalDateTime;

@MappedSuperclass
public interface BaseEntity {
    
    @Null
    LocalDateTime createTime();
    
    @Null
    LocalDateTime updateTime();
}