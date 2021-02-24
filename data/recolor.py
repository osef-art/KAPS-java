from PIL import Image

def color_set(rgb, code):
  dict = {}
  (r,g,b) = rgb

  if r < 20 or 235 < r or g < 20 or 235 < g or b < 20 or 235 < b :
    print("   INVALID COLOR: rgb(" + str(r) + "," + str(g) + "," + str(b) + ")")
    return

  dict['code'] = str(code)
  dict['principal'] = rgb + (255,)
  dict['outline'] = (r-20, g-20, b-20, 255)
  dict['shade'] = (r-10, g-10, b-10, 255)
  dict['mouth'] = (int(r/2), int(g/2), int(b/2), 255)

  dict['flash-filling'] = (r+20, g+20, b+20, 255)
  dict['flash-outline'] = rgb + (255,)
  dict['flash-shade'] = (r+10, g+10, b+10, 255)

  return dict

directories = []
afters = []
colors = []

#before = {'principal': (112, 81, 244, 255), 'outline': (93, 61, 229, 255), 'mouth': (52, 48, 109, 255), 'code': 1}
#before = {'principal': (81, 193, 244, 255), 'outline': (45, 177, 236, 255), 'mouth': (48, 90, 109, 255), 'code': 2}
#before = {'principal': (110, 114, 148, 255), 'outline': (91, 96, 122, 255), 'mouth': (44, 51, 86, 255), 'code': 3}

before = color_set((110, 80,  235), 1)

afters.append( color_set((110, 80,  235), 1) )
afters.append( color_set((90,  190, 235), 2) )
afters.append( color_set((220, 60,  40),  3) )
afters.append( color_set((180, 235, 60),  4) )
afters.append( color_set((50,  235, 215), 5) )
afters.append( color_set((215, 50,  100), 6) )
afters.append( color_set((220, 235, 160), 7) )
afters.append( color_set((40,  50,  60),  8) )
afters.append( color_set((180, 200, 220), 9) )
afters.append( color_set((100, 110, 170), 10) )
afters.append( color_set((50,  180, 180), 11) )
afters.append( color_set((235, 150, 130), 12) )
afters.append( color_set((70,  50,  130), 13) )

#directories.append({ "nb_frames": 8, "path": "../img/caps/" })
#directories.append({ "nb_frames": 8, "path": "../img/caps/pop/" })
#directories.append({ "nb_frames": 8, "path": "../img/caps/bomb/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/basic/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/basic/pop/" })
#directories.append({ "nb_frames": 4, "path": "../img/germs/wall4/" })
#directories.append({ "nb_frames": 4, "path": "../img/germs/wall3/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall2/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall1/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall4/pop/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall3/pop/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall2/pop/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/wall1/pop/" })
directories.append({ "nb_frames": 8, "path": "../img/germs/thorn/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/virus/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/virus/pop/" })
#directories.append({ "nb_frames": 8, "path": "../img/germs/virus/atk/" })


def get_names(directory):
  names = []
  if "../img/caps/" in directory['path']:
    names.append(str(before['code']) + ".png")
    names.append(str(before['code']) + "_up.png")
    names.append(str(before['code']) + "_left.png")
    names.append(str(before['code']) + "_down.png")
    names.append(str(before['code']) + "_right.png")
    return names

  for frame in range(0, directory["nb_frames"]):
      names.append(str(before['code']) + "_" + str(frame) + ".png")
  return names
  
def new_name(name, after):
  new_name = list(name)
  new_name[0] = after['code']
  return "".join(new_name)

def add_color(color):
  if color not in colors:
    colors.append(color)

def rgb_to_hexa(c):
  return '#%02x%02x%02x' % (c[0], c[1], c[2])

def hex_to_rgb(value):
  value = value.lstrip('#')
  lv = len(value.lstrip('#'))
  return tuple(int(value[i:i + lv // 3], 16) for i in range(0, lv, lv // 3)) + (255,)



if __name__ == "__main__":
  for after in afters:
    for directory in directories:
      names = get_names(directory)
      for num, name in enumerate(names):
        im = Image.open(directory["path"] + name) 
        pix = im.load()
        
        for i in range(64):
          for j in range(64):
            add_color(pix[i,j])
            for key in before:
              if (key != 'code' and before[key] == pix[i,j]):
                pix[i,j] = after[key]

        im.save(directory["path"] + new_name(name, after))
        print("[", "{:.1f}".format((num+1)*100/len(names)), "% ", "redessinnés... ]", end= "\r")
            