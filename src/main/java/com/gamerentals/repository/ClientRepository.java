package com.gamerentals.repository;

import com.gamerentals.entity.Client;

public class ClientRepository extends GenericRepository<Client, String>{
    public ClientRepository() { super(Client.class); }
}