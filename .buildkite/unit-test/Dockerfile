FROM circleci/android:api-29-node

USER root
RUN mkdir $HOME/app
WORKDIR $HOME/app
COPY . .
RUN adb version