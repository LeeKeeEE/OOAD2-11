# Elasticsearch 使用说明和安装文档

## 环境准备

### 1. **更新服务器节点标签**
将 Elasticsearch 服务指定运行在 `OOMALL-Search` 节点：
```shell
docker node update --label-add server=es OOMALL-Search
```

---

## 安装 Elasticsearch 和 Kibana

### 1. **拉取 Elasticsearch 和 Kibana 镜像**
```shell
docker pull elasticsearch:8.5.3
docker pull kibana:8.5.3
```

### 2. **部署 Elasticsearch 服务**
运行以下命令启动 Elasticsearch 容器：
```shell
docker service create \
  --name es \
  --constraint 'node.labels.server==es' \
  --env "ES_JAVA_OPTS=-Xms1g -Xmx1g" \
  --env "discovery.type=single-node" \
  --mount type=volume,source=es-data,target=/usr/share/elasticsearch/data \
  --mount type=bind,source=/root/oomall/conf/es/conf/elasticsearch.yml,target=/usr/share/elasticsearch/config/elasticsearch.yml \
  --mount type=bind,source=/root/oomall/conf/es/plugins,target=/usr/share/elasticsearch/plugins \
  --network my-net \
  --publish 9200:9200 \
  --publish 9300:9300 \
  -d \
  elasticsearch:8.5.3
```

- **参数说明**：
  - `--env "ES_JAVA_OPTS=-Xms1g -Xmx1g"`：限制 Elasticsearch JVM 最小和最大内存为 1GB。
  - `--mount`：
    - `source=es-data`：挂载数据目录。
    - `source=/root/oomall/conf/es/conf/elasticsearch.yml`：指定自定义配置文件。
    - `source=/root/oomall/conf/es/plugins`：挂载插件目录。
  - `--network my-net`：加入自定义 Docker 网络。
  - `--publish`：映射 9200 和 9300 端口。

### 3. **部署 Kibana 服务（可选）**
如果服务器内存大于 4GB，可以安装 Kibana：

```shell
docker service create \
  --name kibana \
  --constraint 'node.labels.server==es' \
  --env ELASTICSEARCH_HOSTS=http://es:9200 \
  --memory="256m" \
  --memory-swap="512m" \
  --env "NODE_OPTIONS=--max-old-space-size=512" \
  --network my-net \
  --publish 5601:5601 \
  -d \
  kibana:8.5.3
```

- **参数说明**：
  - `ELASTICSEARCH_HOSTS`：指定 Elasticsearch 地址。
  - `--memory` 和 `--memory-swap`：限制内存使用，避免因资源不足导致服务崩溃。
  - 如果服务器内存不足，可以选择不安装 Kibana，不影响 Elasticsearch 的正常使用。

---

## 安装 Node.js

使用 `nvm` 安装和管理 Node.js 版本。

### 1. **安装 nvm**
```shell
git clone https://gitee.com/mirrors/nvm	# 使用gitee上的镜像源
cd nvm
bash install.sh
source ~/.bashrc
```

### 2. **安装 Node.js**
```shell
nvm install 14	# 由于Ubuntu内核版本限制只能安装14版本
```

### 3. **配置 npm 镜像源**
```shell
npm config set registry https://registry.npmmirror.com
```

---

## 数据和索引导入

### 1. **安装 `elasticdump` 工具**
`elasticdump` 是用于导入和导出 Elasticsearch 数据的工具：
```shell
npm install elasticdump -g
```

### 2. **导入索引 Mapping**
将 `mapping` 文件导入 Elasticsearch：
```shell
elasticdump \
  --input=product_index_mapping.json \
  --output=http://localhost:9200/product_index \
  --type=mapping

elasticdump \
  --input=order_item_name_index_mapping.json \
  --output=http://localhost:9200/order_item_name_index \
  --type=mapping

elasticdump \
  --input=order_index_mapping.json \
  --output=http://localhost:9200/order_index \
  --type=mapping
```

### 3. **导入索引数据**
导入索引的初始数据：
```shell
elasticdump \
  --input=product_index_data.json \
  --output=http://localhost:9200/product_index \
  --type=data

elasticdump \
  --input=order_item_name_index_data.json \
  --output=http://localhost:9200/order_item_name_index \
  --type=data

elasticdump \
  --input=order_index_data.json \
  --output=http://localhost:9200/order_index \
  --type=data
```

---

## 验证安装

### 1. **验证 Elasticsearch 服务**
运行以下命令检查 Elasticsearch 是否启动：
```shell
curl -X GET "http://localhost:9200"
```

预期返回：
```json
{
  "name": "your-node-name",
  "cluster_name": "elasticsearch",
  "cluster_uuid": "xxxxxxxxx",
  "version": {
    "number": "8.5.3",
    ...
  },
  "tagline": "You Know, for Search"
}
```

### 2. **验证 Kibana 服务**
在浏览器访问 Kibana：
```
http://<your-server-ip>:5601
```

---

## 常见问题排查

### 1. **Elasticsearch 启动失败**
- 检查日志：
  ```shell
  docker service logs es
  ```
- 检查 `elasticsearch.yml` 配置文件是否正确。

### 2. **Kibana 无法连接 Elasticsearch**
- 检查 `ELASTICSEARCH_HOSTS` 配置是否正确。
- 检查 Elasticsearch 服务是否运行：
  ```shell
  curl -X GET "http://localhost:9200"
  ```

---

