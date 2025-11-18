import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-list',
  standalone:false,
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = false;
  error = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: (res) => {
        this.products = res || [];
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to load products';
        this.loading = false;
      },
    });
  }

  isBiddable(p: Product): boolean {
    if (!p.startDateTime || !p.endDateTime) return false;
    const now = Date.now();
    return now >= new Date(p.startDateTime).getTime() && now <= new Date(p.endDateTime).getTime();
  }

  placeBid(p: Product) {
    if (!p.myBid || p.myBid <= 0) {
      alert('Enter a valid bid');
      return;
    }
    const payload = { amount: p.myBid };
    this.productService.placeBid(p.id!, payload).subscribe({
      next: (updated) => {
        alert('Bid placed');
        const idx = this.products.findIndex((x) => x.id === updated.id);
        if (idx >= 0) this.products[idx] = updated;
      },
      error: (err) => {
        alert(err?.error?.message || 'Bid failed');
      },
    });
  }
}
