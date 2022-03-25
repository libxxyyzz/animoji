#version 300 es
layout(location = 0) in vec4 in_position;
uniform mat4 mvpTransform;
uniform mat4 texTransform;
out vec2 frag_coord;
void main() {
    vec4 in_coord = clamp(in_position,vec4(0.0),vec4(1.0));
    gl_Position = mvpTransform * in_position;
    frag_coord = (texTransform * in_coord).xy;
    gl_PointSize = 10.0;
}