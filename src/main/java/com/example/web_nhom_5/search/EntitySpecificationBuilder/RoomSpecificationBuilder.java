package com.example.web_nhom_5.search.EntitySpecificationBuilder;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.enums.SearchOperation;
import com.example.web_nhom_5.search.EntitySpecification.RoomSpecification;
import com.example.web_nhom_5.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoomSpecificationBuilder {
    private final List<SearchCriteria> params;
    public RoomSpecificationBuilder()
    {
        params=new ArrayList<>();
    }
    public final RoomSpecificationBuilder with(String key, String operation, Object value,
                                                String prefix, String suffix) {
         return with(null, key, operation, value, prefix, suffix);
    }


    public RoomSpecificationBuilder with(String orPredicate,String key, String operation, Object value, String prefix, String suffix)
    {

        SearchOperation op=SearchOperation.getSimpleOperation(operation.charAt(0));
        if(op!=null) {
            if (op==SearchOperation.EQUALITY) {
                boolean startWithAsterisk =prefix!=null && prefix.contains("*");
            boolean endWithAsterisk=suffix !=null && suffix.contains("*");
            if (startWithAsterisk &&endWithAsterisk) {
                op=SearchOperation.CONTAINS;
            } else if (startWithAsterisk)
            {
                op=SearchOperation.STARTS_WITH;
            } else if(endWithAsterisk)
            {
                op=SearchOperation.ENDS_WITH;
            }
            }
            this.params.add(new SearchCriteria(orPredicate,key,op,value));
        }
        return this;
    }
    public Specification<RoomEntity> build()
    {
        if(params.isEmpty())
        {
            return null;
        }
        Specification<RoomEntity> result= new RoomSpecification(params.get(0));
        for (int i=1;i<params.size();i++)
             result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new RoomSpecification(params.get(i)))
                    : Specification.where(result).and(new RoomSpecification(params.get(i)));
        return result;
    }
}
