package pl.zb3.freej2me.bridge.gles2;

import static pl.zb3.freej2me.bridge.gles2.GLES2.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class IntegerIndexMap {
    private final Map<Integer, Integer> map;
    private int nextIndex;

    public IntegerIndexMap() {
        this.map = new HashMap<>();
        this.nextIndex = 0;
    }

    public int get(int number) {
        Integer index = map.get(number);

        if (index != null) {
            return index;
        } else {
            map.put(number, nextIndex);
            return nextIndex++;
        }
    }

    public static void main(String[] args) {
        IntegerIndexMap indexMap = new IntegerIndexMap();

        // Test the get method
        System.out.println(indexMap.get(10)); // Output: 0
        System.out.println(indexMap.get(20)); // Output: 1
        System.out.println(indexMap.get(10)); // Output: 0
        System.out.println(indexMap.get(30)); // Output: 2
        System.out.println(indexMap.get(40)); // Output: 3
        System.out.println(indexMap.get(20)); // Output: 1
    }
}

public class BufferHelper {
    // handles should be vectors, of integers, initially empty
    private Vector<Integer> handles = new Vector<>();
    private Vector<Integer> sizes = new Vector<>();

    IntegerIndexMap attrToIndex = new IntegerIndexMap();

    private int indexHandle = -1;
    private int indexSize = -1;

    public int getHandle(int index) {
        return handles.get(index);
    }

    public void ensureBuffer(int index, int size) {
        if (index >= handles.size()) {
            handles.add(GLES2.createBuffer());
            sizes.add(0);
        }

        GLES2.bindBuffer(GL_ARRAY_BUFFER, handles.get(index));

        if (sizes.get(index) < size) {
            int newSize = size * 4 / 3;
            GLES2.bufferData(GL_ARRAY_BUFFER, newSize, GL_DYNAMIC_DRAW);
            sizes.set(index, newSize);
        }
    }

    /*
     * we copy data to GPU
     * so we need to know the size of a buffer for the GPU
     *
     * since java buffers are cached and larger-than-needed
     * we only need to consider the relevant parts
     * hence pointers are supplied the relevant length of the array
     *
     * stride complicates this
     *
     *
     * stride is byte distance per element
     */


    public void vertexFloatAttribPointer(int attr, int numPerElement, boolean normalized, int stride, int numElements, float[] elements) {
        int elemSize = stride > 0 ? stride : 4 * numPerElement;
        int totalSize = elemSize * numElements;

        ensureBuffer(attrToIndex.get(attr), totalSize);

        GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, totalSize, elements);

        GLES2.vertexAttribPointer(attr, numPerElement, GL_FLOAT, normalized, stride, 0);

        GLES2.bindBuffer(GL_ARRAY_BUFFER, 0);
    }


    public void vertexShortAttribPointer(int attr, int numPerElement, boolean normalized, int stride, int numElements, short[] elements) {
        int elemSize = stride > 0 ? stride : 2 * numPerElement;
        int totalSize = elemSize * numElements;

        ensureBuffer(attrToIndex.get(attr), totalSize);

        GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, totalSize, elements);

        GLES2.vertexAttribPointer(attr, numPerElement, GL_SHORT, normalized, stride, 0);

        GLES2.bindBuffer(GL_ARRAY_BUFFER, 0); // in webgl version we'll prob not need this
    }


    public void vertexByteAttribPointer(int attr, int numPerElement, boolean unsigned, boolean normalized, int stride, int numElements, byte[] elements) {
        int elemSize = stride > 0 ? stride : 1 * numPerElement;
        int totalSize = elemSize * numElements;

        ensureBuffer(attrToIndex.get(attr), totalSize);

        GLES2.bufferSubData(GL_ARRAY_BUFFER, 0, totalSize, elements);

        GLES2.vertexAttribPointer(attr, numPerElement, unsigned ? GL_UNSIGNED_BYTE : GL_BYTE, normalized, stride, 0);

        GLES2.bindBuffer(GL_ARRAY_BUFFER, 0); // seems necessary.. ??

    }

    // remember to unbind the element array buffer after the draw
    public void setIndexBuffer(int[] elements, int size) {
        if (indexHandle == -1) {
            indexHandle = GLES2.createBuffer();
        }

        GLES2.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexHandle);

        // short - 2 bytes
        if (indexSize < size*2) {
            GLES2.bufferData(GL_ELEMENT_ARRAY_BUFFER, size * 8 / 3, GL_DYNAMIC_DRAW);
        }

        short[] shortElements = new short[size];
        for (int t=0; t<size; t++) {
            shortElements[t] = (short) elements[t];
        }

        GLES2.bufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, shortElements.length*2, shortElements);
    }

    public void deallocate() {
        // iterate handles
        int[] handlesArray = new int[handles.size()];

        for (int t=0; t<handlesArray.length; t++) {
            handlesArray[t] = handles.get(t);
        }

        GLES2.deleteBuffers(handlesArray);

        if (indexHandle != -1) {
            int[] tmp = {indexHandle};
            GLES2.deleteBuffers(tmp);
        }

        handles.clear();
        sizes.clear();
        attrToIndex = new IntegerIndexMap();
        indexHandle = indexSize = -1;
    }

}
