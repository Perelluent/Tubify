# 🎞️ Tubify
### Cloud-Enabled GUI Downloader for [yt-dlp](https://github.com/yt-dlp/yt-dlp.git)

## 👀 Overview
**Tubify** is a desktop application built with **Java**
It acts as a **GUI wrapper** around the powerful command-line tool [`yt-dlp`](https://github.com/yt-dlp/yt-dlp), allowing users to download videos or audio files easily without technical complexity.

With this app, users can:
- 📥 Download videos or audio from multiple platforms.  
- 🧹 Skip ads in downloaded media.
- ☁️ Sync with the cloud: Login to access your media files from anywhere.
- 📂 Manage Library: View a unified table of Local vs Cloud files.

## Screenshots
<img width="1486" height="993" alt="image" src="https://github.com/user-attachments/assets/e0281857-5ade-4275-a13f-a44434bc139b" />
<img width="1486" height="993" alt="image" src="https://github.com/user-attachments/assets/928f75a9-97c6-495c-a6d6-f1dde614bf1f" />
<img width="886" height="893" alt="image" src="https://github.com/user-attachments/assets/e3250e05-cbd2-4862-a5f7-2006f0e6cc99" />

## ☁️ Cloud Integration (Powered by [MediaPollingBean](https://github.com/Perelluent/MediaPollingBean))
Tubify integrates a custom **Java Bean component** to provide enterprise-level features:
- **Secure Authentication:** User Login with JWT Token persistence and "Remember Me" functionality.
- **Real-Time Polling:** The app listens for changes on the server and updates the UI instantly when new files are added.
- **Smart Playback:** Plays local files instantly with the system player.
- ## 🛠️ MediaPollingBean Configuration & Usage

### ⚙️ Configuration Properties of the MediaPollingBean

You can configure these properties via the **NetBeans GUI Builder** (Properties Window) or programmatically in your code.

| Property | Type | Description |
| :--- | :--- | :--- |
| **`apiUrl`** | `String` | **Required.** The base URL of the API (e.g., `http://localhost:5000`). Setting this property initializes the internal `ApiClient`. |
| **`pollingInterval`** | `int` | Time in **seconds** between server checks. The bean automatically converts this to milliseconds for the internal Timer. |
| **`running`** | `boolean` | Controls the background service. Setting this to `true` starts the polling `Timer`; setting it to `false` stops it. |
| **`token`** | `String` | The JWT Bearer token used for authenticated requests (`login`, `download`, `upload`). This is usually set automatically after a successful login. |


### 🔌 API Methods

The bean exposes several public methods to interact with the backend:

* **`login(String email, String password)`**: Authenticates the user, stores the received JWT token internally, and returns it.
* **`getAllMedia()`**: Returns a `List<LibraryItem>` containing all files available on the server.
* **`download(int id, File destFile)`**: Downloads the file with the specified ID to the local destination.
* **`uploadFileMultipart(File file, String downloadedFromUrl)`**: Uploads a local file to the cloud.
* **`startPolling()`**: Manually triggers a check for new media (usually handled automatically by the timer).

### 📡 Event Handling

The component uses a custom event system to notify your application when new files are detected on the server.

## 🧰 Setup Instructions

1. Make sure **yt-dlp** is installed:  
   👉 [Download yt-dlp](https://github.com/yt-dlp/yt-dlp/releases)

2. (Optional) Install **FFmpeg** for audio extraction:  
   👉 [Download FFmpeg](https://ffmpeg.org/download.html)

3. Optional:
   - 📁 Temporary downloads folder
4. Make sure that the ⚙️ `yt-dlp` binary path is in the right path.

## 🎮 How to Use

1. Paste a **video URL**
2. Click ***Download***
3. Save the video in a folder of your choice.

## 👨‍💻 Author & Credits

**Author:** Pere Garcias

**Project:** Tubify

**External technologies and resources:**


- [`yt-dlp`](https://github.com/yt-dlp/yt-dlp)
  
- [`ffmpeg`](https://ffmpeg.org/)
- Java Swing (JDK 24)  
- IDE: **NetBeans 27**
  ## 🧠 References

- [yt-dlp Documentation](https://github.com/yt-dlp/yt-dlp/wiki)  
- [Oracle Swing Components Guide](https://docs.oracle.com/javase/tutorial/uiswing/components/)  
- [FFmpeg Documentation](https://ffmpeg.org/ffmpeg-filters.html)

