package com.green.energy.tracker.user_management.unit.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.keycloak.*;
import com.green.energy.tracker.user_management.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.InvocationTargetException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakUtilTest {

    private static final String REPRESENTATION_TEST_OK = "{\"id\":\"1c8a9b38-a4a0-4640-a5fa-f2391fef9008\",\"username\":\"e3630949\"," +
            "\"firstName\":\"Pluto\",\"lastName\":\"Zemi\",\"email\":\"test22@observa.cloud\",\"emailVerified\":false,\"attributes\":{}," +
            "\"createdTimestamp\":1747644751736,\"enabled\":true,\"totp\":false,\"disableableCredentialTypes\":[],\"requiredActions\":[]," +
            "\"notBefore\":0,\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";

    private static final String REPRESENTATION_TEST_NOK = "\"firstName\":\"Pluto\",\"lastName\":\"Zemi\",\"email\":\"test22@observa.cloud\",\"" +
            "emailVerified\":false,\"attributes\":{},\"createdTimestamp\":1747644751736,\"enabled\":true,\"totp\":false,\"disableableCredentialTypes\":[]," +
            "\"requiredActions\":[],\"notBefore\":0,\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";

    @Mock
    private KeycloakEvent keycloakEvent;

    @Test
    void testIsEventPermittedWithCreateOperationType() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.CREATE.name());
        assertTrue(KeycloakUtil.isEventPermitted(keycloakEvent));
    }

    @Test
    void testIsEventPermittedWithUpdateOperationType() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.UPDATE.name());
        assertTrue(KeycloakUtil.isEventPermitted(keycloakEvent));
    }

    @Test
    void testIsEventPermittedWithDeleteOperationType() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.DELETE.name());
        assertTrue(KeycloakUtil.isEventPermitted(keycloakEvent));
    }

    @Test
    void testIsEventPermittedWithInvalidResourceType() {
        when(keycloakEvent.getResourceType()).thenReturn("UNKNOWN");
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.DELETE.name());
        assertFalse(KeycloakUtil.isEventPermitted(keycloakEvent));
    }

    @Test
    void testIsEventPermittedWithInvalidOperationType() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn("UNKNOWN");
        assertFalse(KeycloakUtil.isEventPermitted(keycloakEvent));
    }

    @Test
    void testGetUserEventReturnCreateUserEvent() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.CREATE.name());
        var userEvent = KeycloakUtil.getUserEvent(keycloakEvent);
        assertTrue(userEvent.isPresent());
        assertEquals(UserEvent.CREATE, userEvent.get());
    }

    @Test
    void testGetUserEventReturnUpdateUserEvent() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.UPDATE.name());
        var userEvent = KeycloakUtil.getUserEvent(keycloakEvent);
        assertTrue(userEvent.isPresent());
        assertEquals(UserEvent.UPDATE, userEvent.get());
    }

    @Test
    void testGetUserEventReturnDeleteUserEvent() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.DELETE.name());
        var userEvent = KeycloakUtil.getUserEvent(keycloakEvent);
        assertTrue(userEvent.isPresent());
        assertEquals(UserEvent.DELETE, userEvent.get());
    }

    @Test
    void testGetUserEventReturnEmpty() {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn("UNKNOWN");
        var userEvent = KeycloakUtil.getUserEvent(keycloakEvent);
        assertTrue(userEvent.isEmpty());
    }

    @Test
    void testGetUserWithEventPermittedCreateReturnUser() throws Exception {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.CREATE.name());
        when(keycloakEvent.getRepresentation()).thenReturn(REPRESENTATION_TEST_OK);
        var result = KeycloakUtil.getUser(keycloakEvent);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserWithEventPermittedUpdateReturnUser() throws Exception {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.UPDATE.name());
        when(keycloakEvent.getRepresentation()).thenReturn(REPRESENTATION_TEST_OK);
        var result = KeycloakUtil.getUser(keycloakEvent);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserWithEventPermittedDeleteReturnUser() throws Exception {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.DELETE.name());
        when(keycloakEvent.getRepresentation()).thenReturn(REPRESENTATION_TEST_OK);
        var result = KeycloakUtil.getUser(keycloakEvent);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserWithEventNotPermittedReturnEmpty() throws Exception {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn("UNKNOWN");
        var result = KeycloakUtil.getUser(keycloakEvent);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserWithEventPermittedCreateRepresentationNokJsonProcessingException() throws Exception {
        when(keycloakEvent.getResourceType()).thenReturn(User.class.getSimpleName());
        when(keycloakEvent.getOperationType()).thenReturn(UserEvent.CREATE.name());
        when(keycloakEvent.getRepresentation()).thenReturn(REPRESENTATION_TEST_NOK);
        assertThrows(JsonProcessingException.class,()->KeycloakUtil.getUser(keycloakEvent));
    }

    @Test
    void constructorThrowExceptionWhenCalled() throws Exception {
        var constructor = KeycloakUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(IllegalStateException.class, exception.getCause());
    }

}
