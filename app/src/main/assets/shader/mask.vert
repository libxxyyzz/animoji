#version 300 es
layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_coord;

out vec2 frag_coord;

void main() {
    gl_Position = vec4(in_position,1.0);
    frag_coord = in_coord;
}