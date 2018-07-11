FROM golang:1.10.3
COPY /src/github.com/ewolff/common /go/src/github.com/ewolff/common
WORKDIR /go/src/github.com/ewolff/common
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o common .

FROM scratch
COPY bootstrap-3.3.7-dist /css/bootstrap-3.3.7-dist
COPY --from=0 /go/src/github.com/ewolff/common/common /
ENTRYPOINT ["/common"]
CMD []
EXPOSE 8180
