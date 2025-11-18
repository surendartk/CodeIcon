export interface Bid {
  id?: number;
  amount: number;
  userId?: number;
   bidTime?: string;  
  time?: string;
}

export interface Product {
  id?: number;
  userId?: number;
  name: string;
  description: string;
  startPrice: number;
  finalPrice?: number;
  lastBidUserId?: number;
  startDateTime: string; // ISO string or 'yyyy-MM-ddTHH:mm' from datetime-local
  endDateTime: string;
  bids?: Bid[];

  // UI only
  myBid?: number;
}

interface ProductUI extends Product {
  // UI-only fields for this component
  isEditing?: boolean;
}

