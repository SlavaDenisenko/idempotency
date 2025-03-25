# === Параметры ===
$dockerHubUser = "sdenisenko"
$tag = "1.0"
$services = @("order-service", "inventory-service", "delivery-service", "payment-service")

if ($args.Count -gt 0) {
    $tag = $args[0]
}

foreach ($service in $services) {
    $imageName = "$dockerHubUser/$service`:$tag"

    Write-Output "Building image for $service..."

    $servicePath = Join-Path ".." $service
    Set-Location -Path $servicePath

    docker build -t $imageName .
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Build failed for $service"
        exit 1
    }

    Set-Location -Path "..\docker"

    Write-Output "Pushing $imageName to Docker Hub..."
    docker push $imageName
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Push failed for $service"
        exit 1
    }

    Write-Output "Successfully built and pushed $imageName"
}

Write-Output "All services have been built and pushed successfully"