import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';

interface ProductUI extends Product {
  myBid?: number;
}

@Component({
  selector: 'app-user-home',
  standalone: false,
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css'],
})
export class UserHomeComponent implements OnInit {
  activeTab: string = 'home';
  allProducts: ProductUI[] = [];
  user: any;
  loading = false;
  error = '';

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.loadAllProducts();
  }

  setActive(tab: string) {
    this.activeTab = tab;
  }

  loadAllProducts() {
    this.loading = true;

    this.productService.getAllProducts().subscribe({
      next: (res) => {
        // convert Product → ProductUI
        this.allProducts = (res || []).map((p) => ({
          ...p,
          myBid: 0,
        }));

        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to load products';
        this.loading = false;
      },
    });
  }

  parseLocal(dateStr: string) {
    console.log('insidepaprselocal' + dateStr);
    const [date, time] = dateStr.split('T');
    const [y, m, d] = date.split('-').map(Number);
    const [hh, mm, ss] = time.split(':').map(Number);

    return new Date(y, m - 1, d, hh, mm, ss).getTime(); // Local time
  }

  isActive(start: string, end: string): boolean {
    if (!start || !end) return false;

    const now = Date.now();
   
    return now >= this.parseLocal(start) && now <= this.parseLocal(end);
  }

  placeBid(p: ProductUI) {
    if (!p.myBid || p.myBid <= 0) {
      alert('Please enter a valid bid amount.');
      return;
    }

    const payload = { amount: p.myBid };

    this.productService.placeBid(p.id!, payload).subscribe({
      next: (updated) => {
        alert('Bid placed successfully');

        const index = this.allProducts.findIndex((x) => x.id === updated.id);
        if (index >= 0) {
          this.allProducts[index] = {
            ...updated,
            myBid: 0,
          };
        }
      },
      error: (err) => {
        alert(err?.error?.message || 'Bid failed');
      },
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
