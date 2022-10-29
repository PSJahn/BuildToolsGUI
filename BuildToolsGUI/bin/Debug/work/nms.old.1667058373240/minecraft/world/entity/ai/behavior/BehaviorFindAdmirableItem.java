package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorFindAdmirableItem<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> predicate;
    private final int maxDistToWalk;
    private final float speedModifier;

    public BehaviorFindAdmirableItem(float f, boolean flag, int i) {
        this((entityliving) -> {
            return true;
        }, f, flag, i);
    }

    public BehaviorFindAdmirableItem(Predicate<E> predicate, float f, boolean flag, int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, flag ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT));
        this.predicate = predicate;
        this.maxDistToWalk = i;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return !this.isOnPickupCooldown(e0) && this.predicate.test(e0) && this.getClosestLovedItem(e0).closerThan(e0, (double) this.maxDistToWalk);
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        // CraftBukkit start
        if (e0 instanceof net.minecraft.world.entity.animal.allay.Allay) {
            Entity target = this.getClosestLovedItem(e0);
            org.bukkit.event.entity.EntityTargetEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent(e0, target, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY);

            if (event.isCancelled()) {
                return;
            }

            target = (event.getTarget() == null) ? null : ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
            if (target instanceof EntityItem item) {
                e0.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, java.util.Optional.of(item));
                BehaviorUtil.setWalkAndLookTargetMemories(e0, target, this.speedModifier, 0);
            } else {
                e0.getBrain().eraseMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
            }
            return;
        }
        // CraftBukkit end
        BehaviorUtil.setWalkAndLookTargetMemories(e0, (Entity) this.getClosestLovedItem(e0), this.speedModifier, 0);
    }

    private boolean isOnPickupCooldown(E e0) {
        return e0.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    private EntityItem getClosestLovedItem(E e0) {
        return (EntityItem) e0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
    }
}
