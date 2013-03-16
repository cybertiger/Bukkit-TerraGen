/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.terragen.nbt;

import java.lang.reflect.Array;

/**
 *
 * @author antony
 */
public final class ListTag extends Tag<Tag[]> {
    private final TagType listType;
    private final Tag[] value;

    public ListTag(String name, TagType listType, Tag[] value) {
        super(name);
        Class c = value.getClass().getComponentType();
        if (listType.getTagClass() != value.getClass().getComponentType())
            throw new IllegalArgumentException("Tag class " + value.getClass().getComponentType().getName() + " is not " + listType.getTagClass());
        this.listType = listType;
        this.value = value;
    }

    @Override
    public Tag[] getValue() {
        return value;
    }

    public TagType getListType() {
        return listType;
    }

    @Override
    public TagType getType() {
        return TagType.LIST;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(getName());
        ret.append(" = [");
        for (int i = 0; i < value.length; i++) {
            ret.append(value[i].toString());
            if (i != value.length-1) {
                ret.append(", ");
            }
        }

        ret.append(']');
        return ret.toString();
    }

}
