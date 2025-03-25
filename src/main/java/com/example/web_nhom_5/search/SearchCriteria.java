package com.example.web_nhom_5.search;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.enums.SearchOperation;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class SearchCriteria {
    private final String keyword;
    private final SearchOperation operation;
    private final Object value;
    private boolean orPredicate;
    public SearchCriteria(String keyword, SearchOperation searchOperation, Object value) {
        super();
        this.keyword=keyword;
        this.operation=searchOperation;
        this.value=value;
    }
    public SearchCriteria(String orPredicate,String keyword,SearchOperation searchOperation,Object value)
    {
        super();
        this.orPredicate=orPredicate!=null &&orPredicate.equals("'");
        this.keyword=keyword;
        this.operation=searchOperation;
        this.value=value;
    }
    public SearchCriteria(String keyword,String operation,Object value,String prefix,String suffix)
    {
        SearchOperation op=SearchOperation.getSimpleOperation(operation.charAt(0));
        if(op!=null) {
            if (op == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix.contains("*");
                boolean endWithAsterisk = suffix.contains("*");
                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                }
            }
        }
        this.keyword=keyword;
        this.operation=op;
        this.value=value;
    }


}
