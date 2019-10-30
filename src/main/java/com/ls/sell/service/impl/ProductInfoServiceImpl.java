package com.ls.sell.service.impl;

import com.ls.sell.dataobject.ProductInfo;
import com.ls.sell.dto.CartDTO;
import com.ls.sell.enums.ProductStatusEnum;
import com.ls.sell.enums.ResultEunm;
import com.ls.sell.exception.SellException;
import com.ls.sell.repository.ProductInfoRepository;
import com.ls.sell.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @program: sell->ProductInfoServiceImpl
 * @description:
 * @author: liushuai
 * @create: 2019-08-04 08:45
 **/
@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ProductInfo findById(String productId) {
        return productInfoRepository.findById(productId).get();
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return productInfoRepository.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return productInfoRepository.save(productInfo);
    }

    /**
     * 加库存
     *
     * @param cartDTOList
     * @return
     * @author liushuai
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(List<CartDTO> cartDTOList) {
        cartDTOList.forEach(
                cartDTO -> {
                    ProductInfo productInfo = productInfoRepository.getOne(cartDTO.getProductId());
                    //如果产品不存在
                    if(Objects.isNull(productInfo)){
                        throw new SellException(ResultEunm.PRODUCT_NOT_EXIST);
                    }

                    Integer result  = productInfo.getProductStock() + cartDTO.getProductQuantity();

                    productInfo.setProductStock(result);
                    productInfoRepository.save(productInfo);

                }
        );
    }

    /**
     * 减去库存
     *
     * @param cartDTOList
     * @return
     * @author liushuai
     */
    @Override
    public void decreaseStock(List<CartDTO> cartDTOList) {
        cartDTOList.forEach(
                cartDTO -> {
                    ProductInfo productInfo = productInfoRepository.getOne(cartDTO.getProductId());
                    //产品不存在
                    if(Objects.isNull(productInfo)){
                        throw new SellException(ResultEunm.PRODUCT_NOT_EXIST);
                    }

                   int result =  productInfo.getProductStock() - cartDTO.getProductQuantity();
                    //库存不足

                    if(result<0)
                    {
                        throw new SellException(ResultEunm.PRODUCT_STOCK_ERROR);
                    }
                    productInfo.setProductStock(result);
                    productInfoRepository.save(productInfo);

                }
        );
    }
}
