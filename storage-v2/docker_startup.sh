docker container kill zpstoragev2
docker container rm zpstoragev2
docker image rm zp-storagev2
docker build -t zp-storagev2 .
docker run -d -p 26308:26308 --name zpstoragev2 -v $PWD/logs:/apps/logs zp-storagev2:latest

