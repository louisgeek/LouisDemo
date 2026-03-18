FROM openjdk:11-jdk

# Install Android SDK
ENV ANDROID_SDK_ROOT /opt/android-sdk
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip commandlinetools-linux-9477386_latest.zip && \
    rm commandlinetools-linux-9477386_latest.zip

ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools/bin:${ANDROID_SDK_ROOT}/platform-tools

# Accept licenses
RUN yes | sdkmanager --licenses

# Install required SDK packages
RUN sdkmanager "platform-tools" "platforms;android-36" "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Grant execute permission
RUN chmod +x gradlew

# Build the app
RUN ./gradlew assembleDebug

CMD ["./gradlew", "build"]
