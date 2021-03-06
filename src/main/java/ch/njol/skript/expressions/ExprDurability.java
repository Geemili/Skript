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
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.Skript.ExpressionType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Slot;

/**
 * @author Peter Güttinger
 * 
 */
public class ExprDurability extends PropertyExpression<ItemStack, Short> {
	
	static {
		Skript.registerExpression(ExprDurability.class, Short.class, ExpressionType.PROPERTY,
				"[the] ((data|damage)[s] [value[s]]|durabilit(y|ies)) of %itemstacks%", "%itemstacks%'[s] ((data|damage)[s] [value[s]]|durabilit(y|ies))");
	}
	
	private Expression<ItemStack> types;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final int isDelayed, final ParseResult parseResult) {
		types = (Expression<ItemStack>) exprs[0];
		setExpr(types);
		return true;
	}
	
	@Override
	public Class<Short> getReturnType() {
		return Short.class;
	}
	
	@Override
	protected Short[] get(final Event e, final ItemStack[] source) {
		return get(source, new Converter<ItemStack, Short>() {
			@Override
			public Short convert(final ItemStack is) {
				return is.getDurability();
			}
		});
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "data of " + types.toString(e, debug);
	}
	
	private Expression<? extends Slot> slots = null;
	
	@Override
	public Class<?> acceptChange(final ChangeMode mode) {
		if ((slots = types.getConvertedExpression(Slot.class)) != null)
			return Integer.class;
		return null;
	}
	
	@Override
	public void change(final Event e, final Object delta, final ChangeMode mode) {
		int a = 0;
		if (mode != ChangeMode.CLEAR)
			a = (Integer) delta;
		switch (mode) {
			case REMOVE:
				a = -a;
				//$FALL-THROUGH$
			case ADD:
				for (final Slot slot : slots.getArray(e)) {
					final ItemStack item = slot.getItem();
					item.setDurability((short) (item.getDurability() + a));
					slot.setItem(item);
				}
			break;
			case CLEAR:
				a = 0;
				//$FALL-THROUGH$
			case SET:
				for (final Slot slot : slots.getArray(e)) {
					final ItemStack item = slot.getItem();
					item.setDurability((short) a);
					slot.setItem(item);
				}
			break;
		}
	}
	
}
