package com.uu2.tinyagents.store.milvus;

import com.alibaba.fastjson2.JSON;
import com.google.gson.JsonObject;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;
import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.rag.store.Query;
import com.uu2.tinyagents.core.rag.store.Store;
import com.uu2.tinyagents.core.util.ArrayUtil;
import com.uu2.tinyagents.core.util.StringUtil;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.ConsistencyLevel;
import io.milvus.v2.common.DataType;
import io.milvus.v2.exception.MilvusClientException;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.partition.request.CreatePartitionReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static io.milvus.common.utils.JsonUtils.toJsonTree;

@Data
@Builder
@Slf4j
public class MilvusVectorStore implements Store {
    private EmbeddingModel embeddingModel;
    private MilvusClientV2 client;
    private MilvusVectorStoreConfig config;


    public MilvusVectorStore(EmbeddingModel embeddingModel, MilvusClientV2 client, MilvusVectorStoreConfig config) {
        this.embeddingModel = embeddingModel;
        this.client = client;
        this.config = config;

        if (config.isAutoCreateCollection()) {
            this.createCollection();
        }
    }

    @Override
    public List<Document> search(Query query) {
        EmbedData textVector = embeddingModel.embed(query.getText());

        if (textVector == null) {
            return new ArrayList<>();
        }
        double[] vector = textVector.getVector()[0];

        List<String> field = query.isOutputVector()
                ? List.of("id", "content", "metadata")
                : List.of("id", "content", "vector", "metadata");

        SearchResp searchResp = this.client.search(SearchReq.builder()
                .collectionName(config.getCollectionName())
                .consistencyLevel(ConsistencyLevel.STRONG)
                .outputFields(field)
                .topK(query.getTopK())
                .annsField("vector")
                .data(List.of(new FloatVec(ArrayUtil.map(vector))))
                .filter(query.filterExpression())
                .build());


        List<Document> res = new ArrayList<>();
        searchResp.getSearchResults().forEach(searchListResult -> {
            searchListResult.forEach(searchResult -> {
                        Map<String, Object> entity = searchResult.getEntity();
                        Document doc = Document.builder()
                                .id((String) searchResult.getId())
                                .content((String) entity.get("content"))
                                .build();
                        if (entity.getOrDefault("metadata", null) != null) {
                            Object metadata = entity.get("metadata");
                            doc.setMetadataMap(JSON.parseObject(metadata.toString(), Map.class));
                        }

                        Object vectorObj = entity.get("vector");
                        if (vectorObj instanceof List) {
                            doc.setVector(JSON.parseObject(vectorObj.toString(), double[].class));
                        }

                        res.add(doc);
                    }
            );
        });

        return res;
    }

    @Override
    public long add(Document document) {
        EmbedData textVector = embeddingModel.embed(document.getContent());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", document.getId());
        jsonObject.addProperty("content", document.getContent());
        jsonObject.add("vector", toJsonTree(textVector.getVector()[0]));
        jsonObject.addProperty("metadata", JSON.toJSONString(document.getMetadataMap()));
        InsertReq insertReq = InsertReq.builder()
                .collectionName(config.getCollectionName())
                .data(List.of(jsonObject))
                .build();
        if (StringUtil.hasText(config.getPartitionName())) {
            insertReq.setPartitionName(config.getPartitionName());
        }
        try {
            InsertResp resp = this.client.insert(insertReq);
            return resp.getInsertCnt();
        } catch (Exception e) {
            log.info("add collection failed", e);
        }
        return -1;

    }

    @Override
    public long addAll(List<Document> documents) {
        List<JsonObject> jsonObjects = new ArrayList<>();
        EmbedData textVector = embeddingModel.embed(documents.stream().map(Document::getContent).toArray(String[]::new));
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", document.getContent());
            jsonObject.add("vector", toJsonTree(textVector.getVector()[i]));
            jsonObject.addProperty("id", document.getId());
            jsonObject.addProperty("metadata", JSON.toJSONString(document.getMetadataMap()));
            jsonObjects.add(jsonObject);
        }

        InsertReq insertReq = InsertReq.builder()
                .collectionName(config.getCollectionName())
                .data(jsonObjects)
                .build();
        if (StringUtil.hasText(config.getPartitionName())) {
            insertReq.setPartitionName(config.getPartitionName());
        }

        try {
            InsertResp resp = this.client.insert(insertReq);
            return resp.getInsertCnt();
        } catch (MilvusClientException e) {
            log.info("addAll collection failed", e);
        }
        return -1;
    }

    @Override
    public long delete(List<?> ids) {
        if (ids != null && !ids.isEmpty()) {
            DeleteReq deleteReq = DeleteReq.builder()
                    .collectionName(config.getCollectionName())
                    .ids(Arrays.asList(ids.toArray()))
                    .build();
            if (StringUtil.hasText(config.getPartitionName())) {
                deleteReq.setPartitionName(config.getPartitionName());
            }
            try {
                DeleteResp resp = client.delete(deleteReq);
                return resp.getDeleteCnt();
            } catch (MilvusClientException e) {
                log.info("delete collection failed", e);
            }
        }
        return 0;
    }

    public void createCollection() {
        if (!client.hasCollection(HasCollectionReq.builder()
                .collectionName(config.getCollectionName())
                .build())) {

            client.createCollection(CreateCollectionReq.builder()
                    .collectionName(config.getCollectionName())
                    .idType(DataType.VarChar)
                    .enableDynamicField(true)
                    .vectorFieldName("vector")
                    .metricType("L2")
                    .dimension(embeddingModel.getDimension())
                    .build());

            if (StringUtil.hasText(config.getPartitionName())) {
                client.createPartition(CreatePartitionReq.builder()
                        .collectionName(config.getCollectionName())
                        .partitionName(config.getPartitionName())
                        .build());
            }
        }
    }
}
