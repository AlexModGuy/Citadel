package com.github.alexthe666.citadel.client.video;

import com.github.alexthe666.citadel.client.texture.VideoFrameTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.sourceforge.jaad.spi.javasound.AACAudioFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.IOUtils;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.RgbToBgr;
import org.jcodec.scale.Transform;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Video {

    public static final Logger LOGGER = LogManager.getLogger("citadel-video");

    private boolean paused;
    private boolean hasAudioLoaded;
    private boolean repeat;

    private boolean muted;

    private String url;
    private ResourceLocation resourceLocation;
    private VideoFrameTexture texture;

    private FrameGrab frameGrabber = null;
    private FrameGrab prevFrameGrabber = null;
    private File mp4FileOnDisk = null;
    private double framesPerSecond;
    private long startTime = -1;
    private int lastFrame = -1;
    private long pausedAudioTime = 0;
    private Clip audioClip;

    public Video(String url, ResourceLocation resourceLocation, VideoFrameTexture texture, double framesPerSecond, boolean muted) {
        this.url = url;
        this.resourceLocation = resourceLocation;
        this.texture = texture;
        this.framesPerSecond = framesPerSecond;
        this.muted = muted;
        setupFrameGrabber();
    }

    public void update() {

        if (frameGrabber != null) {
            if(prevFrameGrabber == null){
                onStart();
            }
            long milliseconds = System.currentTimeMillis() - startTime;
            int frame = (int) (milliseconds / 1000D * framesPerSecond);
            pausedAudioTime = milliseconds * 1000;
            if(lastFrame == frame || this.paused){
                return;
            }else{
                lastFrame = frame;
            }
            try {
                Picture picture = frameGrabber.getNativeFrame();
                if (picture != null) {
                    BufferedImage bufferedImage = toBufferedImage(picture);
                    texture.setPixelsFromBufferedImage(bufferedImage);
                } else if(repeat){
                    frameGrabber.seekToFramePrecise(0);
                    if(audioClip != null && !this.muted){
                        audioClip.loop(-1);
                        audioClip.setFramePosition(0);
                    }
                    startTime = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        prevFrameGrabber = frameGrabber;
    }

    public void onStart(){
        startTime = System.currentTimeMillis();
    }

    private void setupFrameGrabber() {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(() -> {
            try {
                InputStream in = new URL(url).openStream();
                Path path = Paths.get(getVideoCacheFolder().toString(), resourceLocation.getPath());
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                in.close();
                mp4FileOnDisk = path.toFile();
                frameGrabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(mp4FileOnDisk));
                LOGGER.info("loaded mp4 video from " + url);
                if(!this.muted){
                    setupAudio(mp4FileOnDisk, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void setupAudio(File mp4File, long time) {
        AACAudioFileReader aacAudioFileReader = new AACAudioFileReader();
        try {
            AudioInputStream audioInputStream = aacAudioFileReader.getAudioInputStream(mp4File);
            audioClip = AudioSystem.getClip();

            audioClip.open(audioInputStream);

            audioClip.setMicrosecondPosition(time);
            audioClip.start();
            if(!hasAudioLoaded){
                LOGGER.info("loaded mp4 audio from " + url);
            }
            hasAudioLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if(audioClip != null && hasAudioLoaded){
            if(paused || this.muted){
                if(audioClip.isOpen()){
                    audioClip.close();
                }
            }else{
                if(!audioClip.isOpen()){
                    setupAudio(mp4FileOnDisk, pausedAudioTime);
                }
            }
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public double getFramesPerSecond() {
        return framesPerSecond;
    }

    public void setFramesPerSecond(double framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public File getMp4FileOnDisk() {
        return mp4FileOnDisk;
    }

    public int getLastFrame() {
        return lastFrame;
    }

    private static Path getVideoCacheFolder() {
        Path configPath = FMLPaths.GAMEDIR.get();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "citadel/video_cache");
        if (!Files.exists(jsonPath)) {
            try {
                IOUtils.forceMkdir(jsonPath.toFile());
            } catch (Exception e) {
            }
        }
        return jsonPath;
    }

    private static BufferedImage toBufferedImage(Picture src) {
        if (src.getColor() != ColorSpace.BGR) {
            Picture bgr = Picture.createCropped(src.getWidth(), src.getHeight(), ColorSpace.BGR, src.getCrop());
            if (src.getColor() == ColorSpace.RGB) {
                new RgbToBgr().transform(src, bgr);
            } else {
                Transform transform = ColorUtil.getTransform(src.getColor(), ColorSpace.RGB);
                transform.transform(src, bgr);
                new RgbToBgr().transform(bgr, bgr);
            }
            src = bgr;
        }
        BufferedImage dst = new BufferedImage(src.getCroppedWidth(), src.getCroppedHeight(),
                BufferedImage.TYPE_3BYTE_BGR);

        if (src.getCrop() == null)
            toBufferedImage2(src, dst);
        else
            toBufferedImageCropped(src, dst);

        return dst;
    }

    private static void toBufferedImageCropped(Picture src, BufferedImage dst) {
        byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
        byte[] srcData = src.getPlaneData(0);
        int dstStride = dst.getWidth() * 3;
        int srcStride = src.getWidth() * 3;
        for (int line = 0, srcOff = 0, dstOff = 0; line < dst.getHeight(); line++) {
            for (int id = dstOff, is = srcOff; id < dstOff + dstStride; id += 3, is += 3) {
                data[id] = (byte) (srcData[is] + 128);
                data[id + 1] = (byte) (srcData[is + 1] + 128);
                data[id + 2] = (byte) (srcData[is + 2] + 128);
            }
            srcOff += srcStride;
            dstOff += dstStride;
        }
    }

    private static void toBufferedImage2(Picture src, BufferedImage dst) {
        byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
        byte[] srcData = src.getPlaneData(0);
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (srcData[i] + 128);
        }
    }
}
