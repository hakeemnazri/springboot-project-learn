package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.exceptions.APIException;
import com.spring_boot.ecommerce.exceptions.ResourceNotFoundException;
import com.spring_boot.ecommerce.model.Category;
import com.spring_boot.ecommerce.model.Product;
import com.spring_boot.ecommerce.payload.ProductDTO;
import com.spring_boot.ecommerce.payload.ProductResponse;
import com.spring_boot.ecommerce.repositories.CategoryRepository;
import com.spring_boot.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(categoryId, "Category", "categoryId"));

        boolean isProductPresent = false;

        List<Product> byProductNameLikeIgnoreCase = productRepository.findByCategoryOrderByPriceAsc(category);

        for (Product product : byProductNameLikeIgnoreCase){
            if(product.getProductName().equals(productDTO.getProductName())){
                isProductPresent = true;
                break;
            }
        }

        if(isProductPresent){
            throw new APIException("Product is present");
        }else{
            Product newProduct = modelMapper.map(productDTO, Product.class);
            newProduct.setImage("default.png");
            newProduct.setCategory(category);
            double specialPrice = newProduct.getPrice() - ((newProduct.getDiscount() * 0.01) * newProduct.getPrice());
            newProduct.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(newProduct);

            return modelMapper.map(newProduct, ProductDTO.class);
        }


    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> allProductsSort = productPage.getContent();

        List<ProductDTO> productDTOs = allProductsSort.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(categoryId, "Category", "categoryId"));

        List<Product> byCategoryOrderByPriceAsc = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOs = byCategoryOrderByPriceAsc.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        List<Product> byProductNameLikeIgnoreCase = productRepository.findByProductNameLikeIgnoreCase(keyword);

        List<ProductDTO> productDTOS = byProductNameLikeIgnoreCase.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();

        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(productId, "productId", "Product"));

        product.setProductName(productDTO.getProductName());
        product.setDiscount(productDTO.getDiscount());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setSpecialPrice(productDTO.getSpecialPrice());

        Product savedProduct = productRepository.save(product);

        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);

        return savedProductDTO;
    }

    @Override
    public ProductDTO deleteById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(productId, "productId", "Product"));
        productRepository.deleteById(product.getProductId());

        return modelMapper.map(product, ProductDTO.class);
    }
}
