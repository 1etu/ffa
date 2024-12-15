package net.hywave.inventory;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeObject(itemStack);
            
            dataOutput.close();
            return new JsonPrimitive(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(json.getAsString()));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            
            ItemStack itemStack = (ItemStack) dataInput.readObject();
            
            dataInput.close();
            return itemStack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
