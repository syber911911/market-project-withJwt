package com.likelion.market.global.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
// readAll 의 결과인 Page 객체에서 필요한 정보만을 추출해
// 사용자에게 제공하기 위한 Dto
public class PageDto<T> {
    private List<T> content;
    private Integer totalPage;
    private Long totalElements;
    private boolean last;
    private Integer size;
    private Integer number;
    private Integer numberOfElements;
    private boolean first;
    private boolean empty;

    // Page 객체를 받아 PageDto 로 변환
    public PageDto<T> makePage(Page<T> originPage) {
        PageDto<T> page = new PageDto<>();
        page.setContent(originPage.getContent());
        page.setTotalPage(originPage.getTotalPages());
        page.setTotalElements(originPage.getTotalElements());
        page.setLast(originPage.isLast());
        page.setSize(originPage.getSize());
        page.setNumber(originPage.getNumber() + 1);
        page.setNumberOfElements(originPage.getNumberOfElements());
        page.setFirst(originPage.isFirst());
        page.setEmpty(originPage.isEmpty());
        return page;
    }
}
