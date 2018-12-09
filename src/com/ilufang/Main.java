package com.ilufang;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Main {
    private static final int ARG_DOT_FOLDER = 0;
    private static final int ARG_VERSION = 1;
    private static final int ARG_SEED = 2;
    private static final int ARG_RADIUS = 3;

    private static MinecraftInterface mc;

    public static void main(String[] args) {
        if (args.length<4) {
            System.err.println("Invalid Args. Usage: java -jar amidst_api.jar dot_folder version seed radius");
            return;
        }

        File mcPath = new File(args[ARG_DOT_FOLDER]);
        try {
            DotMinecraftDirectory directory = new DotMinecraftDirectory(mcPath);
            MinecraftInstallation install = new MinecraftInstallation(directory);
            List<LauncherProfile> profileList = install.readInstalledVersionsAsLauncherProfiles();
            profileList.forEach((v)->{
                if (v.getVersionName().equals("*" + args[ARG_VERSION])) {
                    try {
                        mc = MinecraftInterfaces.fromLocalProfile(v);
                    } catch (Exception e) {
                        System.err.println("Failed to load Minecraft jar.");
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Minecraft jar.");
            return;
        }
        try {
            mc.createWorld(Long.parseLong(args[ARG_SEED]), WorldType.DEFAULT, "");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create world.");
            return;
        }



        // Create Biomes
        // Stored in bigchunks of 1024x1024. <-- 64x64 Minecraft Chunk
        // Generate 8x8 bigchunks at one Java runtime
        // Fileformat:
        // Part 1: 1024x1024 Bytes of UInt8 Biome data for all blocks
        // Part 2: 1 Byte for Number of slime chunks, followed by sets of 2 UInt8 for Relative Chunk Coordinates
        // Part 3: 1 Byte for Number of villages, followed by sets of 3 UInt8 for Relative Chunk Coordinates (2byte) and Coordinate in Chunk (1byte)
        // Part 4-8: Similar calculation for Witch Hut, Desert Temple, Jungle Temple, Stronghold, and Ocean Monument

        int radius = Integer.parseInt(args[ARG_RADIUS]);
        File outputDir = new File("out");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        for (int x=-radius; x<=radius; x++) for (int y=-radius; y<=radius; y++) {
            try {
                System.out.println("Generating "+x+","+y);
                File biomeFile = new File("out/biomes" + x + "_" + y);
                if (!biomeFile.exists()) {
                    biomeFile.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(biomeFile);
                // 1. Biomes
                int[] biomes = mc.getBiomeData(x*1024, y*1024, 1024, 1024, true);
                byte[] bytebiomes = new byte[biomes.length];
                for (int i=0; i<biomes.length; i++) {
                    bytebiomes[i] = (byte)biomes[i];
                }
                os.write(bytebiomes);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("File operation error.");
                return;
            }
        }
        System.out.println("Completed.");
    }
}
