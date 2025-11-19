# Mutant Detector API – TP Final
### Análisis de ADN Mutante usando Spring Boot, H2, JPA y Testing Automatizado

## 📌 Descripción del Proyecto
Este proyecto implementa una API REST capaz de identificar si una secuencia de ADN pertenece a un mutante mediante patrones en una matriz NxN.  
Incluye persistencia con H2, hashing SHA-256, validación personalizada, manejo global de excepciones, testing automatizado y documentación Swagger.

## 🧪 Endpoints

### ▶ POST /mutant
Determina si un ADN es mutante.

**Respuestas:**
- `200 OK` → ADN mutante  
- `403 Forbidden` → ADN humano  
- `400 Bad Request` → ADN inválido  

---

### ▶ GET /stats  
Devuelve estadísticas globales:

```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

---

## 🏛 Arquitectura
Organizado en 6 capas:

```
controller/
service/
repository/
entity/
validation/
exception/
```

Cumple con separación de responsabilidades y facilita testing.

---

## 🗄 Persistencia
Entidad principal `DnaRecord`:

- `id`
- `dnaHash` (SHA-256, único)
- `isMutant`
- `createdAt`

Se incluye índice por hash e índice por `isMutant`.

---

## 🔐 Validación de ADN
Anotación personalizada:

```
@ValidDnaSequence
```

Valida:
- Letras válidas (A, T, C, G)  
- Formato NxN  
- Tamaño mínimo de 4  

---

## 🧬 Algoritmo Mutante
El detector analiza secuencias:

- Horizontales  
- Verticales  
- Diagonales ↘  
- Diagonales ↗  

Es mutante si encuentra **≥ 2 secuencias** de 4 caracteres iguales consecutivos.

---

## 🔑 Hashing (SHA-256)
Se aplica a la concatenación del ADN para:

- Evitar análisis duplicados  
- Mantener confidencialidad  
- Acelerar consultas con `findByDnaHash()`  

---

## 📊 Estadísticas
Consultas optimizadas:

- `countByIsMutant(true)`  
- `countByIsMutant(false)`  

El servicio devuelve counts y ratio.

---

## ⚠ Manejo Global de Excepciones
`GlobalExceptionHandler` gestiona:

- Validaciones
- JSON inválido
- Hashing fallido

Retorna mensajes claros y consistentes en JSON.

---

## 📘 Swagger
Disponible en:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/api-docs`

Incluye schemas y ejemplos.

---

## 🧪 Testing Automatizado

- MutantDetector (extenso)
- MutantService  
- StatsService  
- MutantController (integración)
- DTOs  
- >35 tests totales

### ✔ Resultados
- **75% cobertura global**
- **94% servicios + detector**
- **100% controller**

Reporte JaCoCo en:

```
build/reports/jacoco/test/html/index.html
```

---

## 🚀 Ejecución

### ▶ Correr la API
```
./gradlew bootRun
```

### ▶ Ejecutar tests + JaCoCo
```
./gradlew clean test jacocoTestReport
```

---

## 🧪 Consola H2

URL: `http://localhost:8080/h2-console`  
JDBC: `jdbc:h2:mem:mutantsdb`  
Usuario: `sa`  
Contraseña: *(vacío)*

---

## 📦 Estructura del proyecto

```
src/
 ├─ main/java/org/example/mutants
 └─ test/java/org/example/mutants

build/
 └─ reports/jacoco/test/html/

README.md
documentacionTest.md
build.gradle
settings.gradle
```

---

## 🏁 Conclusión
El proyecto cumple con todas las consignas del TP Final: arquitectura en capas, validación personalizada, persistencia, estadísticas, pruebas exhaustivas, documentación Swagger y reporte de cobertura completo.

