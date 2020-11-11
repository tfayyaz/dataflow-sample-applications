## Set-up Website with Google Tag Manager and Ecommerce Data Layer (Optional)

Set-up the following example data layer code on your website pages and for specific events.

### Product Viewed

```js
// Measure a view of product details. This example assumes the detail view occurs on pageload,
dataLayer.push({
  'event': 'view_item',
  'user_id': 'UID0001',
  'ecommerce': {
    'items': [{
      'item_name': 'Donut Friday Scented T-Shirt', // Name or ID is required.
      'item_id': '67890',
      'price': '33.75',
      'item_brand': 'Google',
      'item_category': 'Apparel',
      'item_category_2': 'Mens',
      'item_category_3': 'Shirts',
      'item_category_4': 'Tshirts',
      'item_variant': 'Black',
      'item_list_name': 'Search Results',  // If associated with a list selection.
      'item_list_id': 'SR123',  // If associated with a list selection.
      'index': 1,  // If associated with a list selection.
      'quantity': '1'
    }]
  }
});
```

### Product Added To Cart

```js
// Measure when a product is added to a shopping cart
dataLayer.push({
  'event': 'add_to_cart',
  'user_id': 'UID0001',
  'ecommerce': {
    'items': [{
      'item_name': 'Donut Friday Scented T-Shirt', // Name or ID is required.
      'item_id': '67890',
      'price': '33.75',
      'item_brand': 'Google',
      'item_category': 'Apparel',
      'item_category_2': 'Mens',
      'item_category_3': 'Shirts',
      'item_category_4': 'Tshirts',
      'item_variant': 'Black',
      'item_list_name': 'Search Results',
      'item_list_id': 'SR123',
      'index': 1,
      'quantity': '2'
    }]
  }
});
```

### Ecommerce Purchase / Transaction

```js
dataLayer.push({
  'event': 'purchase',
  'user_id': 'UID0001',
  'ecommerce': {
    'purchase': {
      'transaction_id': 'T12345',
      'affiliation': 'Online Store',
      'value': '35.43',
      'tax': '4.90',
      'shipping': '5.99',
      'currency': 'EUR',
      'coupon': 'SUMMER_SALE',
      'items': [{
        'item_name': 'Triblend Android T-Shirt',
        'item_id': '12345',
        'item_price': '15.25',
        'item_brand': 'Google',
        'item_category': 'Apparel',
        'item_variant': 'Gray',
        'quantity': 1,
        'item_coupon': ''
      }, {
        'item_name': 'Donut Friday Scented T-Shirt',
        'item_id': '67890',
        'item_price': '33.75',
        'item_brand': 'Google',
        'item_category': 'Apparel',
        'item_variant': 'Black',
        'quantity': 1
      }]
    }
  }
});
```

## Google Tag Manager tag

Create a custom html tag in Google Tag Manager that send the data from the Data Layer to the Cloud Run proxy 
