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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.util.Time;

/**
 * @author Peter Güttinger
 * 
 */
public class EvtAtTime extends SkriptEvent implements Comparable<EvtAtTime> {
	
	static {
		Skript.registerEvent(EvtAtTime.class, ScheduledEvent.class, false, "at %time% [in %worlds%]");
	}
	
	private final static int CHECKPERIOD = 10;
	
	private final static class EvtAtInfo {
		int lastTick; // as Bukkit's scheduler is inconsistent this saves the exact tick when the events were last checked
		int currentIndex;
		ArrayList<EvtAtTime> list = new ArrayList<EvtAtTime>();
	}
	
	private final static HashMap<World, EvtAtInfo> triggers = new HashMap<World, EvtAtInfo>();
	
	private Trigger t;
	private int tick;
	private World[] worlds;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		tick = ((Literal<Time>) args[0]).getSingle().getTicks();
		worlds = args[1] == null ? Bukkit.getWorlds().toArray(new World[0]) : ((Literal<World>) args[1]).getAll();
		return true;
	}
	
	private static int taskID = -1;
	
	private static void registerListener() {
		if (taskID != -1)
			return;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Skript.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (final Entry<World, EvtAtInfo> e : triggers.entrySet()) {
					final EvtAtInfo i = e.getValue();
					final int tick = (int) e.getKey().getTime();
					if (i.lastTick == tick) // stupid Bukkit scheduler
						continue;
					if (i.lastTick + CHECKPERIOD * 2 < tick || i.lastTick > tick && i.lastTick - 24000 + CHECKPERIOD * 2 < tick) { // time changed
						i.lastTick = tick - CHECKPERIOD;
						if (i.lastTick < 0)
							i.lastTick += 24000;
					}
					final boolean midnight = i.lastTick > tick;
					if (midnight)
						i.lastTick -= 24000;
					final int lastIndex = i.currentIndex;
					while (true) {
						final EvtAtTime next = i.list.get(i.currentIndex);
						final int nextTick = midnight && next.tick > 12000 ? next.tick - 24000 : next.tick;
						if (i.lastTick < nextTick && nextTick <= tick) {
							next.execute(e.getKey());
							i.currentIndex++;
							if (i.currentIndex == i.list.size())
								i.currentIndex = 0;
							if (i.currentIndex == lastIndex)
								break;
						} else {
							break;
						}
					}
					i.lastTick = tick;
				}
			}
		}, CHECKPERIOD, CHECKPERIOD);
	}
	
	private void execute(final World w) {
		final ScheduledEvent e = new ScheduledEvent(w);
		SkriptEventHandler.logEventStart(e);
		SkriptEventHandler.logTriggerEnd(t);
		t.start(e);
		SkriptEventHandler.logTriggerEnd(t);
		SkriptEventHandler.logEventEnd();
	}
	
	@Override
	public void register(final Trigger t) {
		this.t = t;
		for (final World w : worlds) {
			EvtAtInfo i = triggers.get(w);
			if (i == null) {
				triggers.put(w, i = new EvtAtInfo());
				i.lastTick = (int) w.getTime() - 1;
			}
			i.list.add(this);
			Collections.sort(i.list);
		}
		registerListener();
	}
	
	@Override
	public void unregister(final Trigger t) {
		final Iterator<EvtAtInfo> iter = triggers.values().iterator();
		while (iter.hasNext()) {
			final EvtAtInfo i = iter.next();
			i.list.remove(this);
			if (i.list.isEmpty())
				iter.remove();
		}
		if (triggers.isEmpty())
			unregisterAll();
	}
	
	@Override
	public void unregisterAll() {
		if (taskID != -1)
			Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
		triggers.clear();
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "at " + Time.toString(tick) + " in worlds " + Skript.toString(worlds, true);
	}
	
	@Override
	public boolean check(final Event e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int compareTo(final EvtAtTime e) {
		return tick - e.tick;
	}
	
}
