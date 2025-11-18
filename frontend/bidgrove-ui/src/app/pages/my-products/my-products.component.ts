import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

interface ProductUI extends Product {
  isEditing?: boolean; // UI state for this component only
}

@Component({
  selector: 'app-my-products',
  standalone: false,
  templateUrl: './my-products.component.html',
  styleUrls: ['./my-products.component.css'],
})
export class MyProductsComponent implements OnInit {
  products: ProductUI[] = [];
  selected: ProductUI | null = null;
  newProduct: ProductUI = this.emptyProduct();
  viewDetailsProduct: ProductUI | null = null;

  loading = false;
  error = '';
  createMode = false;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.load();
  }

  /** Empty product for creation */
  emptyProduct(): ProductUI {
    return {
      name: '',
      description: '',
      startPrice: 0,
      startDateTime: '',
      endDateTime: '',
    };
  }

load() {
  this.loading = true;

  this.productService.getMyProducts().subscribe({
    next: (res) => {
      this.products = (res || []).map((p) => ({
        ...p,
        bids: p.bids?.map(b => ({
          ...b,
          time: b.bidTime   // <-- FIXED HERE
        })) || []
      }));
      this.loading = false;
    },
    error: (err) => {
      this.error = err?.error?.message || 'Failed to load your products';
      this.loading = false;
    },
  });
}


  /** Start create mode */
  startAdd() {
    this.createMode = true;
    this.selected = null;
    this.newProduct = this.emptyProduct();
    this.error = '';
  }

  /** Create new product */
  saveNew() {
    if (!this.newProduct.name.trim()) {
      this.error = 'Name is required';
      return;
    }

    if (!this.newProduct.startDateTime || !this.newProduct.endDateTime) {
      this.error = 'Start and end date/time are required';
      return;
    }

    if (
      new Date(this.newProduct.startDateTime) >=
      new Date(this.newProduct.endDateTime)
    ) {
      this.error = 'End date/time must be after start';
      return;
    }

    this.productService.addProduct(this.newProduct).subscribe({
      next: (created) => {
        this.products.push({ ...created });
        this.createMode = false;
        this.newProduct = this.emptyProduct();
        this.error = '';
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to create product';
      },
    });
  }

  /** Edit product */
  edit(p: ProductUI) {
    this.createMode = false;
    this.selected = { ...p }; // clone to avoid modifying list directly
    this.error = '';
  }

  /// helper: put somewhere inside the class (private)
  private ensureSeconds(dt?: string): string | undefined {
    if (!dt) return dt;
    // If already contains seconds (i.e. length >= 19: 2025-11-20T10:00:00), return as-is
    // If it's like '2025-11-20T10:00' add ':00'
    return dt.length === 16 ? `${dt}:00` : dt;
  }

  /** Update edited product */
  update() {
    if (!this.selected?.id) return;

    // Build minimal payload expected by backend (do not send UI flags)
    const payload: Partial<Product> = {
      name: this.selected.name,
      description: this.selected.description,
      startPrice: this.selected.startPrice,
      // normalize datetimes to include seconds if missing
      startDateTime: this.ensureSeconds(this.selected.startDateTime) as string,
      endDateTime: this.ensureSeconds(this.selected.endDateTime) as string,
    };

    console.log('Updating product id=', this.selected.id, 'payload=', payload);

    this.productService
      .updateProduct(this.selected.id, payload as Product)
      .subscribe({
        next: (updated) => {
          console.log('Update success', updated);
          const index = this.products.findIndex((x) => x.id === updated.id);
          if (index >= 0) this.products[index] = { ...updated };
          this.selected = null;
          this.error = '';
        },
        error: (err) => {
          // important: log full error so you can inspect network/response body
          console.error('Update failed:', err);
          // try to show helpful message to user
          const serverMsg =
            err?.error?.message ??
            err?.error ??
            err?.message ??
            'Update failed';
          this.error =
            typeof serverMsg === 'string'
              ? serverMsg
              : JSON.stringify(serverMsg);
        },
      });
  }

  deleteProduct(p: ProductUI) {
  if (!p?.id) return;
  if (!confirm("Delete this product?")) return;

  this.loading = true;

  this.productService.deleteProduct(p.id).subscribe({
    next: () => {
      // Accept success even if Angular marked it as error
      this.products = this.products.filter(x => x.id !== p.id);
      this.loading = false;
      this.error = '';
      alert("Product deleted successfully");
    },
    error: (err) => {
      console.log("Delete Error but status = ", err.status);

      // If backend returns 200 but Angular flags error
      if (err.status === 200) {
        this.products = this.products.filter(x => x.id !== p.id);
        this.loading = false;
        this.error = '';
        alert("Product deleted successfully (handled 200 bug)");
        return;
      }

      // Real errors
      this.error = err?.error?.message ?? "Delete failed";
      this.loading = false;
    }
  });
}


  /** Cancel edit/create */
  cancelEdit() {
    this.createMode = false;
    this.selected = null;
    this.error = '';
  }

  /** View detailed info */
  viewDetails(p: ProductUI) {
    this.viewDetailsProduct = p;
  }

  closeDetails() {
    this.viewDetailsProduct = null;
  }
}
