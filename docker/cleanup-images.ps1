# === Параметры ===
$dockerHubUser = "sdenisenko"
$tag = "1.0"
$services = @("order-service", "inventory-service", "delivery-service", "payment-service")

if ($args.Count -gt 0) {
    $tag = $args[0]
}

foreach ($service in $services) {
    $imageName = "$dockerHubUser/$service`:$tag"

    Write-Output "Deleting local image $imageName..."
    docker rmi $imageName -f
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "Image $imageName not found locally"
    } else {
        Write-Output "$imageName removed successfully"
    }
}

Write-Output "Cleanup completed"