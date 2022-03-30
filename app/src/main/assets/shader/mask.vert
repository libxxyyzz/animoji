#version 300 es
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_coord;

uniform mat4 mvpMatrix;
out vec2 frag_coord;

void main() {
    vec4 temp = mvpMatrix * vec4(in_position, 1.0);
    gl_Position =  temp;
    frag_coord = in_coord;
}