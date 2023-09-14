package de.paul2708.worm.attributes;

import de.paul2708.worm.attributes.util.AttributeNameTransformer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeNameTransformerTest {

    @ParameterizedTest
    @MethodSource("provideStrings")
    void test(String input, String transformed) {
        assertEquals(transformed, AttributeNameTransformer.transform(input));
    }

    private static Stream<Arguments> provideStrings() {
        return Stream.of(
                Arguments.of("name", "Name"),
                Arguments.of("person_id", "PersonId"),
                Arguments.of("person-id", "PersonId"),
                Arguments.of("NAME", "Name"),
                Arguments.of("nAmE", "Name"),
                Arguments.of("PeRsOn-Id", "PersonId"),
                Arguments.of("PERSON-ID", "PersonId")
        );
    }
}
