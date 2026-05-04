create table if not exists book_category
(
    id             int auto_increment
        primary key,
    work_direction tinyint unsigned              not null comment '作品方向;0-男频 1-女频',
    name           varchar(20)                   not null comment '类别名',
    sort           tinyint unsigned default '10' not null comment '排序',
    create_time    datetime                      not null comment '创建时间',
    update_time    datetime                      not null comment '更新时间',
    del_flag       tinyint          default 0    not null
)
    comment '小说类别';

create table if not exists book_chapter
(
    id           int auto_increment
        primary key,
    book_id      bigint unsigned              not null comment '小说ID',
    chapter_num  smallint unsigned            not null comment '章节号',
    chapter_name varchar(100)                 not null comment '章节名',
    word_count   int unsigned                 not null comment '章节字数',
    is_vip       tinyint unsigned default '0' not null comment '是否收费;1-收费 0-免费',
    create_time  datetime                     null,
    update_time  datetime                     null,
    del_flag     tinyint          default 0   not null,
    constraint uk_bookId_chapterNum
        unique (book_id, chapter_num)
)
    comment '小说章节';

create index idx_bookId
    on book_chapter (book_id);

create table if not exists book_comment
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    book_id         bigint unsigned          not null comment '评论小说ID',
    user_id         bigint unsigned          not null comment '评论用户ID',
    content         varchar(512)             not null comment '评价内容',
    ip              char(128)                not null,
    address         char(16) default '未知'  not null comment 'ip属地',
    reply_nick_name char(125)                null comment '回复的昵称',
    parent_id       bigint unsigned          null comment '父评论id',
    root_parent_id  bigint unsigned          null comment '根评论id; 为null表示根评论',
    create_time     datetime default (now()) not null comment '创建时间',
    del_flag        tinyint  default 0       not null
)
    comment '小说评论';

create table if not exists book_content
(
    id          int auto_increment
        primary key,
    chapter_id  bigint unsigned   not null comment '章节ID',
    content     mediumtext        not null comment '小说章节内容',
    create_time datetime          null,
    update_time datetime          null,
    del_flag    tinyint default 0 not null,
    constraint uk_chapterId
        unique (chapter_id)
)
    comment '小说内容';

create table if not exists book_info
(
    id                       bigint unsigned auto_increment comment '主键'
        primary key,
    work_direction           tinyint unsigned               null comment '作品方向;0-男频 1-女频',
    category_id              bigint unsigned                null comment '类别ID',
    category_name            varchar(50)                    null comment '类别名',
    pic_url                  varchar(200)                   not null comment '小说封面地址',
    book_name                varchar(50)                    not null comment '小说名',
    author_id                bigint unsigned                not null comment '作家id',
    author_name              varchar(50)                    not null comment '作家名',
    book_desc                varchar(2000)                  not null comment '书籍描述',
    score                    tinyint unsigned               not null comment '评分;总分:10 ，真实评分 = score/10',
    book_status              tinyint unsigned default '0'   not null comment '书籍状态;0-连载中 1-已完结',
    visit_count              bigint unsigned  default '103' not null comment '点击量',
    word_count               int unsigned     default '0'   not null comment '总字数',
    comment_count            int unsigned     default '0'   not null comment '评论数',
    last_chapter_id          bigint unsigned                null comment '最新章节ID',
    last_chapter_name        varchar(50)                    null comment '最新章节名',
    last_chapter_update_time datetime                       null comment '最新章节更新时间',
    is_vip                   tinyint unsigned default '0'   not null comment '是否收费;1-收费 0-免费',
    create_time              datetime                       null comment '创建时间',
    update_time              datetime                       null comment '更新时间',
    del_flag                 tinyint          default 0     not null,
    constraint pk_id
        unique (id),
    constraint uk_bookName_authorName
        unique (book_name, author_name)
)
    comment '小说信息';

create index idx_createTime
    on book_info (create_time);

create index idx_lastChapterUpdateTime
    on book_info (last_chapter_update_time);

create table if not exists chapter_summary
(
    id          bigint auto_increment
        primary key,
    book_id     bigint                   not null,
    chapter_id  bigint                   not null,
    segment_num tinyint                  not null comment '段落号',
    comment_num int unsigned default '0' not null comment '评论量',
    constraint chapter_summary_pk
        unique (book_id, chapter_id, segment_num)
)
    comment '章节段落概要表';

create table if not exists home_book
(
    id          bigint unsigned auto_increment
        primary key,
    type        tinyint unsigned not null comment '推荐类型;0-轮播图 1-顶部栏 2-本周强推 3-热门推荐 4-精品推荐',
    intro       varchar(64)      null,
    tag         char(4)          null,
    sort        tinyint unsigned not null comment '推荐排序',
    book_id     bigint unsigned  not null comment '推荐小说ID',
    create_time datetime         null comment '创建时间',
    update_time datetime         null comment '更新时间',
    constraint pk_id
        unique (id)
)
    comment '小说推荐';

