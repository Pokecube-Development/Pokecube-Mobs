package pokecube.core.database.abilities.f;

import net.minecraft.entity.Entity;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.moves.PokemobTerrainEffects;
import thut.api.terrain.TerrainManager;
import thut.api.terrain.TerrainSegment;

public class Forecast extends Ability
{
    static PokedexEntry rain;
    static PokedexEntry sun;
    static PokedexEntry snow;
    static PokedexEntry base;

    @Override
    public void onUpdate(IPokemob mob)
    {
        if (mob.getPokedexNb() != 351) return;// Only affect castform.

        final Entity pokemob = mob.getEntity();
        if (pokemob.ticksExisted % 20 != 9) return;// Only check once per
                                                   // second.

        if (Forecast.rain == null) Forecast.rain = Database.getEntry("castformrain");
        if (Forecast.base == null) Forecast.base = Database.getEntry("castform");
        if (Forecast.sun == null) Forecast.sun = Database.getEntry("castformsun");
        if (Forecast.snow == null) Forecast.snow = Database.getEntry("castformsnow");

        // TODO check for weather canceling effects in the area, then set to
        // base and return early.

        final TerrainSegment terrain = TerrainManager.getInstance().getTerrainForEntity(pokemob);
        final PokemobTerrainEffects effect = (PokemobTerrainEffects) terrain.geTerrainEffect("pokemobEffects");
        long terrainDuration = effect.getEffect(PokemobTerrainEffects.EFFECT_WEATHER_RAIN);
        if (terrainDuration > 0)
        {
            mob.setPokedexEntry(Forecast.rain);
            return;
        }
        terrainDuration = effect.getEffect(PokemobTerrainEffects.EFFECT_WEATHER_SUN);
        if (terrainDuration > 0)
        {
            mob.setPokedexEntry(Forecast.sun);
            return;
        }
        terrainDuration = effect.getEffect(PokemobTerrainEffects.EFFECT_WEATHER_HAIL);
        if (terrainDuration > 0)
        {
            mob.setPokedexEntry(Forecast.snow);
            return;
        }
        mob.setPokedexEntry(Forecast.base);
    }

}
