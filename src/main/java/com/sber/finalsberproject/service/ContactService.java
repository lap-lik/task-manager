package com.sber.finalsberproject.service;

import com.sber.finalsberproject.dto.ContactDTO;
import com.sber.finalsberproject.mapper.ContactMapper;
import com.sber.finalsberproject.model.Contact;
import com.sber.finalsberproject.repository.ContactRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService extends GenericService<Contact, ContactDTO>{
    public ContactService(ContactRepository contactRepository,
                          ContactMapper contactMapper) {
        super(contactRepository, contactMapper);
    }
}
