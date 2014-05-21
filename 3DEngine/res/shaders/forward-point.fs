#version 120
#include "lighting.glh"
#include "sampling.glh"

varying vec2 texCoord0;
varying mat3 tbnMatrix;
varying vec3 worldPos0;

uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;

uniform PointLight R_pointLight;

uniform float dispScale;
uniform float dispBias;

void main() {
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
    vec2 texCoords = calcTexCoordDisplacement(dispMap, tbnMatrix, directionToEye, texCoord0, dispScale, dispBias);
    vec3 normal = normalize(tbnMatrix * (2. * (texture2D(normalMap, texCoords.xy).xyz) - 1.));

	gl_FragColor = texture2D(diffuse, texCoords.xy) * calcPointLight(R_pointLight, normal, worldPos0);
}
