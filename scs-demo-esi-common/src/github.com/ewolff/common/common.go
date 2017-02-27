package main

import (
	"fmt"
	"time"
	"log"
	"net/http"
)

func main() {
	http.Handle("/common/css/", http.StripPrefix("/common/css/", http.FileServer(http.Dir("/css"))))
	http.HandleFunc("/common/header", Header)
	http.HandleFunc("/common/footer", Footer)
	http.HandleFunc("/common/navbar", Navbar)
	fmt.Println("Starting up on 8180")
	log.Fatal(http.ListenAndServe(":8180", nil))
}

func Header(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintln(w, `
          <link rel="stylesheet" href="/common/css/bootstrap-3.3.7-dist/css/bootstrap.min.css" />
          <link rel="stylesheet" href="/common/css/bootstrap-3.3.7-dist/css/bootstrap-theme.min.css" />
        `)
}

func Navbar(w http.ResponseWriter, req *http.Request) {
	t := time.Now()
	fmt.Fprintln(w, `
  	<div class="navbar">
	  <div calass="navbar-inner">
	   <a class="brand" href="/"> Home</a>
           <a class="brand"
		href="https://github.com/ultraq/thymeleaf-layout-dialect">Thymeleaf - Layout </a>`,
		t.Format("Mon Jan 2 2006 15:04:05"),
	"</div></div>")
}


func Footer(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintln(w, `<script src="/common/css/bootstrap-3.3.7-dist/js/bootstrap.min.js" />`)
}
