#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform vec4 ColorModulate;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    fragColor = color * ColorModulate;
}
