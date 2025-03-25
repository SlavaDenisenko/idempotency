# === Параметры ===
$services = @("order-service", "inventory-service", "delivery-service", "payment-service")

foreach ($service in $services) {
    Write-Output "Cleaning and building $service with Maven..."
    Set-Location -Path "../$service"
    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Maven build failed for $service"
        exit 1
    }

    Write-Output "Successfully built $service"
}

Write-Output "All services have been built successfully"