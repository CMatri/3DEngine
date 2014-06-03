#version 120
#include "lighting.glh"
#include "sampling.glh"
#include "lighting.fsh"

uniform DirectionalLight R_directionalLight;

float calcShadowMapEffect(sampler2D shadowMap, vec4 initialShadowMapCoords) {
	vec3 shadowMapCoords = (initialShadowMapCoords.xyz / initialShadowMapCoords.w);

	return sampleVarianceShadowMap(shadowMap, shadowMapCoords.xy, initialShadowMapCoords.z);
}

void main() {
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
    vec2 texCoords = calcTexCoordDisplacement(dispMap, tbnMatrix, directionToEye, texCoord0, dispScale, dispBias);
    vec3 normal = normalize(tbnMatrix * (2. * (texture2D(normalMap, texCoords.xy).xyz) - 1.));

    gl_FragColor = texture2D(diffuse, texCoords.xy) * calcDirectionalLight(R_directionalLight, normal, worldPos0) * calcShadowMapEffect(R_shadowMap, shadowMapCoords0);
}