create table if not exists news_category
(
    id          bigint unsigned auto_increment
        primary key,
    name        varchar(20)                   not null comment '类别名',
    sort        tinyint unsigned default '10' not null comment '排序',
    create_time datetime                      null comment '创建时间',
    update_time datetime                      null comment '更新时间',
    constraint pk_id
        unique (id)
)
    comment '新闻类别';

create table if not exists news_content
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    news_id     bigint unsigned not null comment '新闻ID',
    content     mediumtext      not null comment '新闻内容',
    create_time datetime        null comment '创建时间',
    update_time datetime        null comment '更新时间',
    constraint pk_id
        unique (id),
    constraint uk_newsId
        unique (news_id)
)
    comment '新闻内容';

create table if not exists news_info
(
    id            bigint unsigned auto_increment comment '主键'
        primary key,
    category_id   bigint unsigned not null comment '类别ID',
    category_name varchar(50)     not null comment '类别名',
    source_name   varchar(50)     not null comment '新闻来源',
    title         varchar(100)    not null comment '新闻标题',
    create_time   datetime        null comment '创建时间',
    update_time   datetime        null comment '更新时间',
    constraint pk_id
        unique (id)
)
    comment '新闻信息';

create table if not exists segment_comment
(
    id          bigint unsigned auto_increment
        primary key,
    user_id     bigint unsigned                        not null comment '用户id',
    chapter_id  bigint unsigned                        not null comment '章节id',
    segment_num tinyint                                not null comment '段落号',
    level       int unsigned default '1'               not null comment '楼层',
    content     varchar(128)                           not null comment '评论内容',
    like_count  int unsigned default '0'               not null,
    ip          char(15)                               not null,
    ip_address  char(10)                               not null comment 'ip属地',
    create_time datetime     default CURRENT_TIMESTAMP not null,
    del_flag    tinyint      default 0                 not null
)
    comment '章节段落评论';

create index segment_comment_segment_num_chapter_id_index
    on segment_comment (segment_num, chapter_id);

create table if not exists user_bookshelf
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    user_id        bigint unsigned                    not null comment '用户ID',
    book_id        bigint unsigned                    not null comment '小说ID',
    pre_content_id bigint unsigned                    null comment '上一次阅读的章节内容表ID',
    state          tinyint  default 0                 not null,
    create_time    datetime default (now())           not null comment '创建时间;',
    update_time    datetime default CURRENT_TIMESTAMP not null comment '更新时间;',
    constraint pk_id
        unique (id),
    constraint uk_userId_bookId
        unique (user_id, book_id)
)
    comment '用户书架';

create table if not exists user_comment
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    user_id         bigint unsigned              not null comment '评论用户ID',
    book_id         bigint unsigned              not null comment '评论小说ID',
    comment_content varchar(512)                 not null comment '评价内容',
    reply_count     int unsigned     default '0' not null comment '回复数量',
    audit_status    tinyint unsigned default '0' not null comment '审核状态;0-待审核 1-审核通过 2-审核不通过',
    create_time     datetime                     null comment '创建时间',
    update_time     datetime                     null comment '更新时间',
    constraint pk_id
        unique (id),
    constraint uk_bookId_userId
        unique (book_id, user_id)
)
    comment '用户评论';

create table if not exists user_info
(
    id          bigint unsigned auto_increment
        primary key,
    email       varchar(50)                      not null comment '邮箱',
    password    varchar(100)                     not null comment '登录密码',
    nick_name   varchar(50)                      not null comment '昵称',
    user_photo  varchar(100)                     null comment '用户头像',
    status      tinyint unsigned default '0'     not null comment '用户状态;0-正常',
    role        varchar(16)      default 'user'  not null comment '0 - 普通用户; 1 - 管理员',
    create_time datetime         default (now()) null comment '创建时间',
    update_time datetime         default (now()) null comment '更新时间',
    constraint pk_id
        unique (id),
    constraint uk_username
        unique (email)
)
    comment '用户信息';

create table if not exists user_read_history
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    user_id        bigint unsigned          not null comment '用户ID',
    book_id        bigint unsigned          not null comment '小说ID',
    pre_content_id bigint unsigned          not null comment '上一次阅读的章节内容表ID',
    create_time    datetime default (now()) null comment '创建时间;',
    update_time    datetime default (now()) null comment '更新时间;',
    constraint pk_id
        unique (id),
    constraint uk_userId_bookId
        unique (user_id, book_id)
)
    comment '用户阅读历史';


