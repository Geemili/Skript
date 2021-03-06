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

package ch.njol.skript.lang;

import ch.njol.skript.lang.SkriptParser.ParseResult;

/**
 * Represents a general part of the syntax. Implementing classes/interfaces are {@link Expression} and {@link Statement}.
 * 
 * @author Peter Güttinger
 * 
 */
public interface SyntaxElement {
	
	/**
	 * called just after the constructor.
	 * 
	 * @param exprs all %var%s included in the matching pattern in the order they appear in the pattern. If an optional value was left out it will still be included in this list
	 *            holding the default value of the desired type which usually depends on the event.
	 * @param matchedPattern the index of the pattern which matched
	 * @param isDelayed whether this expression is used after a delay or not (i.e. if the event has already passed when this expression will be called)
	 * @param parseResult The parser osed to parse this expression. Might hold useful information in the future.
	 * @return Whether this expression was sucessfully initialized.
	 *         If this returns false, an error should be logged stating what went wrong,
	 *         but if no error is logged the effect is the same as if no pattern matched for this expression.
	 */
	public boolean init(Expression<?>[] exprs, int matchedPattern, int isDelayed, ParseResult parseResult);
	
}
