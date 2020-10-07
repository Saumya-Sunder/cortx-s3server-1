# Supported operations/api on Bucket

1.  Lists all your buckets
    `aws s3 ls`
2.  GetBucket (List objects in the specified bucket)
    `aws s3 ls s3://<your_bucket>`
    `aws s3api list-objects --bucket 'your_bucket'`
    `aws s3api list-objects-v2 --bucket 'your_bucket'`
3.  Put bucket
    `aws s3 mb s3://<your_bucket>`
    `aws s3api create-bucket --bucket <your_bucket>`
4.  HEAD bucket
    `aws s3api head-bucket --bucket <your_bucket>`
5.  Delete bucket
    `aws s3 rb s3://<your_bucket>`
    `aws s3api delete-bucket --bucket <your_bucket>`
6.  Put Bucket ACL
    `aws s3api put-bucket-acl --bucket <your_bucket>`
7.  Get Bucket ACL
    `aws s3api get-bucket-acl --bucket <your_bucket>`
8.  Multipart upload
    8.1    Create multipart upload in bucket
           `aws s3api create-multipart-upload --bucket <your_bucket> --key <key>`
    8.2    Upload part
           `aws s3api upload-part --bucket <your_bucket> --key <key> --part-number 1 --body <part>`
    8.3    Abort/Complete part
           `aws s3api complete-multipart-upload --multipart-upload <mpustruct_file> --bucket <your_bucket> --key <key> --upload-id <id>`
           `aws s3api abort-multipart-upload --bucket <your_bucket> --key <key> --upload-id <id>`
    8.4    Lists in-progress multipart uploads in a bucket
           `aws s3api list-multipart-uploads --bucket <your_bucket>`
9.  Put Bucket policy
    `aws s3api put-bucket-policy --bucket <your_bucket> --policy <policy_json_file>`
10. Get Bucket policy
    `aws s3api get-bucket-policy --bucket <your_bucket>`
11. Delete bucket policy
    `aws s3api delete-bucket-policy --bucket <your_bucket>`
12. Put Bucket tagging
    `aws s3api put-bucket-tagging --bucket <your_bucket> --tagging <tagging_json_file>`
13. Get Bucket tagging
    `aws s3api get-bucket-tagging --bucket <your_bucket>`
14. Delete bucket tagging
    `aws s3api delete-bucket-tagging --bucket <your_bucket>`



# Supported operations/api on Object

1.  Put Object
    `aws s3api put-object --bucket <your_bucket> --key <key> --body <file>`
2.  Get Object
    `aws s3api get-object --bucket <your_bucket> --key <key> <save_to_file>`
3.  Delete Object
    `aws s3api delete-object --bucket <your_bucket> --key <key>`
4.  Head Object
    `aws s3api head-object --bucket <your_bucket> --key <key>`
5.  Put Object ACL
    `aws s3api put-object-acl --bucket <your_bucket> --key <key> --grant-* emailaddress=<email>`
6.  Get Object ACL
    `aws s3api get-object-acl --bucket <your_bucket> --key <key>`
7.  Delete multiple objects
    `aws s3api delete-objects --bucket <your_bucket> --delete <del_struct_json_file>`