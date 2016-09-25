package io.soramitsu.iroha.models.apis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.soramitsu.iroha.models.Asset;
import io.soramitsu.iroha.models.Domain;
import io.soramitsu.iroha.models.History;
import io.soramitsu.iroha.models.Message;
import io.soramitsu.iroha.models.Operation;
import io.soramitsu.iroha.models.ResponseObject;
import okhttp3.Response;

import static io.soramitsu.iroha.utils.NetworkUtil.ENDPOINT_URL;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_BAD;
import static io.soramitsu.iroha.utils.NetworkUtil.STATUS_OK;
import static io.soramitsu.iroha.utils.NetworkUtil.get;
import static io.soramitsu.iroha.utils.NetworkUtil.post;


/**
 * Wrapper class of the Iroha transaction API.
 */
public class TransactionClient {
    private static TransactionClient transactionClient;

    private Gson gson;

    private TransactionClient(Gson gson) {
        this.gson = gson;
    }

    /**
     * To generate new instance. (This is singleton)
     *
     * @return Instance of IrohaUserClient
     */
    public static TransactionClient newInstance() {
        if (transactionClient == null) {
            transactionClient = new TransactionClient(new Gson());
        }
        return transactionClient;
    }

    public Domain registerDomain(String name, String owner, String signature) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("owner", owner);
        body.put("signature", signature);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(ENDPOINT_URL + "/domain/register", gson.toJson(body));
        Domain domain;
        switch (response.code()) {
            case STATUS_OK:
                domain = gson.fromJson(responseToString(response), Domain.class);
                domain.setName(name);
                break;
            case STATUS_BAD:
                domain = gson.fromJson(responseToString(response), Domain.class);
                break;
            default:
                domain = new Domain();
                domain.setStatus(response.code());
                domain.setMessage(response.message());
        }
        return domain;
    }

    public Asset registerAsset(String name, String domain, String creator, String signature) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("domain", domain);
        body.put("creator", creator);
        body.put("signature", signature);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(ENDPOINT_URL + "/asset/create", gson.toJson(body));
        Asset asset;
        switch (response.code()) {
            case STATUS_OK:
                asset = gson.fromJson(responseToString(response), Asset.class);
                asset.setName(name);
                asset.setDomain(domain);
                asset.setCreator(creator);
                break;
            case STATUS_BAD:
                asset = gson.fromJson(responseToString(response), Asset.class);
                break;
            default:
                asset = new Asset();
                asset.setStatus(response.code());
                asset.setMessage(response.message());
        }
        return asset;
    }

    public List<Domain> findDomains() throws IOException {
        Response response = get(ENDPOINT_URL + "/domain/list");
        List<Domain> domains = new ArrayList<>();
        switch (response.code()) {
            case STATUS_OK:
                Type type = new TypeToken<List<Domain>>(){}.getType();
                domains = gson.fromJson(responseToString(response), type);
                break;
            case STATUS_BAD:
                break;
            default:
        }
        return domains;
    }

    public List<Asset> findAssets(String domain) throws IOException {
        Response response = get(ENDPOINT_URL + "/asset/list/" + domain);
        List<Asset> assets = new ArrayList<>();
        switch (response.code()) {
            case STATUS_OK:
                Type type = new TypeToken<List<Asset>>(){}.getType();
                assets = gson.fromJson(responseToString(response), type);
                break;
            case STATUS_BAD:
                break;
            default:
        }
        return assets;
    }

    public ResponseObject assetOperation(String assetUuid, String signature, Operation params) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("asset-uuid", assetUuid);
        body.put("params", gson.toJson(params));
        body.put("signature", signature);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(ENDPOINT_URL + "/asset/operation", gson.toJson(body));
        ResponseObject responseObject;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                responseObject = gson.fromJson(responseToString(response), ResponseObject.class);
                break;
            default:
                responseObject = new ResponseObject();
                responseObject.setStatus(response.code());
                responseObject.setMessage(response.message());
        }
        return responseObject;
    }

    public History history(String uuid) throws IOException {
        Response response = get(ENDPOINT_URL + "/history/transaction/" + uuid);
        return history(response);
    }

    public History history(Domain domain, Asset asset) throws IOException {
        Response response = get(ENDPOINT_URL + "/history/" + domain + "." + asset);
        return history(response);
    }

    private History history(Response response) throws IOException {
        History history;
        switch (response.code()) {
            case STATUS_OK:
                history = gson.fromJson(responseToString(response), History.class);
                break;
            default:
                history = new History();
                history.setStatus(response.code());
                history.setMessage(response.message());
        }
        return history;
    }

    public ResponseObject sendMessage(Message message, String signature) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message.getBody());
        body.put("creator", message.getCreator());
        body.put("receiver", message.getReceiver());
        body.put("signature", signature);
        body.put("timestamp", System.currentTimeMillis() / 1000L);

        Response response = post(ENDPOINT_URL + "/message", gson.toJson(body));
        ResponseObject responseObject;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                responseObject = gson.fromJson(responseToString(response), ResponseObject.class);
                break;
            default:
                responseObject = new ResponseObject();
                responseObject.setStatus(response.code());
                responseObject.setMessage(response.message());
        }
        return responseObject;
    }

    /**
     * To convert okhttp3.Response to String.
     *
     * @param response Response object
     * @return Response body after converted string.
     * @throws IOException
     */
    private String responseToString(Response response) throws IOException {
        String result;
        switch (response.code()) {
            case STATUS_OK:
            case STATUS_BAD:
                result = response.body().string();
                break;
            default:
                result = "";
        }
        return result;
    }
}