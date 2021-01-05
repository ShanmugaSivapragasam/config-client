mvn clean install
project=shan-demos3
repository=configclient
docker build . --tag gcr.io/$project/$repository
docker push gcr.io/$project/$repository
# kubectl create secret generic cloudsql-instance-credentials --from-file  abcd-one-x9-6ef7ec7d6006.json
# kubectl create secret generic sql-db-credentials \\n   --from-literal=username=covid\\n   --from-literal=password=doyouwantoknow
