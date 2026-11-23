package io.github.hiveko.vanillafinest.mixin.folders;

import java.nio.file.Path;

import io.github.hiveko.vanillafinest.folders.Folders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Axiom sets its blueprint folder once, in Axiom.onInitialize (config/axiom/blueprints, set in up to
 * three places depending on the server), creates it and hands it to its blueprint browser and its
 * server blueprint manager. Right after each of those assignments the folder from the helper's
 * settings is put in instead, so Axiom creates and uses that one. A change counts after a restart.
 */
@Mixin(targets = "com.moulberry.axiom.Axiom", remap = false)
public abstract class AxiomMixin {
    @Shadow
    private Path blueprintDirectory;

    @Inject(method = "onInitialize",
            at = @At(value = "FIELD", target = "Lcom/moulberry/axiom/Axiom;blueprintDirectory:Ljava/nio/file/Path;",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void vanillaFinest$blueprintFolder(CallbackInfo ci) {
        Path folder = Folders.override(Folders.AXIOM);
        if (folder != null) this.blueprintDirectory = folder;
    }
}
