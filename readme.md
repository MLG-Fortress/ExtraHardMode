# ExtraHardMode

version: 3.4-SNAPSHOT

build: 57

> Accept the challenges so that you can 
> feel the exhilaration of victory.
> --George S. Patton

## Useful Links
 [Materials in Bukkit >](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
 [Minecraft ids >](http://minecraft.gamepedia.com/Data_values)

## Setup

By default the plugin is disabled for all worlds. You need to manually set the worlds where you want to have it enabled.

It doesn't matter which difficulty you set your server on. The difficulty level in your server.properties file determines how much damage monsters do to players and how much damage a player can take from starvation. On "hard" difficulty, zombies can break down wooden doors and spawn in reinforcements.

``` yaml
Enabled Worlds: [world, world_nether]
# or:
Enabled Worlds:
- world
- world_nether
```

## Basic config info

The config cleans itself automatically and corrects incorrect input. So if something resets that means that you most likely did something wrong.
Don't use tabs in yaml, you will get a nice error in the console and large parts of your config might reset.

``` yaml

# Lists can either be inputed like so

Option: [world, world2, etc]

# or

Option:
- world
- world2
- etc

```

If you see any '@' symbols in the config that means that the info behind the '@' symbol is extra information.
For example this can be damage values which would define the type of slab.
In the default falling blocks you can find "STEP@3". That is a slab with damage value 3, which is a cobblestone slab.

In general if you can input a material/blocktype in the config you can either use bukkit names or the minecraft id, which can be from a mod too.
Use which is easier for you. Note that ids will be converted to bukkit names automatically if recognized.

## Bypassing
By default operators, players in creative and players with the [bypass permission](#permissions) bypass all player related features of extrahardmode. You can change it so that your ops can also play like regular players.

``` yaml
Bypassing:
    # Check for bypass permissions extrahardmode.bypass.xyz
    Check For Permission: true
    # as ehm doesn't make sense for creative mode players bypass
    Creative Mode Bypasses: true
    # If you are an op bypass, I use this for testing.
    Operators Bypass: false
```

## World Rules
All the features that affect your world or how players perceive your world.

### Mining

Stone is extremely hard, making tunneling with a pickaxe impractical. Players will have to scout the wilderness for natural caverns, or make their own caves with much-improved TNT (see below).
This brings exploration, navigation, and risk-taking to the forefront of gameplay.
The most successful players will be those who map the surface and develop a clever marking system for caverns.

#### Inhibit Tunelling

Stone is extremely hard, making tunneling with a pickaxe impractical. Players will have to scout the wilderness for natural caverns, or make their own caves with much-improved TNT (see below).
This brings exploration, navigation, and risk-taking to the forefront of gameplay.
The most successful players will be those who map the surface and develop a clever marking system for caverns.

``` yaml
Inhibit Tunneling:
    Enable: true

    Amount of Stone Tool Can Mine (Tool@Blocks):
    # Tool can be an item id or a bukkit-tool-name, f.e.
    - IRON_PICKAXE@32
    # which means that you can mine 32 blocks of stone before your pickaxe will break

    #257 is the minecraft id of a iron pickaxe and and basically means the same as "IRON_PICKAXE@32"
    - 257@32

#Note: You can also use item ids from mods that add new tools (mcpc+)!

```

#### Breaking Blocks Softens Surrounding Stone: 

Cave-ins are a persistent threat. Mining ore softens the stone around it, which can then fall and injure the careless player.
Dirt and grass, which is often compacted into a solid mass in cavern ceilings and floors, will also come crashing down when disturbed.
Of course, TNT can make a really big mess, since it also softens stone to subject it to the pull of gravity. #link

``` yaml

Breaking Blocks Softens Surrounding Stone:
    Enable: true

    #A list of blocks (ores) that will soften the surrounding stone
    Blocks (Block@id,id2):

    # You can use minecraft ids and data
    # If f.e. you wanted only spruce wood planks to soften surrounding stone
    # and not the other planks you would check
    # http://minecraft.gamepedia.com/Data_values#Wood_Planks
    # for the data of spruce wood which is 1

    - WOOD@1 # = spruce wood
    - WOOD@1,2 # spruce and birch wood

    - ADD_OTHER_BLOCKS

```

### Torches 

* No permanent flames near diamond level (there's not enough air!). Players will have to get creative with their lighting, for example using dimmer redstone torches (spooky!), moving lava around with buckets (dangerous!), using glowstone (expensive!), or lighting temporary flint/steel fires (risky!). This also discourages players from dumping water on all the lava, since it can be a valuable source of light, holding monsters at bay.
* Players may not attach torches to loose materials like dirt, grass, and sand.
* Torches left out in the rain will go out, falling to the ground as items.


``` yaml
Torches:
    #Block placement of torches below y-level 30. Makes for scarrier caves on the lower levels,
    increases mob spawns and makes diamonds harder to get. Set to 0 to disable.
    No Placement Under Y: 30
    #Soft materials include sand and dirt. Idea is that players don't litter the landscape with torches.
    No Placement On Soft Materials: true
    # When it rains there is a chance that torches will be removed in a chunk.
    The torches wil drop to the ground. Even a nonsolid block is enough to protect your torches.
    Rain Breaks Torches: true

```

### Campfires

* Campfires left out in the rain will burn out and will need to be manually re-lit.

``` yaml
Campfires:
    # When it rains there is a chance that campfires will be extinguished in a chunk.
    # Any kind of block above the campfire is enough to protect the campfire
    Rain Extinguishes Campfires: true
```

### Play Sounds

Warn player when a creeper is about to drop tnt and play a fizzing when torch goes out. Sounds only play if appropriate options are activated.

``` yaml
Play Sounds:
    Torch Fizzing: true #A lava fizz when a torch's placement has been blocked
    Creeper Tnt Warning: true #A Ghast shriek when a creeper drops tnt
```

### Breaking Netherrack Starts Fire Percent

Sparks a fire when mining netherrack. The careless player will put out the fire by hand, which will set him on fire.
This results in it being tuffer to dig tunnels in the nether and avoid all dangers by doing so.

``` yaml
# 0 disables
Breaking Netherrack Starts Fire Percent: 20
```

### Limited Block Placement

Realistic block placement rules will force players to think a little harder about construction, especially when climbing higher or crossing water, lava, or a trench.
It blocks:

- Straight pillaring up (jumping and placing a block directly beneath you)
- Building bridges in the sky (branching out with no blocks to support)

``` yaml

Limited Block Placement: true

```

It's a bit hard to explain, try it out. -vid

### Better Tree Felling

This is a cool feature. It adds a unique twist to chopping trees! The trunk and branches of a tree will fall and potentially injure you. It makes it easier to chop trees, but you have to watch out a little for the falling logs. Also by making branchlogs fall down most treetops should decay.

``` yaml

Better Tree Felling: true

```

-vid

### Player

#### Enhanced Environmental Injuries

Environmental damage like falling, explosions, and suffocation reduces health more, and often applies temporary effects like slowing, blindness, or dizziness.

``` yaml

Enhanced Environmental Injuries: true

```

#### Extinguishing Fires Ignites Player

Putting out a fire up close (by hitting it or trying to smother it with a block) will catch the player on fire. The best approach is to dump water or destroy the block beneath it.

``` yaml

Extinguishing Fires Ignites Player: true

```

#### Player Death

On death, a small portion of the player's inventory disappears forever, discouraging players from killing themselves to restore health and hunger.
After respawn, the player won't have a full health and food bar.

``` yaml

Death:
    # percentage of items that get removed, 0 disables feature
    Item Stacks Forfeit Percent: 10
    # spawn player with less health
    Override Respawn Health:
        # Enable health and foodlevel
        Enable: true
        # percentage of max health a player has. Custom max values are supported too
        Percentage: 75
        # food bars of a max of 20
        Respawn Foodlevel: 15

```
#### No Swimming When Too Heavy

Adds a weight system to your inventory. If your inventory exceeds the weight you will be pulled down and eventually drown. This is to encourage players to use boats and make swimming up waterfalls harder.

``` yaml
No Swimming When Too Heavy:
    Enable: true #Enable feature
    # Set to false if you want to exempt players from drowning when swimming up 1x1 waterstreams
    Block Elevators/Waterfalls: true
    # The maximum inventory weight you can have before starting to drown
    Max Points: 18.0
    # One piece of worn armor would add 2.0 weight. So full set of armor adds 8.0
    One Piece Of Worn Armor Adds: 2.0
    # A stack of any item adds 1.0, half a stack add 0.5 so it calculates fractions
    One Stack Adds: 1.0
    # A tool is any item that doesn't stack, swords, axes, not worn armor, shears etc
    One Tool Adds: 0.5
    # Basically an esoteric percentage of how fast you drown.
    # 35 actually doesnt really make you drown. 50 would make you drown
    Drown Rate: 35
    # If your inventory weight exceeds the max weight every weightpoint will add 2 to the drownrate.
    # Weight = 25 => (base) + (exceeding) * (modifier) = 35 + 7 * 2 = 49 (new drown rate)
    Overencumbrance Adds To Drown Rate: 2

```

## General Monster Rules 

Rules affecting all types of monsters.

### Inhibit Monster Grinders

Players should go out adventuring while taking risks to gain rewards. The use of monster grinders imbalances the game
and makes good weapons "worthless", because they are easily obtainable. By forcing players to work for their gear, they will treasure it more.

This module completely removes drops if it recognizes grinders or when a player has a great advantage over a monster.

A great advantage can be:
* Monster cannot reach the player (player behind a wall or standing on a pillar or similar)
* Monster has taken more than 50% damage from natural causes (fall damage, being on fire, etc)
* Monster is in water and therefore has a great disadvantage towards the player

Activating this will also block monsters from spawning on non natural blocks to make most grinders not spawn any monsters.

``` yaml

# simple on/off, request if you want any additional configuration
Inhibit Monster Grinders: true

```

#### More Monsters 
Increase monsters in caves. This will make caves more dangerous and subsequently also reduce the amount of monsters on the surface because the spawnlimit is reached quicker in caves.
It does this by increasing the packspawns with random monsters. So if a pack of 2 skeletons spawned 2 other monsters could spawn at the same time.

``` yaml

More Monsters:
    #Maximum y value where packspawns are increased. Set to 0 to disable
    Max Y: 55
    # Multiplier. Normal packspawns are 1-4 monsters. A setting of 2 would increase it to 2-8.
    # Note that this doesn't increase spawnlimits.
    # If the spawnlimit is reached and no natural spawns occur this won't increase the limit.
    Multiplier: 2

```

#### Monsters Spawn In Light

This works indepent from "More Monsters". It spawns monsters even if there is sufficient light. Currently it will spawn monsters in locations where you have been previously. It's meant to make players not feel completely safe once they have lit up caves.

``` yaml

#Feature enabled below 55 (caves). Set to 0 to disable
Monsters Spawn In Light Max Y: 55

```

#### Horses 
WIP: Will include food as requirement for horses.

``` yaml
Horses:
    #Block the usage of chests on horses in caves, to prevent usage of horses as mobile chests.
    Block Usage Of Chest Below: 55
```

### Zombies 

Instead of speeding Zombies up, a Zombie will slow a player down for a few seconds when the player is hit by a zombie.
Zombies may resurrect when slain. They will respawn after a few seconds and might ambush a player.

``` yaml

Zombies:
    Slow Players: true
    # percentage of zombies that respawn. A zombie is less likely to respawn again
    # function used: 1/x * percentage. So with 50% it's 50% for first respawn and 25% for second.
    Reanimate Percent: 50

```

### Skeletons 

Skeletons have special abilities like shooting knockback, blindness and fire arrows. They can also spawn silverfish to make it harder for the player to reach them.

``` yaml

# Plugin goes through all possible arrows when evaluating which to shoot
# in the order they are in the config. If you set one to 100% the ones behind
# that will never fire. The percentage is like a chance not an actual percentage.
Skeletons:
    # snowballs will make the player blind for a few seconds
    Shoot Snowballs:
      Enable: true
      Percent: 20
      Blind Player (ticks): 100
    Shoot Fireworks:
      Enable: true
      Percent: 30
      # 0.5 is a moderate knockback, 1.0 is a couple of blocks,
      # set higher to send the player flying
      Knockback Player Velocity: 1.0
    Shoot Fireballs:
      Enable: true
      Percentage: 10
      # how long player should burn from the arrow.
      # 40 ticks = 4 seconds. Default from fireball is 50
      Player Fireticks: 40
    Shoot Silverfish:
      Enable: true
      Percent: 20
      Kill Silverfish After Skeleton Died: true
      # One skeleton can have 5 silverfish spawned at a time
      # 2 skeletons can have 10 together
      # set to 100 or something to disable
      Limit To X Spawned At A Time: 5
      # After a skeleton has spawned 15 silverfish he stops
      Limit To X Spawned In Total: 15
    # 100 percent = players can't use bows as the arrows pass through
    Deflect Arrows Percent: 100

```

### Silverfish

Silverfish may spawn naturally in caves if "More Monsters" is activated and may be summoned by skeletons.
These buggers are easily overseen and a not often seen monster. Sadly silverfish are glitching in to floors atm, this is an issue with the game I cannot fix.

``` yaml
Silverfish:
    # Block silverfish from entering blocks like stone and cobblestone to prevent them from despawning.
    Cant enter blocks: true
    # Drop 1 cobble on death
    Drop Cobble: true
    # Show particles to make silverfish more visible.
    # This is to combat silverfish glitching into blocks and not being visible.
    Particles To Make Better Visible: true
```

### Spiders 

Spiders are more common under sea level and randomly drop web around them when slain,
potentially introducing obstacles into an ongoing combat situation. Monsters can break through web if stuck.
The webs will be removed on the surface after a while, but stay in caves.

``` yaml

Spiders:
    Bonus Underground Spawn Percent: 20
    Drop Web On Death: true

```

### Creepers

Charged creepers will spawn natually and explode on death. It's supposed to be a fearsome monster and fighting it should be avoided.
Killing a creeper may drop ignited tnt. The dropped tnt will act the same as player ignited tnt which can be configured at the bottom.
A creeper which dies while being on fire will launch in the air and explode with some fireworks. Small gimmick, doesn't really increase difficulty ;).

``` yaml

Creepers:
    Charged Creeper Spawn Percent: 10
    Drop Tnt On Death:
      Percent: 20
      Max Y: 50
    Charged Creepers Explode On Damage: true
    # a burning creeper will launch launch in the air
    Fire Triggers Explosion:
      Enable: true
      # how many creeper style explosions
      Firework Count: 3
      # how fast they should be propelled upwards,
      # set it a bit higher to have real creeper rockets.
      Launch In Air Speed: 0.5

```

### Blazes 
Blazes will spawn in the nether naturally and near bedrock in the overworld.
Blazes in the overworld are unstable and will explode on death and cause cave ins.

``` yaml
Blazes:
    # Should blazes spawn in the overworld near lava level, set to 0 to disable
    Near Bedrock Spawn Percent: 50
    # Percentage of blazes spawning outside of fortresses
    Bonus Nether Spawn Percent: 20
    # Drop fire around a blaze when hit
    Drop Fire On Damage: true
    # Bonus loot including gunpowder for nether blazes
    Bonus Loot: true
    # Should blazes split into 2 blazes when slain in the nether
    Nether Split On Death Percent: 25
```

### MagmaCubes

Make magmacubes turn into blazes on hit and increase the spawns.


``` yaml
MagmaCubes:
    # How often a magmacube should spawn when a blaze spawns
    Spawn With Nether Blaze Percent: 100
    # Should a magmacube explode and turn into a blaze
    Grow Into Blazes On Damage: true
```

### PigZombie


Make the nether truely fearsome by having always aggroed PigZombies!


``` yaml
PigZombies:
	# Should PigZombies be aggressive all the time
    Always Angry: true
    # Drop 1 netherwart in fortresses
    Always Drop Netherwart In Fortresses: true
    Percent Chance to Drop Netherwart Elsewhere In Nether: 25
    #If 1-3 PigZombies should spawn when a lightning strikes
    Spawn on Lighting Strikes:
      Enable: true
```

### Ghasts

Turn those white baloons into fearsome enemies by greatly increasing the amount of arrows it takes to kill them. Good loot encourage players to still pursue them.

``` yaml

Ghasts:
    Arrows Do % Damage: 20
    Exp Multiplier: 10
    Drops Multiplier: 5

```

### Enderman

Enderman are easy you think? Sitting behind your wall or 2 high roof? No longer! With their teleporting powers they can teleport you too. Now you better not mess with these guys...

``` yaml

Endermen:
    # Enable improved enderman teleportation
    May Teleport Players: true

```

### Witches

Add new attacks like explosions and baby zombies to the arsenal of witches. Witches naturally spawn on the surface on grass.

``` yaml 

Witches:
    Additional Attacks: true
    Bonus Spawn Percent: 5

```
### Killer Bunny
This allows killer bunnies to spawn naturally by replacing some of the rabbit spawns. 

``` yaml 

  KillerBunny:
    Bonus Spawn Percent: 1
    
```
### Vidicator
This allows Vidicator to spawn naturally in roofed forests by replacing some of the skeletons spawns. 

``` yaml 

  Vindicator:
    Bonus Spawn Percent: 20
    
```
### Cave Spider
This allows Cavespiders to spawn naturally in swamplands by replacing some of the spider spawns. 

``` yaml 

  CaveSpider:
    Bonus Spawn Percent: 5
    
```
### Guardians
This allows Guardians to spawn naturally in ocean and deep ocean by replacing some of the squid spawns.  

``` yaml 

  Guardians:
    Bonus Spawn Percent: 10
    
```
### Vex
This allows Vexes to spawn naturally by replacing some of the bat spawns.

``` yaml 

  Vex:
    Bonus Spawn Percent: 5
    
```
### EnderDragon

Is the EnderDragon boring, just flying around and doing nothing? No more! This dragon is really tough. She spawns lots of minions and can call enderman to help her out. Good gear will be required to beat her, but the rewards are great.

* The Ender Dragon respawns so that all players have a challenging common goal, and she always drops a dragon egg when killed. It makes a great trophy for the slayer's house.
* The Ender Dragon spews explosive fireballs which throw flaming shrapnel on impact. She also summons minions to his aid, making combat challenging and frenzied - forcing players to hit a moving target with arrows while simultaneously dodging fireballs and battling minions.
* Building is not allowed in the end, so players must face the dragon and his minions head-on.
* All players are notified when a player challenges the dragon, and will also be notified of the outcome of the battle. When the dragon defeats a player, she regains 25% of his health.

``` yaml

EnderDragon:
    # Respawn the dragon once all players leave the end
    Respawns: true
    # Add a dragonegg to the drops
    Drops Dragonegg: true
    # Drop villager eggs to encourage building of a city 
    Drops 2 Villager Eggs: true
    # The additional attacks
    Harder Battle: true
    # Announce when players challenge the dragon
    Battle Announcements: true
    # Block building in the end, to prevent fortifications
    No Building Allowed: true

```

## Farming

Includes a few farming fixes and nerfes.

### Weak Crops

Plants can die if not tended to correctly. This includes exposure to daylight, sufficient water to keep your crops alive.

``` yaml

Weak Crops:
    # Enable plants being able to die and requiring light etc.
    Enable: true
    # Percentage of plants that will die even when taking care
    Loss Rate: 25
    # Block trees from growing and make farming in the desert not profitable
    Infertile Deserts: true
    # Direct exposure to snow will kill your crops eventually
    Snow Breaks Crops: true

	# Require players to find melonseeds, by disabling crafting
    Cant Craft Melonseeds: true
    # block easy mushroom farming by use of bonemeal
    No Bonemeal On Mushrooms: true
    # Netherwart has to be either found in fortresses or earned by killing pigmen
    No Farming Nether Wart: true
    # Players need to craft dyes and can't just get colored wool indefinitely
    Sheep Grow Only White Wool: true
    # This is a cool one, it forces players to build farms around lakes.
    # This makes the farms look more natural. You can still use ice to create sourceblocks-
    Buckets Dont Move Water Sources: true
    # Animals don't drop exp
    Animal Experience Nerf: true
    # Block iron farms by removing iron golem drops
    Iron Golem Nerf: true 
```

## Animal Crowd Control

Placing animals into small spaces will cause the animals to get claustrophobic this can be indicated by a villager angry effect showing above their heads. Eventually this causes the animals to slowly drive them insane leading to death.

Technical: The animal will scan 3x3x3 around him everytime a animal spawns once detected. It will then check if the area is crowded if so will show the effect once it reached 10 seconds and still crowded. It will then damage itself and repeat again the process until its dead or is far away from other animals.

``` yaml
    Animal Overcrowding Control:
      Enable: true
      # Maximum amount of animals allowed in a small area before they start dying
      threshold: 10
```

## Additional Falling Blocks

Loose materials like cobble and dirt will fall like sand and gravel, forcing players to solve mining obstacles (like giant pits) by bringing appropriate building materials with them
or getting very creative in their approach, rather than just using the dirt and cobble they conveniently picked up along the way.


``` yaml
Additional Falling Blocks:
    Enable: true
    # wheter falling blocks can break torches, effectively blocking the easy way to farm sand.
    # This option has been removed/currently doesn't do anything as it causes issues with other falling blocks, nor is it complete. I also don't see how it makes the game more difficult - just seems to make it a tad more tedious at best.
    Break Torches: true
    # if blocks should damage player if they hit him directly
    Dmg Amount When Hitting Players: 2
    # falling grass turns into dirt when landing
    Turn Mycel/Grass To Dirt: true
    # list of blocks, use bukkit names or ids
    Enabled Blocks:
    - GRASS
    - MOSSY_COBBLESTONE
    - DIRT
    - COBBLESTONE
    - MYCEL
    - DOUBLE_STEP@3
    - STEP@3,11

```

## Explosions

Explosions are a big part of ehm and what makes it pretty awesome. We have custom explosion physics and allow you to tweak explosions to your heart's content.

### TNT

The TNT recipe produces 3 TNT, and each TNT explodes 100% more violently versus Vanilla TNT, making TNT a useful tool for mining and worthwhile to craft.
Further, exploding TNT will produce a more "natural" devastation with lots of fallen rock and other rubble.

``` yaml

Tnt:
    # Override vanilla explosion
    Enable Custom Explosion: true
    # Add more explosions to make the crater look more random
    # and not so symmetrical like in vanilla
    Enable Multiple Explosions: true
    # Produce more tnt per recipe to encourage usage of tnt
    Tnt Per Recipe: 3

```

### Custom Explosions

``` yaml

Explosions:
	# turn stone into cobble instead of breaking it
    Turn Stone To Cobble: true
    # Flying blocks
    Physics:
      Enable: true
      # How many blocks will go flying
      Blocks Affected Percentage: 20
      How much blocks are propelled upwards, set higher if you want them to fly faster
      Up Velocity: 2.0
      # How much blocks fly to the side
      Spread Velocity: 3.0
      # Blocks that exceed this radius from the center of the explosion will not be placed
      Exceed Radius Autoremove: 10

```

The idea behind the explosions is that you can set them up to be different on the surface and in caves. E.g. disabling explosions on the surface.

You set a border and can set the explosions below and above the border.

Vanilla power values:

- Ghast: 1
- Creeper: 3
- Tnt: 4
- Charged creeper: 6

``` yaml

Creeper:
      # Override the vanilla explosion
      Enable Custom Explosion: true
      # settings for below the border we set 
      Below Border:
        # size of explosion
        Explosion Power: 3
        # 1/3 of blocks catch fire
        Set Fire: false
        # cause block damage, if false still damages players
        World Damage: true
      # settings for above border, e.g. surface
      Above Border:
        Explosion Power: 3
        Set Fire: false
        World Damage: true

```

## Commands

### ehm
help menu

### ehm reload
reloads the config and restarts some tasks
requires admin permission

### ehm version
shows the version number of the plugin


## Permissions

### Admin

#### extrahardmode.admin
Needed for commands like reload

### Bypassing

#### extrahardmode.bypass.*
Bypasses all features which are triggered by a player, defaults to op

#### extrahardmode.bypass.creepers
Bypasses all creeper rules

### Silence

#### extrahardmode.silent.*
Grants ALL silent permission nodes

#### extrahardmode.silent.stone_mining_help
Hides the stone mining help message

#### extrahardmode.silent.no_placing_ore_against_stone
Hides the no placing ore against stone message

#### extrahardmode.silent.realistic_building
Hides the realistic building message

#### extrahardmode.silent.limited_torch_placement
Hides the limited torch placement message

#### extrahardmode.silent.no_torches_here
Hides the no torches here message
