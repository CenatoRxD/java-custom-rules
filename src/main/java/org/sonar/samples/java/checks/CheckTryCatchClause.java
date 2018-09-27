/*
 * SonarQube Java
 * Copyright (C) 2012-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.List;

@Rule(
        key = "CheckTryCatchClause",
        name = "Catch clause must throw ServiceException",
        description = "Catch clause must throw ServiceException.",
        priority = Priority.CRITICAL,
        tags = {"bug"})
public class CheckTryCatchClause extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.TRY_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (!hasSemantic()) {
            return;
        }
        TryStatementTree tst = (TryStatementTree) tree;
        CatchTree catchTree = tst.catches().get(0);
        ThrowStatementTree throwStatement = findThrowStatement(catchTree);
        if (throwStatement != null) {
            if (!checkExceptionType(throwStatement)) {
                reportIssue(catchTree.block().body().get(0), "Catch clause must " +
                        "throw ServiceException");
            }
        } else {
            reportIssue(catchTree.block(), "Catch clause must " +
                    "throw ServiceException");
        }
    }

    private ThrowStatementTree findThrowStatement(CatchTree catchTree) {
        List<StatementTree> catchBody = catchTree.block().body();
        for (StatementTree aCatchBody : catchBody) {
            if (aCatchBody.is(Tree.Kind.THROW_STATEMENT)) {
                return (ThrowStatementTree) aCatchBody;
            }
        }
        return null;

    }

    private boolean checkExceptionType(ThrowStatementTree throwStatement) {
        String type = "";
        if (throwStatement.expression().is(Tree.Kind.NEW_CLASS)) {
            NewClassTree expression = (NewClassTree) throwStatement.expression();
            IdentifierTree identifierTree = (IdentifierTree) expression.identifier();
            type = identifierTree.name();
        }

        return "ServiceException".equals(type);
    }
}
