from pathlib import Path
from math import sin, cos, pi, sqrt
from PIL import Image, ImageDraw
import random, json

ROOT = Path('/mnt/data/step510r3_work')
ASSET_DIR = ROOT / 'src/main/resources/assets/dracarysmod/models/entity/dragon/head'
TEX_DIR = ROOT / 'src/main/resources/assets/dracarysmod/textures/entity/dragon/head'
JAVA_DIR = ROOT / 'src/main/java/com/dracarys/dracarysmod/client/model/mesh'
ASSET_DIR.mkdir(parents=True, exist_ok=True)
TEX_DIR.mkdir(parents=True, exist_ok=True)
JAVA_DIR.mkdir(parents=True, exist_ok=True)

class Mesh:
    def __init__(self):
        self.v=[]  # x y z u v group
        self.t=[]  # a b c group
    def addv(self,x,y,z,u,v,group):
        self.v.append((float(x),float(y),float(z),float(u),float(v),group)); return len(self.v)-1
    def tri(self,a,b,c,group): self.t.append((a,b,c,group))
    def quad(self,a,b,c,d,group):
        self.tri(a,b,c,group); self.tri(a,c,d,group)

SKULL=Mesh(); JAW=Mesh()

# --- coherent upper skull shell ------------------------------------------------
profile=[(-0.92,0.34),(-1.00,0.02),(-0.90,-0.50),(-0.55,-0.88),(0.0,-1.0),(0.55,-0.88),(0.90,-0.50),(1.00,0.02),(0.92,0.34)]
rings=[
    # STEP 5.10R3: collar -> enlarged rear cranium -> short tapered snout.
    # z, half-width, half-height, centerY, cheek side bulge
    ( 6.0,5.40,3.60,-0.20,1.02),   # authored skull/neck collar
    ( 3.0,7.20,4.50,-0.35,1.08),
    ( 0.0,8.20,5.10,-0.42,1.12),
    (-3.5,8.70,5.25,-0.35,1.16),   # maximum rear/temporal skull
    (-7.0,7.60,4.70,-0.10,1.13),   # orbital region
    (-9.8,6.40,3.90,0.10,1.08),
    (-12.3,5.30,3.20,0.25,1.04),
    (-14.7,4.25,2.55,0.40,1.00),
    (-16.8,3.20,1.95,0.55,1.00),
    (-18.5,2.15,1.35,0.68,1.00),   # compact nose (-16% width / -18% height vs R2)
]
ring_ids=[]
for ri,(z,w,h,cy,bulge) in enumerate(rings):
    ids=[]
    for j,(nx,ny) in enumerate(profile):
        # lower lateral profile points widen around temporal/cheek region
        local_bulge = bulge if j in (0,1,7,8) else 1.0
        # slightly narrow the center cranial roof so top view reads wedge, not rectangle
        x=nx*w*local_bulge
        y=cy+ny*h
        u=0.035 + (j/(len(profile)-1))*0.625
        v=0.055 + (ri/(len(rings)-1))*0.665
        ids.append(SKULL.addv(x,y,z,u,v,'skull'))
    ring_ids.append(ids)

for i in range(len(ring_ids)-1):
    a=ring_ids[i]; b=ring_ids[i+1]
    for j in range(len(profile)-1):
        SKULL.quad(a[j],a[j+1],b[j+1],b[j],'skull')

# Rear cap (fan) and nose cap
for ids,center,group in [
    (ring_ids[0], (0,-0.12,6.35), 'skull'),
    (ring_ids[-1], (0,0.72,-18.85), 'nose')
]:
    ci=SKULL.addv(*center,0.34,0.07 if group=='skull' else 0.72,group)
    for j in range(len(profile)-1):
        if group=='skull': SKULL.tri(ci,ids[j+1],ids[j],group)
        else: SKULL.tri(ci,ids[j],ids[j+1],group)

