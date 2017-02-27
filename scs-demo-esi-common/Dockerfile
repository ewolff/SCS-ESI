FROM golang

# Copy Go code
COPY /src/github.com/ewolff/common /go/src/github.com/ewolff/common
# Add bootstrap
COPY bootstrap-3.3.7-dist /css/bootstrap-3.3.7-dist

# Compile Go program
RUN go install github.com/ewolff/common

ENTRYPOINT /go/bin/common
EXPOSE 8180
