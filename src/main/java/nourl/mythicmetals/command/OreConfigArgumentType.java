package nourl.mythicmetals.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;
import nourl.mythicmetals.config.OreConfig;
import java.util.concurrent.CompletableFuture;

public class OreConfigArgumentType implements ArgumentType<OreConfig> {
    private final SimpleCommandExceptionType EXCEPTION = new SimpleCommandExceptionType(
        Text.translatable("command.mythicmetals.argument.ore-config.error")
    );

    public static <S> OreConfig getOreConfig(CommandContext<S> context, String name) {
        return context.getArgument(name, OreConfig.class);
    }

    @Override
    public OreConfig parse(StringReader reader) throws CommandSyntaxException {
        final String oreConfig = reader.readString();
        if (MythicCommands.ORECONFIG.containsKey(oreConfig)) {
            return MythicCommands.ORECONFIG.get(oreConfig);
        }
        throw EXCEPTION.create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        MythicCommands.ORECONFIG.forEach((s, oreConfig) -> builder.suggest(s));
        return builder.buildFuture();
    }

    public static OreConfigArgumentType oreConfig() {
        return new OreConfigArgumentType();
    }
}
