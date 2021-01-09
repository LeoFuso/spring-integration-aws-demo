#!/bin/sh

MAX_PARALLEL=5

create_queue(){
    aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs create-queue --queue-name $1
}

create_queues() {
    COUNT=0
	while read line; do
        create_queue $line & COUNT=$(( $COUNT + 1 ))
        if (( $COUNT >= $MAX_PARALLEL )) ; then
            wait
            COUNT=0
        fi
	done < $1
}

create_fifo_queue(){
		aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs create-queue --attributes "FifoQueue=true,ContentBasedDeduplication=true" --queue-name $1
}

create_fifo_queues() {
    COUNT=0
	while read line; do
        create_fifo_queue $line & COUNT=$(( $COUNT + 1 ))
        if (( $COUNT >= $MAX_PARALLEL )) ; then
            wait
            COUNT=0
        fi
	done < $1
}


create_fifo_queue_with_deduplication_enabled(){
    aws --endpoint-url=http://localhost:$SQS_PORT --region=$SQS_REGION sqs create-queue --attributes "FifoQueue=true,ContentBasedDeduplication=true" --queue-name $1
}

create_fifo_queues_with_deduplication_enabled() {
    COUNT=0
        while read line; do
        create_fifo_queue_with_deduplication_enabled $line &
            COUNT=$(( $COUNT + 1 ))
        if (( $COUNT >= $MAX_PARALLEL )) ; then
            wait
            COUNT=0
        fi
        done < $1
}


for directory in */; do
	cd $directory

    for file in fifo-with-deduplication-list.txt; do
        if [ -f $file ]; then
            create_fifo_queues_with_deduplication_enabled $file
        fi
    done

    for file in sqs-fifo-list.txt; do
        if [ -f $file ]; then
	    create_fifo_queues $file
	fi
    done

    for file in sqs-list.txt; do
	if [ -f $file ]; then
            create_queues $file
	fi
    done

    cd ..
done

wait