# Dorsal midline keel plates integrated into skull silhouette
for k,(z,w,h) in enumerate([(-8.8,1.25,0.52),(-6.0,1.55,0.72),(-3.0,1.95,0.98),(0.0,2.20,1.15),(2.8,1.95,1.02)]):
    # low wedge using 6 vertices
    y=-4.15 if z<-8 else (-4.75 if z<-3 else -5.0)
    pts=[(-w,y,z-1.6),(w,y,z-1.6),(w*0.78,y-0.45-h,z),( -w*0.78,y-0.45-h,z),
         (w*0.55,y,z+1.5),(-w*0.55,y,z+1.5)]
    ids=[SKULL.addv(x,y0,z0,0.20+k*.03,0.15+k*.02,'skull') for x,y0,z0 in pts]
    SKULL.quad(ids[0],ids[1],ids[2],ids[3],'skull'); SKULL.quad(ids[3],ids[2],ids[4],ids[5],'skull')
    SKULL.quad(ids[0],ids[3],ids[5],ids[5],'skull'); SKULL.quad(ids[1],ids[4],ids[2],ids[2],'skull')

# Helper: custom wedge/prism (non-box) for brow / cheek integration
def add_wedge(mesh, pts_front, pts_back, uv_base, group='skin'):
    # each is 4 points around cross section
    ids1=[mesh.addv(*p,uv_base[0]+(i%2)*0.03,uv_base[1]+(i//2)*0.03,group) for i,p in enumerate(pts_front)]
    ids2=[mesh.addv(*p,uv_base[0]+0.04+(i%2)*0.03,uv_base[1]+(i//2)*0.03,group) for i,p in enumerate(pts_back)]
    mesh.quad(ids1[0],ids1[1],ids1[2],ids1[3],group)
    mesh.quad(ids2[3],ids2[2],ids2[1],ids2[0],group)
    for i in range(4): mesh.quad(ids1[i],ids1[(i+1)%4],ids2[(i+1)%4],ids2[i],group)

for side in (-1,1):
    # STEP 5.10R3: stronger supraorbital overhang; still part of one skull silhouette.
    add_wedge(SKULL,
        [(side*3.2,-4.35,-3.6),(side*7.25,-3.55,-4.0),(side*6.95,-2.45,-4.25),(side*3.3,-2.95,-3.9)],
        [(side*2.55,-3.55,-9.0),(side*5.35,-3.05,-9.25),(side*4.9,-2.05,-9.30),(side*2.4,-2.55,-9.05)],
        (0.10,0.34),'brow')
    # Enlarged temporal/cheek mass behind the eye; rear-heavy and jaw-supporting.
    add_wedge(SKULL,
        [(side*6.15,-0.45,-1.2),(side*8.35,0.45,-2.0),(side*7.55,2.35,-2.8),(side*5.35,1.45,-1.6)],
        [(side*4.65,0.05,-9.2),(side*6.10,0.65,-9.55),(side*5.45,2.05,-9.7),(side*3.85,1.30,-9.25)],
        (0.18,0.40),'cheek')

# Eyes as inset side quads with dedicated UV patch
for side in (-1,1):
    x=side*6.62
    zc=-6.8; yc=-1.58
    # plane slightly angled by z offset across top/bottom
    pts=[(x,yc-0.65,zc-0.75),(x,yc-0.65,zc+0.75),(x,yc+0.55,zc+0.68),(x,yc+0.55,zc-0.68)]
    if side<0: pts=pts[::-1]
    uv=[(0.842,0.050),(0.900,0.050),(0.900,0.108),(0.842,0.108)]
    ids=[SKULL.addv(*p,*uv[i],'eye') for i,p in enumerate(pts)]
    SKULL.quad(*ids,'eye')

# Nostrils: dark inset plaques at the nose sides
for side in (-1,1):
    x=side*1.75; yc=-0.10; zc=-17.35
    pts=[(x,yc-0.18,zc-0.38),(x,yc-0.18,zc+0.34),(x,yc+0.22,zc+0.32),(x,yc+0.22,zc-0.36)]
    if side<0: pts=pts[::-1]
    uv=[(0.842,0.145),(0.900,0.145),(0.900,0.195),(0.842,0.195)]
    ids=[SKULL.addv(*p,*uv[i],'nostril') for i,p in enumerate(pts)]
    SKULL.quad(*ids,'nostril')

# Cone/frustum between two points; basis generated robustly
def add_tapered_segment(mesh,p0,p1,r0,r1,sides=7,uvrect=(.72,.05,.82,.70),group='horn'):
    ax=p1[0]-p0[0]; ay=p1[1]-p0[1]; az=p1[2]-p0[2]
    L=sqrt(ax*ax+ay*ay+az*az); ax/=L; ay/=L; az/=L
    # choose reference vector not parallel
    rx,ry,rz=(0,1,0) if abs(ay)<0.85 else (1,0,0)
    # u = axis x ref
    ux=ay*rz-az*ry; uy=az*rx-ax*rz; uz=ax*ry-ay*rx
    ul=sqrt(ux*ux+uy*uy+uz*uz); ux/=ul; uy/=ul; uz/=ul
    # v = axis x u
    vx=ay*uz-az*uy; vy=az*ux-ax*uz; vz=ax*uy-ay*ux
    ids0=[]; ids1=[]
    for i in range(sides):
        a=2*pi*i/sides; c=cos(a); s=sin(a)
        o0=(ux*c+vx*s,uy*c+vy*s,uz*c+vz*s)
        q0=(p0[0]+o0[0]*r0,p0[1]+o0[1]*r0,p0[2]+o0[2]*r0)
        q1=(p1[0]+o0[0]*r1,p1[1]+o0[1]*r1,p1[2]+o0[2]*r1)
        u=uvrect[0]+(i/sides)*(uvrect[2]-uvrect[0])
        ids0.append(mesh.addv(*q0,u,uvrect[1],group)); ids1.append(mesh.addv(*q1,u,uvrect[3],group))
    for i in range(sides): mesh.quad(ids0[i],ids0[(i+1)%sides],ids1[(i+1)%sides],ids1[i],group)
    return ids1

def add_horn_chain(mesh,points,radii,group='horn'):
    for i in range(len(points)-1):
        add_tapered_segment(mesh,points[i],points[i+1],radii[i],radii[i+1],7,(.72,.05+i*.12,.82,.17+i*.12),group)

# Major and secondary horns + side horns — R3: thicker integrated roots, stronger backward curve.
for side in (-1,1):
    add_horn_chain(SKULL,[
        (side*4.8,-4.15,1.0),(side*6.0,-6.10,5.8),(side*7.5,-7.35,10.7),(side*8.6,-7.85,15.2)
    ],[1.85,1.34,.78,.14],'main_horn')
    add_horn_chain(SKULL,[
        (side*5.35,-3.25,-1.0),(side*6.55,-4.75,2.7),(side*7.45,-5.35,6.7)
    ],[1.28,.72,.12],'secondary_horn')
    add_horn_chain(SKULL,[
        (side*6.55,-1.65,-3.8),(side*7.75,-2.30,-0.4),(side*8.45,-2.55,3.2)
    ],[.88,.46,.10],'temporal_horn')

# Crown spikes from cranial roof, true tapered custom cones
for i,(x,z,h,r) in enumerate([(0,1.8,4.5,1.05),(0,-1.0,4.9,1.0),(0,-3.8,4.2,.9),
                               (-2.4,0.8,3.5,.8),(2.4,0.8,3.5,.8),(-3.4,-2.0,3.0,.7),(3.4,-2.0,3.0,.7)]):
    base_y=-4.8 if z>0 else (-5.0 if z>-2 else -4.7)
    add_tapered_segment(SKULL,(x,base_y,z),(x*1.03,base_y-h,z+0.5),r,.08,6,(.72,.30,.82,.58),'crown_spike')

# Cheek spikes, swept rearward, embedded root
for side in (-1,1):
    for j,(z,y) in enumerate([(-4.0,0.5),(-7.0,0.9),(-9.8,1.0)]):
        add_tapered_segment(SKULL,(side*5.8,y,z),(side*8.3,y-0.2,z+2.2),.65-j*.08,.07,6,(.72,.40,.82,.62),'cheek_spike')

# --- upper teeth (custom conical teeth, rooted inside lip) ----------------------
def add_tooth(mesh,base,tip,r=.26,group='teeth'):
    add_tapered_segment(mesh,base,tip,r,.035,5,(.915,.05,.962,.14),group)

# R3: smaller, irregular upper teeth; mostly hidden in a closed idle mouth.
for z,w,y,ln,r in [
    (-6.8,5.45,1.05,1.02,.22),(-9.1,4.95,1.02,.98,.20),(-11.3,4.35,0.98,.94,.19),
    (-13.4,3.75,0.94,.90,.18),(-15.3,3.15,0.90,.86,.17),(-16.9,2.55,0.86,.82,.16)
]:
    for side in (-1,1):
        add_tooth(SKULL,(side*w,y,z),(side*w*.985,y+ln,z-.12),r)
# central front fangs remain the dominant teeth, but no white saw-blade in idle.
for side in (-1,1):
    add_tooth(SKULL,(side*1.15,0.84,-17.65),(side*1.12,1.99,-17.78),.24)

# --- jaw shell -----------------------------------------------------------------
jprofile=[(-1.0,-0.16),(-1.0,0.12),(-0.72,0.76),(0.0,1.08),(0.72,0.76),(1.0,0.12),(1.0,-0.16)]
jrings=[
    # rear jaw is deep/muscular; strong width/height taper toward the front.
    (2.0,6.30,2.65,0.00),(-1.0,6.10,2.55,0.02),(-4.0,5.60,2.35,0.05),(-7.0,4.85,2.05,0.08),
    (-9.8,4.05,1.72,0.10),(-12.2,3.25,1.38,0.12),(-14.25,2.48,1.08,0.14)
]
jids=[]
for ri,(z,w,h,cy) in enumerate(jrings):
    ids=[]
    for j,(nx,ny) in enumerate(jprofile):
        x=nx*w; y=cy+ny*h
        u=.035+(j/(len(jprofile)-1))*.625; v=.755+(ri/(len(jrings)-1))*.205
        ids.append(JAW.addv(x,y,z,u,v,'jaw'))
    jids.append(ids)
for i in range(len(jids)-1):
    for j in range(len(jprofile)-1): JAW.quad(jids[i][j],jids[i][j+1],jids[i+1][j+1],jids[i+1][j],'jaw')
# rear/nose caps on jaw
for ids,center in [(jids[0],(0,0.65,2.35)),(jids[-1],(0,0.62,-17.25))]:
    ci=JAW.addv(*center,.30,.90,'jaw')
    for j in range(len(jprofile)-1): JAW.tri(ci,ids[j],ids[j+1],'jaw')

# Tongue / mouth floor (curved ribbon)
tongue_pts=[]
for i,(z,w,y) in enumerate([(0.0,3.7,-0.12),(-3.5,3.5,-0.08),(-7.0,3.0,-0.04),(-10.5,2.3,0.0),(-13.0,1.4,0.04)]):
    l=JAW.addv(-w,y,z,.945,.30+i*.035,'tongue'); r=JAW.addv(w,y,z,.992,.30+i*.035,'tongue'); tongue_pts.append((l,r))
for i in range(len(tongue_pts)-1): JAW.quad(tongue_pts[i][0],tongue_pts[i][1],tongue_pts[i+1][1],tongue_pts[i+1][0],'tongue')

# lower teeth, jaw-local — smaller and mostly hidden at idle.
for z,w,y,ln,r in [(-3.0,5.25,-0.12,.90,.19),(-5.8,4.85,-0.10,.88,.18),(-8.5,4.25,-0.08,.85,.17),(-11.0,3.55,-0.06,.82,.16),(-13.2,2.85,-0.04,.80,.15)]:
    for side in (-1,1):
        add_tooth(JAW,(side*w,y,z),(side*w*.985,y-ln,z-.10),r)
for side in (-1,1):
    add_tooth(JAW,(side*1.05,-0.03,-13.75),(side*1.03,-1.03,-13.88),.21)

# Submandibular small spikes, rooted into jaw outer surface
for side in (-1,1):
    for k,(z,w) in enumerate([(-1.5,5.9),(-4.5,5.3),(-7.8,4.5)]):
        add_tapered_segment(JAW,(side*w,1.0,z),(side*(w+1.2),2.7,z+0.5),.42,.04,5,(.72,.48,.82,.63),'jaw_spike')

# STEP 5.10R3 idle-mouth correction: move the authored jaw shell upward relative
# to the existing jaw bone without changing animation code/pivots.  This closes
# the idle seam while preserving the bone's ability to open normally.
JAW.v=[(x,y-1.15,z,u,v,g) for (x,y,z,u,v,g) in JAW.v]

# ----------------------------------------------------------------------------
# OBJ source asset export. Mesh coordinates are model-pixel units.
# ----------------------------------------------------------------------------
def write_obj(path, skull, jaw):
    lines=['# Dracarys STEP 5.10R3 authored head asset','mtllib dracarys_head.mtl','o dracarys_head']
    allv=[]; allt=[]; offset=0
    for name,m in [('skull',skull),('jaw',jaw)]:
        lines.append(f'g {name}')
        for x,y,z,u,v,g in m.v:
            lines.append(f'v {x:.6f} {-y:.6f} {-z:.6f}')  # conventional preview coordinates
            lines.append(f'vt {u:.6f} {1-v:.6f}')
        for a,b,c,g in m.t:
            aa=a+1+offset; bb=b+1+offset; cc=c+1+offset
            lines.append(f'f {aa}/{aa} {bb}/{bb} {cc}/{cc}')
        offset+=len(m.v)
    path.write_text('\n'.join(lines)+'\n')

write_obj(ASSET_DIR/'dracarys_head.obj',SKULL,JAW)
(ASSET_DIR/'dracarys_head.mtl').write_text('newmtl dracarys_head\nmap_Kd ../../../textures/entity/dragon/head/gold.png\n')

# Manifest with runtime pivot contract
manifest={
  'format':'Dracarys authored head source 1.1-R3',
  'units':'Minecraft model pixels (runtime divides by 16)',
  'head_anchor':'existing neck_03/head ModelPart',
  'jaw_anchor':{'offset':[0.0,3.0,-4.15],'bone':'head/jaw'},
  'skull_vertices':len(SKULL.v),'skull_triangles':len(SKULL.t),
  'jaw_vertices':len(JAW.v),'jaw_triangles':len(JAW.t),
  'reference':'reference/STEP5_10_APPROVED_HEAD_REFERENCE.png'
}
(ASSET_DIR/'dracarys_head.asset.json').write_text(json.dumps(manifest,indent=2))

# ----------------------------------------------------------------------------
# Dedicated head textures per variant
# ----------------------------------------------------------------------------
colors={
'black':(32,32,32),'blue':(45,93,179),'brown':(99,66,42),'crimson':(112,21,43),
'dark_blue':(28,56,114),'dark_green':(28,80,47),'gold':(193,151,36),'gray':(106,106,106),
'green':(56,123,61),'orange':(202,97,32),'purple':(112,66,158),'red':(161,37,31),
'silver':(155,163,175),'turquoise':(37,148,148),'white':(206,206,206)}

def clamp(x): return max(0,min(255,int(x)))
def mix(c,f): return tuple(clamp(v*f) for v in c)

def make_tex(name,base):
    random.seed(7100+sum(base))
    im=Image.new('RGB',(256,256),mix(base,.88)); pix=im.load()
    # organic mottled skin
    for y in range(256):
        for x in range(184):
            n=(random.random()-.5)*0.14
            wave=0.035*sin(x*.11)+0.025*cos(y*.08)
            f=.88+n+wave
            pix[x,y]=mix(base,f)
    d=ImageDraw.Draw(im,'RGB')
    dark=mix(base,.48); mid=mix(base,.68); light=mix(base,1.18)
    # overlapping scale pattern (not every scale geometric)
    for row,y in enumerate(range(8,250,9)):
        off=0 if row%2==0 else 6
        for x in range(-6+off,184,12):
            d.arc([x,y,x+11,y+8],0,180,fill=dark,width=1)
            if (x//12+row)%3==0: d.line([(x+2,y+5),(x+5,y+7),(x+9,y+5)],fill=mid,width=1)
    # dorsal plates / lighter scute streaks
    for x in range(12,176,26): d.line([(x,2),(x+10,62)],fill=light,width=2)
    # horn keratin region 184..214
    for y in range(256):
        t=y/255
        col=tuple(clamp((32*(1-t)+150*t)*(.8+base[i]/600)) for i in range(3))
        d.rectangle([184,y,214,y],fill=col)
        if y%13==0: d.line([(184,y),(214,y+5)],fill=(26,24,22),width=1)
    # eye patch
    d.rectangle([215,0,232,34],fill=(12,10,8))
    d.ellipse([217,3,230,28],fill=(188,122,24))
    d.ellipse([222,4,225,27],fill=(8,5,3))
    d.point((220,8),fill=(255,230,140))
    # nostril / deep cavity patch
    d.rectangle([215,35,232,64],fill=(14,12,12))
    # teeth
    d.rectangle([233,0,248,64],fill=(222,217,192))
    for y in range(0,64,8): d.line([(233,y),(248,y+5)],fill=(174,164,140),width=1)
    # gum / tongue / mouth interior
    d.rectangle([233,65,255,170],fill=(70,18,24))
    for y in range(70,168,10): d.line([(234,y),(254,y+2)],fill=(105,30,38),width=1)
    # remaining area scale dark
    d.rectangle([215,171,255,255],fill=dark)
    im.save(TEX_DIR/f'{name}.png')

for n,c in colors.items(): make_tex(n,c)


# ----------------------------------------------------------------------------
# Binary runtime mesh resource (external data; keeps javac bytecode small)
# ----------------------------------------------------------------------------
import struct

def normals_for(mesh):
    ns=[]
    for a,b,c,g in mesh.t:
        p0=mesh.v[a][:3]; p1=mesh.v[b][:3]; p2=mesh.v[c][:3]
        ax=p1[0]-p0[0]; ay=p1[1]-p0[1]; az=p1[2]-p0[2]
        bx=p2[0]-p0[0]; by=p2[1]-p0[1]; bz=p2[2]-p0[2]
        nx=ay*bz-az*by; ny=az*bx-ax*bz; nz=ax*by-ay*bx
        l=sqrt(nx*nx+ny*ny+nz*nz) or 1.0
        ns.extend((nx/l,ny/l,nz/l))
    return ns

def mesh_streams(mesh):
    vv=[]; ii=[]
    for x,y,z,u,v,g in mesh.v: vv.extend((x,y,z,u,v))
    for a,b,c,g in mesh.t: ii.extend((a,b,c))
    return vv,ii,normals_for(mesh)

def write_farr(out, vals):
    out.write(struct.pack('>i',len(vals)))
    if vals: out.write(struct.pack('>'+('f'*len(vals)),*vals))

def write_iarr(out, vals):
    out.write(struct.pack('>i',len(vals)))
    if vals: out.write(struct.pack('>'+('i'*len(vals)),*vals))

with open(ASSET_DIR/'dracarys_head.mesh','wb') as out:
    out.write(struct.pack('>ii',0x4452484D,1))
    for mesh in (SKULL,JAW):
        vv,ii,nn=mesh_streams(mesh)
        write_farr(out,vv); write_iarr(out,ii); write_farr(out,nn)

print('STEP 5.10R3 generated')
print('skull vertices/triangles',len(SKULL.v),len(SKULL.t))
print('jaw vertices/triangles',len(JAW.v),len(JAW.t))
print('mesh bytes',(ASSET_DIR/'dracarys_head.mesh').stat().st_size)
