package pl.zb3.freej2me.bridge.gles2;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.*;

public class TextureResourceManager {
    private final ReferenceQueue<TextureHolder> referenceQueue = new ReferenceQueue<>();

    private final Map<PhantomReference<TextureHolder>, Integer> textureIdMap = new HashMap<>();
    private final Map<TextureHolder, PhantomReference<TextureHolder>> holderToPR = new WeakHashMap<>();

    public synchronized void registerHolder(TextureHolder obj, int textureId) {
        // check if we have a PR for obj in our weak map
        PhantomReference<TextureHolder> pr = holderToPR.get(obj);
        if (pr == null) {
            // no PR, create a new one
            pr = new PhantomReference<>(obj, referenceQueue);
            holderToPR.put(obj, pr);
        }

        // place the new (possibly 0) textureId
        // zeros need to be kept so we know
        textureIdMap.put(pr, textureId);
    }

    public synchronized void resetHolder(TextureHolder obj) {
        registerHolder(obj, 0);
        obj.clearId();
    }

    @SuppressWarnings("unchecked")
    public synchronized void deleteUnusedTextures() {
        // poll the reference queue
        PhantomReference<TextureHolder> pr;
        while ((pr = (PhantomReference<TextureHolder>) referenceQueue.poll()) != null) {
            // remove the textureId from the map
            Integer textureId = textureIdMap.remove(pr); // might be null if all deleted before
            if (textureId != null && textureId != 0) {
                deleteGLTexture(textureId);
            }
        }
    }

    public synchronized void deleteAllTextures() {
        // delete all textures allocated
        for (int textureId : textureIdMap.values()) {
            if (textureId != 0) {
                deleteGLTexture(textureId);
            }
        }

        // clear the maps
        textureIdMap.clear();

        // use TextureHolder.clearId if the holder exists (a key present in our weak map)
        for (TextureHolder holder : holderToPR.keySet()) {
            holder.clearId();
        }

        // clear PR map so PRs get GC'ed
        holderToPR.clear();
    }

    public void deleteGLTexture(int id) {
        System.out.println("[trm] actually deleting texture: "+id);
        GLES2.deleteTexture(id);
    }
}
