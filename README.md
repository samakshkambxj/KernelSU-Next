# Custom KernelSU Manager

A custom KernelSU Manager built on top of KernelSU Next with additional features:

## Features

### Theme Switching (Built-in from KSU Next)
- **MIUIX** - Xiaomi's design language
- **Material Design 3** - Google's Material Design
- 7 color modes including Material You (Monet) support
- 15+ preset color palettes
- AMOLED dark mode, blur effects, floating bottom bar

### SUSFS Management (Ported from SukiSU Ultra)
- **Uname Spoofing** - Custom kernel version strings
- **Build Time Spoofing** - Custom build timestamps
- **SUS Paths** - Manage hidden paths
- **SUS Loop Paths** - Manage hidden loop-mount paths
- **SUS Maps** - Manage overlay/mount root maps
- **Kstat Config** - Static and dynamic kstat spoofing
- **Toggle Features**:
  - Auto-start module
  - Hide BL (bootloader unlock state)
  - Cleanup residue
  - AVC log spoofing
  - Hide SUS mounts for all procs
- **Slot Management** - A/B slot info and switching
- **Backup/Restore** - SUSFS configuration backup and restore
- **Enabled Features** - View which SUSFS features are compiled into the kernel

### AnyKernel3 Zip Flashing (Enhanced from KSU Next)
- Flash AnyKernel3 zip files directly from the manager
- A/B slot selection support
- KPM (Kernel Patch Module) patch/undo support
- Real-time flash progress and logging
- Automatic mkbootfs tool detection

## Building

### GitHub Actions (Recommended)
1. Fork this repository
2. Go to Actions tab
3. Run the "Build Custom KernelSU Manager" workflow
4. Download the APK from Artifacts

### Local Build
```bash
# Requirements: JDK 21+, Android SDK
cd manager
chmod +x gradlew
./gradlew :app:assembleRelease
```

## Important Notes

### SUSFS Requirements
- Your kernel must have **SUSFS patches** applied (`CONFIG_KSU_SUSFS=y`)
- The `ksud` binary must include SUSFS support (from susfs4ksu or SukiSU)
- SUSFS features will only work if the kernel supports them

### AnyKernel3 Requirements
- Root access is required for AnyKernel3 flashing
- Device must support the kernel being flashed

## Credits

- [KernelSU Next](https://github.com/KernelSU-Next/KernelSU-Next) - Base manager with theme switching
- [SukiSU Ultra](https://github.com/SukiSU-Ultra/SukiSU-Ultra) - SUSFS management UI reference
- [susfs4ksu](https://gitlab.com/simonpunk/susfs4ksu) - SUSFS kernel patches
- [WildKernels](https://github.com/WildKernels) - AnyKernel3 flashing reference
