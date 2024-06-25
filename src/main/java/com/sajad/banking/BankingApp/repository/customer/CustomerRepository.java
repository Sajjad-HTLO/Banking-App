package com.sajad.banking.BankingApp.repository.customer;

import com.sajad.banking.BankingApp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, PagingAndSortingRepository<Customer, Integer> {

    @Override
    Optional<Customer> findById(Integer integer);

    Optional<Customer> findByPhoneNumber(String phoneNumber);
}
