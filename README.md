
GST Demo - Spring Boot Microservices
===================================

This demo contains 3 Spring Boot microservices (user-service, invoice-service, returns-service),
configured for Prometheus metrics, dockerized, and runnable with docker-compose (with Kafka + Zookeeper).
It is a minimal starter to learn Spring Boot + Kafka + Prometheus + Grafana + Docker.

How to run locally (prereqs: docker, docker-compose, maven for local builds if you prefer):
1. Build jars (optional): cd user-service && mvn -q clean package -DskipTests
   Repeat for invoice-service and returns-service or rely on Docker build which uses Maven inside image.
2. From project root: docker-compose up --build
3. Grafana: http://localhost:3000 (admin/admin)
   Prometheus: http://localhost:9090
4. APIs:
   - User: http://localhost:8081 (register/login)
   - Invoice: http://localhost:8082 (upload)
   - Returns: http://localhost:8083 (health & metrics)

Notes:
- This is intentionally minimal to keep it runnable in local environments.
- For production-like setup (GKE, GCR, OpenObserve, GCS, BigQuery), adapt the Dockerfiles and k8s manifests provided in k8s/.
# gst-demo-springboot-app
