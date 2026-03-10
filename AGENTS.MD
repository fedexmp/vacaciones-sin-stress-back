# AGENTS.md

## Objetivo

Este proyecto es un **backend monolítico** organizado por funcionalidades, con una estructura simple, clara y mantenible.

Las reglas de este documento deben ser respetadas por **todos los modelos de IA** que generen, modifiquen o refactoricen código en este repositorio.

## Principios generales

1. **Priorizar simplicidad por sobre complejidad.**
    - No implementar arquitectura hexagonal, clean architecture, CQRS, event sourcing ni patrones innecesarios.
    - La solución debe ser directa, legible y fácil de mantener por un equipo backend estándar.

2. **Organización por funcionalidad.**
    - El código debe agruparse por módulos funcionales, por ejemplo:
        - `account`
        - `auth`
        - `user`
        - `notification`
        - `order`
    - Cada funcionalidad contiene sus propias capas internas.

3. **Consistencia estructural.**
    - Todos los módulos deben respetar la misma estructura de carpetas y naming.
    - No crear estilos distintos según el modelo que esté generando código.

4. **Código orientado a negocio.**
    - La lógica de negocio debe vivir en servicios.
    - Los controladores solo orquestan entrada/salida HTTP.
    - Los repositories solo acceden a datos.
    - Las entidades representan persistencia.
    - Los DTO representan contratos de entrada/salida.
    - Uso de controller advice para manejar excepciones.

5. **No agregar complejidad no solicitada.**
    - No agregar nuevas librerías, frameworks o patrones sin necesidad explícita.
    - No reestructurar masivamente un módulo si el cambio pedido es puntual.
    - No introducir abstracciones genéricas si no aportan valor claro.

6. **Reglas de Mapper (MapStruct).**

    - Todo mapeo entre DTO y Entity debe hacerse con MapStruct.
    - Todos los mappers viven en `mapper/`.
    - Formato obligatorio:
      @Mapper(componentModel = "spring")
      public interface XxxMapper {
      }

7. **Reglas de Tests.**
   ### Prohibido
   - JUnit 4
   - Tests de integración pesados salvo que se pidan explícitamente

8. **Reglas de Java Doc**
    - Agregar JavaDoc en clases y métodos públicos cuando aporte claridad.
    - Priorizar JavaDoc en:
      - services
      - métodos de negocio
      - controllers
      - componentes con lógica no trivial
    - No escribir comentarios obvios o redundantes.
    - El JavaDoc debe explicar intención, no repetir el nombre del método.
---

## Estructura estándar del proyecto

La estructura base debe ser similar a la siguiente:

```text
src/main/java/com/company/project/
├── config/
├── common/
├── exception/
├── account/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   ├── service/
│   │   ├── AccountService.java
│   │   └── impl/
│   │       └── AccountServiceImpl.java
│   └── specification/      (solo si realmente aplica)
├── auth/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   ├── service/
│   │   ├── AuthService.java
│   │   └── impl/
│   │       └── AuthServiceImpl.java
│   └── security/           (si corresponde al módulo)
```
