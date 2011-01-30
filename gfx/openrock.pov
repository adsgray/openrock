/*
 * Copyright (C) 2009  Daniel Nilsson
 * 
 * This file is part of OpenRock.
 *
 * OpenRock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenRock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenRock.  If not, see <http://www.gnu.org/licenses/>.
 */
#version 3.6
#include "colors.inc"

global_settings {
    assumed_gamma 1.0
    ambient_light 0.0
    max_trace_level 5
}

camera {
    location <0, 130, 400>
    up <0, 1, 0>
    right <320/200, 0, 0>
    look_at <0, 0, 50>
    angle 20
}

#declare X = -500;
#while(X <= 500)
    #declare Z = -500;
    #while(Z <= 35000)
	light_source { <X, 5000, Z>, 2*White
	    fade_power 2
	    fade_distance 1000
	}
	#declare Z = Z + 1000;
    #end
    #declare X = X + 1000;
#end

#declare P_Granite = pigment {
    granite 
    scale 1/12
    color_map {
	[ 0.0 rgb 0.0 ]
       	[ 0.1 rgb 0.1 ]
	[ 0.7 rgb 0.5 ]
	[ 0.8 rgb 0.9 ]
	[ 1.0 rgb 1.0 ]
    }
}

#declare T_PolishedStone = texture {
    pigment { P_Granite }
    finish {
	diffuse 0.2
	specular 2.0
	roughness 0.001
	reflection { 0.01, 0.02 }
    }
}
#declare T_RoughStone = texture {
    pigment { P_Granite }
    finish {
	diffuse 0.6
	specular 0.3
	roughness 0.01
    }
}

#macro Stone(X,Z,Rot,Color)
union {
    union {
	torus { 8, 6 scale <1,5/6,1> translate y*7 }
	torus { 8, 6 scale <1,5/6,1> translate y*5 }
	cylinder { <0, 5, 0>, <0, 7, 0>, 14 }
	texture {
	    gradient y
	    texture_map {
		[ 0.35 T_PolishedStone ]
		[ 0.37 T_RoughStone ]
		[ 0.63 T_RoughStone ]
		[ 0.65 T_PolishedStone ]
	    }
	    scale 12
	}
    }
    lathe { cubic_spline 6
	<-1, 12.5>, <0, 12.5>, <5, 13>, <10, 12>, <10, 11.5>, <9.5, 11.5>
	pigment { Color }
	finish {
	    specular 0.6
	    roughness 0.02
	}
    }
    union {
	intersection {
	    torus { 3, 1
		rotate z*90
		translate <0, 15, -3>
	    }
	    plane { z, -3 }
	}
	cylinder { <0, 18, -3>, <0, 18, 5>, 1 }
	pigment { rgb 0.05 }
	finish {
	    specular 5.0
	    roughness 0.001
	    reflection 0.95
	}
    }
    rotate y*Rot
    translate <X, 0, Z>
}
#end

Stone(-13, 15, 133, rgb <0.95, 0.07, 0.02>)
Stone(42, -20, 234, rgb <0.80, 0.76, 0.02>)

plane { y, 0
    pigment {
	onion
	scale 30*10
	color_map {
	    [ 0.050 White ]
	    [ 0.051 Blue ]
	    [ 0.200 Blue ]
	    [ 0.201 White ]
	    [ 0.400 White ]
	    [ 0.401 Red ]
	    [ 0.600 Red ]
	    [ 0.601 White ]
	}
    }
    translate <0, -2, 0>
}

plane { y, 0
    pigment {
	bumps
	scale 0.5
	color_map {
	    [ 0.0 rgbf <1, 1, 1, 0.9> ]
	    [ 0.5 rgbf <1, 1, 1, 0.7> ]
	    [ 1.0 rgbf <1, 1, 1, 0.3> ]
	}
    }
    finish {
	specular 0.6
	roughness 0.001
	reflection { 0.02 1.0
	    fresnel on
	}
	conserve_energy
    }
    normal {
	bumps 0.3
	scale 0.5
    }
    interior {
	ior 1.31
    }
}

text { ttf "timrom.ttf" "OpenRock", 0.5, <0, 0, 0>
    rotate x*90
    rotate y*180
    scale <25, 1, 25>
    translate <55, -1.5, 90>
}

text { ttf "timrom.ttf" "1.0", 0.5, <0, 0, 0>
    rotate x*90
    rotate y*180
    scale <15, 1, 15>
    translate <7, -1.5, 110>
}
