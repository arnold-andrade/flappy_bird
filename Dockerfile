# Use a lightweight nginx image to serve static files
FROM nginx:alpine

# Copy all files from the game directory to the nginx html directory
COPY game /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
