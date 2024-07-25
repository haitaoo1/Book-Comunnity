import { Component } from '@angular/core';
import {AuthenticatioRequest} from "../../services/models/authenticatio-request";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {TokenService} from "../../services/token/token.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  authRequest: AuthenticatioRequest ={username: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    //inject
    private router: Router,
    private authServe: AuthenticationService,
    private tokenServes: TokenService
  ) {
  }

  login() {
    //clean login errors that make before
    this.errorMsg = [];
    this.authServe.authenticate({
      body : this.authRequest
    }).subscribe({
      next: (res)=> {
        this.tokenServes.token = res.token as string;
        this.router.navigate(['books'])
      },
      error: (err) =>{
        console.log(err);
        if (err.error.validationErrors){
          this.errorMsg = err.error.validationErrors;
        }else{
          this.errorMsg.push(err.error.errorMsg);
        }

      }
    });
  }
  register() {
    this.router.navigate(['register'])

  }
}
