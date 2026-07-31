# CreativeKit Plus

Client-side Fabric utility mod for **single-player creative building and testing** on Minecraft **1.21.11**. Each feature is a self-contained module toggled by a keybind, with state persisted to config.

## Modules & default keys

| Module           | Key | What it does                                              |
|------------------|-----|----------------------------------------------------------|
| Flight           | F   | Velocity-driven flight (jump/sneak = up/down)            |
| X-Ray            | X   | Hides everything except the ore allow-list               |
| Entity Highlight | H   | Forces a glow outline on living mobs                     |
| Auto Tool        | T   | Swaps hotbar to the fastest tool while mining            |
| No-Clip          | N   | Phase through blocks (pair with Flight)                  |
| Speed            | G   | Horizontal movement multiplier                          |
| Full Bright      | B   | Max brightness without a potion                         |
| **Click GUI**    | Right Shift | Panel to toggle modules and change settings     |

Rebind any of these in **Options > Controls > CreativeKit Plus**. Toggle state shows on the action bar.

## Click GUI

Press **Right Shift** to open the panel. Left-click a module row to toggle it (green = on, red = off). Flight and Speed have a numeric setting adjustable with the `[-]` / `[+]` buttons. Right Shift or Escape closes it and saves. The world keeps running while it's open.

## Build

You have two paths.

**Option A: GitHub Actions (no local setup).** Push this project to a new GitHub repo. The included `.github/workflows/build.yml` builds it on GitHub's runners and attaches the jar as an artifact. Open the run, go to Artifacts, download `creativekitplus-jar`, unzip, and you have the mod. This works because GitHub's runners can reach Mojang and Fabric to download Minecraft and the mappings.

**Option B: Local build.** Install a **JDK 21** (not just a JRE), then run `.\gradlew build` (Windows) or `./gradlew build`. The jar lands in `build/libs/creativekitplus-1.0.0.jar`.

Toolchain is pinned to match a real shipped 1.21.11 mod: Minecraft 1.21.11, Fabric Loom 1.16.1, Gradle 9.4.1 (via wrapper), Fabric Loader >= 0.16.7. Confirm two values at https://fabricmc.net/develop that a mod manifest doesn't record: the exact `yarn_mappings` build and the `fabric_version` (Fabric API). Both live in `gradle.properties`. Either way the output jar is compiled and remapped to intermediary, the same format as any Fabric mod you download.

## Version-mapping caveats (read before first build)

These spots depend on exact 1.21.11 yarn names. If the build errors, this is where to look:

- **`AutoToolSelector.miningSpeed`** uses `ItemStack#getMiningSpeedMultiplier(BlockState)`. Confirm the current name.
- **`BlockShouldDrawSideMixin`** targets `Block.shouldDrawSide(BlockState, BlockState, Direction)`. Recent versions have also used a `(BlockState, BlockView, BlockPos, Direction, BlockPos)` form. `require = 0` makes a mismatch fail soft, but X-ray won't work until the descriptor matches your build.
- **`Entity#noClip`** and **`Entity#isGlowing`** are stable, but verify if a build fails to apply the glow mixin.
- **Full Bright** sets the gamma option directly. If brightness caps at vanilla max, add a small mixin over the gamma option's value getter (noted in `FullBright.java`).

## Scope note

Flight, Speed, and No-Clip drive movement client-side. On single-player (you host the integrated server) they behave. Against a remote authoritative server they'll rubber-band or be rejected, and X-ray is bannable on essentially every multiplayer server. Keep this to your own worlds.
