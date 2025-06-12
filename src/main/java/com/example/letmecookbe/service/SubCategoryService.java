package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.SubCategoryCreationRequest;
import com.example.letmecookbe.dto.request.SubCategoryUpdateRequest;
import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.entity.SubCategory;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.SubCategoryMapper;
import com.example.letmecookbe.repository.MainCategoryRepository;
import com.example.letmecookbe.repository.SubCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryService {
    MainCategoryRepository MainRepository;
    SubCategoryRepository SubRepository;
    SubCategoryMapper subCategoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponse createSubCategory(String id,SubCategoryCreationRequest request){
        MainCategory main = MainRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST)
        );
        if(SubRepository.existsBySubCategoryName(request.getSubCategoryName())){
            throw new AppException(ErrorCode.SUB_CATEGORY_EXISTED);
        }
        SubCategory sub = subCategoryMapper.toSubCategory(request);
        sub.setMainCategory(main);
        SubCategory savedSub = SubRepository.save(sub);
        return subCategoryMapper.toSubCategoryResponse(savedSub);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponse updateSubCategoryName(String id, SubCategoryUpdateRequest request){
        SubCategory sub = SubRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        if(!request.getSubCategoryName().isBlank()){
            if(SubRepository.existsBySubCategoryName(request.getSubCategoryName())){
                throw new AppException(ErrorCode.SUB_CATEGORY_EXISTED);
            }
            sub.setSubCategoryName(request.getSubCategoryName());
        }
        if(!request.getSubCategoryImg().isBlank())
            sub.setSubCategoryImg(request.getSubCategoryImg());
        if(!request.getCategoryId().isBlank())
            sub.setMainCategory(MainRepository.findById(request.getCategoryId()).orElseThrow(
                    ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST)
            ));
        SubCategory savedSub = SubRepository.save(sub);
        return subCategoryMapper.toSubCategoryResponse(savedSub);
    }

    @PreAuthorize("hasAuthority('GET_SUB_CATEGORY_BY_MAIN_CATEGORY')")
    public List<SubCategory> getSubCategoryByManCategoryId(String id){
        if(!MainRepository.existsById(id)){
            throw new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST);
        }
        return SubRepository.findAllByMainCategoryId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSubCategory(String id){
        SubCategory sub = SubRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        SubRepository.delete(sub);
        if(SubRepository.existsBySubCategoryName(sub.getSubCategoryName())){
            return "delete sub category failed: "+ id;
        }
        return "delete sub category success: "+ id;
    }
}
