package nourl.mythicmetals.config;


import nourl.mythicmetals.data.MythicOreKeys;

/**
 * A config constructor that lets you quickly create all the settings used
 * for an ore in the {@link MythicOreKeys} class.
 */
public class OreConfig {
    public int veinSize;
    public int perChunk;
    public int bottom;
    public int top;
    public float discardChance;
    public boolean offset;
    public boolean trapezoid;

    /**
     *  The constructor of the OreConfig. Contains all the elements needed to configure a single ore feature.
     * @param veinSize          The vein size of the ore feature
     * @param perChunk          How many times the ore should attempt to generate per chunk
     * @param bottom            The lowest value in the fixed/offset range of where the ore can generate
     * @param top               The highest value in the fixed/offset range of where the ore can generate
     * @param discardChance     The chance the ore is discarded when exposed to air
     */
    public OreConfig(int veinSize, int perChunk, int bottom, int top, float discardChance, boolean offset) {
        this.veinSize = veinSize;
        this.perChunk = perChunk;
        this.bottom = bottom;
        this.top = top;
        this.discardChance = discardChance;
        this.offset = offset;
    }

    /**
     *  Extra constructor that contains an extra boolean, which is whether the feature contains an offset.
     * @param veinSize          The vein size of the ore feature
     * @param perChunk          How many times the ore should attempt to generate per chunk
     * @param bottom            The lowest value in the fixed/offset range of where the ore can generate
     * @param top               The highest value in the fixed/offset range of where the ore can generate
     * @param discardChance     The chance the ore is discarded when exposed to air
     * @param offset            If true the feature is offset from the bottom, else it is not offset
     */
    public OreConfig(int veinSize, int perChunk, int bottom, int top, float discardChance, boolean offset, boolean trapezoid) {
        this.veinSize = veinSize;
        this.perChunk = perChunk;
        this.bottom = bottom;
        this.top = top;
        this.discardChance = discardChance;
        this.offset = offset;
        this.trapezoid = trapezoid;
    }

}
