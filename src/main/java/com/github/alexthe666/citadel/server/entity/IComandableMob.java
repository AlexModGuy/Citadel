package com.github.alexthe666.citadel.server.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public interface IComandableMob {

    int getCommand();

    void setCommand(int command);

    default InteractionResult playerSetCommand(Player owner, Animal ourselves) {
        if (!owner.level.isClientSide) {
            int command = (getCommand() + 1) % 3;
            this.setCommand(command);
            this.sendCommandMessage(owner, command, ourselves.getName());

            if(ourselves instanceof TamableAnimal){
                ((TamableAnimal)(ourselves)).setOrderedToSit(command == 1);
            }
        }
        return InteractionResult.PASS;
    }

    default void sendCommandMessage(Player owner, int command, Component name){

    }

}
