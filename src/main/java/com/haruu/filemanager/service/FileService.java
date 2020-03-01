package com.haruu.filemanager.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.haruu.filemanager.model.FileInfo;
import com.haruu.filemanager.util.Common;
import com.haruu.filemanager.util.SubtitleConverter;
import com.haruu.filemanager.util.ThumbnailGenerater;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

@Service
public class FileService {
	/**
	 * 경로 관련 변수명 설명
	 * 
	 * realPath    C:/.../resource-root
	 * rootDirName /files
	 * 
	 * dirPath     /some-dir/dir2
	 * realDirPath   C:/.../resource-root/files/some-dir
	 * 
	 * filePath    /some-dir/fileName.ext
	 * realFilePath   C:/.../resource-root/files/some-dir/fileName.ext
	 * 
	 */
	public static final String DIR_NAME = "files";
	public static final String SAFE_DIR_NAME = "safe";
	public static final String[] fs = { DIR_NAME, SAFE_DIR_NAME };

	@Autowired
	private ServletContext context;

	/* search */
	// 파일정보 목록 조회
	public List<FileInfo> findAll(String rootDirName, String dirPath, FileFilter fileFilter) {
		List<FileInfo> fileInfos = new ArrayList<>();

		// 파일 목록
		String realDirPath = context.getRealPath("/") + rootDirName + dirPath;
		File directory = new File(realDirPath);
		File[] files = (directory.exists()) ? directory.listFiles(fileFilter) : new File[0];

		// 파일정보 목록
		for (File file : files) {
			if (file.length() == 0 || file.isDirectory())
				continue;
			FileInfo fileInfo = Common.getFileInfo(file);
			fileInfos.add(fileInfo);
		}

		return fileInfos;
	}

	public FileInfo findOne(String rootDirName, String dirPath, String fileName) {
		// 상위 디렉터리 문자열 여부 검사
		if (Common.isUnSafe(fileName))
			return null;

		File file = new File(context.getRealPath("/") + rootDirName + dirPath + "/" + fileName);
		// 파일 유효성 검사
		if (file == null || !file.exists() || file.isDirectory())
			return null;

		return Common.getFileInfo(file);
	}

	/* create */
	/* file upload */
	public List<FileInfo> saveAll(String rootDirName, String dirPath, MultipartFile[] mpFiles) throws IOException {
		List<FileInfo> fileInfos = new ArrayList<>();

		String realDirPath = context.getRealPath("/") + rootDirName + dirPath;
		FileUtils.forceMkdir(new File(realDirPath));

		for (MultipartFile mpFile : mpFiles) {
			if (mpFile == null || mpFile.isEmpty() || Common.isUnSafe(mpFile.getOriginalFilename()))
				continue;

			// 중복확인
			FileInfo fileInfo = findOne(rootDirName, dirPath, mpFile.getOriginalFilename());
			if (fileInfo != null)
				continue;

			try {
				File file = Common.save(mpFile, realDirPath);
				fileInfo = Common.getFileInfo(file);
				fileInfos.add(fileInfo);
			} catch (IOException e) {
				continue;
			}
		}

		return fileInfos;
	}

	public ID3v2 generateMp3Id3Tag(String rootDirName, String dirPath, String mp3FileName) throws UnsupportedTagException, InvalidDataException, IOException {
		String realFilePath = context.getRealPath("/") + rootDirName + dirPath + "/" + mp3FileName;

		// 사용가능한 자막 파일 확인
		Mp3File mp3File = new Mp3File(realFilePath);
		if (mp3File == null || !mp3File.hasId3v2Tag())
			return null;
		
		ID3v2 id3 = mp3File.getId3v2Tag();
		
		byte[] albumImage = id3.getAlbumImage();
		if (albumImage != null) {
			String mimeType = id3.getAlbumImageMimeType();
			System.out.println(mimeType);
			
			int extStartIdx = mp3FileName.lastIndexOf('.');
			String noExtFileName = mp3FileName.substring(0, extStartIdx);

			String noExtRealFilePath = context.getRealPath("/") + rootDirName + dirPath + "/" + noExtFileName;

			File albumImageFile = new File(noExtRealFilePath + ".png");
			FileOutputStream writer = new FileOutputStream(albumImageFile);
			writer.write(albumImage);
			writer.close();
		}
		
		return id3;
	}

	public void generateVttSubtitle(String rootDirName, String dirPath, String videoFileName) {
		int extStartIdx = videoFileName.lastIndexOf('.');
		String noExtFileName = videoFileName.substring(0, extStartIdx);

		String noExtRealFilePath = context.getRealPath("/") + rootDirName + dirPath + "/" + noExtFileName;

		// 사용가능한 자막 파일 확인
		File vttFile = new File(noExtRealFilePath + ".vtt");
		if (vttFile.exists())
			return;

		// 변환 가능한 자막 파일 확인
		File smiFile = new File(noExtRealFilePath + ".smi");
		if (smiFile.exists())
			vttFile = SubtitleConverter.convertSmiToVtt(smiFile, vttFile);
	}

	public void generateThumbnail(String rootDirName, String dirPath, String videoFileName) {
		int extStartIdx = videoFileName.lastIndexOf('.');
		String noExtFileName = videoFileName.substring(0, extStartIdx);

		String noExtRealFilePath = context.getRealPath("/") + rootDirName + dirPath + "/" + noExtFileName;

		// 썸네일 파일 확인
		File thumbnailFile = new File(noExtRealFilePath + ".png");
		if (thumbnailFile.exists())
			return;

		String realFilePath = context.getRealPath("/") + rootDirName + dirPath + "/" + videoFileName;

		ThumbnailGenerater.generateThumbnail(realFilePath, thumbnailFile);
	}

	/* delete */
	public boolean delete(String rootDirName, String dirPath, String fileName) {

		// 상위 디렉터리 문자열 여부 검사
		if (Common.isUnSafe(fileName))
			return false;

		File file = new File(context.getRealPath("/") + rootDirName + dirPath + "/" + fileName);
		// 파일 유효성 검사
		if (file == null || !file.exists() || file.isDirectory())
			return false;

		return file.delete();
	}

}
