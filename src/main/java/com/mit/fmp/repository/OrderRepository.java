package com.mit.fmp.repository;

import com.mit.fmp.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

	List<CustomerOrder> findByBuyer_Id(Long buyerId);

	List<CustomerOrder> findByProduct_Farmer_Id(Long farmerId);
}
