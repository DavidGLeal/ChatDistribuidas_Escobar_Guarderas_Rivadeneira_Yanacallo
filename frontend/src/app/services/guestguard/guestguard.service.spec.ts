import { TestBed } from '@angular/core/testing';

import { GuestguardService } from './guestguard.service';

describe('GuestguardService', () => {
  let service: GuestguardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuestguardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
