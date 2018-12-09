package com.ilufang;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Main {
    private static final int ARG_MCJARFILE = 1;
    private static final int ARG_SEED = 2;
    private static final int ARG_CHUNKX = 3;
    private static final int ARG_CHUNKY = 4;

    private static MinecraftInterface mc;

    public static void main(String[] args) {
        if (args.length<5) {
            System.err.println("Invalid Args. Usage: [jarFile] [seed] [chunkX] [chunkY]");
            //return;
        }

        File lockFile = new File("AMIDST_LOCK");
        if (lockFile.exists()) {
            System.err.println("Concurrent modification.");
            //return;
        }

        //File mcJar = new File(args[ARG_MCJARFILE]);
        File mcPath = new File("C:\\Users\\huqin\\AppData\\Roaming\\.minecraft");
        try {
            lockFile.createNewFile();
            DotMinecraftDirectory directory = new DotMinecraftDirectory(mcPath);
            MinecraftInstallation install = new MinecraftInstallation(directory);
            List<LauncherProfile> profileList = install.readInstalledVersionsAsLauncherProfiles();
            profileList.forEach((v)->{
                if (v.getVersionName().equals("*1.13.2")) {
                    try {
                        mc = MinecraftInterfaces.fromLocalProfile(v);
                    } catch (Exception e) {
                        System.err.println("Failed to load Minecraft jar.");
                        lockFile.delete();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Minecraft jar.");
            lockFile.delete();
            return;
        }
        //mc.createWorld(Long.parseLong(args[ARG_SEED]), "default", "");
        try {
            mc.createWorld(-6271427238188031176L, WorldType.DEFAULT, "");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create world.");
            lockFile.delete();
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

        //int beginX = Integer.parseInt(args[ARG_CHUNKX]);
        //int beginY = Integer.parseInt(args[ARG_CHUNKY]);
        int beginX = 0, beginY = 0;
        for (int x=-10; x<=10; x++) for (int y=-10; y<=10; y++) {
            try {
                System.out.println("Generating "+x+","+y);
                File biomeFile = new File("biomes" + x + "_" + y);
                if (!biomeFile.exists()) {
                    biomeFile.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(biomeFile);
                //DataOutputStream dataos = new DataOutputStream(os);
                // 1. Biomes
                int[] biomes = mc.getBiomeData(x*1024, y*1024, 1024, 1024, true);
                byte[] bytebiomes = new byte[biomes.length];
                for (int i=0; i<biomes.length; i++) {
                    bytebiomes[i] = (byte)biomes[i];
                    //dataos.writeByte(biomes[i]);
                }
                os.write(bytebiomes);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("File operation error.");
                lockFile.delete();
                return;
            }
        }
        lockFile.delete();
        System.out.println("Completed.");
    }
}
