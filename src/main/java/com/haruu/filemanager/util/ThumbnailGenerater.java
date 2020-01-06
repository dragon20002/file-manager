package com.haruu.filemanager.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbnailGenerater {
	private static Logger log = LoggerFactory.getLogger(ThumbnailGenerater.class);

	public static File generateThumbnail(String videoFileName, File thumbnailFile) {
	
		int frameNumber = 0;
		try {
			SeekableByteChannel byteChannel = NIOUtils.readableFileChannel(videoFileName);
			DemuxerTrack track = MP4Demuxer.createMP4Demuxer(byteChannel)
					.getVideoTrack();

			frameNumber = (int) (track.getMeta().getTotalDuration() / 5.);
			log.debug("total duration = {}", track.getMeta().getTotalDuration());
			log.debug("frame number = {}", frameNumber);

			Picture frame = FrameGrab.getFrameFromChannel(byteChannel, frameNumber);
			BufferedImage image = AWTUtil.toBufferedImage(frame);

			thumbnailFile.createNewFile();
			ImageIO.write(image, "png", thumbnailFile);

		} catch (IOException e) {
			log.debug("error = {}", e.getMessage());
		} catch (JCodecException e) {
			log.debug("error = {}", e.getMessage());
		}

		return thumbnailFile;
	}

}
