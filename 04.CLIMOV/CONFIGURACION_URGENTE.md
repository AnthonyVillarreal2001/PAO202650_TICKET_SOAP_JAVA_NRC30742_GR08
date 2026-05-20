# 🔧 CONFIGURACIÓN URGENTE - LEER ANTES DE EJECUTAR

## ⚠️ IMPORTANTE: Configurar URL del Servidor

Antes de ejecutar la aplicación, **DEBES** configurar la URL del servidor SOAP.

### 📝 Pasos:

1. Abre el archivo: `app/src/main/java/ec/edu/monster/service/SoapService.kt`

2. Busca la línea 25 aproximadamente:
   ```kotlin
   private val baseUrl = "http://localhost:8080/WS_TicketPremium_Server/WSTicketPremium"
   ```

3. Reemplázala según tu caso:

#### 🖥️ Si usas EMULADOR Android (el servidor está en tu PC):
```kotlin
private val baseUrl = "http://10.0.2.2:8080/WS_TicketPremium_Server/WSTicketPremium"
```

#### 📱 Si usas DISPOSITIVO FÍSICO (en la misma red WiFi):
```kotlin
private val baseUrl = "http://TU_IP_LOCAL:8080/WS_TicketPremium_Server/WSTicketPremium"
```
*Para obtener tu IP local:*
- Windows: `ipconfig` en CMD, busca "IPv4"
- Ejemplo: `http://192.168.1.100:8080/WS_TicketPremium_Server/WSTicketPremium`

#### ☁️ Si usas SERVIDOR EN LA NUBE:
```kotlin
private val baseUrl = "http://tu-servidor.com:8080/WS_TicketPremium_Server/WSTicketPremium"
```

### ✅ Verificación

Para verificar que el servidor está accesible desde el emulador/dispositivo:

1. Asegúrate de que el servidor SOAP esté ejecutándose
2. Desde el navegador del emulador/dispositivo, intenta acceder a:
   ```
   http://10.0.2.2:8080/WS_TicketPremium_Server/WSTicketPremium?wsdl
   ```
   (Usa la URL que configuraste)

3. Deberías ver el WSDL del servicio

### 🔴 Errores Comunes

**Error: "Connection timeout" o "Failed to connect"**
- ✓ Verifica que el servidor esté ejecutándose
- ✓ Verifica que la URL sea correcta
- ✓ Si usas emulador, usa `10.0.2.2` en vez de `localhost`
- ✓ Si usas dispositivo físico, asegúrate de estar en la misma red WiFi
- ✓ Verifica que el firewall no bloquee el puerto 8080

**Error: "Credenciales incorrectas"**
- ✓ Verifica que el usuario y contraseña sean correctos
- ✓ Consulta con el administrador del servidor

### 📋 Credenciales de Prueba

Las credenciales correctas son:
- **Usuario**: `MONSTER`
- **Contraseña**: `MONSTER9` (NO es MONSTER89, es MONSTER9)

⚠️ **Importante**: 
- La aplicación envía la contraseña sin hashear
- El servidor la hashea internamente con SHA-256
- El servidor retorna "Exitoso" o "Denegado" (no true/false)

### 🔒 Seguridad

⚠️ **Nota de Seguridad**: La aplicación está configurada para permitir tráfico HTTP sin cifrar (`usesCleartextTraffic="true"`). Esto es solo para desarrollo. En producción, usa HTTPS.

---

## 📞 Soporte

Si tienes problemas, verifica:
1. El servidor está ejecutándose
2. La URL es correcta
3. El dispositivo/emulador puede acceder al servidor
4. Las credenciales son válidas
