# SpringBoot-K8s

A production-ready, 3-tier application demonstrating Kubernetes deployment best practices. The stack consists of an **Nginx** frontend serving a single-page UI, a **Spring Boot** REST API backend, and a **PostgreSQL** database — all orchestrated on Kubernetes with namespace isolation, network policies, persistent storage, and an NGINX Ingress controller for external access.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Kubernetes Architecture](#kubernetes-architecture)
- [Deployment Instructions](#deployment-instructions)
- [Verification](#verification)
- [API Reference](#api-reference)

---

## Project Overview

This project deploys a full 3-tier web application to Kubernetes (Minikube) and showcases real-world patterns used in production environments:

- **Frontend** — Nginx container serving static HTML/JS; doubles as an internal reverse proxy, forwarding `/api/*` requests to the backend without exposing the backend externally.
- **Backend** — Spring Boot 3.2 REST API (Java 17) exposing CRUD endpoints for a `Message` entity; connects to PostgreSQL using credentials injected from Kubernetes Secrets and ConfigMaps.
- **Database** — PostgreSQL 15 with a PersistentVolumeClaim (PVC) ensuring data survives pod restarts.

---

## Tech Stack

| Layer    | Technology                        |
|----------|-----------------------------------|
| Frontend | Nginx (static files + proxy)  |
| Backend  | Java · Spring Boot · Maven    |
| Database | PostgreSQL                    |
| Container| Docker · Docker Hub               |
| Orchestration | Kubernetes · Minikube        |
| Ingress  | NGINX Ingress Controller          |

---

## Kubernetes Architecture

The cluster is divided into **three isolated namespaces**, each labelled with a `tier` key that drives NetworkPolicy enforcement:

| Namespace     | Label       | Workloads                          |
|---------------|-------------|------------------------------------|
| `frontend-ns` | `tier: web` | Frontend Deployment, Service, Ingress |
| `backend-ns`  | `tier: app` | Backend Deployment, Service, ConfigMap, Secret |
| `database-ns` | `tier: data`| PostgreSQL Deployment, Service, PVC, ConfigMap, Secret |

### Key Design Decisions

- **High Availability** — Both the frontend and backend Deployments run **2 replicas**, enabling load balancing and zero-downtime rolling updates.
- **Secrets & ConfigMaps** — Database credentials are stored in Kubernetes Secrets (base64-encoded); the JDBC connection URL is held in a ConfigMap. The backend pod mounts these as environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
- **Persistent Storage** — A `ReadWriteOnce` PVC (`postgres-claim`, 1 Gi) is bound to the PostgreSQL pod so data persists across pod restarts and rescheduling.
- **Network Segmentation** — A `NetworkPolicy` on `database-ns` restricts inbound traffic to port `5432` exclusively from pods labelled `app: springboot-app` running in `backend-ns`. No other namespace or pod can reach the database.
- **Single Ingress Entry Point** — An NGINX Ingress resource in `frontend-ns` routes all external HTTP traffic to `frontend-service:80`. The Nginx container then proxies `/api/*` calls to the backend internally — keeping the backend and database completely off the public network.
- **Cross-Namespace DNS** — Services communicate using Kubernetes' built-in DNS:
  - Backend → Database: `postgres-service.database-ns.svc.cluster.local:5432`
  - Frontend → Backend (via Nginx proxy): `backend-service.backend-ns.svc.cluster.local:8080`
- **Resource Limits** — The frontend container defines explicit CPU/memory `requests` and `limits`, enabling the scheduler to bin-pack pods efficiently and prevent noisy-neighbour issues.
- **Health Probes** — `livenessProbe` and `readinessProbe` are configured on the frontend so Kubernetes only routes traffic to pods that are genuinely ready and restarts any that become unhealthy.

---

## Deployment Instructions

### Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/) configured to use the Minikube context
- Docker (for building images; pre-built images are available on Docker Hub)

### 1 — Start Minikube & enable the Ingress add-on

```bash
minikube start
minikube addons enable ingress
```

### 2 — Apply manifests in the correct order

```bash
# 1. Namespaces first — all other resources depend on these
kubectl apply -f k8s/namespaces/

# 2. Secrets and ConfigMaps
kubectl apply -f k8s/database/postgres-secret.yaml
kubectl apply -f k8s/database/postgres-configmap.yaml
kubectl apply -f k8s/backend/backend-secret.yaml
kubectl apply -f k8s/backend/backend-configmap.yaml

# 3. Persistent storage
kubectl apply -f k8s/database/postgres-pvc.yaml

# 4. Deployments
kubectl apply -f k8s/database/postgres-deployment.yaml
kubectl apply -f k8s/backend/backend-deployment.yaml
kubectl apply -f k8s/frontend/frontend-deployment.yaml

# 5. Services
kubectl apply -f k8s/database/postgres-service.yaml
kubectl apply -f k8s/backend/backend-service.yaml
kubectl apply -f k8s/frontend/frontend-service.yaml

# 6. Network Policies
kubectl apply -f k8s/network-policies/

# 7. Ingress
kubectl apply -f k8s/ingress/
```

### 3 — Access the application

**Option A — `minikube tunnel` (recommended for localhost access)**

```bash
# Run in a separate terminal; keeps the tunnel alive
minikube tunnel
```

Then open `http://localhost` in your browser.

**Option B — Minikube IP**

```bash
minikube ip          # e.g. 192.168.49.2
```

Open `http://<minikube-ip>` in your browser (you may need a NodePort service instead of ClusterIP for this approach).

---

## Verification

```bash
# Check all pods are Running across all namespaces
kubectl get pods -A

# Confirm 2 replicas are ready for frontend and backend
kubectl get deployments -n frontend-ns
kubectl get deployments -n backend-ns

# Inspect the Ingress and confirm an address is assigned
kubectl get ingress -n frontend-ns

# View PVC is Bound
kubectl get pvc -n database-ns

# Tail backend logs to confirm DB connection
kubectl logs -n backend-ns -l app=springboot-app --tail=50
```

---

## API Reference

All requests are made relative to the application root (proxied through Nginx).

| Method   | Endpoint              | Description              |
|----------|-----------------------|--------------------------|
| `GET`    | `/api/message`        | Retrieve all messages    |
| `POST`   | `/api/message`        | Create a new message     |
| `DELETE` | `/api/message/{id}`   | Delete a message by ID   |

**Example — create a message:**

```bash
curl -X POST http://localhost/api/message \
  -H "Content-Type: application/json" \
  -d '{"content": "Hello from Kubernetes!"}'
```

