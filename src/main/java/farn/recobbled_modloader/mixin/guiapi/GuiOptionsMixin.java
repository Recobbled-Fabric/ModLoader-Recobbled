package farn.recobbled_modloader.mixin.guiapi;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiOptions.class)
public class GuiOptionsMixin extends GuiScreen {

    @WrapOperation(method = "initGui", at = {
            @At(value = "NEW", target = "(IIILjava/lang/String;)Lnet/minecraft/src/GuiButton;", ordinal = 0),
            @At(value = "NEW", target = "(IIILjava/lang/String;)Lnet/minecraft/src/GuiButton;", ordinal = 1)
    })
    private GuiButton gui$adaptButtonSize(int id, int x, int y, String text, Operation<GuiButton> original) {
        return original.call(id, x, guiapi$hideButton() ? y : y - 12, text);
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void guiapi$init(CallbackInfo ci) {
        if (!guiapi$hideButton()) {
            this.controlList.add(new GuiButton(300, this.width / 2 - 100, this.height / 6 + 144, "Global Mod Settings"));
        }
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    private void guiapi$buttonClicked(GuiButton par1, CallbackInfo ci) {
        if (par1.enabled && par1.id == 300 && !guiapi$hideButton()) {
            this.mc.gameSettings.saveOptions();
            ModSettingScreen.guiContext = "";
            WidgetSetting.updateAll();
            GuiModScreen.show(new GuiModSelect(this));
        }
    }

    @Unique
    private boolean guiapi$hideButton() {
        return ModSettingScreen.modScreens.isEmpty();
    }
}
