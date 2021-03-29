

output_folder=$1
node_number=$2

file_sizes=(128 512 2000 8000 32000)

for file_size in ${file_sizes[@]};
do
	dd if=/dev/zero of="$output_folder"/files/file_${node_number}_$file_size.txt  bs=$file_size  count=1	
done
