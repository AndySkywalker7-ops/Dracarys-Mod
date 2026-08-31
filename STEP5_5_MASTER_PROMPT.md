# STEP 5.5 — BALANCED v2
# FINAL-CONCEPT RADICAL ANATOMICAL REBUILD
# REFERENCE-DRIVEN VOXEL SCULPT
# 4 PATAS + 2 ALAS INDEPENDIENTES

Actúa como desarrollador sénior experto en Minecraft Forge 1.20.1, Java,
ModelPart/HierarchicalModel, modelado voxel/low-poly anatómico, rigging y
diseño biomecánico de criaturas.

Trabajas sobre Dracarys Mod.

Esta etapa NO es un refinement incremental.
NO quiero cambiar cinco números y añadir algunos cubos.
Quiero una RECONSTRUCCIÓN RADICAL del modelo BALANCED v2.

El objetivo visual es que el jugador vea primero un DRAGÓN anatómicamente
creíble y solo después note que está construido mediante voxels.

============================================================
REGLA ANATÓMICA ABSOLUTA
============================================================

Dracarys BALANCED debe conservar SIEMPRE:

- 2 patas delanteras
- 2 patas traseras
- 2 alas independientes

TOTAL:
4 extremidades terrestres + 2 alas.

NO wyvern.
NO convertir alas en patas.
NO eliminar patas delanteras.

La referencia final se usa para:
- silueta
- musculatura
- proporciones
- cabeza
- cuello
- tórax
- pelvis
- cola
- alas
- postura

pero se adapta a nuestra anatomía de seis extremidades.

============================================================
PROBLEMAS QUE DEBEN DESAPARECER
============================================================

La versión actual aún presenta:
- lectura de "cuadrados unidos";
- cuello segmentado demasiado recto;
- torso poco musculoso;
- patas demasiado finas;
- articulaciones lineales;
- cola con aspecto de bloques consecutivos;
- alas estrechas o con apariencia de placa;
- membranas sin planform orgánico;
- poca masa en hombros, pecho, cadera y muslos.

STEP 5.5 NO ESTÁ APROBADO si alguno de estos defectos sigue dominando
la silueta.

============================================================
CAMBIO RADICAL PERMITIDO
============================================================

Puedes reemplazar 70–95 % de la geometría visual de BalancedDragonModel.

Puedes aumentar notablemente el número de ModelParts.

Objetivo razonable:
~180–280 piezas visuales efectivas incluyendo las creadas mediante loops.

NO conservar mala geometría solo por compatibilidad.

Mantén únicamente los bones de contrato:
body
neck_01
neck_02
neck_03
head
jaw
left_wing_root
right_wing_root
upper_arm
forearm
left_foreleg
right_foreleg
left_hindleg
right_hindleg
lower_leg
tail_01
tail_02
tail_03
tail_04

Puedes añadir todos los hijos que sean necesarios.

============================================================
PRINCIPIO DE ESCULTURA VOXEL
============================================================

NO crear curvas con una sola caja.

Crear volumen mediante:
- masas solapadas;
- tapering;
- piezas rotadas;
- transiciones de 5–20 % de overlap;
- distintos anchos/alturas;
- pivots anatómicos;
- xRot + yRot + zRot.

No debe verse aire entre regiones anatómicamente continuas.

============================================================
PROPORCIONES OBJETIVO
============================================================

Usa TORSO_LENGTH = 1.0 como referencia relativa.

Cabeza:
0.28–0.34 torso.

Cuello:
0.70–0.85 torso.

Cola:
1.35–1.60 torso.

Envergadura total:
3.6–4.5 torso.

Pecho:
la mayor masa corporal.

Abdomen:
65–72 % del ancho máximo del pecho.

Pelvis:
78–88 % del ancho máximo del pecho.

El dragón debe verse:
LARGO + BAJO + MUSCULOSO + ALADO.

NO modificar renderScale, DragonStage o DragonSizeTier.

============================================================
MUSCULATURA — OBLIGATORIA
============================================================

Debe existir lectura visual de:

TORSO:
- rib cage
- sternum
- left/right pectoral
- left/right latissimus
- wing scapula
- wing deltoid

