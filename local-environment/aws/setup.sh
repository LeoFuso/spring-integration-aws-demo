#!/bin/sh

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export SQS_PORT=4566
export SQS_REGION=us-east-1

echo "::::: RUNNING AWS SETUP :::::"

for setup in "sqs-setup.sh" "sqs-dlq-setup.sh"
do
	echo ">>> RUNNING SETUP: $setup"
	/bin/bash ./$setup
	echo ">>> FINISHED SETUP: $setup"
done

echo "::::: SETUP EXECUTION HAS FINISHED :::::"