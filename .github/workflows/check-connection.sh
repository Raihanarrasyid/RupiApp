chmod 644 /tmp/ov.log
while ! grep -q "Initialization Sequence Completed" /tmp/ov.log; do
    echo "Connecting..."
    sleep 5
done
echo "Connected"