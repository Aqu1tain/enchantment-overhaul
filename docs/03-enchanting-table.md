# System 3: Enchanting Table — Magic Only

The enchanting table keeps its block and appearance. The UI changes completely. The vanilla RNG system is replaced by a catalogue unlocked through exploration, paid with Lapis + Reagent + XP.

## Slot System

Each enchantable item has a max slot count determined by its material. Each enchantment level costs 1 slot. Cannot exceed the maximum.

| Material | Slots | Examples |
|---|---|---|
| Leather / Wood / Stone | 3 | Mending (3) fills everything |
| Copper | 3 | Same as Leather |
| Iron | 4 | Mending (3) + Fortune I (1) |
| Diamond | 5 | Mending (3) + Fortune II (2) |
| Netherite | 5 | Same as Diamond |
| Gold | 6 | Fortune III (3) + Magnetic III (3) |

### Slot Rules
- 1 level = 1 slot
- Exception: Mending costs 3 slots (unique level)
- Curses grant +1 slot instead of consuming one

## Unlocking — Unified System

One system, one rule for all 35 enchantments. No common/rare tiers.

**Books are levelless keys.** Since the level is chosen at the enchanting table, books no longer display a level. A "Fortune" book is just a "Fortune" book — it unlocks all levels of that enchantment in the catalogue.

**Chiseled Bookshelves + enchanted book** = unlock. Find a book in exploration, place it in a chiseled bookshelf around the table. The enchantment appears in the catalogue. The book is not consumed — permanent key.

**Normal bookshelves** = reduce reagent cost. 0 = 100% cost. 15 = 50% cost. Approximately -3.3% per bookshelf. Does not reduce lapis or XP.

## UI Layout

**Left side — 3 stacked slots**: item (top), Lapis Lazuli (middle), Reagent (bottom).

**Right side — Scrollable catalogue**. Each line shows:
- Name and icon
- Level selector (I, II, III...)
- Lapis cost
- Reagent cost (with material icon)
- XP cost in levels
- Slot cost (e.g. "2/5")

Unavailable enchantments are greyed out with visible reason.

**Bottom — Visual slot bar**. Lozenges: filled (taken), blinking (current selection), empty (free). Enchant button activates when all costs are met.

## Triple Cost

Each enchantment has three simultaneous costs:
- **Lapis Lazuli** — universal magic fuel, increases with level
- **Reagent** — thematic component specific to each enchantment, increases with level, reduced by normal bookshelves
- **XP (levels)** — player investment. Level I = 1-3, Level II = 3-5, Level III = 5-8, Level IV = 8-10. Mending = 8 levels.

### Example: Fortune III on Diamond Pickaxe (5 slots, 10 bookshelves ~33% reagent reduction)

| Level | Slots | Lapis | Reagent (Emerald) | Reagent (reduced) | XP |
|---|---|---|---|---|---|
| I | 1 | 1 | 2 | 2 | 2 levels |
| II | 2 | 2 | 4 | 3 | 4 levels |
| III | 3 | 3 | 6 | 4 | 8 levels |

## Curses

Curses open +1 slot instead of consuming one. Risk/reward tradeoff.

| Curse | Negative Effect | Bonus |
|---|---|---|
| Curse of Fragility | Double durability damage | +1 slot |
| Curse of Hunger | 30% faster hunger | +1 slot |
| Curse of Binding | Cannot remove armor | +1 slot |
| Curse of Vanishing | Item disappears on death | +1 slot |

## Grindstone

Removes enchantments but permanently reduces max slots by 1. Irreversible. Three uses on a Diamond sword: 5 → 4 → 3 → 2 slots max forever.

## Mending vs Soulbound

Mutually exclusive. Two preservation philosophies:

- **Mending (3 slots, 8 XP levels)** — XP repairs the item instead of filling the bar. Item never dies as long as you earn XP. But it drains levels that could go toward enchanting or smithing.
- **Soulbound (1 slot)** — item stays in inventory on death. Cheaper in slots, but doesn't repair anything. The item still breaks from durability.
