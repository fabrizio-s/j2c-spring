package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Address;
import com.j2c.j2c.domain.entity.Address.AddressBuilder;
import com.j2c.j2c.service.input.CreateAddressForm;
import com.j2c.j2c.service.input.UpdateAddressForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public abstract class AddressVOMapper {

    public Address fromCreateForm(final CreateAddressForm form) {
        if (form == null) {
            return null;
        }
        final AddressBuilder builder = toAddressBuilder(form);
        return builder.build();
    }

    public Address fromUpdateForm(final Address address, final UpdateAddressForm form) {
        if (address == null || form == null) {
            return null;
        }
        final AddressBuilder builder = address.copy();
        update(form, builder);
        return builder.build();
    }

    abstract AddressBuilder toAddressBuilder(CreateAddressForm form);

    abstract void update(UpdateAddressForm form, @MappingTarget AddressBuilder builder);

    @ObjectFactory
    protected AddressBuilder builder() {
        return Address.builder();
    }

}
