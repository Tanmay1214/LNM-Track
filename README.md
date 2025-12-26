# 🚀 LNM Track

**LNM Track** is a specialized attendance management and student utility application built specifically for the **LNMIIT Jaipur** community. It combines academic tracking with AI-driven assistance to provide a seamless experience for students.

---

## 🌟 The Problem & Our Solution (Main USP)

The biggest pain point for every LNMIITian is checking attendance on the official portal. 
- **The Struggle:** You have to log in every single time, navigate through clunky menus, and scroll horizontally/vertically on a non-mobile-friendly site just to see one percentage.
- **LNM Track Solution:** Our app eliminates this friction. 
    - **One-Time Login:** Log in once and stay logged in. 
    - **Direct Dashboard:** No more scrolling! See your attendance for all subjects instantly on a clean, mobile-first dashboard.
    - **Zero Clutter:** No unnecessary menus—just the data you need, right when you need it.

---

## ✨ Key Features

* **🤖 LNM-AI Bot:** Powered by **Google Gemini 2.5 Flash**, providing an intelligent assistant to answer queries about mess menus, academic policies, and campus life.
* **📊 One-Tap Attendance:** Instant visibility of your theory and lab attendance without the hassle of portal navigation.
* **🔐 Secure Encryption:** Uses **AES-256 encryption** to store your portal credentials locally on your device, ensuring your data never leaves your hand in plain text.
* **🤖 LNM-AI Bot:** Powered by **Google Gemini 2.5 Flash**, providing an intelligent assistant to answer queries about mess menus, academic policies, and campus life.
* **⚡ Modern UI/UX:** Built with Material Design 3, featuring smooth transitions and an intuitive interface.

---

## 🛠️ Tech Stack

* **Core Language:** Java (Android SDK)
* **AI Integration:** Google Generative AI (Gemini API)
* **Encryption:** AES-256 (Advanced Encryption Standard)
* **Navigation:** ChipNavigationBar
* **UI Components:** RecyclerView, NestedScrollView, Extended FAB

---

## 📸 Screenshots

*(Add your screenshots here!)*

| Dashboard | AI Assistant | Portal Sync |
| :---: | :---: | :---: |
| <img src="screenshots/dashboard.png" width="220"> | <img src="screenshots/ai_chat.png" width="220"> | <img src="screenshots/portal.png" width="220"> |

---

## 🔒 Security & Privacy

Security Architecture: We use a In-Memory Encryption/Decryption model. When you enter your password, it is immediately converted into an encrypted hash using AES-256 and only the encrypted version is stored in the database. During authentication, the password is decrypted strictly within the device's volatile memory (RAM) and is never sent to any external server in plain text. This ensures that even if the database is accessed, your actual password remains unreadable.

---

## 👨‍💻 Developer

**Tanmay Shrivastava**
* **GitHub:** [@Tanmay1214](https://github.com/Tanmay1214)
* **College:** The LNM Institute of Information Technology, Jaipur

---

> Built by a student, for the students. 🎓
