This project sets up and runs the Nethermind Ethereum client, creates an API verification and performance testing suite for the Nethermind client's JSON-RPC, and sets up GitHub Actions pipelines for the test suites.

## Table of Contents

1. [Setup](#setup)
2. [Tests](#tests)
3. [GitHub Actions Workflow](#github-actions-workflow)
4. [Future Improvements](#future-improvements)
5. [Testing Reports](#testing-reports)

## Setup

### Prerequisites

- Docker
- Docker Compose
- Java 11
- Maven
### Steps to Set Up and Run Nethermind Using Sedge

1. **Install Sedge**:
    ```sh
    curl -L https://github.com/NethermindEth/sedge/releases/download/v1.3.2/sedge-v1.3.2-linux-amd64  --output sedge
        sudo ls -l
        sudo mv sedge /usr/local/bin/sedge
        sudo chmod +x /usr/local/bin/sedge
        sedge version
    ```

2. **Run Sedge**:
    ```sh
    echo 'Running sedge...'
        sedge deps install >>sedge.logs
        sedge generate --logging none -p $HOME full-node \
        --map-all --no-mev-boost --no-validator \
        --network chiado -c lighthouse:sigp/lighthouse:latest \
        -e nethermind:nethermindeth/nethermind:master \
        --el-extra-flag Sync.NonValidatorNode=true \
        --el-extra-flag Sync.DownloadBodiesInFastSync=false \
        --el-extra-flag Sync.DownloadReceiptsInFastSync=false \
        --cl-extra-flag checkpoint-sync-url=http://139.144.26.89:4000/ >>sedge.logs
        cd $HOME
        sedge run -p . >>sedge.logs
    ```

3. **Check Sync Status**:
    ```sh
    until curl -s -X POST -H "Content-Type: application/json" --data '{"jsonrpc":"2.0","method":"eth_syncing","params":[],"id":1}' http://localhost:8545 | grep -q '"result":false'; do
          echo "Waiting for Nethermind to sync..."
          sleep 60
        done
        echo "Nethermind is fully synced."
    ```

## Tests


### Test 1: JSON-RPC Verification

1. Retrieve the chain head using `eth_blockNumber`.
2. Obtain block details using `eth_getBlockByNumber`.
3. Ensure the response is not empty, contains block data, and is free of error information.

### Test 2: JSON-RPC Benchmark

1. Execute `eth_getBlockByNumber` for a selected block head.
2. Assess endpoint behavior when executed 1,000 and 10,000 times with different levels of parallelism.
3. Prepare a report based on the results.

### Test 3: Test Suite Extension for Selected Endpoint

1. Choose any JSON-RPC endpoint from the Nethermind documentation.
2. Prepare additional verification and performance tests for the selected endpoint.

## GitHub Actions Workflow
### Overview

The workflow includes steps to set up the environment, run Nethermind using Sedge, and execute the test suites.

### Configuration

```yaml
# .github/workflows/ci.yml

name: NethermindTest

on:
  push:
    branches:
      - main

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v2

    - name: Set up JDK
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'temurin'  # Specify the distribution of the JDK
        java-package: jdk
        architecture: x64
        check-latest: false
        server-id: github
        server-username: ${{ secrets.GITHUB_ACTOR }}
        server-password: ${{ secrets.GITHUB_TOKEN }}
        overwrite-settings: true

   - name: Install Docker
      run: |
        curl -fsSL https://get.docker.com -o get-docker.sh
        sudo sh get-docker.sh
        sudo usermod -aG docker $USER
        sudo systemctl start docker
        sudo systemctl enable docker
    - name: Install Docker Compose
      run: |
        sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
        docker-compose --version
    - name: Install Nethermind
      run: |
        sudo add-apt-repository ppa:nethermindeth/nethermind
        sudo apt update
        sudo apt install nethermind
        
    - name: Install Sedge
      run: |
        curl -L https://github.com/NethermindEth/sedge/releases/download/v1.3.2/sedge-v1.3.2-linux-amd64  --output sedge
        sudo ls -l
        sudo mv sedge /usr/local/bin/sedge
        sudo chmod +x /usr/local/bin/sedge
        sedge version
  - name: Run Sedge to set up Nethermind
      run: |
        echo 'Running sedge...'
        sedge deps install >>sedge.logs
        sedge generate --logging none -p $HOME full-node \
        --map-all --no-mev-boost --no-validator \
        --network chiado -c lighthouse:sigp/lighthouse:latest \
        -e nethermind:nethermindeth/nethermind:master \
        --el-extra-flag Sync.NonValidatorNode=true \
        --el-extra-flag Sync.DownloadBodiesInFastSync=false \
        --el-extra-flag Sync.DownloadReceiptsInFastSync=false \
        --cl-extra-flag checkpoint-sync-url=http://139.144.26.89:4000/ >>sedge.logs
        cd $HOME
        sedge run -p . >>sedge.logs
    - name: Wait for Nethermind sync
      run: |
        until curl -s -X POST -H "Content-Type: application/json" --data '{"jsonrpc":"2.0","method":"eth_syncing","params":[],"id":1}' http://localhost:8545 | grep -q '"result":false'; do
          echo "Waiting for Nethermind to sync..."
          sleep 60
        done
        echo "Nethermind is fully synced."
    - name: Cache Maven packages
      uses: actions/cache@v2
      with:
        path: ~/.m2/repository
        key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-maven

    - name: Install dependencies
      run: mvn install

    - name: Run JSON-RPC verification tests
      run: mvn -Dtest=JsonRpcVerificationTest test

    - name: Run JSON-RPC benchmark tests
      run: mvn -Dtest=JsonRpcBenchmarkTest test

    - name: Run extended JSON-RPC tests
      run: mvn -Dtest=JsonRpcExtendedTest test


Future Improvements

Enhanced Error Handling: Implement more robust error handling mechanisms in the test scripts.
Scalability: Optimize the test suite to handle larger loads and more parallel requests.
Reporting: Integrate a reporting tool for more detailed test results and performance metrics.
Docker Optimization: Further optimize the Docker setup for quicker builds and reduced resource usage.
