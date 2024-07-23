import { Component } from '@angular/core';
import {AuthenticatioRequest} from "../../services/models/authenticatio-request";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  authRequest: AuthenticatioRequest ={username: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authServe: AuthenticationService
    //another service
  ) {
  }

  login() {
    //clean login errors that make before
    this.errorMsg = [];
    this.authServe.authenticate({
      body : this.authRequest
    }).subscribe({
      next: (res)=> {
        //save the token
        this.router.navigate(['books'])
      },
      error: (err) =>{
        console.log(err);
      }
    });
  }
  register() {
    this.router.navigate(['register'])

  }
}
