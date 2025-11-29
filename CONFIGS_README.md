# Maven Setup Guide for Candy Store Application

## Installation Details

### Maven Version
- **Version**: 3.9.11
- **Java Version**: 25.0.1 (Homebrew)

## Quick Start Commands

### 1. Run the Login Page UI
```bash
mvn clean javafx:run -Plogin-ui
```

### 2. Run the Main CandyStoreApp 
```bash
mvn clean javafx:run
```
or
```bash
mvn clean javafx:run -P main-app
```

### 3. Compile the Project
```bash
mvn clean compile
```

### 4. Build JAR File
```bash
mvn clean package
```
This creates:
- Regular JAR: `target/candy-store-app-1.0.0.jar`
- Fat JAR (with dependencies): `target/candy-store-app-1.0.0-shaded.jar`

### 6. Clean Build Artifacts
```bash
mvn clean
```

## Project Structure

```
candy-store-app/
├── pom.xml                    # Maven configuration file
├── src/
│   ├── main/
│   │   ├── java/             # Java source files
│   │   └── resources/        # Resources (CSS, images, etc.)
├── build/                    # Build output 
```

## Maven Profiles

The project includes two Maven profiles for different entry points:

### 1. `login-ui` Profile
Runs the LoginPage JavaFX application:
```bash
mvn javafx:run -Plogin-ui
```

### 2. `demo-be` Profile - DEPRECATED
Run the sample Demo BE file to validate functionality for back-end
```bash
mvn javafx:run -Pdemo-be
```

### 3. `main-app` Profile (Default)
Runs the CandyStoreApp console application:
```bash
mvn javafx:run
```
