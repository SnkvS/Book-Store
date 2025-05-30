package com.senkiv.bookstore.service.impl;

import com.senkiv.bookstore.dto.OrderItemResponseDto;
import com.senkiv.bookstore.dto.OrderRequestDto;
import com.senkiv.bookstore.dto.OrderResponseDto;
import com.senkiv.bookstore.dto.OrderStatusUpdateDto;
import com.senkiv.bookstore.exception.OrderProcessingException;
import com.senkiv.bookstore.mapper.OrderItemMapper;
import com.senkiv.bookstore.mapper.OrderMapper;
import com.senkiv.bookstore.model.Order;
import com.senkiv.bookstore.model.OrderItem;
import com.senkiv.bookstore.model.ShoppingCart;
import com.senkiv.bookstore.model.User;
import com.senkiv.bookstore.repository.OrderItemRepository;
import com.senkiv.bookstore.repository.OrderRepository;
import com.senkiv.bookstore.repository.ShoppingCartRepository;
import com.senkiv.bookstore.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final String TOTAL_CALCULATION_ERR_MESSAGE =
            "Error during total price calculation.";
    private static final String NO_ORDER_FOR_USER_MESSAGE =
            "User with id %s does not have order with id %s";
    private static final String NO_ITEM_IN_ORDER_MESSAGE =
            "There is no item with id %s in order with id %s";
    private static final String EMPTY_CART_MESSAGE =
            "Your cart is empty. Cannot place an order.";
    private static final String NO_ORDER_WITH_SUCH_ID =
            "There is no order with such id %s.";
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = getShoppingCart(userId);
        User owner = shoppingCart.getUser();
        if (shoppingCart.isEmpty()) {
            throw new OrderProcessingException(EMPTY_CART_MESSAGE);
        }
        Order order = createEmptyOrder(owner);
        order.setShippingAddress(orderRequestDto.shippingAddress());
        Set<OrderItem> orderItems = transferOrderItemsFromCartItems(shoppingCart, order);
        order.setOrderItems(orderItems);
        order.setTotal(calculateTotalPrice(order));
        orderRepository.save(order);
        shoppingCart.clearShoppingCart(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderResponseDto> retrieveHistoryOrders(Pageable pageable, Long userId) {
        return orderRepository.findOrdersByUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemResponseDto> getOrderItems(Pageable pageable, Long orderId, Long userId) {
        if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
            throw new EntityNotFoundException(
                    NO_ORDER_FOR_USER_MESSAGE.formatted(userId, orderId));
        }
        return orderItemRepository.findAllByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemResponseDto getSpecificItem(Long itemId, Long orderId, Long userId) {
        return orderItemRepository.findByIdAndOrderIdAndOrderUserId(itemId, orderId, userId)
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        NO_ITEM_IN_ORDER_MESSAGE.formatted(itemId,
                                orderId)));
    }

    @Override
    public OrderResponseDto updateStatus(OrderStatusUpdateDto dto, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException(NO_ORDER_WITH_SUCH_ID.formatted(orderId
                        )));
        order.setStatus(dto.orderStatus());
        return orderMapper.toDto(order);
    }

    private Set<OrderItem> transferOrderItemsFromCartItems(ShoppingCart shoppingCart, Order order) {
        return shoppingCart.getCartItems().stream()
                .map(item -> {
                    OrderItem orderItem = orderItemMapper.toOrderItem(item);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    private ShoppingCart getShoppingCart(Long userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ShoppingCartServiceImpl.SHOPPING_CART_INIT_ERROR_MSG.formatted(
                                userId)));
    }

    private Order createEmptyOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        return order;
    }

    private BigDecimal calculateTotalPrice(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getPrice()
                        .multiply(new BigDecimal(orderItem.getQuantity())))
                .reduce(BigDecimal::add).orElseThrow(() -> new OrderProcessingException(
                        TOTAL_CALCULATION_ERR_MESSAGE));
    }
}
