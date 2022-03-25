#version 300 es
layout(location = 0) in vec2 in_position;

void main() {

    vec4 temp = vec4(in_position.x, 1.0-in_position.y, 0.0,1.0);
    temp = vec4(temp.xy * 2.0 - 1.0, temp.zw);


    gl_Position = temp;
    gl_PointSize = 2.0;
}