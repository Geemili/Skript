/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.Skript.ExpressionType;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.UnparsedLiteral;

/**
 * @author Peter Güttinger
 * 
 */
public class ExprTimeState extends WrapperExpression<Object> {
	
	static {
		Skript.registerExpression(ExprTimeState.class, Object.class, ExpressionType.PROPERTY,
				"(former|past) [state] [of] %object%", "%object% before [the event]",
				"(future|to-be) [state] [of] %object%", "%object%(-to-be| after[(wards| the event)])");
	}
	
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final int isDelayed, final ParseResult parseResult) {
		final Expression<?> expr = vars[0];
		if (expr instanceof UnparsedLiteral)
			return false;
		if (isDelayed == 1) {
			Skript.error("Cannot use time states after the event has already passed");
			return false;
		}
		if (!expr.setTime(matchedPattern >= 2 ? 1 : -1)) {
			Skript.error(expr + " does not have a " + (matchedPattern >= 2 ? "future" : "past") + " state");
			return false;
		}
		setExpr(expr);
		return true;
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "the " + (getTime() == -1 ? "past" : "future") + " state of " + getExpr().toString(e, debug);
	}
	
}
