# Build stage: Compile Go program
FROM golang

# Copy Go code
COPY /src/github.com/ewolff/common /go/src/github.com/ewolff/common
WORKDIR /go/src/github.com/ewolff/common

# Compile Go program
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o common .
#RUN go install github.com/ewolff/common

# Run: minimal Docker container
FROM scratch

# Add bootstrap
COPY bootstrap-3.3.7-dist /css/bootstrap-3.3.7-dist

COPY --from=0 /go/src/github.com/ewolff/common/common /

ENTRYPOINT ["/common"]
CMD []

EXPOSE 8180
