FROM nginx:1.29-alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY ./Nginx.conf /etc/nginx/conf.d/default.conf

RUN rm -rf /usr/share/nginx/html/*

COPY ./frontend/ /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]