# URL Shortener Service

This is a URL shortening service built with Spring Boot and Java.

## Overview

This URL shortening service provides the following functionality:

- Generate short URLs.
- Redirect from the short URL to the original URL.
- Ability for the user to enable or disable a short URL.
- Expiration of URLs after a specific timeframe.
- Maintain statistics for each short URL like the number of clicks and the last accessed time.

## Installation

This project requires Java 17 and Gradle.

To install, follow these steps:

1. Clone the repository: `git clone https://github.com/beriafra/urlshortener-spring-boot.git`
2. Go to the project directory: `cd urlshortener`

## Running the Service

To run the service, use the following command in the terminal:

`./gradlew bootRun`

The service should now be running at `http://localhost:8080`.

## Usage

### Generate Short URL
To generate a short URL, make a POST request to `/generate` with the following JSON body:

```json
{
    "originalUrl": "http://your-original-long-url.com"
}
```

### Enable URL
To enable a URL, make a PUT request to /enable/{shortLink}.

### Disable URL
To disable a URL, make a PUT request to /disable/{shortLink}.

### Get User's URLs
To get all URLs for a user, make a GET request to /user/urls.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.