# j2c

Sample b2c e-commerce web app written using Spring Boot 2.

##### Some available features:
- Users, Products, Categories, Checkout and Order processing
- Fully documented REST api with OpenAPI 3.0 swagger-ui
- JWT authentication
- Supported payment gateways: Stripe
- PostgreSQL
- File system image storage

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
