/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySink;
import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveOutputStream;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;
import static global.namespace.fun.io.spi.Copy.copy;

/**
 * Adapts a {@link TarArchiveOutputStream} to an {@link ArchiveOutputStream}.
 *
 * @author Christian Schlichtherle
 */
final class TarArchiveOutputStreamAdapter implements ArchiveOutputStream {

    private final TarArchiveOutputStream tar;

    TarArchiveOutputStreamAdapter(final TarArchiveOutputStream tar) { this.tar = tar; }

    public ArchiveEntrySink sink(String name) {
        return sink(new TarArchiveEntry(requireInternal(name)));
    }

    private ArchiveEntrySink sink(TarArchiveEntry entry) {
        return new ArchiveEntrySink() {

            @Override
            public Socket<OutputStream> output() {
                return () -> {
                    tar.putArchiveEntry(entry);
                    return new FilterOutputStream(tar) {

                        boolean closed;

                        @Override
                        public void close() throws IOException {
                            if (!closed) {
                                closed = true;
                                tar.closeArchiveEntry(); // not idempotent!
                            }
                        }
                    };
                };
            }

            @Override
            public void copyFrom(final ArchiveEntrySource source) throws Exception {
                entry.setSize(source.size());
                copy(source, this);
            }
        };
    }

    @Override
    public void close() throws IOException { tar.close(); }
}
