#!/usr/bin/env python3
from pathlib import Path
import json, re, sys
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources'
JAVA=ROOT/'src/main/java'
errors=[]; warnings=[]; checks=[]

def ok(label, cond, detail=''):
    (checks if cond else errors).append((label, detail))

# 1 JSON parse
json_files=list(RES.rglob('*.json'))
for p in json_files:
    try: json.loads(p.read_text(encoding='utf-8'))
    except Exception as e: errors.append((f'JSON {p.relative_to(ROOT)}',str(e)))
checks.append((f'JSON syntax ({len(json_files)} files)','parsed'))

variants=['black','white','gray','red','crimson','orange','gold','green','dark_green','blue','dark_blue','turquoise','purple','silver','brown']
base_items=['dragon_bone','dragon_fang','dragon_claw','dragon_heart','dragon_blood','wing_membrane','raw_dragon_meat','dragonbone_sword','dragonbone_pickaxe','dragonbone_axe','dragon_fang_dagger']
armor_suffix=['helmet','chestplate','leggings','boots']
items=set(base_items)
for v in variants:
    items.add(f'{v}_dragon_scale'); items.add(f'{v}_dragon_egg')
    items.update(f'{v}_dragon_scale_{s}' for s in armor_suffix)

# 2 item models and textures
for item in sorted(items):
    model=RES/f'assets/dracarysmod/models/item/{item}.json'
    texture=RES/f'assets/dracarysmod/textures/item/{item}.png'
    ok(f'model:{item}',model.exists(),str(model.relative_to(ROOT)))
    ok(f'texture:{item}',texture.exists(),str(texture.relative_to(ROOT)))
    if model.exists():
        try:
            d=json.loads(model.read_text())
            tex=d.get('textures',{}).get('layer0','')
            if tex.startswith('dracarysmod:item/'):
                target=RES/'assets/dracarysmod/textures/item'/f'{tex.split("/")[-1]}.png'
                ok(f'model texture ref:{item}',target.exists(),str(target.relative_to(ROOT)))
        except Exception: pass

# 3 dragon + armor textures
for v in variants:
    ok(f'dragon texture:{v}',(RES/f'assets/dracarysmod/textures/entity/dragon/{v}.png').exists())
    for layer in (1,2): ok(f'armor texture:{v}:{layer}',(RES/f'assets/dracarysmod/textures/models/armor/{v}_dragon_scale_layer_{layer}.png').exists())

# 4 recipes output ids and tag ingredients
for p in (RES/'data/dracarysmod/recipes').glob('*.json'):
    d=json.loads(p.read_text())
    result=d.get('result',{}).get('item')
    if result and result.startswith('dracarysmod:'):
        rid=result.split(':',1)[1]
        ok(f'recipe output:{p.name}',rid in items,result)

# 5 worldgen references
configured=RES/'data/dracarysmod/worldgen/configured_feature/dragon_nest.json'
placed=RES/'data/dracarysmod/worldgen/placed_feature/dragon_nest.json'
addfeat=RES/'data/dracarysmod/forge/biome_modifier/add_dragon_nests.json'
addspawn=RES/'data/dracarysmod/forge/biome_modifier/add_dragons.json'
for p in [configured,placed,addfeat,addspawn]: ok(f'worldgen:{p.name}',p.exists())
if placed.exists(): ok('placed feature ref',json.loads(placed.read_text()).get('feature')=='dracarysmod:dragon_nest')
if addspawn.exists(): ok('spawn entity ref',json.loads(addspawn.read_text()).get('spawners',{}).get('type')=='dracarysmod:dracarys_dragon')

# 6 namespace and dependency ranges
mods=(RES/'META-INF/mods.toml').read_text()
ok('Forge version floor','[47.4.10,48)' in mods)
ok('Minecraft exact branch','[1.20.1,1.20.2)' in mods)
ok('No mixin configs',not list(RES.rglob('*mixin*.json')))

# 7 meat tag optional external refs
meat=json.loads((RES/'data/dracarysmod/tags/items/dragon_meats.json').read_text())
external=[x for x in meat['values'] if isinstance(x,dict) and str(x.get('id','')).startswith('#forge:')]
ok('optional meat tags',bool(external) and all(x.get('required') is False for x in external),str(external))

# 8 Java duplicate method sanity + simple brace balance
for p in JAVA.rglob('*.java'):
    s=p.read_text()
    if s.count('{')!=s.count('}'):
        errors.append((f'brace balance:{p.relative_to(ROOT)}',f'{s.count("{")} != {s.count("}")}'))
entity=(JAVA/'com/dracarys/dracarysmod/entity/DracarysDragonEntity.java').read_text()
ok('single isFlying declaration',len(re.findall(r'\bboolean\s+isFlying\s*\(',entity))==1)

# Summary
failed=len(errors)
print(f'Dracarys static validation: {len(checks)} checks recorded, {failed} error(s), {len(warnings)} warning(s)')
if errors:
    for label,detail in errors[:100]: print('ERROR',label,detail)
    sys.exit(1)
print('PASS')
