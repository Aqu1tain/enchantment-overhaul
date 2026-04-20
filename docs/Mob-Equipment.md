# Mob Equipment

Mobs spawn with up to one enchanted piece of gear and up to one smithing upgrade. Both rolls are independent.

## Chance by Difficulty

Chances scale with world difficulty and regional difficulty (time spent in an area, moon phase).

| Difficulty | Enchant Chance | Upgrade Chance |
|---|---|---|
| Peaceful | 0% | 0% |
| Easy | 10-20% | 5-12% |
| Normal | 20-35% | 12-22% |
| Hard | 35-60% | 22-40% |

The lower bound applies in a freshly loaded area. The upper bound applies at maximum regional difficulty (long-inhabited chunks, full moon). Only one random equipped slot is rolled per category, so a mob will not get enchantments on multiple pieces.

## Gamerule

The system is enabled by default. Disable with:

```
/gamerule enchantment-overhaul:mob_gear_enchantments false
```

## How the Rolls Work

When a mob spawns with equipment, the game rolls independently for enchantment and upgrade:

1. Enchantment roll: if the roll passes, one random equipped slot is chosen. The item in the slot receives a random compatible enchantment via the vanilla mob equipment provider.
2. Upgrade roll: if the roll passes, one random equipped slot is chosen. A compatible [[Smithing Table]] upgrade type is picked at random and applied at level 1.

Both rolls target the same slot pool, so a single piece of gear will sometimes be both enchanted and upgraded.
