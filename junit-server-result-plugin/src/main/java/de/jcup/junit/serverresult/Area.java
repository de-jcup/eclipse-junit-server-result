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

public class Area {
    
    public Area() {
         this(0,0,0,0);
    }    

    public Area(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public int x;
    public int y;
    
    public int width;
    public int height;
    
    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("x=");
       sb.append(x);
       sb.append(",y=");
       sb.append(y);
       sb.append(",width=");
       sb.append(width);
       sb.append(",height=");
       sb.append(height);
       
       return sb.toString();
    }
}
