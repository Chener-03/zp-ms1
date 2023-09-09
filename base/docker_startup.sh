docker container kill zpbase
docker container rm zpbase
docker image rm zp-base
docker build -t zp-base .
docker run -d -p 6050:6050 --name zpbase -v $PWD/logs:/apps/logs zp-base:latest

