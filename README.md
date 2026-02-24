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
<img width="1920" height="1032" alt="image" src="https://github.com/user-attachments/assets/06d6a79a-2329-4eb9-b6f9-deed249a2744" />
<img width="1920" height="1032" alt="image" src="https://github.com/user-attachments/assets/b1535dc4-ff0d-4627-85e4-764208d46955" />
<img width="1920" height="1032" alt="image" src="https://github.com/user-attachments/assets/4a7d1520-6dcb-48c7-aab1-ec2b67d35394" />
<img width="1920<img width="1920" height="1032" alt="image" src="https://github.com/user-attachments/assets/6647d4c0-9f53-418c-b33b-92e2a60a32f3" />

__________________________________________________________________________________________________________________

# 🎨 Visual Appearance & Style (Look & Feel)
## 🌓 FlatLaf & Modern Design
The FlatLaf library has been implemented to achieve a clean, modern aesthetic.

- ***Color Scheme:*** A high-contrast palette is used (dark grey background with pink accents, utilizing the brand's corporate color #fb3f62).
* **Dynamic Styling:** A **Toggle Theme** feature has been implemented, allowing users to switch seamlessly between **Dark** and **Light** modes.

- ***Layouts:*** The nullLayout has been completely removed. Hybrid Layout Strategy:

- BorderLayout: Used as the top-level manager in MainWindow to anchor the Sidebar, Library, and Status Bar.

- MigLayout: Used in all internal panels to achieve a flexible grid system. This allows for Dynamic Resizing where components grow or shrink proportionally, maintaining visual integrity even when the window is maximized.

- Constraints: Defined setMinimumSize to prevent UI "collapse" and ensure all controls remain accessible.

## 🔠 Text and Fonts
- ***Typographic Hierarchy:*** The Montserrat font is used with different weights (Bold for titles, Regular for data) to significantly improve legibility and information architecture.

## 🖼️ Iconography
Icons have been integrated to make the app more visual, modern, and intuitive.

- ***Interactive Buttons:*** For aesthetic purposes, buttons are displayed as "primary" action components, which transition into icons upon mouse hover.

- ***Tooltips:*** All buttons feature descriptive tooltips and cursor state changes (hand cursor) to clearly indicate they are clickable.

# 🖱️ Interaction and Affordance
## 👁️ Visibility and Feedback

- ***Status Reporting:*** The application constantly informs the user of its internal state. During downloads, a JProgressBar shows the real-time percentage. Upon completion, a text label changes color to confirm either success or failure (Red).

- ***Auto-Scroll & Selection:*** After a successful download, the system automatically scrolls to the new row and selects it in blue. This ensures the user does not have to manually search for the newly added file.

- ***Informative Dialogs:*** JOptionPane alerts have been added to actions like "Delete" or "Play" (if no file is selected), providing immediate guidance to the user.
- ***Informative JLabels:*** that alerts if you press the "Download" button without a URL.
 
## 🛑 Constraints
- ***State Control:*** The download button is disabled while a process is in progress, preventing the user from saturating bandwidth or causing file conflicts.

- ***Conditional Logic:*** If the "Only Audio" option is not selected, the JComboBox for choosing the audio format remains disabled to prevent invalid configurations.

# 🔍 Data Management and Search
## 🧪 Regular Expressions (Regex)
- ***Real-Time Filtering:*** The search bar utilizes Regex to filter the table dynamically. As the user types a name, the table updates instantly to show matching results.

# 🧵 Concurrency & Performance
- ***Multi-threading (SwingWorker):*** All heavy processes, such as video downloading via yt-dlp and cloud synchronization with  [MediaPollingBean](https://github.com/Perelluent/MediaPollingBean), are executed in background threads.
  This ensures the UI remains responsive (non-blocking), allowing the user to browse the library while a download is in progress.
__________________________________________________________________________________________________________________________

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

___________________________________________________________________________________________________________________



