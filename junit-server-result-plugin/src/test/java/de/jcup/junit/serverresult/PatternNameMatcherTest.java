/*
 * Copyright 2020 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.junit.serverresult;

import static org.junit.Assert.*;

import org.junit.Test;

public class PatternNameMatcherTest {

    @Test
    public void pattern_null_always_true() {
        assertTrue(PatternNameMatcher.patternMatchesName(null,"ello"));
        assertTrue(PatternNameMatcher.patternMatchesName(null,""));
        assertTrue(PatternNameMatcher.patternMatchesName(null,null));
    }
    
    @Test
    public void pattern_empty_always_true() {
        assertTrue(PatternNameMatcher.patternMatchesName("","ello"));
        assertTrue(PatternNameMatcher.patternMatchesName("",""));
        assertTrue(PatternNameMatcher.patternMatchesName("",null));
    }
    
    @Test
    public void pattern_for_straight_text() {
        /* part1: matching */
        assertTrue(PatternNameMatcher.patternMatchesName("ell","hello"));
        assertTrue(PatternNameMatcher.patternMatchesName("hello","chello"));
        assertTrue(PatternNameMatcher.patternMatchesName("hello","chelloline"));
        assertTrue(PatternNameMatcher.patternMatchesName("A","a"));
        assertTrue(PatternNameMatcher.patternMatchesName("a","A"));
        assertTrue(PatternNameMatcher.patternMatchesName("h","hello"));
        assertTrue(PatternNameMatcher.patternMatchesName("e","hello"));
        assertTrue(PatternNameMatcher.patternMatchesName("l","hello"));
        assertTrue(PatternNameMatcher.patternMatchesName("llo","hello"));
        
        /* part2: NOT matching */
        assertFalse(PatternNameMatcher.patternMatchesName("Ab","Ac"));
        assertFalse(PatternNameMatcher.patternMatchesName("elo","hello"));
        assertFalse(PatternNameMatcher.patternMatchesName("chelxo","hello"));
        assertFalse(PatternNameMatcher.patternMatchesName("bello","hello"));
    }
    
    @Test
    public void pattern_with_asterisks() {
        /* part1: matching */
        assertTrue(PatternNameMatcher.patternMatchesName("h*ell","hello"));
        
        /* part2: NOT matching */
        assertFalse(PatternNameMatcher.patternMatchesName("a*ell","hello"));
    }

}
