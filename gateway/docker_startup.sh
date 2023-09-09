docker container kill zpgateway
docker container rm zpgateway
docker image rm zp-gateway
docker build -t zp-gateway .
docker run -d -p 5001:5001 --name zpgateway -v $PWD/logs:/gatewayapps/logs zp-gateway:latest

