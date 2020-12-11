#FROM maven:3-openjdk-8-slim
FROM openjdk:8-jdk-slim

RUN apt-get update && apt-get -y install curl

ARG MAVEN_VERSION=3.6.3
ARG SHA=c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV HOME /home/safnari

COPY . $HOME/nsi-dds
RUN groupadd -r safnari && useradd --home-dir $HOME --no-log-init -r -g safnari safnari
RUN chown -Rv safnari:safnari $HOME
USER safnari:safnari

WORKDIR $HOME/nsi-dds
RUN mvn clean install -Dmaven.test.skip=true -Ddocker.nocache
RUN cp -Rv config target/dds.jar $HOME

#CMD /usr/bin/java \
CMD java \
    -Xmx1024m -Djava.net.preferIPv4Stack=true  \
    -Dcom.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot=true \
    -Djava.util.logging.config.file=$HOME/config/logging.properties \
    -Dbasedir=$HOME \
    -jar $HOME/dds.jar
