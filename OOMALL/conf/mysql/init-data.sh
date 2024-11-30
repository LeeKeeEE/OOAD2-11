#!/bin/bash
docker rm $(docker ps -a -q)

cd /root/oomall-2023/mysql
git pull
rm -r sql
unzip sql.zip

for M in 'payment' 'shop' 'product' 'alipay' 'wechatpay' 'region' 'freight' 'sfexpress' 'jtexpress' 'ztoexpress'
do
  docker exec -i $(docker container ls -aq -f name=mysql.*) mysql -udemouser -p123456 -D $M < /root/oomall-2023/mysql/sql/$M.sql
done