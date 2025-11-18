import { Product } from './product.model';

describe('Product interface', () => {
  it('should create a product object', () => {
    const p: Product = {
      name: '',
      description: '',
      startPrice: 0,
      startDateTime: '',
      endDateTime: '',
    };
    expect(p).toBeTruthy();
  });
});
