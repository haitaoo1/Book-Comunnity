import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {

  message:string = '';
  isOkay:boolean = true;
  summited:boolean = false;

  constructor(
    private route: Router,
    private authService: AuthenticationService
  ) {
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }

  redirectToLogin() {
    this.route.navigate(['login']);
  }

  private confirmAccount(token: string) {
    this.authService.confirm({
      token
    }).subscribe({
      next:() =>{
        this.message = 'Your account has been successfully activated. \n Now go login ';
        this.summited = true;
        this.isOkay = true;
      },
      error: ()=>{
        this.message = 'Token has been expired or invalid';
        this.summited = true;
        this.isOkay = false;
      }
    })
  }
}
