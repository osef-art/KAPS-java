# KAPS

A *'Dr. Mario'-like* colorful mini-game. Match the colored capsules and
get rid of every germ in the grid ! 🧪


## LAUNCH THE GAME 🎮
⚠ You must have [**Java 13** or +](https://www.oracle.com/java/technologies/javase/jdk13-archive-downloads.html)
installed to play the game.

Once the project **cloned** / **unzipped** the <code>.zip</code> file, open the <code>/ministick-moves</code>
directory and

#### WINDOWS
- Click on `launch.bat`

#### LINUX
- Execute `./launch.sh`

...or place yourself in the root directory,
open a terminal and launch the command:
```bash
java -jar KAPS.jar
```

## HOW TO PLAY 🕹

#### 💊 In-game
`⬅`, `➡` : **move** the capsule left/right  
`⬆` : **flip** the capsule  
`⬇` : **dip** the capsule of one row  
`[SPACEBAR]` : **drop** the capsule at the bottom  
`🇭` : save gelule in **HOLD**

#### 💊 General
`🇵` : **pause** the game  
`M` : toggle **mute**  
`🇶` : **exit** the game

---

## RULES 📜
Move falling capsules 💊 in the grid and make matches of **4 tiles** of the same color or more
to destroy them.  
Destroy tiles of a sidekick's color to fill its **mana gauge** and unleash its attack !

![alt text](img/screens/KAPSjava-clip.gif "Quick gaeplay")

Smash **every germ** 🦠 of the grid to win !  
But make sure not to exceed the grid ! Beware, the game gets faster over time.


## SIDEKICKS 🤜‍🤛
| Name | | Mana | Dmg | Power |  
|---:|:---:|:---:|:---:|:---|   
| MIMAPS | ![alt text](img/sidekicks/Mimaps_0.png "Mimaps") | 15       | 2 | Hits 3 random objects
| RED    | ![alt text](img/sidekicks/Red_0.png "Red")       | 20       | 2 | Slices a random object and all tiles on the same column
| XERETH | ![alt text](img/sidekicks/Xereth_0.png "Xereth") | 25       | 1 | Slices a random object and all tiles on the same diagonals
| JIM    | ![alt text](img/sidekicks/Jim_0.png "Jim")       | 25      | 1 | Slices a random object and all tiles on the same line
| SEAN   | ![alt text](img/sidekicks/Sean_0.png "Sean")     | 20       | 2 | Hits a random object and adjacent tiles
| ZYRAME | ![alt text](img/sidekicks/Zyrame_0.png "Zyrame") | 20       | 2 | Slices two random germs
| PAINT  | ![alt text](img/sidekicks/Paint_0.png "Paint")   | 10       | 0 | Paint 5 random caps
| COLOR  | ![alt text](img/sidekicks/Color_0.png "Color")   | 4 turns  | 0 | Generates a gelule with both caps of same color
| ???    | ? | ? | ? | (Coming soon !)

## GERMS 🦠
| Name | | Cooldown | Power |  
|---:|:---:|:---:|:---|   
| BASIC | ![alt text](img/germs/basic/1_0.png "Basic") | - | Exists
| WALL  | ![alt text](img/germs/wall4/2_0.png "Wall")  | - | Needs several hits (4 max.) to be destroyed
| VIRUS | ![alt text](img/germs/virus/5_0.png "Virus") | 8 | Turns a random tile into a virus
| THORN | ![alt text](img/germs/thorn/4_0.png "Thorn") | 5 | Destroys a random capsule among tiles around
| ???   | ? | 6 | Turns a random caps into a basic germ, or a random basic germ into a wall (2 HP), or can heal a wall (by 1 HP) (Coming soon !)


## SPECIAL CAPSULES ✨💊
| Name | | Effect |  
|---:|:---:|:---|   
| EXPLOSIVE | ![alt text](img/caps/bomb/7.png "Explosive") | Explodes when destroyed, hitting all tiles around
| ???       | ? | (Coming soon !)



### TIPS 💡

- Kill **viruses** first. They can easily ruin a game.

- Don't forget to use the **HOLD** feature !

- The choice of **sidekicks** can be decisive for some levels.
