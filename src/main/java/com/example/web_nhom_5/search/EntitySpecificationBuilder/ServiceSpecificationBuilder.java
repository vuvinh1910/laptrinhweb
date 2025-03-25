package com.example.web_nhom_5.search.EntitySpecificationBuilder;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.ServiceEntity;
import com.example.web_nhom_5.enums.SearchOperation;
import com.example.web_nhom_5.search.EntitySpecification.RoomSpecification;
import com.example.web_nhom_5.search.EntitySpecification.ServiceSpecification;
import com.example.web_nhom_5.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecificationBuilder {
    private final List<SearchCriteria> params;
    public ServiceSpecificationBuilder(){
        params=new ArrayList<>();
    }
    public final ServiceSpecificationBuilder with(String key, String operation, Object value,
                                               String prefix, String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public ServiceSpecificationBuilder with(String orPredicate,String key, String operation, Object value, String prefix, String suffix)
    {

        SearchOperation op=SearchOperation.getSimpleOperation(operation.charAt(0));
        if(op!=null) {
            if (op==SearchOperation.EQUALITY) {
                boolean startWithAsterisk =prefix.contains("*");
                boolean endWithAsterisk=suffix.contains("*");
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
            params.add(new SearchCriteria(orPredicate,key,op,value));
        }
        return this;
    }
    public Specification<ServiceEntity> build()
    {
        if(params.isEmpty())
        {
            return null;
        }
        Specification<ServiceEntity> result= new ServiceSpecification(params.getFirst());
        for(int i=1;i<params.size();i++)
        {
           result=params.get(i).isOrPredicate()?Specification.where(result).or(new ServiceSpecification(params.get(i))):
                   Specification.where(result).and(new ServiceSpecification(params.get(i)));
        }
        return result;
    }
}
