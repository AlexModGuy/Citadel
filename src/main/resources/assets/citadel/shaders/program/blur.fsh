#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 BlurDir;
uniform float Radius;

out vec4 fragColor;

void main() {
    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    float rad = Radius;
    
    for(float r = -rad; r <= rad; r += 1.0) {
        vec4 sampleValue = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        
        // Gaussian weighting
        float strength = 1.0 - abs(r / rad);
        totalStrength += strength;
        blurred.rgb += sampleValue.rgb * strength;
        totalAlpha += sampleValue.a;
        totalSamples += 1.0;
    }

    fragColor = vec4(blurred.rgb / totalStrength, totalAlpha / totalSamples);
}
