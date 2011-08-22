/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.visualization.rendering.apiimpl.command.node;

import com.jogamp.common.nio.Buffers;
import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.rendering.command.buffer.Layout;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Shape2DLayoutGL2 extends Layout<VizNode2D> {
    private float texBorderSize; 
    
    private static final ByteBuffer indexBuffer;
    static {
        indexBuffer = createIndexBuffer();
    }

    private static ByteBuffer createIndexBuffer() {
        ByteBuffer buffer = Buffers.newDirectByteBuffer(196608);
        short i = 0;
        while(buffer.remaining() > 12) {
            buffer.putShort(i);
            buffer.putShort((short)(i+1));
            buffer.putShort((short)(i+2));
            buffer.putShort(i);
            buffer.putShort((short)(i+2));
            buffer.putShort((short)(i+3));
            i += 4;
        }
        buffer.flip();
        return buffer;
    }

    public Shape2DLayoutGL2() {
        this.texBorderSize = 0.0f;
    }
    
    public void texBorderSize(float texBorderSize) {
        this.texBorderSize = texBorderSize;
    }
    
    @Override
    public boolean useStaticBuffer() {
        return true;
    }

    @Override
    public int glDrawMode() {
        return GL.GL_TRIANGLES;
    }

    @Override
    public int glDrawType() {
        return GL.GL_UNSIGNED_SHORT;
    }

    @Override
    public ByteBuffer getStaticIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public void enableClientStates(GL gl) {
        /* EMPTY BLOCK */
    }

    @Override
    public void disableClientStates(GL gl) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public void setPointers(GL gl, ByteBuffer b) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glInterleavedArrays(GL2.GL_T2F_C3F_V3F, 0, b);
    }

    @Override
    public void setOffsets(GL gl) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glInterleavedArrays(GL2.GL_T2F_C3F_V3F, 0, 0);
    }

    @Override
    public int add(ByteBuffer b, VizNode2D e) {
        if (b.remaining() < 128) return -1;

        // BOTTOM LEFT
        b.putFloat(texBorderSize);
        b.putFloat(1.0f - texBorderSize);
        b.putFloat(e.color.r);
        b.putFloat(e.color.g);
        b.putFloat(e.color.b);
        b.putFloat(e.position.x() - e.size);
        b.putFloat(e.position.y() - e.size);
        b.putFloat(-e.size);

        // BOTTOM RIGHT
        b.putFloat(1.0f - texBorderSize);
        b.putFloat(1.0f - texBorderSize);
        b.putFloat(e.color.r);
        b.putFloat(e.color.g);
        b.putFloat(e.color.b);
        b.putFloat(e.position.x() + e.size);
        b.putFloat(e.position.y() - e.size);
        b.putFloat(-e.size);

        // TOP RIGHT
        b.putFloat(1.0f - texBorderSize);
        b.putFloat(texBorderSize);
        b.putFloat(e.color.r);
        b.putFloat(e.color.g);
        b.putFloat(e.color.b);
        b.putFloat(e.position.x() + e.size);
        b.putFloat(e.position.y() + e.size);
        b.putFloat(-e.size);

        // TOP LEFT
        b.putFloat(texBorderSize);
        b.putFloat(texBorderSize);
        b.putFloat(e.color.r);
        b.putFloat(e.color.g);
        b.putFloat(e.color.b);
        b.putFloat(e.position.x() - e.size);
        b.putFloat(e.position.y() + e.size);
        b.putFloat(-e.size);
        
        return 6;
    }

    @Override
    public boolean add(ByteBuffer b, ByteBuffer ind, VizNode2D e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
