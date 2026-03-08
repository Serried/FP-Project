# CountryDelightSales

A functional Scala application that processes the Country Delight sales dataset. It demonstrates data processing pipelines using both sequential and parallel collections in Scala 3, handling malformed data effectively.

## Prerequisites

- **Java**: JDK 8 or higher is required.
- **sbt**: Scala Build Tool must be installed. Instructions to install `sbt` can be found [here](https://www.scala-sbt.org/download.html).

## How to Pull

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```
2. Navigate to the project directory:
   ```bash
   cd <project_directory> # Replace with your actual directory name, e.g., cd FP
   ```

## How to Run

1. **Dataset Requirement**: Extract or place your input dataset named exactly **`Country Delight Diary Sales and Inventory Dataset.csv`** into the root directory of the project (at the same level as the `build.sbt` file).
2. **Run the Application**: Execute the application using `sbt` from your terminal:
   ```bash
   sbt run
   ```
   *Note: On the first run, `sbt` will download the necessary Scala compiler version (3.3.1) and library dependencies (like `scala-parallel-collections`).*

## How to Run with Docker

If you prefer to run the application using Docker, follow these steps:

1. **Build the Docker Image**:
   ```bash
   docker build -t countrydelight-sales .
   ```
2. **Run the Container**:
   Due to the large size of the dataset, it is not copied into the Docker image. Instead, you should mount your local project directory to the container so it can access the input CSV and save the output CSVs back to your host machine:

   **On Windows (PowerShell):**
   ```powershell
   docker run --rm -v "${PWD}:/app" countrydelight-sales
   ```

   **On Linux/Mac:**
   ```bash
   docker run --rm -v "$(pwd):/app" countrydelight-sales
   ```

## Expected Output Files

Once the process finishes successfully, it will display a performance comparison between sequential and parallel execution in the console and produce the following files in the project root:
- `output_sequential.csv`
- `output_parallel.csv`
- `malformed_seq.csv` (If any malformed data is found during sequential run)
- `malformed_par.csv` (If any malformed data is found during parallel run)
