# 🚀 GUÍA DE DESPLIEGUE EN RENDER

## ✅ Cambios realizados para Render

Se configuró el proyecto para funcionar con **PostgreSQL en Render** manteniendo compatibilidad con **MySQL local**.

### Archivos creados/modificados:

1. ✅ **`application.properties`** - Configuración por defecto (MySQL local)
2. ✅ **`application-dev.properties`** - Perfil desarrollo (MySQL)
3. ✅ **`application-prod.properties`** - Perfil producción (PostgreSQL para Render)
4. ✅ **`Dockerfile`** - Para construir la imagen en Render

---

## 📋 PASO 1: Preparar el repositorio en GitHub

### 1.1 Inicializar Git (si no lo has hecho)

```bash
cd c:\Users\vic-s\OneDrive\Desktop\Files\Frases7CM1
git init
```

### 1.2 Crear `.gitignore`

Crea un archivo `.gitignore` en la raíz con este contenido:

```
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### VS Code ###
.vscode/

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/
```

### 1.3 Subir a GitHub

```bash
git add .
git commit -m "Configuración para despliegue en Render"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/Frases7CM1.git
git push -u origin main
```

---

## 🗄️ PASO 2: Crear base de datos PostgreSQL en Render

1. Ve a https://dashboard.render.com
2. Click en **"New +"** → **"PostgreSQL"**
3. Configura:
   - **Name:** `api-frases-db` (o el nombre que quieras)
   - **Database:** `api_frases`
   - **User:** `frases_user` (se crea automáticamente)
   - **Region:** Oregon (o el más cercano)
   - **Plan:** Free
4. Click en **"Create Database"**
5. **IMPORTANTE:** Copia la **"Internal Database URL"** (la necesitarás en el siguiente paso)

---

## 🌐 PASO 3: Crear Web Service en Render

1. En Render Dashboard, click en **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configura:

### Configuración básica:
- **Name:** `api-frases` (o el nombre que quieras)
- **Region:** Oregon (mismo que la BD)
- **Branch:** `main`
- **Root Directory:** (dejar vacío)
- **Runtime:** `Docker`
- **Plan:** Free

### Variables de entorno:
Click en **"Advanced"** → **"Add Environment Variable"**

Agrega esta variable:

| Key | Value |
|-----|-------|
| `DATABASE_URL` | Pega aquí la "Internal Database URL" que copiaste del paso 2 |
| `SPRING_PROFILES_ACTIVE` | `prod` |

**Ejemplo de DATABASE_URL:**
```
postgresql://frases_user:contraseña@dpg-xxxxx-a.oregon-postgres.render.com/api_frases
```

4. Click en **"Create Web Service"**

---

## ⏳ PASO 4: Esperar el despliegue

Render construirá automáticamente tu aplicación usando el `Dockerfile`. Esto puede tardar **5-10 minutos** la primera vez.

Verás logs como:
```
==> Building...
==> Downloading dependencies...
==> Building with Maven...
==> Creating Docker image...
==> Deploying...
==> Your service is live at https://api-frases.onrender.com
```

---

## 🎉 PASO 5: Probar tu API en Render

Una vez desplegado, tu API estará disponible en:

```
https://api-frases.onrender.com
```

(Reemplaza `api-frases` con el nombre que le pusiste)

### Endpoints disponibles:

| Endpoint | URL completa |
|----------|--------------|
| Listar frases | `https://api-frases.onrender.com/api/frases/frase` |
| Obtener por ID | `https://api-frases.onrender.com/api/frases/frase/1` |
| Crear frase | `POST https://api-frases.onrender.com/api/frases/frase` |
| Swagger | `https://api-frases.onrender.com/documentacion` |

### Prueba rápida con curl:

```bash
# Listar todas las frases
curl https://api-frases.onrender.com/api/frases/frase

# Crear una nueva frase
curl -X POST https://api-frases.onrender.com/api/frases/frase \
  -H "Content-Type: application/json" \
  -d '{
    "autor": "Albert Einstein",
    "textoFrase": "La imaginación es más importante que el conocimiento"
  }'
```

---

## 🔧 CONFIGURACIÓN LOCAL vs PRODUCCIÓN

### Para trabajar en LOCAL (MySQL):

```bash
# Asegúrate que el contenedor MySQL esté corriendo
docker start contenedorApiFrases

# Ejecuta la aplicación (usa perfil dev por defecto)
mvnw spring-boot:run
```