PATA DELANTERA:
- shoulder mass
- triceps/biceps mass
- upper arm
- elbow
- proximal forearm
- distal forearm
- wrist
- metacarpal
- foot

PATA TRASERA:
- hip
- gluteal
- large thigh
- distal thigh
- knee
- proximal shin
- distal shin
- hock
- tarsus
- foot

ALAS:
- scapular root
- flight shoulder
- humerus
- elbow
- radius/ulna
- wrist
- hand
- metacarpal
- long digits

Las masas musculares deben solaparse con los huesos para que las
articulaciones no parezcan piezas separadas.

============================================================
CABEZA
============================================================

Objetivo:
cabeza larga, baja, agresiva y reptiliana.

Construir mediante al menos:
- occipital mass
- back skull
- temporal cranium
- jaw muscles
- cheeks
- brows
- snout base
- snout mid
- nose
- independent lower jaw
- jaw tip
- chin keel
- main horns
- secondary horns
- crown spines

El hocico debe estrecharse progresivamente.

La mandíbula debe ser robusta, no una placa fina.

============================================================
CUELLO
============================================================

Mantén neck_01, neck_02, neck_03 como bones compatibles.

Visualmente quiero 9–12 masas superpuestas.

La neutral pose debe formar una S:

THORAX
   \__
      \__
         \__
            HEAD

No todos los segmentos en xRot=0.

Debe comenzar grueso en pecho y adelgazar de forma progresiva.

============================================================
TORSO
============================================================

NO tres cajas.

Quiero una sucesión volumétrica:

THORAX:
5–7 masas solapadas.

ABDOMEN:
4–5 masas.

PELVIS:
4–6 masas.

Vista superior:

       THORAX
   █████████████
    ███████████
       █████
       WAIST
        ████
      ███████
       PELVIS
    ██████████

El cuerpo debe tener musculatura lateral y ventral.

============================================================
PATAS DELANTERAS
============================================================

No columnas.

Cadena visual:

SCAPULA/SHOULDER
      \
    UPPER ARM
       \
       ELBOW
          \
      FOREARM
          \
         WRIST
           \
      METACARPAL
           \
          FOOT
        /  |  \
      TOES + CLAWS

Usar masas proximales grandes y tapering distal.

============================================================
PATAS TRASERAS
============================================================

Muy musculosas y claramente digitígradas.

HIP/GLUTEAL
     \
   LARGE THIGH
        \
        KNEE
          \
          SHIN
            /
          HOCK
            \
           TARSUS
              \
              FOOT

La vista lateral debe mostrar claramente una Z anatómica.

============================================================
GROUNDING
============================================================

Los cuatro pies deben tocar visualmente el suelo en idle.

No bajar toda la entidad como hack.

Corregir:
- longitudes
- pivots
- ángulos
- metapodiales/tarsos
- posición del pie

Tolerancia visual objetivo:
lowest foot surface = ground ± 1.0 model unit.

============================================================
COLA
============================================================

Quiero 12–16 masas visuales solapadas sobre tail_01..04.

Base muy gruesa.

Taper continuo.

Curvatura acumulativa.

No fila de cubos.

La cola debe continuar la columna vertebral y funcionar visualmente
como contrapeso.

============================================================
ALAS — PRIORIDAD ABSOLUTA
============================================================

Las alas deben cambiar RADICALMENTE.

Deben ser MUCHO MÁS:
- largas
- anchas
- musculosas en raíz
- membranosas
- orgánicas

No quiero una pala.
No quiero un rectángulo.
No quiero tres palitos con paneles.

============================================================
DIMENSIONES OBJETIVO DE CADA ALA
============================================================

En unidades internas de modelo BALANCED:

shoulder -> outer tip:
aprox. 145–175 model units.

maximum membrane chord:
aprox. 60–85 model units.

La zona media del ala debe ser MUY ANCHA.

La punta debe afinarse.

La membrana debe dominar la superficie visual.

============================================================
ESQUELETO DEL ALA
============================================================

Cadena obligatoria:

SCAPULAR ROOT
   \
   HUMERUS
      \
      ELBOW
         \
       RADIUS/ULNA
            \
            WRIST
              \
              HAND
                 \
              METACARPAL
                / | \
             D1  D2  D3

