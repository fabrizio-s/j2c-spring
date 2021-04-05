package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.repository.spring.CheckoutLineSDJRepository;
import com.j2c.j2c.domain.repository.spring.OrderLineSDJRepository;
import com.j2c.j2c.domain.repository.spring.ProductSDJRepository;
import com.j2c.j2c.domain.entity.Product;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Repository
public class ProductRepository
        extends BaseRepository<Product, Long> {

    private final ProductSDJRepository repository;
    private final CheckoutLineSDJRepository checkoutLineRepository;
    private final OrderLineSDJRepository orderLineRepository;

    protected ProductRepository(
            final ProductSDJRepository repository,
            final CheckoutLineSDJRepository checkoutLineRepository,
            final OrderLineSDJRepository orderLineRepository
    ) {
        super(Product.class, repository);
        this.repository = repository;
        this.checkoutLineRepository = checkoutLineRepository;
        this.orderLineRepository = orderLineRepository;
    }

    public Page<Product> findAll(final Predicate predicate, final Pageable pageable) {
        return repository.findAll(predicate, pageable);
    }

    @Override
    public void remove(final Product product) {
        optional(product)
                .map(Product::getId)
                .ifPresent(productId -> {
                    checkoutLineRepository.dereferenceProduct(productId);
                    orderLineRepository.dereferenceProduct(productId);
                });
        super.remove(product);
    }

}
