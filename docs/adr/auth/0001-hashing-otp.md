# ADR 1: Uso de BCrypt para Hashing de OTP peor a la espera de SH25

## Estado
Aceptado

## Contexto
El sistema genera códigos OTP de 6 dígitos. Si se guardan en texto plano en la tabla `otps`, un acceso no autorizado a la base de datos permitiría a un atacante ver los códigos activos y suplantar usuarios.

## Decisión
Utilizaremos `PasswordEncoder` (BCrypt) para almacenar el hash del OTP.

## Consecuencias
- **Positivo:** Mayor seguridad; el código real nunca reside en la DB.
- **Negativo:** El proceso de verificación requiere un paso extra de CPU para el `matches()`.