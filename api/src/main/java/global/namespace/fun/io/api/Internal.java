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
package global.namespace.fun.io.api;

import java.io.InputStream;
import java.io.OutputStream;

final class Internal {

    private Internal() { }

    static Transformation compose(Transformation first, Transformation second) {
        return new Transformation() {

            @Override
            public Loan<OutputStream> apply(Loan<OutputStream> osl) { return second.apply(first.apply(osl)); }

            @Override
            public Loan<InputStream> unapply(Loan<InputStream> isl) { return second.unapply(first.unapply(isl)); }

            @Override
            public Transformation inverse() {
                final Transformation composite = this;
                return new Transformation() {

                    @Override
                    public Loan<OutputStream> apply(Loan<OutputStream> osl) {
                        return first.inverse().apply(second.inverse().apply(osl));
                    }

                    @Override
                    public Loan<InputStream> unapply(Loan<InputStream> isl) {
                        return first.inverse().unapply(second.inverse().unapply(isl));
                    }

                    @Override
                    public Transformation inverse() { return composite; }
                };
            }
        };
    }

    static ConnectedCodec connect(Codec c, Store s) {
        return new ConnectedCodec() {
            @Override
            public Codec codec() { return c; }

            @Override
            public Store store() { return s; }
        };
    }
}
