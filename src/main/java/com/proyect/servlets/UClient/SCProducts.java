package com.proyect.servlets.UClient;

import com.proyect.modelsDAO.OProduct.ProductDAO;
import com.proyect.modelsDTO.OProduct.Category;
import com.proyect.modelsDTO.OProduct.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SCProducts", value = "/SCProducts")
public class SCProducts extends HttpServlet {

    List<Product> products = new ArrayList<>(); // Listar productos del inicio (todos)
    List<Product> productsCategories = new ArrayList<>(); // Para listar productos segun categorias
    List<Product> categories = new ArrayList<>(); //Para listar categorias
    Product product = new Product();
    Category category = new Category();
    ProductDAO pdao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "listProduct":
                int idCategory = Integer.parseInt(request.getParameter("idCategory"));
                productsCategories = pdao.listCategory(idCategory);
                request.setAttribute("idCategory", idCategory);
                request.setAttribute("productsC", productsCategories);
                this.list(request, response);
                break;
            case "search":
                categories = pdao.list();
                String text = request.getParameter("search");
                products = pdao.search(text);
                request.setAttribute("categories", categories);
                request.setAttribute("products", products);
                request.getRequestDispatcher("/views/user/store.jsp").forward(request, response);
                break;
            case "detail":
                int idProduct = Integer.parseInt(request.getParameter("idProduct"));
                product = pdao.byId(idProduct);
                category = product.getCategory();
                idCategory = category.getIdCategory();
                productsCategories = pdao.listCategory(idCategory);
                request.setAttribute("productsC",productsCategories);
                request.setAttribute("product", product);
                request.getRequestDispatcher("/views/user/detail-product.jsp").forward(request, response);
                break;
            default:
                this.list(request, response);
                break;
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        products = pdao.list();
        categories = pdao.list();
        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/views/user/store.jsp").forward(request, response);
    }

}
