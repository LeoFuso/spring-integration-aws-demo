#!/bin/sh

build_arn_attribute() {
  #echo $( jq -n --arg bn "$1" '{RedrivePolicy: {deadLetterTargetArn: $bn, maxReceiveCount: "5"} }')
  #echo {"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:80398EXAMPLE:MyDeadLetterQueue\",\"maxReceiveCount\":\"1000\"}"}

  #parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
  #echo $parent_path

  sed -e "s/\${arn}/$1/" ../queue-attribute-template.txt
}

resolve_queue_arn() {
  echo $(aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs get-queue-attributes --queue-url $1 | jq -r '.Attributes.QueueArn')
}

get_queue_url() {
  echo $(aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs get-queue-url --queue-name $1 | jq -r '.QueueUrl')
}

configure_dlq() {
  queue_url_dlq="$(get_queue_url $2)"
  queue_arn="$(resolve_queue_arn $queue_url_dlq)"
  attribute="$(build_arn_attribute $queue_arn)"

  queue_url_source="$(get_queue_url $1)"
  aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs set-queue-attributes --queue-url $queue_url_source --attributes "$attribute"
  echo "$attribute"
}

read_files() {
  while read line; do

    IFS=' ' read -r -a line_array <<< "$line"
    configure_dlq ${line_array[0]} ${line_array[1]}

  done <$1
}

for directory in */; do
  cd $directory

  for file in sqs-dlq-list.txt; do
    if [ -f $file ]; then
      read_files $file
    fi
  done

  cd ..
done

wait
