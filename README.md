# 파일 관리용 웹앱

## 주요 기능

* 드래그로 서버에 파일 업로드<br>
![aa](https://postfiles.pstatic.net/MjAxOTA1MTFfMjQ2/MDAxNTU3NTY2NDk3MzE5.6l4H6QyBr5lf55TQqflZ5MdkAxqPv0mEtOsMQMFEMigg.0jFHMrhhYkjd-sL4rsM_LP97d0CYEya4IK7H_Uy1SrAg.PNG.dragon20002/SE-e3cd1eab-848d-4e8c-9511-8eaa49f8420c.png?type=w773)

* MP3/MP4 재생<br>
![aa](https://postfiles.pstatic.net/MjAxOTEyMjJfMjc5/MDAxNTc2OTk0NzY0ODg0.ZHq1_kUKpIsIYkEj406W9iSwM9eOFoXJz53WWdO2RwAg.pwlZwOeYlXDqH4IJR4VxjISgZ_bRlXfV6rxRDZ-aR-kg.PNG.dragon20002/SE-54d22854-b31a-4c1b-802c-773e32fc5bb9.png?type=w773)

* ID3v2 태그<br>

* SMI to VTT 자막 자동 변환<br>
![aa](https://postfiles.pstatic.net/MjAxOTEyMjJfNiAg/MDAxNTc2OTk0NzI4NDE1.M8hAbqKF29ZE7T3LFOx9mgmkGCP0MWmJTaqecS3gT74g.jvYrfEPFzPspX5JX3yZF98UaECjnQfQAXRsKzZqLYzkg.PNG.dragon20002/11.png?type=w773)

## Request Mapping Table
| Name | Method | URL | BODY |
|-|-|-|-|
| 메인 | GET | / | - |
| 로그인 | GET/POST | /login | ... |
| 로그아웃 | POST | /logout | ... |
| 작업위치 변경 | GET | /api/dir/{depth}/[dirName] | - |
| 작업위치 추가 | POST | /api/dir/%ROOT_DIR_NAME% | dirName |
| 작업위치 추가(safe) | POST | /api/dir/%ROOT_SAFE_DIR_NAME% | dirName |
| 파일 목록 | GET | /api/files | - |
| 파일 정보 | GET | /api/files/%ROOT_DIR_NAME%/{fileName} | - |
| 파일 정보(safe) | GET | /api/files/%ROOT_SAFE_DIR_NAME%/{fileName} | - |
| 파일 업로드 | POST | /api/files/%ROOT_DIR_NAME% | files |
| 파일 업로드(safe) | POST | /api/files/%ROOT_SAFE_DIR_NAME% | files |
| 파일 삭제 | DELETE | /api/files/%ROOT_DIR_NAME%/{fileName} | - |
| 파일 삭제(safe) | DELETE | /api/files/%ROOT_SAFE_DIR_NAME%/{fileName} | - |
| 음악 목록 | GET | /api/mp3-files | - |
| 음악 재생 | POST | /api/mp3-files/%ROOT_DIR_NAME%/{fileName} | dirPath |
| 음악 재생(safe) | POST | /api/mp3-files/%ROOT_SAFE_DIR_NAME%/{fileName} | dirPath |
| 음악 태그 | POST | /api/mp3-files/%ROOT_DIR_NAME%/{fileName}/id3 | dirPath |
| 음악 태그(safe) | POST | /api/mp3-files/%ROOT_SAFE_DIR_NAME%/{fileName}/id3 | dirPath |
| 영상 작업위치 변경 | GET | /api/mp4-dir/{depth}/[dirName] | - |
| 영상 목록 | GET | /api/mp4-files | - |
| 영상 재생 | GET | /api/mp4-files/%ROOT_DIR_NAME%/{fileName} | - |
| 영상 재생 | GET | /api/mp4-files/%ROOT_SAFE_DIR_NAME%/{fileName} | - |
