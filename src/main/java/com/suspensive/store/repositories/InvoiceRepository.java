package com.suspensive.store.repositories;

import com.suspensive.store.models.entities.InvoiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends CrudRepository<InvoiceEntity,Long> {
}
