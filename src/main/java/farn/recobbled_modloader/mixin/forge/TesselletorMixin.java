package farn.recobbled_modloader.mixin.forge;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import farn.recobbled_modloader.mixin_config.MakePublic;
import forge.ForgeHooksClient;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

@Mixin(Tessellator.class)
public abstract class TesselletorMixin implements TessellatorAccessor {

    @Shadow
    private ByteBuffer byteBuffer;
    @Shadow
    private int vertexCount;
    @Shadow
    private boolean hasColor;
    @Shadow
    private boolean hasTexture;
    @Shadow
    private boolean hasNormals;
    @Shadow
    private int rawBufferIndex;
    @Shadow
    private int addedVertices;
    @Shadow
    private boolean isColorDisabled;
    @Shadow
    private boolean isDrawing;
    @Shadow
    private int vboIndex;
    @Shadow
    private int drawMode;
    @Shadow
    private static boolean convertQuadsToTriangles;
    @Shadow
    private int[] rawBuffer;

    @Shadow
    public static Tessellator instance;
    @Shadow
    private boolean useVBO;
    @Shadow
    private static boolean tryVBO;
    @Unique
    @MakePublic
    private static boolean renderingWorldRenderer;
    @Unique
    public boolean defaultTexture;
    @Unique
    private int rawBufferSize;
    @Unique
    private static int nativeBufferSize;
    @Unique
    private static int trivertsInBuffer;
    @Unique
    private static FloatBuffer sharedFloatBuffer;
    @Unique
    private static ByteBuffer sharedByteBuffer;
    @Unique
    private static IntBuffer sharedVboBuffer;
    @Unique
    private static IntBuffer sharedIntBuffer;
    @Unique
    private static int sharedVboCount;
    @Unique
    private static boolean sharedUseVbo;

    @Unique
    @MakePublic
    private static Tessellator firstInstance;

    @Inject(method="<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        this.byteBuffer = null;
        this.vertexCount = 0;
        this.hasColor = false;
        this.hasTexture = false;
        this.hasNormals = false;
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
        this.isColorDisabled = false;
        this.isDrawing = false;
        this.vboIndex = 0;
        this.defaultTexture = false;
        this.rawBufferSize = 0;

        if (firstInstance == null) {
            firstInstance = (Tessellator) (Object) this;
            ForgeHooksClient.defaultTessellator = firstInstance;
        }
    }

    @Overwrite
    public void draw() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            this.isDrawing = false;
            int offs = 0;

            while(offs < this.vertexCount) {
                int vtc;
                if (this.drawMode == 7 && convertQuadsToTriangles) {
                    vtc = Math.min(this.vertexCount - offs, trivertsInBuffer);
                } else {
                    vtc = Math.min(this.vertexCount - offs, nativeBufferSize >> 5);
                }

                ((Buffer) sharedIntBuffer).clear();
                /* Buffer overflow fix */
                ensureCapacity(offs * 8 + vtc * 8);
                sharedIntBuffer.put(this.rawBuffer, offs * 8, vtc * 8);
                ((Buffer) sharedByteBuffer).position(0);
                ((Buffer) sharedByteBuffer).limit(vtc * 32);
                offs += vtc;
                if (sharedUseVbo) {
                    this.vboIndex = (this.vboIndex + 1) % sharedVboCount;
                    ARBVertexBufferObject.glBindBufferARB(34962, sharedVboBuffer.get(this.vboIndex));
                    ARBVertexBufferObject.glBufferDataARB(34962, sharedByteBuffer, 35040);
                }

                if (this.hasTexture) {
                    if (sharedUseVbo) {
                        GL11.glTexCoordPointer(2, 5126, 32, 12L);
                    } else {
                        ((Buffer) sharedFloatBuffer).position(3);
                        GL11.glTexCoordPointer(2, 32, sharedFloatBuffer);
                    }

                    GL11.glEnableClientState(32888);
                }

                if (this.hasColor) {
                    if (sharedUseVbo) {
                        GL11.glColorPointer(4, 5121, 32, 20L);
                    } else {
                        ((Buffer) sharedByteBuffer).position(20);
                        GL11.glColorPointer(4, true, 32, sharedByteBuffer);
                    }

                    GL11.glEnableClientState(32886);
                }

                if (this.hasNormals) {
                    if (sharedUseVbo) {
                        GL11.glNormalPointer(5120, 32, 24L);
                    } else {
                        ((Buffer) sharedByteBuffer).position(24);
                        GL11.glNormalPointer(32, sharedByteBuffer);
                    }

                    GL11.glEnableClientState(32885);
                }

                if (sharedUseVbo) {
                    GL11.glVertexPointer(3, 5126, 32, 0L);
                } else {
                    ((Buffer) sharedFloatBuffer).position(0);
                    GL11.glVertexPointer(3, 32, sharedFloatBuffer);
                }

                GL11.glEnableClientState(32884);
                if (this.drawMode == 7 && convertQuadsToTriangles) {
                    GL11.glDrawArrays(4, 0, vtc);
                } else {
                    GL11.glDrawArrays(this.drawMode, 0, vtc);
                }

                GL11.glDisableClientState(32884);
                if (this.hasTexture) {
                    GL11.glDisableClientState(32888);
                }

                if (this.hasColor) {
                    GL11.glDisableClientState(32886);
                }

                if (this.hasNormals) {
                    GL11.glDisableClientState(32885);
                }
            }

