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

package ch.njol.skript.events;

import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;

/**
 * @author Peter Güttinger
 * 
 */
public final class EvtGameMode extends SkriptEvent {
	
	static {
		Skript.registerEvent(EvtGameMode.class, PlayerGameModeChangeEvent.class, "gamemode change [to %gamemode%]");
	}
	
	private Literal<GameMode> mode;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		mode = (Literal<GameMode>) args[0];
		return true;
	}
	
	@Override
	public boolean check(final Event e) {
		if (mode == null)
			return true;
		return mode.check(e, new Checker<GameMode>() {
			@Override
			public boolean check(final GameMode m) {
				return ((PlayerGameModeChangeEvent) e).getNewGameMode().equals(m);
			}
		});
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "gamemode change" + (mode == null ? "" : " to " + mode.toString().toLowerCase());
	}
	
}
