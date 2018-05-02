/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.zstd;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Socket;

import java.io.InputStream;
import java.io.OutputStream;

final class ZstdFilter implements Filter {

    final int level;

    ZstdFilter(final int level) { this.level = level; }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) {
        return output.map(out -> new ZstdOutputStream(out, level));
    }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) { return input.map(ZstdInputStream::new); }
}
