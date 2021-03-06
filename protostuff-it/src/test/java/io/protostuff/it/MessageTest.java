package io.protostuff.it;

import io.protostuff.it.message_test.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.protostuff.it.message_test.TestOneof.OneofNameCase.FOO_INT;
import static io.protostuff.it.message_test.TestOneof.OneofNameCase.ONEOF_NAME_NOT_SET;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kostiantyn Shchepanovskyi
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class MessageTest {

    public static final SimpleMessage A = SimpleMessage.newBuilder()
            .setInt32(42)
            .setString("abra")
            .build();
    public static final SimpleMessage A_COPY = SimpleMessage.newBuilder()
            .setInt32(42)
            .setString("abra")
            .build();
    public static final SimpleMessage B = SimpleMessage.newBuilder()
            .setInt32(43)
            .setString("cadabra")
            .build();
    public static final TestMap TEST_MAP = TestMap.newBuilder()
            .putMapStringString("key", "value")
            .putMapStringString("test", "test")
            .build();

    @Test
    public void createdInstance() throws Exception {
        ParentMsg instance = ParentMsg.newBuilder()
                .setNestedMsg(NestedMsg.newBuilder()
                        .setName("1")
                        .build())
                .addNestedRepeatedMsg(NestedMsg.newBuilder()
                        .setName("2")
                        .build())
                .build();
        assertTrue(instance.hasNestedMsg());
        assertEquals("1", instance.getNestedMsg().getName());
        assertEquals(1, instance.getNestedRepeatedMsgCount());
        assertEquals("2", instance.getNestedRepeatedMsg(0).getName());
    }

    @Test
    public void builder_mergeFrom() throws Exception {
        ParentMsg source = ParentMsg.newBuilder()
                .setNestedMsg(NestedMsg.newBuilder()
                        .setName("1")
                        .build())
                .addNestedRepeatedMsg(NestedMsg.newBuilder()
                        .setName("2")
                        .build())
                .build();
        ParentMsg instance = ParentMsg.newBuilder()
                .mergeFrom(source)
                .build();
        assertTrue(instance.hasNestedMsg());
        assertEquals("1", instance.getNestedMsg().getName());
        assertEquals(1, instance.getNestedRepeatedMsgCount());
        assertEquals("2", instance.getNestedRepeatedMsg(0).getName());
    }

    @Test
    public void clearScalar() throws Exception {
        SimpleMessage.Builder buider = SimpleMessage.newBuilder()
                .setInt32(10);
        assertTrue(buider.hasInt32());
        assertEquals(10, buider.getInt32());
        buider.clearInt32();
        assertFalse(buider.hasInt32());
        assertEquals(0, buider.getInt32());
    }

    @Test
    public void clearRepeatedScalar() throws Exception {
        SimpleMessage.Builder buider = SimpleMessage.newBuilder()
                .addRepeatedInt32(10);
        assertEquals(1, buider.getRepeatedInt32Count());
        assertEquals(10, buider.getRepeatedInt32(0));
        buider.clearRepeatedInt32();
        assertEquals(0, buider.getRepeatedInt32Count());
    }

    @Test
    public void clearMap() throws Exception {
        TestMap.Builder buider = TestMap.newBuilder()
                .putMapInt32Int32(10, 10);
        assertEquals(1, buider.getMapInt32Int32Count());
        assertEquals(10, buider.getMapInt32Int32(10).intValue());
        buider.clearMapInt32Int32();
        assertEquals(0, buider.getMapInt32Int32Count());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(A, A_COPY);
        assertNotEquals(A, B);
    }

    @Test
    public void testEquals_Builder() throws Exception {
        SimpleMessage.Builder a = SimpleMessage.newBuilder()
                .setInt32(42)
                .setString("abra");
        SimpleMessage.Builder copy = SimpleMessage.newBuilder()
                .setInt32(42)
                .setString("abra");
        SimpleMessage.Builder b = SimpleMessage.newBuilder()
                .setInt32(43)
                .setString("cadabra");
        assertEquals(a, copy);
        assertNotEquals(a, b);
    }


    @Test
    public void testHashCode() throws Exception {
        assertEquals(A.hashCode(), A_COPY.hashCode());
        assertNotEquals(A.hashCode(), B.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("SimpleMessage{int32=42, string=abra}", A.toString());
    }

    @Test
    public void testToString_integer_field() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder()
                .setInt32(15)
                .build();
        assertEquals("SimpleMessage{int32=15}", message.toString());
    }

    @Test
    public void testToString_string_field() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder()
                .setString("test")
                .build();
        assertEquals("SimpleMessage{string=test}", message.toString());
    }

    @Test
    public void testToString_message_field() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder()
                .setMessage(TestMessage.newBuilder()
                        .setA(123)
                        .build())
                .build();
        assertEquals("SimpleMessage{message=TestMessage{a=123}}", message.toString());
    }

    @Test
    public void testToString_repeated_string_field() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder()
                .addRepeatedString("test1")
                .addRepeatedString("test2")
                .build();
        assertEquals("SimpleMessage{repeatedString=[test1, test2]}", message.toString());
    }

    @Test
    public void testToString_repeated_int32_field() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder()
                .addRepeatedInt32(41)
                .addRepeatedInt32(42)
                .build();
        assertEquals("SimpleMessage{repeatedInt32=[41, 42]}", message.toString());
    }

    @Test
    public void testToString_MessageWithoutFields() throws Exception {
        MessageWithoutFields message = MessageWithoutFields.newBuilder().build();
        assertEquals("MessageWithoutFields{}", message.toString());
    }

    @Test
    public void testMap_getter_map() throws Exception {
        Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");
        expected.put("test", "test");
        assertEquals(expected, TEST_MAP.getMapStringStringMap());
    }

    @Test
    public void testMap_getter_single() throws Exception {
        assertEquals("value", TEST_MAP.getMapStringString("key"));
    }

    @Test
    public void testMap_builder_mergeFrom() throws Exception {
        assertEquals("value", TestMap.newBuilder()
                .mergeFrom(TEST_MAP)
                .getMapStringString("key"));
    }

    @Test
    public void testMap_getter_count() throws Exception {
        assertEquals(2, TEST_MAP.getMapStringStringCount());
    }

    @Test
    public void testMap_setter_map() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("test", "test");
        TestMap instance = TestMap.newBuilder()
                .putAllMapStringString(map)
                .build();
        assertEquals(map, instance.getMapStringStringMap());
    }

    @Test
    public void testOneof_default_value() throws Exception {
        TestOneof testOneof = TestOneof.newBuilder().build();
        assertEquals(ONEOF_NAME_NOT_SET, testOneof.getOneofNameCase());
        assertFalse(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(0, testOneof.getFooInt());
        assertEquals("", testOneof.getFooString());
        assertSame(SimpleMessage.getDefaultInstance(), testOneof.getSimpleMessage());
    }

    @Test
    public void testOneof_set_value() throws Exception {
        TestOneof testOneof = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        assertEquals(FOO_INT, testOneof.getOneofNameCase());
        assertTrue(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(42, testOneof.getFooInt());
        assertEquals("", testOneof.getFooString());
    }

    @Test
    public void testOneof_builder() throws Exception {
        TestOneof source = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        TestOneof.Builder testOneof = TestOneof.newBuilder()
                .mergeFrom(source);
        assertEquals(FOO_INT, testOneof.getOneofNameCase());
        assertTrue(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(42, testOneof.getFooInt());
        assertEquals("", testOneof.getFooString());
    }

    @Test
    public void testOneof_builder_mergeFrom() throws Exception {
        TestOneof.Builder testOneof = TestOneof.newBuilder()
                .setFooInt(42);
        assertEquals(FOO_INT, testOneof.getOneofNameCase());
        assertTrue(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(42, testOneof.getFooInt());
        assertEquals("", testOneof.getFooString());
    }

    @Test
    public void testOneof_builder_clear() throws Exception {
        TestOneof.Builder testOneof = TestOneof.newBuilder()
                .setFooInt(42)
                .clearOneofName();
        assertEquals(ONEOF_NAME_NOT_SET, testOneof.getOneofNameCase());
        assertFalse(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(0, testOneof.getFooInt());
        assertEquals("", testOneof.getFooString());
    }

    @Test
    public void testOneof_equals() throws Exception {
        TestOneof a1 = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        TestOneof a2 = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        TestOneof b = TestOneof.newBuilder()
                .setFooString("test")
                .build();
        assertEquals(a1, a2);
        assertNotEquals(a1, b);
    }

    @Test
    public void testOneof_hashCode() throws Exception {
        TestOneof a1 = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        TestOneof a2 = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        TestOneof b = TestOneof.newBuilder()
                .setFooString("test")
                .build();
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), b.hashCode());
    }

    @Test
    public void testOneof_toString() throws Exception {
        TestOneof a1 = TestOneof.newBuilder()
                .setFooInt(42)
                .build();
        assertEquals("TestOneof{fooInt=42}", a1.toString());
    }

    @Test
    public void testFieldNamedClass() throws Exception {
        TestFieldNamedClass message = TestFieldNamedClass.newBuilder()
                .setClass(42)
                .build();
        assertEquals(42, message.getClass_());
    }

    @Test
    public void setStringFieldToNull() throws Exception {
        String s = "Cannot set SimpleMessage#string to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            SimpleMessage.newBuilder()
                    .setString(null);
        });
        assertEquals(s, exception.getMessage());

    }

    @Test
    public void setMessageFieldToNull() throws Exception {
        String s = "Cannot set SimpleMessage#message to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            SimpleMessage.newBuilder()
                    .setMessage(null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void setEnumFieldToNull() throws Exception {
        String s = "Cannot set SimpleMessage#enum_ to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            SimpleMessage.newBuilder()
                    .setEnum(null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void addRepeatedStringFieldNull() throws Exception {
        String s = "Cannot set SimpleMessage#repeatedString to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            String nullString = null;
            SimpleMessage.newBuilder()
                    .addRepeatedString(nullString);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void setRepeatedStringFieldNull() throws Exception {
        String s = "Cannot set SimpleMessage#repeatedString to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            SimpleMessage.newBuilder()
                    .addAllRepeatedString(null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void setOneofStringToNull() throws Exception {
        String s = "Cannot set TestOneof#fooString to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            TestOneof.newBuilder()
                    .setFooString(null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void setMapValueNull() throws Exception {
        String s = "Cannot set TestMap#mapBoolBool - map value is null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            TestMap.newBuilder()
                    .putMapBoolBool(true, null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void mapListGetter_returnImmutableMap() throws Exception {
        expectThrows(UnsupportedOperationException.class, () -> {
            TEST_MAP.getMapStringStringMap().put("1", "2");
        });
    }

    @Test
    public void setMapKeyNull() throws Exception {
        String s = "Cannot set TestMap#mapBoolBool - map key is null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            TestMap.newBuilder()
                    .putMapBoolBool(null, true);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void setMapFieldNull() throws Exception {
        String s = "Cannot set TestMap#mapBoolBool to null";
        NullPointerException exception = expectThrows(NullPointerException.class, () -> {
            TestMap.newBuilder()
                    .putAllMapBoolBool(null);
        });
        assertEquals(s, exception.getMessage());
    }

    @Test
    public void defaultMessageFieldValue() throws Exception {
        SimpleMessage message = SimpleMessage.newBuilder().build();
        assertSame(TestMessage.getDefaultInstance(), message.getMessage());
    }
}
