# STEP 5.10R — Head Asset Rebuild

## Status
Implementation patch produced. **Visual acceptance is still pending in Minecraft.** This step must not be marked approved from static/build checks alone.

## Strategy change
The rejected STEP 5.10 procedural skull geometry was disabled. `head` and `jaw` remain as empty animation anchors, but the visible skull/jaw is now an authored polygon mesh rendered through a dedicated entity render layer.

The implementation deliberately stops using `CubeListBuilder.addBox()` as the primary head sculpting method.

## Runtime architecture

```text
BalancedDragonModel
  neck_03
    head (empty animation anchor)
      jaw (empty animation anchor)

DracarysHeadAssetLayer
  -> dedicated variant head texture
  -> BalancedDragonModel.renderAuthoredHead(...)
       -> AuthoredDragonHeadMesh.renderSkull(...)
       -> jaw bone transform
       -> AuthoredDragonHeadMesh.renderJaw(...)
```

The body remains on its current normal renderer/variant texture. The new layer renders only the skull and jaw. It does not create another entity, another full dragon model, a FAR proxy, an outline pass, or a screen-space impostor.

## Authored geometry
- Skull: 653 authored vertices / 732 triangles.
- Jaw: 241 authored vertices / 272 triangles.
- Coherent tapered shell from occipital region to nose.
- Open ventral skull surface so the animated lower jaw can open without a fake cuboid mouth floor.
- Lateral/anterior eye planes positioned inside orbital/brow structures.
- Custom polygon brow and cheek wedges.
- Tapered multi-segment main/secondary/temporal horns.
- Integrated crown and cheek spikes.
- Individual conical upper/lower teeth.
- Separate tongue ribbon and lower-jaw shell.

## Editable source asset
`src/main/resources/assets/dracarysmod/models/entity/dragon/head/dracarys_head.obj`

The OBJ is intended as the editable source asset and can be imported into Blockbench. Runtime geometry is compiled into `AuthoredDragonHeadMesh.java` so there is no per-frame file parsing.

`dracarys_head.asset.json` records the animation anchor/pivot contract.

## UV / textures
A dedicated 256x256 head texture exists for every current variant:

black, blue, brown, crimson, dark_blue, dark_green, gold, gray, green, orange, purple, red, silver, turquoise, white.

The texture contains separate authored regions for:
- scale field / cranial plates;
- horn keratin;
- eye/iris;
- nostril cavity;
- teeth;
- gums/tongue/mouth interior.

## Long-range rendering
The existing STEP 4.0.8 `shouldRender()` / culling / custom render distance logic is preserved. `DracarysDragonRenderer` receives only one functional addition: registration of `DracarysHeadAssetLayer`.

## What is intentionally not changed
- body geometry;
- neck geometry except using its existing head attachment point;
- wings;
- legs;
- tail;
- multipart hitbox layout;
- entity gameplay;
- AI;
- taming;
- stage/size logic;
- culling distances;
- fallback/long-range architecture.

## Acceptance
This patch is not approved until the in-game head is compared against the approved turnaround from FRONT / LEFT / RIGHT / TOP / BOTTOM / REAR / 3/4 LEFT / 3/4 RIGHT and open-jaw 3/4.
