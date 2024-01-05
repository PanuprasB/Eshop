package com.example.kursinis.Controllers;

import com.example.kursinis.model.Comment;
import com.example.kursinis.model.Order;
import com.example.kursinis.model.Product;
import com.example.kursinis.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HibernateController {
    private EntityManagerFactory emf;

    public HibernateController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManager getEntityManager() {
        return emf.createEntityManager();

    }


    public void createProduct(Product product) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }

    public void updateProduct(Product product) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteProduct(Product product) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            product = em.merge(product);
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }


    public void createUser(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }

    }

    public void updateUser(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteUser(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            user = em.merge(user);
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }


    public List<Product> getAllProducts() {
        EntityManager em = null;
        List<Product> products = new ArrayList<>();
        try {
            em = getEntityManager();
            CriteriaQuery query = em.getCriteriaBuilder().createQuery();
            query.select(query.from(Product.class));
            Query q = em.createQuery(query);
            products = q.getResultList();
        } finally {
            if (em != null) em.close();
        }
        return products;
    }

    public List<User> getAllUsers() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }


    public Product getProductById(int i) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.find(Product.class, i);
        } finally {
            if (em != null) em.close();
        }
    }

    public User getUserById(int i) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.find(User.class, i);
        } finally {
            if (em != null) em.close();
        }
    }


    public void createOrder(Order order) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();

        }


    }

    public  void updateOrder(Order order){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(order);
            em.getTransaction().commit();
        } finally {
            if (em != null) em.close();
        }
    }


    public User getUserByCredentials(String login, String password) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.and(cb.like(root.get("login"), login), cb.like(root.get("password"), password)));
            Query q;

            q = em.createQuery(query);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public List<Order> getAllUnassignedOrders() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            query.select(root).where(cb.like(root.get("status"), "unassigned"));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }
    }
    public List<Order> getAllClaimedOders(){
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            query.select(root).where(cb.like(root.get("status"), "Claimed"));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }



    }
    public List<Order> getAllCompletedOrders(){
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            query.select(root).where(cb.like(root.get("status"), "Completed"));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }



    }

    public List<Order> getAllOrdersByUserId(int id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);

            query.select(root).where(cb.equal(root.get("client").get("id"), id));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }
    }

    public void addComment(Comment comment) {
    EntityManager em = null;
    try {
        em = getEntityManager();
        em.getTransaction().begin();
        em.persist(comment);
        em.getTransaction().commit();
    } finally {
        if (em != null) em.close();
    }
}

    public List<Comment> getCommentsByProductId(int productId) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
            Root<Comment> root = query.from(Comment.class);
            query.select(root).where(cb.equal(root.get("product").get("id"), productId));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }
    }

        public void deleteOrder(Order order) {
            EntityManager em = null;
            try {
                em = getEntityManager();
                em.getTransaction().begin();
                Order managedOrder = em.find(Order.class, order.getId());
                if (managedOrder != null) {
                    // Remove the order from the client
                    managedOrder.getClient().getMyOrders().remove(managedOrder);

                    managedOrder.setClient(null);

                    // Remove the order itself
                    em.remove(managedOrder);
                }
                em.getTransaction().commit();
            } finally {
                if (em != null) em.close();
            }
        }


    public List<Comment> getCommentsByOrderId(int orderId) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
            Root<Comment> root = query.from(Comment.class);
            query.select(root).where(cb.equal(root.get("order").get("id"), orderId));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }
    }

    public List<Order> getOrdersCreatedBetween(LocalDate startDate, LocalDate endDate) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);
            query.select(root).where(cb.between(root.get("date"), startDate, endDate));
            Query q = em.createQuery(query);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (em != null) em.close();
        }

    }
}