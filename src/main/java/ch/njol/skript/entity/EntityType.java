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

package ch.njol.skript.entity;

import org.bukkit.entity.Entity;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.util.Utils;
import ch.njol.util.Validate;

/**
 * 
 * @author Peter Güttinger
 * 
 */
public class EntityType {
	
	static {
		Skript.registerClass(new ClassInfo<EntityType>(EntityType.class, "entitytype", "entity type")
				.defaultExpression(new SimpleLiteral<EntityType>(new EntityType(Entity.class, 1), true))
				.parser(new Parser<EntityType>() {
					@Override
					public EntityType parse(final String s, final ParseContext context) {
						return EntityType.parse(s);
					}
					
					@Override
					public String toString(final EntityType t) {
						return t.toString();
					}
					
					@Override
					public String toCodeString(final EntityType t) {
						return "entitytype:" + t.toString();
					}
				})/*.serializer(new Serializer<EntityType>() {
					@Override
					public String serialize(final EntityType t) {
						return t.data..getName() + "*" + t.amount;
					}
					
					@Override
					public EntityType deserialize(final String s) {
						final String[] split = s.split("\\*");
						if (split.length != 2)
							return null;
						try {
							return new EntityType(Class.forName(split[0]).asSubclass(Entity.class), Integer.parseInt(split[1]));
						} catch (final LinkageError e) {
							return null;
						} catch (final ClassNotFoundException e) {
							return null;
						} catch (final ClassCastException e) {
							return null;
						} catch (final NumberFormatException e) {
							return null;
						}
					}
					})*/);
	}
	
	public int amount = -1;
	
	public final EntityData<?> data;
	
	public EntityType(final EntityData<?> data, final int amount) {
		Validate.notNull(data);
		this.data = data;
		this.amount = amount;
	}
	
	public EntityType(final Class<? extends Entity> c, final int amount) {
		Validate.notNull(c);
		data = EntityData.fromClass(c);
		this.amount = amount;
	}
	
	public EntityType(final Entity e) {
		data = EntityData.fromEntity(e);
	}
	
	public boolean isInstance(final Entity entity) {
		return data.isInstance(entity);
	}
	
	@Override
	public String toString() {
		return getAmount() == 1 ? data.toString() : amount + " " + Utils.toPlural(data.toString());
	}
	
	public int getAmount() {
		return amount == -1 ? 1 : amount;
	}
	
	public boolean sameType(final EntityType other) {
		if (other == null)
			return false;
		return data.equals(other.data);
	}
	
	public static EntityType parse(String s) {
		Validate.notNullOrEmpty(s, "s");
		int amount = -1;
		if (s.matches("\\d+ .+")) {
			amount = Skript.parseInt(s.split(" ", 2)[0]);
			s = s.split(" ", 2)[1];
		} else if (s.matches("(?i)an? .+")) {
			s = s.split(" ", 2)[1];
		}
//		final Pair<String, Boolean> p = Utils.getPlural(s, amount != 1 && amount != -1);
//		s = p.first;
		final EntityData<?> data = EntityData.parseWithoutAnOrAny(s);
		if (data == null)
			return null;
		return new EntityType(data, amount);
	}
	
}
