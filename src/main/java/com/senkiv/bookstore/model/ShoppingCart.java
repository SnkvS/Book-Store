package com.senkiv.bookstore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "shopping_carts")
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false ")
@Getter
@Setter
@NamedEntityGraph(
        name = "cart-with-items-and-user",
        attributeNodes = {
                @NamedAttributeNode(value = "cartItems"),
                @NamedAttributeNode(value = "user")
        }
)
public class ShoppingCart {
    @Id
    private Long id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private User user;
    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted;
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    public void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems()
                .forEach(item -> item.setShoppingCart(null));
        shoppingCart.getCartItems().clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
