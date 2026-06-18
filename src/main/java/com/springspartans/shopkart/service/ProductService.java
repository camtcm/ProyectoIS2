package com.springspartans.shopkart.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springspartans.shopkart.exception.InvalidImageUploadException;
import com.springspartans.shopkart.model.Product;
import com.springspartans.shopkart.model.ProductDetails;
import com.springspartans.shopkart.repository.ProductRepository;
import com.springspartans.shopkart.util.ImageUploadValidator;

@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private String uploadPath;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
    private ImageUploadValidator imageUploadValidator;

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(int id) {
		return productRepository.findById(id).orElse(null);
	}

	public List<String> getAllCategories() {
		List<String> categoryList = new ArrayList<>();
		categoryList.add("All");
		categoryList.addAll(productRepository.findAllCategories());
		return categoryList;
	}

	public List<Product> getProductsByCategory(String category) {
		if (category.equals("All"))
			return getAllProducts();
		return productRepository.findByCategory(category);
	}

	public List<Product> getProductsByStartName(String prefix) {
		return productRepository.findByStartName(prefix);
	}
	
	public void addProduct(int id, ProductDetails details, MultipartFile image)
			throws IOException, InvalidImageUploadException {
		String imageName = null;
		if (image != null && !image.isEmpty()) {
			if (!imageUploadValidator.isValidImage(image)) {
	            throw new InvalidImageUploadException("Improper file format!");
	        }
	        imageName = image.getOriginalFilename();
	    }
		Product product = new Product(id, new ProductDetails(details.getName(), details.getCategory(), details.getBrand(), details.getPrice(), imageName, details.getStock(), details.getDiscount()));
		productRepository.save(product);
		if (imageName != null)
			saveImageToDirectory(image, imageName, "product");
	}

	public void updateProduct(int id, ProductDetails details, MultipartFile image)
			throws IOException, InvalidImageUploadException {
		Product existingProduct = productRepository.findById(id)
		        .orElseThrow(() -> new RuntimeException("Product not found"));
		existingProduct.setName(details.getName());
	    existingProduct.setCategory(details.getCategory());
	    existingProduct.setBrand(details.getBrand());
	    existingProduct.setPrice(details.getPrice());
	    existingProduct.setStock(details.getStock());
	    existingProduct.setDiscount(details.getDiscount());
		if (image != null && !image.isEmpty()) {
			if (!imageUploadValidator.isValidImage(image)) {
	            throw new InvalidImageUploadException("Improper file format!");
	        }
	        String imageName = image.getOriginalFilename();
	        existingProduct.setImage(imageName);
	        saveImageToDirectory(image, imageName, "product");
		}
		productRepository.save(existingProduct);
	}
	
	private void saveImageToDirectory(MultipartFile image, String imageName, String folderName) throws IOException {
	    File destination = new File(uploadPath, folderName);
	    if (!destination.exists()) {
	        boolean created = destination.mkdirs(); 
	        boolean writable = destination.setWritable(true);
        if (!writable) {
            logger.warn("Could not set directory as writable: {}", destination.getAbsolutePath());
        }
	        if (created) {
        		logger.info("Created directory : {}", destination.getAbsolutePath());
        	} else {
        		logger.info("{} already exists", destination.getAbsolutePath());
        	}
	    }
	    File fileToSave = new File(destination, imageName);
	    image.transferTo(fileToSave);
	    logger.info("Saved file : {}", fileToSave.getAbsolutePath());
	}

	public void deleteProduct(int id) {
		productRepository.deleteById(id);
	}
	
	public int countProducts() {
		return (int) productRepository.count();
	}
	
}