            if (this.rawBufferSize > 131072 && this.rawBufferIndex < this.rawBufferSize << 3) {
                this.rawBufferSize = 65536; /* original: 0 */
                this.rawBuffer = new int[this.rawBufferSize]; /* original: null */
            }

            this.reset();
        }
    }

    /* Buffer overflow fix */
    private static void ensureCapacity(int criticalCapacity) {
        if (criticalCapacity > sharedIntBuffer.capacity()) {
            System.out.println("Increasing the buffer size from " + sharedIntBuffer.capacity() + " to " + (sharedIntBuffer.capacity() * 2) + " for Tessellator.");
            sharedByteBuffer = GLAllocation.createDirectByteBuffer(sharedIntBuffer.capacity() * 2);
            sharedIntBuffer = sharedByteBuffer.asIntBuffer();
            sharedFloatBuffer = sharedByteBuffer.asFloatBuffer();
        }
    }

    @Overwrite
    private void reset() {
        this.vertexCount = 0;
        ((Buffer) sharedByteBuffer).clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    @Inject(method = "addVertex", at = @At("HEAD"))
    private void forge$vertex(double y, double z, double par3, CallbackInfo ci) {
        if(this.rawBufferIndex >= this.rawBufferSize - 32) {
            if(this.rawBufferSize == 0) {
                this.rawBufferSize = 65536;
                this.rawBuffer = new int[this.rawBufferSize];
            } else {
                this.rawBufferSize *= 2;
                this.rawBuffer = Arrays.copyOf(this.rawBuffer, this.rawBufferSize);
            }
        }
    }

    @Definition(id = "vertexCount", field = "Lnet/minecraft/src/Tessellator;vertexCount:I")
    @Expression("this.vertexCount % 4")
    @Inject(method = "addVertex", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void forge$exitEarly(double y, double z, double par3, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void classInit(CallbackInfo ci) {
        sharedUseVbo = false;
        renderingWorldRenderer = false;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void classInitLate(CallbackInfo ci) {
        ((TessellatorAccessor) instance).setDefaultTexture(true);
        nativeBufferSize = 2097152;
        trivertsInBuffer = nativeBufferSize / 48 * 6;
        sharedByteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
        sharedIntBuffer = sharedByteBuffer.asIntBuffer();
        sharedFloatBuffer = sharedByteBuffer.asFloatBuffer();
        sharedUseVbo = tryVBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;
        sharedVboCount = 10;
        if (sharedUseVbo) {
            sharedVboBuffer = GLAllocation.createDirectIntBuffer(sharedVboCount);
            ARBVertexBufferObject.glGenBuffersARB(sharedVboBuffer);
        }
    }

}
