#version 300 es
layout(location = 0) in vec2 in_position;

uniform mat4 texTransform;

void main() {
    // origin landmark
    //   0,0 ____ 1,0
    //      |    |
    //      |____|
    //   0,1      1,1

    // 1.0 - y

    // texture
    //   0,1 ____ 1,1
    //      |    |
    //      |____|
    //   0,0      1,0

    // after with texture matrix , y turns to x
    // 1.0 - y to origin coordinate

    // * 2.0 - 1.0
    //   0,1 ____ 1,1
    //      |    |
    //      |____|
    //   0,0      1,0

    vec4 temp = texTransform*vec4(in_position.x, 1.0-in_position.y, 0.0, 1.0);
    temp = vec4(temp.x, 1.0-temp.y, temp.zw);
    temp = vec4(temp.xy * 2.0 - 1.0, temp.zw);


    gl_Position = temp;
    gl_PointSize = 10.0;

}