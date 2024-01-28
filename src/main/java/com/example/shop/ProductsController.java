package com.example.shop;


import com.example.shop.model.Products;
import com.example.shop.model.Users;
import com.example.shop.repositories.ProductsRepository;
import com.example.shop.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductsController {

    private ProductsRepository productRepository;
    private UsersRepository usersRepository;
    private Users logged = null;

    @Autowired
    public void setProductsRepository(ProductsRepository productsRepository){
        productRepository = productsRepository;
    }
    @Autowired
    public void setUsersRepository(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public String getCenterPage(Model model){
        Iterable<Products> products = productRepository.findAll();
        model.addAttribute("products", products);
        Optional<List<String>> categories = productRepository.getAllCategories();
        model.addAttribute("categories", categories);
        if (logged != null){
            model.addAttribute("status", logged.getStatus());
        }
        return "center";
    }
    @GetMapping("/category/{category}")
    public String getCategoryItems(@PathVariable("category") String category, Model model) {
        List<Products> products = productRepository.findByCategory(category).orElse(null);
        Optional<List<String>> categories = productRepository.getAllCategories();

        if (products.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category not found"
            );
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        if (logged != null){
            model.addAttribute("status", logged.getStatus());
        }
        return "category";
    }

    @GetMapping("/details/{id}")
    public String getItemDetails(@PathVariable int id, Model model){
        Optional<Products> product = productRepository.getProductDetails(id);
        Optional<List<String>> categories = productRepository.getAllCategories();
        if (product.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found"
            );
        }
        model.addAttribute("product", product.get());
        model.addAttribute("categories", categories);
        if (logged != null){
            model.addAttribute("status", logged.getStatus());
        }
        return "details";
    }

    @PostMapping("/buy")
    public String buyProduct(@RequestParam("quantity") Integer quantity, @RequestParam("id") int id, Model model) {
        Optional<List<String>> categories = productRepository.getAllCategories();
        Products product = productRepository.getProductDetails(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
        ));
        Integer availableQuantity = product.getQuantity();

        if (availableQuantity >= quantity) {
            product.setQuantity(availableQuantity - quantity);
            productRepository.save(product);
            model.addAttribute("announcement", "Kupiłeś " + quantity + " sztuk produktu " + product.getName() + ".");
        }

        else {
            model.addAttribute("announcement", "Nie ma wystarczającej ilości produktu " + product.getName() + " w magazynie.");
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "details";
    }



    @GetMapping("/login")
    public String loginPage (Model model){
        if (logged != null){
            return "redirect:/";
        }
        Optional<List<String>> categories = productRepository.getAllCategories();
        model.addAttribute("categories",categories);
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   Model model) {
        Optional<List<String>> categories = productRepository.getAllCategories();
        Optional<String> passwordCorrect = usersRepository.findPassword(username);

        if (passwordCorrect.isEmpty()) {
            model.addAttribute("categories", categories);
            model.addAttribute("announcement", "Nie posiadamy w bazie użytkownika o takiej nazwie, spróbuj ponownie.");
            return "login";
        }

        else if(passwordCorrect.get().equals(password)) {
            logged = usersRepository.getUser(username);
            return "redirect:/";
        }

        else{
            model.addAttribute("categories",categories);
            model.addAttribute("announcement", "Podałeś błędne hasło. Spróbuj ponownie.");
            return "login";
        }
    }

    @GetMapping("/login/register")
    public String addNewUserPage(Model model){
        if(logged != null){
            return "redirect:/";
        }
        else{
            Optional<List<String>> categories = productRepository.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("user", new Users());
            return "register";
        }
    }

    @PostMapping("/login/register")
    public String addNewUser(@ModelAttribute("user") Users user,
                             @RequestParam("confirm_password") String confirm_password,
                             Model model){
        Optional<List<String>> categories = productRepository.getAllCategories();
        if(!user.getPassword().equals(confirm_password)){
            model.addAttribute("categories", categories);
            model.addAttribute("announcement", "Podane hasła nie są takie same.");
            return "register";
        }
        else if(usersRepository.userExist(user.getUsername())){
            model.addAttribute("categories", categories);
            model.addAttribute("announcement", "Użytkownik o takiej nazwie już istnieje.");
            return "register";
        }
        else{
            usersRepository.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/admin")
    public String adminPanel (Model model){
        if(logged == null || !logged.getStatus().equals("admin")){
            return "redirect:/";
        }
        else{
            model.addAttribute("user", logged);
            Optional<List<String>> categories = productRepository.getAllCategories();
            model.addAttribute("categories", categories);
            return "admin";
        }

    }

    @GetMapping("/admin/addNewProduct")
    public String addNewProduct(Model model){
        if(logged == null || !logged.getStatus().equals("admin")){
            return "redirect:/";
        }
        else {
            Optional<List<String>> categories = productRepository.getAllCategories();
            model.addAttribute("product", new Products());
            model.addAttribute("categories", categories);
            model.addAttribute("status",logged.getStatus());
            return "addNewProduct";
        }
    }
    @PostMapping("/admin/addNewProduct")
    public String addNewProductPost(@ModelAttribute("product") Products product, Model model){
        productRepository.save(product);
        Iterable<Products> products = productRepository.findAll();
        model.addAttribute("products", products);
        Optional<List<String>> categories = productRepository.getAllCategories();
        model.addAttribute("categories", categories);
        return "redirect:/admin";
    }

    @GetMapping("/admin/deleteProduct")
    public String deleteProduct(Model model){
        if(logged == null){
            return "redirect:/";
        }
        else{
            Iterable<Products> products = productRepository.findAll();
            model.addAttribute("products", products);
            Optional<List<String>> categories = productRepository.getAllCategories();
            model.addAttribute("categories", categories);
            return "deleteProduct";
        }
    }
    @PostMapping("/admin/deleteProduct/{id}")
    public String deleteProductPost(@PathVariable int id, Model model){
        productRepository.deleteById(id);
        return "redirect:/admin/deleteProduct";
    }

    @GetMapping("/admin/editProduct")
    public String editProductListPage(Model model){
        if(logged == null || !logged.getStatus().equals("admin")){
            return "redirect:/";
        }
        else{
            Iterable<Products> products = productRepository.findAll();
            model.addAttribute("products", products);
            Optional<List<String>> categories = productRepository.getAllCategories();
            model.addAttribute("categories", categories);
            return "editProduct";
        }

    }

    @GetMapping("/admin/editProduct/{id}")
    public String getEditProduct(@PathVariable int id, Model model){
        if(logged == null || !logged.getStatus().equals("admin")){
            return "redirect:/";
        }
        else {
            Optional<Products> product = productRepository.getProductDetails(id);
            Optional<List<String>> categories = productRepository.getAllCategories();
            if (product.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"
                );
            }
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categories);
            return "editProductPage";
        }

    }
    @PostMapping("/admin/editProduct/{id}")
    public String editProduct(@PathVariable int id, @ModelAttribute("product") Products product) {
        Products editedProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu o id: " + id));
        editedProduct.setName(product.getName());
        editedProduct.setDescription(product.getDescription());
        editedProduct.setImageUrl(product.getImageUrl());
        editedProduct.setPrice(product.getPrice());
        editedProduct.setQuantity(product.getQuantity());
        editedProduct.setCategory(product.getCategory());
        editedProduct.setManufacturer(product.getManufacturer());
        productRepository.save(editedProduct);
        return "redirect:/admin/editProduct";
    }

    @GetMapping("/api")
    public String apiPage(Model model){
        Iterable<Products> products = productRepository.findAll();
        model.addAttribute("products", products);
        Optional<List<String>> categories = productRepository.getAllCategories();
        model.addAttribute("categories", categories);
        if (logged != null){
            model.addAttribute("status", logged.getStatus());
        }
        return "api";
    }

    @GetMapping("/logout")
    public String logout(Model model){
        logged = null;
        return "redirect:/";
    }

}


