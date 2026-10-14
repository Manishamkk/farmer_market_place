package com.mit.fmp.repository;

import com.mit.fmp.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

	List<CustomerOrder> findByBuyer_Id(Long buyerId);

	List<CustomerOrder> findByProduct_Farmer_Id(Long farmerId);
}
