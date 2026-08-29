# Dracarys Mod 0.1.0 — Forge 1.20.1

Primera implementación jugable del concepto Dracarys Mod.

## Requisitos
- Minecraft 1.20.1
- Minecraft Forge 47.4.10 (Recommended)
- Java 17
- Cliente y servidor deben instalar el mod.

## Implementado en v0.1.0
- Dragón salvaje con 15 variantes de color.
- 4 potenciales de tamaño: small, medium, large, giant.
- 6 etapas: baby, juvenile, adolescent, adult, ancient, colossal.
- Genética individual: tamaño, fuerza, vitalidad, velocidad, fuego y crecimiento.
- Stats escalados por tamaño/edad/genética.
- Combate físico, barrido AOE para dragones grandes y aliento de fuego.
- Vuelo de combate y vuelo montado básico.
- Estado derribado antes de morir para dragones salvajes.
- Ventana configurable de 60 s para matarlo o domesticarlo.
- Domesticación con carne mediante tag `dracarysmod:dragon_meats` (incluye carnes vanilla y referencias opcionales a tags comunitarios de carne; los modpacks también pueden extender `dracarysmod:dragon_meats`).
- Requisito de carne escalado por tamaño.
- Huevos de las 15 variantes que generan una cría ya vinculada al jugador.
- Crecimiento por tiempo y aceleración mediante alimentación.
- Drops: escamas por color, huesos, colmillos, garras, sangre, corazón, membrana y carne.
- 15 sets completos de armadura de escamas; el color corresponde al dragón.
- Bonus de set completo: resistencia al fuego.
- Espada, pico y hacha de hueso de dragón + daga de colmillo.
- Nidos de worldgen muy raros (1 intento por ~800 chunks por defecto) con tesoro y posibilidad muy baja de huevo.
- Spawn natural raro mediante Forge biome modifiers.
- Creative Tab propio.
- Recetas vanilla/JEI-friendly.
- Configuración COMMON.
- Comandos de prueba.

## Comandos
`/dracarys spawn <variant> <small|medium|large|giant> <baby|juvenile|adolescent|adult|ancient|colossal>`

Ejemplo:
`/dracarys spawn red giant colossal`

`/dracarys info` muestra información del dragón más cercano a 20 bloques.

## Domesticación
1. Reduce un dragón salvaje hasta el umbral configurado (20 HP por defecto).
2. Quedará derribado durante 60 segundos.
3. Durante ese tiempo puedes matarlo o darle carne.
4. La cantidad requerida escala con su potencial de tamaño: 16/24/40/64 por defecto.
5. Si se completa la alimentación, el dragón queda domesticado y sentado.
6. Si expira el tiempo, se levanta con parte de su vida y vuelve al combate.

## Montura
Un dragón domesticado adolescente o mayor se puede montar con clic derecho sin carne. Mira hacia arriba y avanza para despegar. El control de vuelo de v0.1.0 es deliberadamente simple y será sustituido por un controlador de vuelo/animación dedicado en versiones posteriores.

## Rendimiento
Los dragones gigantes conservan un tamaño conceptual de hasta ~100 bloques para stats/progresión, pero su hitbox y render scale se limitan para evitar problemas graves de broad-phase collision, pathfinding y renderizado en modpacks de cientos de mods.

## Configuración
Forge genera `config/dracarysmod-common.toml`. Ahí puedes cambiar umbral/tiempo derribado, carne requerida, velocidad de crecimiento, daño de fuego, griefing y probabilidad genética gigante.

La frecuencia base de spawn y nidos es data-driven y puede ajustarse desde:
- `data/dracarysmod/forge/biome_modifier/add_dragons.json`
- `data/dracarysmod/worldgen/placed_feature/dragon_nest.json`

## Compatibilidad
- Se usan registries y biome modifiers de Forge; no se reemplazan biomas vanilla.
- No se usan mixins.
- Las carnes de otros mods se integran mediante tags opcionales comunes cuando existen; cualquier modpack puede añadir otras carnes directamente a `dracarysmod:dragon_meats` sin tocar Java.
- Epic Fight: esta versión evita mixins y hooks invasivos. Compatibilidad de animaciones/hitboxes específica de Epic Fight queda como fase posterior.

## Assets
Los modelos/texturas incluidos son placeholders funcionales generados para esta build. El modelo de dragón usa geometría vanilla-style propia, sin GeckoLib.

## Build
El proyecto está configurado para ForgeGradle 6 y Forge `1.20.1-47.4.10`.

Usa JDK 17 y Gradle 8.8:
`gradle build`

El JAR reobfuscado se generará en `build/libs/`.

## Estado de validación de esta entrega
El código y recursos fueron generados y sometidos a validaciones estáticas locales (JSON, rutas, namespaces y consistencia de archivos). El entorno de ejecución de ChatGPT no dispone de Gradle/ForgeGradle ni puede descargar binarios externos desde Maven, por lo que el JAR runtime no pudo compilarse aquí. No se afirma prueba dentro de Minecraft.

## Próximas fases recomendadas
- GeckoLib o animación propia avanzada: caminar, correr, despegar, planear, aterrizar, dormir, rugir, cola, mordida, garras, inconsciencia.
- Multipart hitboxes para colosales.
- Guaridas subterráneas y estructuras Jigsaw complejas.
- IA territorial, sueño, caza, defensa de huevos y patrullaje avanzado.
- Inventario de montura, órdenes y GUI.
- Compatibilidad explícita con Epic Fight.
- Más armas, escudos, arcos, pociones y artefactos.
