package dcq.wlog;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Arrays;

public class WaterlogSavedData extends SavedData {
    public static final Codec<WaterlogSavedData> CODEC = Codec.LONG_STREAM.xmap(
            stream -> new WaterlogSavedData(new LongOpenHashSet(stream.toArray())),
            data -> Arrays.stream(data.positions.toLongArray())
    );
    public static final SavedDataType<WaterlogSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Waterlog.MOD_ID, "waterlogged_positions"),
            WaterlogSavedData::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final LongSet positions;

    public WaterlogSavedData() {
        this(new LongOpenHashSet());
    }

    private WaterlogSavedData(LongSet positions) {
        this.positions = positions;
    }

    public LongSet positions() {
        return positions;
    }
}
