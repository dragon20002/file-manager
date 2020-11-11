package com.haruu.filemanager.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger log = LoggerFactory.getLogger(FileService.class);
	
	/**
	 * @implNote
	 * 
	 * ## 경로 관련 변수명 설명
	 * - XXDirName  폴더명
	 * - YYPath     타고 들어온 폴더명들을 '/'로 합친 문자열
	 * - realZZ     절대경로 (real이 없으면 상대경로이며 앞에 '/'를 붙여준다)
	 * 
	 * 사용 중인 변수명 예시 >>
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

	private final ServletContext context;

	private final String realRootDirPath;

	public FileService(ServletContext context) throws IOException {
		this.context = context;
		this.realRootDirPath = context.getRealPath("/");
		log.debug("realRootDirPath={}", realRootDirPath);
	}

	/* ----------- search ---------- */

	// 파일정보 목록 조회
	public List<FileInfo> findAll(String rootDirName, String dirPath, FileFilter fileFilter) {
		List<FileInfo> fileInfos = new ArrayList<>();

		// 파일 목록
		String realDirPath = Common.getDirPath(realRootDirPath, rootDirName, dirPath);
		File directory = new File(realDirPath);
		if (directory.exists()) {
			File[] files = directory.listFiles(fileFilter);
	
			// 파일정보 목록
			List<FileInfo> dirInfos = new ArrayList<>();
			for (File file : files) {
				FileInfo fileInfo = Common.getFileInfo(rootDirName, dirPath, file);
				if (fileInfo.isDir())
					dirInfos.add(fileInfo);
				else
					fileInfos.add(fileInfo);
			}
			fileInfos.addAll(0, dirInfos);
		}

		return fileInfos;
	}

	// 파일정보 목록 조회 (최하위경로까지 탐색)
	public List<FileInfo> findAllRecursive(String rootDirName, String dirPath, FileFilter fileFilter) {
		List<FileInfo> fileInfos = new ArrayList<>();

		fileInfos.addAll(this.findAll(rootDirName, dirPath, fileFilter));

		findAll(rootDirName, dirPath, null).stream().filter(FileInfo::isDir)
			.forEach((fileInfo) -> {
				String subDirPath = fileInfo.getDirPath() + "/" + fileInfo.getName();
				List<FileInfo> subFileInfos = this.findAllRecursive(rootDirName, subDirPath, fileFilter);
				if (subFileInfos != null) {
					fileInfos.addAll(subFileInfos);
				}
			});

		return fileInfos;
	}

	// 파일정보 조회
	public FileInfo findOne(String rootDirName, String dirPath, String fileName) {
		// 상위 디렉터리 문자열 여부 검사
		if (Common.isUnSafe(fileName))
			return null;

		String realFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, fileName);
		File file = new File(realFilePath);
		// 파일 유효성 검사
		if (file == null || !file.exists())
			return null;

		return Common.getFileInfo(rootDirName, dirPath, file);
	}

	/* ---------- create ----------- */

	/* file upload */
	public List<FileInfo> saveAll(String rootDirName, String dirPath, MultipartFile[] mpFiles) throws IOException {
		List<FileInfo> fileInfos = new ArrayList<>();

		String realDirPath = Common.getDirPath(realRootDirPath, rootDirName, dirPath);
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
				fileInfo = Common.getFileInfo(rootDirName, dirPath, file);
				fileInfos.add(fileInfo);
			} catch (IOException e) {
				continue;
			}
		}

		return fileInfos;
	}

	public FileInfo generateDir(String rootDirName, String dirPath, String dirName) {
		String realDirPath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, dirName);

		FileInfo dirInfo = null;
		try {
			File dir = new File(realDirPath);
			FileUtils.forceMkdir(dir);
			dirInfo = Common.getFileInfo(rootDirName, dirPath, dir);
		} catch(Exception e) {
			log.debug(e.getMessage());
		}
		return dirInfo;
	}

	public ID3v2 generateMp3Id3Tag(String rootDirName, String dirPath, String mp3FileName) throws UnsupportedTagException, InvalidDataException, IOException {
		String realFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, mp3FileName);

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

			String noExtRealFilePath = realRootDirPath + "/" + rootDirName + "/" + dirPath + "/" + noExtFileName;

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

		String noExtRealFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, noExtFileName);

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

		String noExtRealFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, noExtFileName);

		// 썸네일 파일 확인
		File thumbnailFile = new File(noExtRealFilePath + ".png");
		if (thumbnailFile.exists())
			return;

		String realFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, videoFileName);

		ThumbnailGenerater.generateThumbnail(realFilePath, thumbnailFile);
	}

	/* ---------- delete ---------- */
	
	public boolean delete(String rootDirName, String dirPath, String fileName) {

		// 상위 디렉터리 문자열 여부 검사
		if (Common.isUnSafe(fileName))
			return false;

		String realFilePath = Common.getDirPath(realRootDirPath, rootDirName, dirPath, fileName);
		File file = new File(realFilePath);
		// 파일 유효성 검사
		if (file == null || !file.exists())
			return false;

		boolean delRslt;
		try {
			// 하위 디렉터리 및 파일 삭제
			FileUtils.forceDelete(file);
			delRslt = true;
		} catch(Exception e) {
			log.debug(e.getMessage());
			delRslt = false;
		}

		return delRslt;
	}

}
