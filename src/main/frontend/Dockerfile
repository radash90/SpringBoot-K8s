# Frontend Dockerfile - Nginx-based Static File Server with Reverse Proxy
# Purpose: Serve static HTML and proxy API requests to backend service

# Base image: Official Nginx with Alpine Linux (lightweight ~23MB)
FROM nginx:alpine

# Copy static HTML file to Nginx's default document root
COPY index.html /usr/share/nginx/html/

# Copy custom Nginx configuration for reverse proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (standard HTTP port)
EXPOSE 80

# Start Nginx in foreground mode (required for Docker containers)
CMD ["nginx", "-g", "daemon off;"]