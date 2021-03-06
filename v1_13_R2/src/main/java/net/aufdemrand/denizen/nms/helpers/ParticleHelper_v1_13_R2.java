package net.aufdemrand.denizen.nms.helpers;

import net.aufdemrand.denizen.nms.abstracts.ParticleHelper;
import net.aufdemrand.denizen.nms.impl.effects.Effect_v1_13_R2;
import net.aufdemrand.denizen.nms.impl.effects.Particle_v1_13_R2;
import org.bukkit.Effect;
import org.bukkit.Particle;

public class ParticleHelper_v1_13_R2 extends ParticleHelper {

    public ParticleHelper_v1_13_R2() {
        for (Particle particle : Particle.values()) {
            register(particle.name(), new Particle_v1_13_R2(particle));
        }
        for (Effect effect : Effect.values()) {
            register(effect.name(), new Effect_v1_13_R2(effect));
        }
        //register("DRIP_WATER", new Effect_v1_13_R2(Effect.WATERDRIP)); -- not relevant anymore
        //register("DRIP_LAVA", new Effect_v1_13_R2(Effect.LAVADRIP));
        register("SMOKE", new Particle_v1_13_R2(Particle.SMOKE_NORMAL));
        register("HUGE_EXPLOSION", new Particle_v1_13_R2(Particle.EXPLOSION_HUGE));
        register("LARGE_EXPLODE", new Particle_v1_13_R2(Particle.EXPLOSION_LARGE));
        register("BUBBLE", new Particle_v1_13_R2(Particle.WATER_BUBBLE));
        register("SUSPEND", new Particle_v1_13_R2(Particle.SUSPENDED));
        register("DEPTH_SUSPEND", new Particle_v1_13_R2(Particle.SUSPENDED_DEPTH));
        register("CRIT", new Particle_v1_13_R2(Particle.CRIT));
        register("MAGIC_CRIT", new Particle_v1_13_R2(Particle.CRIT_MAGIC));
        register("MOB_SPELL", new Particle_v1_13_R2(Particle.SPELL_MOB));
        register("MOB_SPELL_AMBIENT", new Particle_v1_13_R2(Particle.SPELL_MOB_AMBIENT));
        register("INSTANT_SPELL", new Particle_v1_13_R2(Particle.SPELL_INSTANT));
        register("WITCH_MAGIC", new Particle_v1_13_R2(Particle.SPELL_WITCH));
        register("STEP_SOUND", new Particle_v1_13_R2(Particle.HEART));
        register("EXPLODE", new Particle_v1_13_R2(Particle.EXPLOSION_NORMAL));
        register("SPLASH", new Particle_v1_13_R2(Particle.WATER_SPLASH));
        register("LARGE_SMOKE", new Particle_v1_13_R2(Particle.SMOKE_LARGE));
        register("RED_DUST", new Particle_v1_13_R2(Particle.REDSTONE));
        register("SNOWBALL_POOF", new Particle_v1_13_R2(Particle.SNOWBALL));
        register("ANGRY_VILLAGER", new Particle_v1_13_R2(Particle.VILLAGER_ANGRY));
        register("HAPPY_VILLAGER", new Particle_v1_13_R2(Particle.VILLAGER_HAPPY));
    }
}
