package com.suspensive.store.services;

import com.suspensive.store.DataProvider;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.repositories.ProductRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProductTest() {
        when(productRepository.save(any(ProductEntity.class))).thenReturn(DataProvider.productMock());

        ProductEntity productSaved = productService.addProduct(DataProvider.productMock());

        verify(productRepository).save(any(ProductEntity.class));
        assertEquals("Charger",productSaved.getName());
        assertEquals("electronics",productSaved.getCategory());
    }

    @Test
    void addProductsTest(){
        when(productRepository.saveAll(any(List.class))).thenReturn(DataProvider.productsMock());

        List<ProductEntity> products = productService.addProducts(DataProvider.productsMock());

        verify(productRepository).saveAll(any(List.class));
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    void getProductTest() {
        when(productRepository.findAll()).thenReturn(DataProvider.productsMock());

        List<ProductEntity> products = productService.getProducts();

        verify(productRepository).findAll();
        assertEquals("Charger",products.get(0).getName());
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    void editProductTest() throws ProductNotFoundException {
        Long id = 1L;
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.productMock()));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(DataProvider.newProductMock());

        ProductEntity productUpdated = productService.editProduct(id,DataProvider.productMock());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());
        verify(productRepository).save(any(ProductEntity.class));

        assertEquals(id,idCaptor.getValue());
        assertEquals(25f,productUpdated.getPrice(),0.0001f);
        assertEquals(5f, productUpdated.getStock(),0.0001f);
        assertTrue(productUpdated.isPremium());
    }

    @Test
    void editProductNotFoundTest(){
        Long id = 1L;
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.editProduct(id,DataProvider.productMock()));
        verify(productRepository).findById(anyLong());
    }

    @Test
    void getProductsByCategoryTest(){
        String category = "home";
        when(productRepository.findByCategory(any(String.class))).thenReturn(DataProvider.productsMockByCategory());

        List<ProductEntity> products = productService.getProducts(category);

        verify(productRepository).findByCategory(category);
        verify(productRepository).findByCategory(any(String.class));
        assertNotNull(products);
        assertTrue(products.get(0).getCategory().equals("home"));
        assertTrue(products.get(1).getCategory().equals("home"));
    }

    @Test
    void deleteProductTest() throws ProductNotFoundException {
        Long id = 1L;
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.productMock()));
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProduct(id);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());
        verify(productRepository).deleteById(anyLong());
        assertEquals(id,idCaptor.getValue());
    }

    @Test
    void deleteProductNotFoundTest(){
        Long id = 1L;
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
        verify(productRepository).findById(anyLong());
    }

}
