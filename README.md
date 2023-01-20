# Stash

File sharing desktop application

**See also:**

[Android client](https://github.com/numq/Stash)

[React Electron](https://github.com/numq/stash-electron)

## Architecture

- **Clean Architecture**
- **Domain driven design**
- **Reactive programming**
- **Functional programming** *- monadic error handling, pipeline, clean functions (side effects control)*
- **Screaming architecture** *(features)*
- **MVVM**
- **Unidirectional Data Flow** *(state reduction)*

## Structure

![Overview](./media/stash-overview.png)

Infrastructure:

- **Socket server (desktop specific)**

Data layer:

- **Socket client**

Domain layer:

- **Entities (```File```, ```Folder```, etc.)**
- **Interactors (```Start sharing```, ```Share file```, etc.)**
- ```FileRepository```
- ```FolderRepository```
- ```TransferService```

Framework:

- **DI**
- **Application**
- **Navigation**
- **Presentation (viewModels, screens)**

## Features:

- **Folder sharing mode**
- **Offline mode**
- **Share file** *(if sharing)*
- **Remove file** *(if sharing)*
- **Download file**
- **Show image content**
- **Show file details** *(carousel)*
- **Files selection**
- **Selected files actions**
- **Download multiple files as ZIP**
- **Auto reconnection**

## Tech:

- **Kotlin**
- **Jetpack Compose**
- **Koin DI**
- **Java WebSocket**
- **Utilities**