#version 300 es
layout(location = 0) in vec2 in_position;
uniform mat4 mvpTransform;
uniform mat4 texTransform;
out vec2 frag_coord;
void main() {
    vec2 in_coord = clamp(in_position,vec2(0.0),vec2(1.0));
    gl_Position = mvpTransform * vec4(in_position,0.0,1.0);
    frag_coord = (texTransform * vec4(in_coord,0.0,1.0)).xy;
    gl_PointSize = 10.0;
}