URL local: `http://localhost:8081/api/frases/frase`

### Para trabajar en PRODUCCIÓN (Render + PostgreSQL):

Simplemente haz `git push` y Render desplegará automáticamente:

```bash
git add .
git commit -m "Nueva funcionalidad"
git push origin main
```

Render detecta el push y redespliega automáticamente.

---

## ⚠️ IMPORTANTE: Diferencias entre MySQL y PostgreSQL

Tu aplicación funciona con **ambas bases de datos** gracias a JPA/Hibernate, pero hay pequeñas diferencias:

### Tipos de datos:
- **MySQL:** `DATETIME`
- **PostgreSQL:** `TIMESTAMP`

Hibernate maneja esto automáticamente con `@Temporal(TemporalType.TIMESTAMP)`.

### Auto-increment:
- **MySQL:** `AUTO_INCREMENT`
- **PostgreSQL:** `SERIAL` o `IDENTITY`

Hibernate maneja esto automáticamente con `@GeneratedValue(strategy = GenerationType.IDENTITY)`.

**✅ Tu código ya está preparado para ambas, no necesitas cambiar nada.**

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: "Application failed to start"

**Causa:** La variable `DATABASE_URL` no está configurada.

**Solución:** Ve a tu Web Service en Render → Environment → Agrega `DATABASE_URL` con la URL de tu base de datos PostgreSQL.

---

### Error: "Connection timeout"

**Causa:** La base de datos PostgreSQL está en otra región.

**Solución:** Asegúrate que el Web Service y la base de datos estén en la **misma región** (Oregon recomendado).

---

### Error: "Port already in use"

**Causa:** En Render, el puerto es dinámico (variable `PORT`).

**Solución:** Ya está configurado en el `Dockerfile` con `-Dserver.port=${PORT:-8080}`.

---

### La aplicación funciona local pero no en Render

**Checklist:**
1. ✅ ¿Está la variable `DATABASE_URL` configurada?
2. ✅ ¿El perfil activo es `prod`? (variable `SPRING_PROFILES_ACTIVE=prod`)
3. ✅ ¿La base de datos PostgreSQL está creada y corriendo?
4. ✅ ¿El `Dockerfile` está en la raíz del proyecto?
5. ✅ ¿Hiciste `git push` después de los cambios?

---

## 📊 RESUMEN DE CONFIGURACIÓN

| Ambiente | Base de datos | Puerto | Perfil Spring | URL |
|----------|---------------|--------|---------------|-----|
| **Local** | MySQL (Docker) | 8081 | `dev` | http://localhost:8081 |
| **Render** | PostgreSQL | 8080 (dinámico) | `prod` | https://api-frases.onrender.com |

---

## 🎯 PRÓXIMOS PASOS

1. ✅ Crea el `.gitignore`
2. ✅ Sube tu código a GitHub
3. ✅ Crea la base de datos PostgreSQL en Render
4. ✅ Crea el Web Service en Render
5. ✅ Configura la variable `DATABASE_URL`
6. ✅ Espera el despliegue
7. ✅ Prueba tu API en la URL de Render

---

## 📞 COMANDOS ÚTILES

```bash
# Ejecutar en local con MySQL (perfil dev)
mvnw spring-boot:run

# Ejecutar en local con PostgreSQL (si quieres probar)
mvnw spring-boot:run -Dspring.profiles.active=prod -DDATABASE_URL=postgresql://...

# Ver logs en Render
# Ve a tu Web Service → Logs (en el dashboard de Render)

# Compilar y crear JAR
mvnw clean package

# Ejecutar el JAR manualmente con perfil prod
java -Dspring.profiles.active=prod -jar target/Frases7CM1-0.0.1-SNAPSHOT.jar
```

---

## ✨ VENTAJAS DE ESTA CONFIGURACIÓN

✅ **Funciona en local con MySQL** (sin cambios)  
✅ **Funciona en Render con PostgreSQL** (automáticamente)  
✅ **Perfiles separados** (dev y prod)  
✅ **Dockerfile optimizado** (construcción multi-stage)  
✅ **Variables de entorno** (seguras, no están en el código)  
✅ **Despliegue automático** (cada push a main)  

---

**¿Listo para desplegar?** Sigue los pasos en orden y en 15 minutos tendrás tu API funcionando en Render. 🚀

Si tienes algún error durante el despliegue, copia el mensaje de error y te ayudo a resolverlo.

