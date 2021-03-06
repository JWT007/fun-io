/*
 * Copyright (C) 2013-2018 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.commons.compress;

import global.namespace.fun.io.api.ArchiveEntrySource;
import global.namespace.fun.io.api.ArchiveInputStream;
import global.namespace.fun.io.api.Socket;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static global.namespace.fun.io.spi.ArchiveEntryNames.isInternal;
import static global.namespace.fun.io.spi.ArchiveEntryNames.requireInternal;

/**
 * Adapts a {@link ZipFile} to an {@link ArchiveInputStream}.
 *
 * @author Christian Schlichtherle
 */
final class ZipFileAdapter implements ArchiveInputStream {

    private final ZipFile zip;

    ZipFileAdapter(final ZipFile zip) { this.zip = zip; }

    @Override
    public Iterator<ArchiveEntrySource> iterator() {
        return new Iterator<ArchiveEntrySource>() {

            final Enumeration<ZipArchiveEntry> en = zip.getEntries();
            ZipArchiveEntry next;

            @Override
            public boolean hasNext() {
                if (null != next) {
                    return true;
                } else {
                    while (en.hasMoreElements()) {
                        final ZipArchiveEntry entry = en.nextElement();
                        if (isInternal(entry.getName())) {
                            next = entry;
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override
            public ArchiveEntrySource next() {
                if (hasNext()) {
                    final ZipArchiveEntry entry = next;
                    next = null;
                    return source(entry);
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    @Override
    public Optional<ArchiveEntrySource> source(String name) {
        return Optional.ofNullable(zip.getEntry(requireInternal(name))).map(this::source);
    }

    private ZipArchiveEntrySource source(ZipArchiveEntry entry) {
        return new ZipArchiveEntrySource() {

            @Override
            public Socket<InputStream> input() { return () -> zip.getInputStream(entry); }

            @Override
            Socket<InputStream> rawInput() { return () -> zip.getRawInputStream(entry); }

            @Override
            ZipArchiveEntry entry() { return entry; }

            @Override
            public String name() { return entry.getName(); }

            @Override
            public boolean directory() { return entry.isDirectory(); }

            @Override
            public long size() { return entry.getSize(); }
        };
    }

    @Override
    public void close() throws IOException { zip.close(); }
}
