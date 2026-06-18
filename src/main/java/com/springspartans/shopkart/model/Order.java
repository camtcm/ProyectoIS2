package com.springspartans.shopkart.model;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;

@Entity
@Table(name="Orders")
public class Order
{
	// 1. SOLUCIÓN ROJOS: Enums en MAYÚSCULAS para Sonar
	public enum OrderStatus 
	{
		PENDING, SHIPPED, DELIVERED, CANCELLED;
		
		// MÉTODOS PUENTE: Evitan que colapsen las llamadas viejas basadas en "Pending", "Shipped", etc.
		public static final OrderStatus Pending = PENDING;
		public static final OrderStatus Shipped = SHIPPED;
		public static final OrderStatus Delivered = DELIVERED;
		public static final OrderStatus Cancelled = CANCELLED;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="cust_id", nullable=false, foreignKey= @ForeignKey(name="FR_Customer"))
	private Customer customer;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="prod_id", nullable=false, foreignKey=@ForeignKey(name="FR_Product"))
	private Product product;
	
	@Column(nullable=false)
	private int quantity;
	
	// 2. SOLUCIÓN AMARILLOS: Variables internas en camelCase mapeadas explícitamente a nombres con guion bajo
	@Column(name = "order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp orderDate = Timestamp.from(Instant.now());
	
	@Column(name = "delivered_date", columnDefinition = "TIMESTAMP DEFAULT NULL")
	private Timestamp deliveredDate;
	
	@Column(columnDefinition = "ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending'")
	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.PENDING;
	
	@Column(name = "total_amount", nullable=false)
	private double totalAmount;

	public Order() {
		// Requerido por JPA
	}

	// 3. SOLUCIÓN NARANJA: Mantenemos el constructor de 8 parámetros para que no falle ninguna clase,
	// pero mapeamos internamente a las nuevas propiedades limpias.
	public Order(int id, Customer customer, Product product, int quantity, Timestamp order_date,
			Timestamp delivered_date, OrderStatus status, double total_amount) {
		this.id = id;
		this.customer = customer;
		this.product = product;
		this.quantity = quantity;
		this.orderDate = order_date;
		this.deliveredDate = delivered_date;
		this.status = status;
		this.totalAmount = total_amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	// =========================================================================
	// 4. MÉTODOS PUENTE (MANTIENEN LA COMPATIBILIDAD CON EL RESTO DE TU PROYECTO)
	// =========================================================================
	
	public Timestamp getOrder_date() {
		return this.orderDate;
	}

	public void setOrder_date(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public Timestamp getDelivered_date() {
		return this.deliveredDate;
	}

	public void setDelivered_date(Timestamp deliveredDate) {
		this.deliveredDate = deliveredDate;
	}

	public OrderStatus getStatus() {
		return this.status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public double getTotal_amount() {
		return this.totalAmount;
	}

	public void setTotal_amount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", customer=" + customer + ", product=" + product + ", quantity=" + quantity
				+ ", order_date=" + orderDate + ", delivered_date=" + deliveredDate + ", status=" + status
				+ ", total_amount=" + totalAmount + "]";
	}
}
