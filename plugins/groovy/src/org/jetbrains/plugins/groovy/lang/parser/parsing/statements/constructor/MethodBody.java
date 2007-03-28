/*
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.groovy.lang.parser.parsing.statements.constructor;

import org.jetbrains.plugins.groovy.lang.parser.GroovyElementTypes;
import org.jetbrains.plugins.groovy.lang.parser.parsing.util.ParserUtils;
import org.jetbrains.plugins.groovy.lang.parser.parsing.statements.blocks.OpenOrClosableBlock;
import org.jetbrains.plugins.groovy.lang.parser.parsing.auxiliary.Separators;
import org.jetbrains.plugins.groovy.GroovyBundle;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;

/**
 * @author: Dmitry.Krasilschikov
 * @date: 26.03.2007
 */
public class MethodBody implements GroovyElementTypes {
  public static IElementType parse(PsiBuilder builder) {
    PsiBuilder.Marker cbMarker = builder.mark();

    if (!ParserUtils.getToken(builder, mLCURLY)) {
      builder.error(GroovyBundle.message("lcurly.expected"));
      cbMarker.rollbackTo();
      return WRONGWAY;
    }

    ParserUtils.getToken(builder, mNLS);

    if (!WRONGWAY.equals(ExplicitConstructorStatement.parse(builder))) {

      builder.error(GroovyBundle.message("constructor.expected"));
      //explicit constructor invocation
      if (!WRONGWAY.equals(Separators.parse(builder))) {
        if (WRONGWAY.equals(OpenOrClosableBlock.parseBlockBody(builder))) {
          cbMarker.rollbackTo();
          return WRONGWAY;
        }
      }

      cbMarker.done(CONSTRUCTOR_BODY);
      return CONSTRUCTOR_BODY;

    } else {
      //just list block statements
      if (WRONGWAY.equals(OpenOrClosableBlock.parseBlockBody(builder))) {
        cbMarker.rollbackTo();
        return WRONGWAY;
      }
    }

    ParserUtils.waitNextRCurly(builder);

    if (!ParserUtils.getToken(builder, mRCURLY)) {
      builder.error(GroovyBundle.message("rcurly.expected"));
    }

    cbMarker.done(METHOD_BODY);
    return METHOD_BODY;
  }
}

