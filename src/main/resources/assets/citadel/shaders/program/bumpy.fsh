#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform vec2 OutSize;
uniform float Time;

out vec4 fragColor;

void main() {
    // Create a wavy/distortion effect
    vec2 offset = vec2(0.0);
    
    // Add sine-based distortion for the "bumpy" shimmer effect
    float frequency = 10.0;
    float amplitude = 2.0;
    
    offset.x = sin(texCoord.y * frequency + Time * 0.5) * oneTexel.x * amplitude;
    offset.y = cos(texCoord.x * frequency + Time * 0.7) * oneTexel.y * amplitude;
    
    vec4 color = texture(DiffuseSampler, texCoord + offset);
    
    fragColor = color;
}
