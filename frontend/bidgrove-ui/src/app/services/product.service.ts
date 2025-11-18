import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Bid } from '../models/product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private API = 'http://localhost:8082/products';

  constructor(private http: HttpClient) {}

  /** Build Authorization Header */
  private authHeaders() {
    const token = localStorage.getItem('accessToken');

    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return {
      headers: new HttpHeaders(headers),
    };
  }

  /** Fetch all products */
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API}/all`, this.authHeaders());
  }

  /** Fetch products created by logged-in user */
  getMyProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.API}/my`, this.authHeaders());
  }

  /** Create a new product */
  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(
      `${this.API}/add`,
      product,
      this.authHeaders()
    );
  }

  /** Update existing product */
  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(
      `${this.API}/update/${id}`,
      product,
      this.authHeaders()
    );
  }

  deleteProduct(id: number): Observable<any> {
  // return whatever backend returns (string message or empty)
  return this.http.delete(`${this.API}/delete/${id}`, {
    ...this.authHeaders(),
    observe: 'body' as const
  });}

  /** Place a bid on a product */
  placeBid(productId: number, bid: Bid): Observable<Product> {
    return this.http.post<Product>(
      `${this.API}/bid/${productId}`,
      bid,
      this.authHeaders()
    );
  }

  /** Get orders (products where the user won) */
  getMyOrders(): Observable<Product[]> {
    return this.http.get<Product[]>(
      `${this.API}/orders/my`,
      this.authHeaders()
    );
  }

  /** Get single product details */
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.API}/${id}`, this.authHeaders());
  }
}
