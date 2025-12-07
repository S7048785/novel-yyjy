package com.novel.service.impl;

import com.novel.dto.BookInfoDoc;
import com.novel.repository.BookRepository;
import com.novel.result.PageResult;
import com.novel.service.SearchService;
import com.novel.user.dto.book.AllBookQueryInput;
import com.novel.user.dto.book.BookInfoSearchView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "enable", havingValue = "false")
@Service
@RequiredArgsConstructor
@Slf4j
public class DbSearchServiceImpl implements SearchService {
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public PageResult<BookInfoDoc> conditionSearchBooks(AllBookQueryInput condition, int pageNum, int pageSize) {
        return null;
    }
    
    @Override
    public List<BookInfoSearchView> searchBooksByName(String name) {
        return bookRepository.search(name);
    }
}