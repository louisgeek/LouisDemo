package com.louis.mycomposeproject.util

import org.junit.Assert.*
import org.junit.Test

class ValidatorTest {
    
    @Test
    fun `valid email returns true`() {
        assertTrue(Validator.isValidEmail("test@example.com"))
        assertTrue(Validator.isValidEmail("user.name@domain.co.uk"))
    }
    
    @Test
    fun `invalid email returns false`() {
        assertFalse(Validator.isValidEmail(""))
        assertFalse(Validator.isValidEmail("invalid"))
        assertFalse(Validator.isValidEmail("@example.com"))
        assertFalse(Validator.isValidEmail("test@"))
    }
    
    @Test
    fun `valid password returns true`() {
        assertTrue(Validator.isValidPassword("123456"))
        assertTrue(Validator.isValidPassword("password123"))
    }
    
    @Test
    fun `invalid password returns false`() {
        assertFalse(Validator.isValidPassword(""))
        assertFalse(Validator.isValidPassword("12345"))
    }
    
    @Test
    fun `valid name returns true`() {
        assertTrue(Validator.isValidName("John"))
        assertTrue(Validator.isValidName("John Doe"))
    }
    
    @Test
    fun `invalid name returns false`() {
        assertFalse(Validator.isValidName(""))
        assertFalse(Validator.isValidName(" "))
        assertFalse(Validator.isValidName("J"))
    }
}