Cada dedo principal debe tener 3 secciones:
base
mid
tip

Ratios aproximados de longitud total:
D1 = 1.00
D2 = 0.82
D3 = 0.65

Los dedos deben divergir.

NO paralelos.

============================================================
MEMBRANA DEL ALA
============================================================

ModelPart no ofrece triángulos arbitrarios, así que la membrana debe
aproximarse mediante MUCHOS cuboides finos y solapados.

Usar aprox. 20–35 strips por ala.

Thickness:
0.25–0.50 model units.

Cada strip debe tener:
- posición X distinta;
- leading edge distinto;
- trailing edge distinto;
- overlap lateral.

Desde arriba debe construirse un gran polígono blocky continuo.

Planform objetivo:

ROOT
 \____________________
  \______________________
   \________________________
     \_______________________
       \___________________
          \______________
             \_________
                \____

Borde posterior:
con 2–3 concavidades/scallops.

NO debe existir cielo atravesando accidentalmente la membrana.

============================================================
POSE IDLE DE ALAS
============================================================

No plegarlas tanto que parezcan cuchillas.

En idle deben mantenerse:
- ampliamente visibles;
- ligeramente elevadas;
- moderadamente barridas hacia atrás.

Objetivo aproximado:
wingRoot yaw 0.15–0.22 rad por lado.

No 0.45–0.60 rad de plegado.

La vista superior debe permitir evaluar toda la planform.

============================================================
VISTA SUPERIOR — TEST PRINCIPAL
============================================================

Debe verse aproximadamente:

                       HEAD
                        |
                       NECK
                        |
                 ______CHEST______
          ______/                 \______
         /                               \
   LEFT WING                         RIGHT WING
  /========\                       /========\
 /==========\                     /==========\
/============\                   /============\
   finger fan                       finger fan

                      WAIST
                        |
                      PELVIS
                        |
                       TAIL

Si desde arriba las alas siguen pareciendo barras:
FAIL.

============================================================
VISTA LATERAL
============================================================

Debe verse:
- cráneo largo;
- cuello curvo;
- pecho profundo;
- abdomen recogido;
- pelvis robusta;
- hombros musculosos;
- patas articuladas;
- cola larga;
- alas con raíz gruesa y superficie amplia.

No eje horizontal rígido.

============================================================
RENDER / GAMEPLAY PROTEGIDOS
============================================================

NO modificar:
DracarysDragonRenderer
CleanLongRangeDragonRenderEvents
tracking
culling
fallback renderer
renderScale
DragonStage
DragonSizeTier
AI
combat
taming
worldgen

NO reescribir multipart architecture en este step.

La calibración fina de hitboxes puede hacerse después de aprobar la nueva
silueta.

============================================================
CRITERIOS DE ACEPTACIÓN
============================================================

No declarar éxito por compilar.

STEP 5.5 solo está aprobado si:

1. Hay 4 patas + 2 alas.
2. El cuello ya no parece una cadena recta.
3. Se reconoce musculatura en pecho/hombros/cadera/muslos.
4. Thorax > abdomen en masa.
5. Pelvis recupera volumen.
6. Patas delanteras tienen articulación real.
7. Patas traseras muestran Z digitígrada.
8. Los 4 pies están apoyados.
9. Cola tiene taper continuo.
10. Cada ala tiene humerus/elbow/forearm/wrist/hand.
11. Cada ala tiene 3 dedos largos articulados.
12. Las alas son MUCHO más anchas que en STEP 5.4.
13. La membrana es una superficie continua.
14. La vista superior cambia drásticamente.
15. La silueta se aproxima claramente al reference sheet.
16. El jugador ve primero "dragón" y no "cubos conectados".

Si el cambio visual parece incremental:
STEP 5.5 FALLÓ.

============================================================
ENTREGABLE
============================================================

Genera:
STEP 5.5 — BALANCED v2 Final-Concept Radical Anatomy

Entregar:
- BalancedDragonModel.java
- AbstractDracarysDragonModel.java solo si requiere neutral pose
- documentación
- static validation
- build attempt
- patch ZIP
- SHA-256
- lista de archivos modificados

Commit sugerido:

Rebuild Balanced v2 final concept anatomy
