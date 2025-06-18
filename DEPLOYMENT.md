# 微服务部署指南

## 概述

本项目使用Kubernetes和Kustomize进行微服务部署，支持多环境部署和自动化版本管理。

## 版本管理策略

### 问题解决
- **之前的问题**: 在deployment文件中硬编码镜像版本号
- **解决方案**: 使用Kustomize动态管理镜像版本

### 版本标签策略
- `latest`: 最新版本，用于开发和测试
- `{commit-sha}`: 具体提交的版本，用于生产环境

## 目录结构

```
k8s/
├── base/                          # 基础配置
│   ├── kustomization.yaml        # 基础Kustomize配置
│   ├── inventory-service-deployment.yaml
│   └── order-service-deployment.yaml
└── overlays/                      # 环境特定配置
    ├── dev/                       # 开发环境
    │   └── kustomization.yaml
    └── prod/                      # 生产环境
        └── kustomization.yaml
```

## 部署方式

### 1. 自动化部署 (推荐)

#### 开发环境
```bash
# 推送到develop分支自动部署到开发环境
git push origin develop
```

#### 生产环境
```bash
# 推送到main分支自动部署到生产环境
git push origin main
```

### 2. 手动部署

#### 使用部署脚本
```bash
# 部署到开发环境，使用latest版本
./deploy.sh dev latest

# 部署到生产环境，使用特定版本
./deploy.sh prod e5e98933a8209b3e768d9ff3c716f924b3004888
```

#### 直接使用kubectl
```bash
# 开发环境
kubectl apply -k k8s/overlays/dev

# 生产环境
kubectl apply -k k8s/overlays/prod
```

## CI/CD 流程

### 构建阶段
1. 检出代码
2. 设置JDK 17
3. Maven构建
4. 构建Docker镜像
5. 推送镜像到GHCR (同时推送latest和commit-sha标签)

### 部署阶段
1. 根据分支选择环境
   - `develop` → 开发环境
   - `main` → 生产环境
2. 更新Kustomize配置中的镜像版本
3. 应用Kubernetes配置

## 环境配置

### 开发环境 (dev)
- Namespace: `demi`
- Replicas: 1 (每个服务)
- 使用latest标签

### 生产环境 (prod)
- Namespace: `demi`
- Replicas: 3 (每个服务)
- 使用commit-sha标签

## 优势

1. **版本追踪**: 每个部署都有明确的版本标识
2. **环境隔离**: 开发和生产环境完全分离
3. **自动化**: 推送代码即可自动部署
4. **可回滚**: 可以轻松回滚到之前的版本
5. **配置管理**: 使用Kustomize管理不同环境的配置差异

## 故障排除

### 查看部署状态
```bash
# 查看pod状态
kubectl get pods -n demi-dev    # 开发环境
kubectl get pods -n demi        # 生产环境

# 查看服务状态
kubectl get svc -n demi-dev
kubectl get svc -n demi
```

### 查看日志
```bash
# 查看特定pod的日志
kubectl logs -n demi <pod-name>
```

### 回滚部署
```bash
# 回滚到上一个版本
kubectl rollout undo deployment/inventory-service -n demi
kubectl rollout undo deployment/order-service -n demi
``` 
