varying vec2 texCoord0;
varying mat3 tbnMatrix;
varying vec4 shadowMapCoords0;
varying vec3 worldPos0;

uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;

uniform float dispScale;
uniform float dispBias;

uniform sampler2D R_shadowMap;