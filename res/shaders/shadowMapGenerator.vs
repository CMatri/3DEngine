#version 120

attribute vec3 position;

uniform mat4 T_MVP;

void main() {
	gl_Position = T_MVP * vec4(position.xyz, 1.0);
}
