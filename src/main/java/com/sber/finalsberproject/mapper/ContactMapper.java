package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.ContactDTO;
import com.sber.finalsberproject.model.Contact;
import com.sber.finalsberproject.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.util.List;

@Component
public class ContactMapper extends GenericMapper<Contact, ContactDTO> {
    private final UserRepository userRepository;

    public ContactMapper(ModelMapper modelMapper,
                         UserRepository userRepository) {
        super(modelMapper, Contact.class, ContactDTO.class);
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Override
    protected void setupMapper() {
        modelMapper.createTypeMap(Contact.class, ContactDTO.class)
                .addMappings(mapping ->mapping.skip(ContactDTO::setUserId)).setPostConverter(toDTOConverter());
        modelMapper.createTypeMap(ContactDTO.class, Contact.class)
                .addMappings(mapping -> mapping.skip(Contact::setUser)).setPostConverter(toEntityConverter());
    }

    @Override
    protected void mapSpecificFields(ContactDTO source, Contact destination) {
        destination.setUser(userRepository.findById(source.getUserId()).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    protected void mapSpecificFields(Contact source, ContactDTO destination) {
        destination.setUserId(source.getUser().getId());
    }

    @Override
    protected List<Long> getIds(Contact source) {
        throw new UnsupportedOperationException("Метод недоступен");
    }
}
