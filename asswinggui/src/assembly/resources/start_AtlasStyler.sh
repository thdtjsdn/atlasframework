#!/bin/bash
# Copyright 2010 Stefan Alfons Tzeggai
# atlas-framework - This file is part of the Atlas Framework
# This library is free software; you can redistribute it and/or modify it under the terms of the GNU  Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the  License, or (at your option) any later version.
# This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser  General Public License for more details.
#  You should have received a copy of the GNU Lesser General Public License along with this library;  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
echo "Starting AtlasStyler..."
java -Xmx160m -Dfile.encoding=UTF8 -Djava.library.path=. -cp ~/.Geopublishing:asswinggui-${project.version}.jar org.geopublishing.atlasStyler.swing.AtlasStylerGUI ${1} ${2} ${3} ${4} ${5} ${6}