package io.soramitsu.irohaandroid.cache.serializer;

import com.google.gson.Gson;

import io.soramitsu.irohaandroid.entity.AccountEntity;
import io.soramitsu.irohaandroid.entity.TransactionListEntity;
import io.soramitsu.irohaandroid.model.KeyPair;

public class JsonSerializer {
    private final Gson gson = new Gson();

    public String serialize(AccountEntity accountEntity) {
        return gson.toJson(accountEntity, AccountEntity.class);
    }

    public String serialize(TransactionListEntity transactionListEntity) {
        return gson.toJson(transactionListEntity, TransactionListEntity.class);
    }

    public String serialize(KeyPair keyPairEntity) {
        return gson.toJson(keyPairEntity, KeyPair.class);
    }

    public AccountEntity deserializeToAccountEntity(String jsonString) {
        return gson.fromJson(jsonString, AccountEntity.class);
    }

    public TransactionListEntity deserializeToTransactionListEntity(String jsonString) {
        return gson.fromJson(jsonString, TransactionListEntity.class);
    }

    public KeyPair deserializeToKeyPairEntity(String jsonString) {
        return gson.fromJson(jsonString, KeyPair.class);
    }
}