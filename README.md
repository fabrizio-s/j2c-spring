# j2c

Backend for sample b2c e-commerce web app made using Spring Boot 2, JPA/Hibernate and PostgreSQL. Stripe is currently the only supported payment gateway.

### Summary:
(Get the swagger.json from the latest release. Copy the contents of swagger.json and paste them [here](https://editor.swagger.io/) to see all currently available operations.)

On application start-up, if no users exist on the database, a user with the email 'admin@j2c.com' and password 'admin' will be created.

This application allows staff members (**Users** with **Role** *Staff* or *Admin*) to create **Products** (i.e. t-shirt) and **Product Variants** (i.e. blue t-shirt, white t-shirt). It's possible to upload a single **Product Image** for a Product (which is the image that customers see while browsing Products) and multiple **Product Variant Images** (images that the customer sees while they're on a specific **Product**'s site). Currently, only filesystem image storage is supported.

Staff members can also do CRUD operations on **Product Categories**, **Product Tags**, **Shipping Methods** and **Shipping Zones**. A single **Product** may belong to at most one **Product Category**, but it can be associated with multiple **Product Tags**, thus allowing customers to filter products based on **Product Category** or **Product Tag**. A **Product Category** may also have multiple sub-categories, and those sub-categories may also themselves have sub-categories, and so on, to the desired level of nesting.

RBAC with JWT Tokens is used for authentication. **Users** with the **Role** *Admin* can do any operation. *Staff* is like *Admin*, but they cannot modify **Users**. *Customers* can only browse **Products** and purchase **Product Variants**, and modify their own data (email, password, etc). *Viewers* are like *Customers*, but they can also view (but not modify) anything (including **Orders**).

The checkout process (purchase of **Product Variants**) can be done by either registered or anonymous **Users**. During **Checkout**, the customer must provide a shipping address and a **Shipping Method** (if at least one of the **Products** they are trying to purchase is not digital), billing address (which can be the same as the shipping address) and card details. The **Checkout** cannot be completed if the customer has not provided all required information.

If the **Checkout** is completed successfully, an **Order** is created. For any of the purchased **Product Variants** that belong to a non-digital **Product**, staff members can create an **Order Fulfillment**, which is essentially a package to be shipped containing some or all of the items in the **Order**. An **Order Fulfillment** can only contain products listed in the **Order**, and may not contain a higher quantity of a product than the quantity purchased by the customer. Once an **Order Fulfillment** has been shipped, it can be marked as completed and assigned a tracking number, and the status of the **Order** will automatically change accordingly. After all of an **Order**'s **Order Fulfillments** have been completed, the status of the **Order** can be manually set to *FULFILLED*.

#### To do:
- For security reasons, card details are never sent to the application or stored on the database. They should be sent directly to Stripe from the frontend [as explained in this article](https://stripe.com/docs/payments/accept-a-payment?platform=web&ui=elements)
- A new entity (i.e. **Media**) which represents all digital products that a **User** has purchased, and a way to retrieve (download) them
- All checkouts which have existed for more than 72 hours should be automatically cancelled
- In a production environment, a proper OAuth solution should be used instead of the custom JWT implementation (for dev purposes only)

##### Environment variables
- **J2C_PROD_DB_URL** *(required)*: jdbc url for production db (ex: jdbc:postgresql://127.0.0.1:5432/j2c)
- **J2C_PROD_DB_USERNAME** *(required)*: production db user username (ex: j2c-user)
- **J2C_PROD_DB_PASSWORD** *(required)*: production db user password (ex: j2c-user)
- **J2C_DEV_DB_URL**: jdbc url for development db
- **J2C_DEV_DB_USERNAME**: development db user username
- **J2C_DEV_DB_PASSWORD**: development db user password
- **J2C_TEST_DB_URL**: jdbc url for test db. Required for running tests
- **J2C_TEST_DB_USERNAME**: test db user username. Required for running tests
- **J2C_TEST_DB_PASSWORD**: test db user password. Required for running tests
- **J2C_JWT_SECRET** *(required)*: secret to be used for signing JWT tokens
- **J2C_STRIPE_LIVE_KEY** *(required)*: [Stripe live key](https://stripe.com/docs/keys)
- **J2C_STRIPE_TEST_KEY**: [Stripe test key](https://stripe.com/docs/keys)
- **J2C_PROD_LOCAL_IMAGE_STORAGE_PATH** *(required)*: file system directory where product and category images should be stored
- **J2C_TEST_LOCAL_IMAGE_STORAGE_PATH**: file system directory where product and category images should be stored during testing. Required for running tests
