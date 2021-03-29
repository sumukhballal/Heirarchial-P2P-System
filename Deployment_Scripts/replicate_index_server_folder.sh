input_directory=$1
output_directory=$2

# Create the Peer Node directory

output="$output_directory/indexserver"

mkdir -p "$output"
cp -r "$input_directory/." "$output"

