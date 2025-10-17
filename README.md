# Spring AI MCP Server Demo - E-commerce Order Management

## Overview

This project demonstrates how to integrate Spring AI with Model Context Protocol (MCP) to create a natural language interface for REST API operations. The demo showcases an E-commerce Order Management System where developers can use natural language prompts to create customers, products, and orders instead of manually crafting complex API requests.

## Problem Statement

In enterprise development, testing features often requires setting up complex data through multiple API calls in a specific sequence. This process is:
- Time-consuming for developers
- Error-prone when dealing with complex request payloads
- Requires deep understanding of API contracts and business logic
- Difficult to maintain and document

## Solution: MCP Server Integration

The MCP server allows developers to use natural language to:
- Create customers with complete address information
- Add products with pricing and inventory details
- Place orders with multiple products
- Query existing data

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MCP Client    │    │   MCP Server    │    │  Spring Boot    │
│ (Claude/Other)  │◄──►│  (This Demo)    │◄──►│   REST APIs     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   PostgreSQL    │
                       │    Database     │
                       └─────────────────┘
```

## Technology Stack

- **Spring Boot 3.2.0** - Main application framework
- **Spring AI 1.0.0-M4** - AI integration and MCP server
- **PostgreSQL 15** - Database
- **Gradle** - Build tool
- **Docker** - Containerization
- **OpenAI GPT-3.5-turbo** - Language model

## API Endpoints

### Customer Management
- `POST /api/customers` - Create a new customer
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/email/{email}` - Get customer by email
- `GET /api/customers` - Get all customers

### Product Management
- `POST /api/products` - Create a new product
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/search?name={name}` - Search products by name
- `GET /api/products/sku/{sku}` - Get product by SKU
- `GET /api/products` - Get all products

### Order Management
- `POST /api/orders` - Create a new order
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/customer/{customerId}` - Get orders by customer
- `PUT /api/orders/{id}/status?status={status}` - Update order status
- `GET /api/orders` - Get all orders

## MCP Tools Available

The MCP server exposes the following tools for natural language interaction:

1. **create_customer** - Create customers with address information
2. **create_product** - Add products with pricing and inventory
3. **create_order** - Place orders with multiple products
4. **get_customer** - Retrieve customer information
5. **get_product** - Find products by various criteria
6. **get_order** - Get order details and history

## Quick Start

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- OpenAI API key (for MCP client integration)

### 1. Clone and Setup

```bash
git clone <repository-url>
cd spring-ai-mcp-server-demo
```

### 2. Environment Configuration

Create a `.env` file in the project root:

```bash
OPENAI_API_KEY=your-openai-api-key-here
```

### 3. Run with Docker Compose

```bash
# Start the application and database
docker-compose up --build

# The application will be available at http://localhost:8080
# PostgreSQL will be available at localhost:5432
```

### 4. Alternative: Local Development

```bash
# Start PostgreSQL (if not using Docker)
docker run --name ecommerce-postgres -e POSTGRES_DB=ecommerce_demo -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15

# Run the application
./gradlew bootRun
```

## Usage Examples

### Traditional API Approach

Creating a complete order scenario requires multiple API calls:

```bash
# 1. Create customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "address": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
  }'

# 2. Create products
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro",
    "description": "Apple MacBook Pro 16-inch",
    "price": 2499.99,
    "category": "Electronics",
    "sku": "MBP-16-001",
    "stockQuantity": 10
  }'

# 3. Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "orderItems": [
      {
        "productId": 1,
        "quantity": 1
      }
    ]
  }'
```

### MCP Natural Language Approach

With MCP integration, the same scenario becomes:

```
"Create a customer named John Doe with email john.doe@example.com, phone +1-555-0123, and address at 123 Main St, New York, NY 10001, USA. Then add a MacBook Pro product priced at $2499.99 with SKU MBP-16-001 and 10 units in stock. Finally, create an order for John Doe with 1 MacBook Pro."
```

## MCP Client Setup

### Option 1: Claude Desktop Integration

1. Install Claude Desktop application
2. Add the following to your Claude Desktop configuration:

```json
{
  "mcpServers": {
    "ecommerce-demo": {
      "command": "java",
      "args": ["-jar", "/path/to/ecommerce-mcp-demo.jar"],
      "env": {
        "OPENAI_API_KEY": "your-api-key"
      }
    }
  }
}
```

### Option 2: Local MCP Client

1. Install MCP client tools:
```bash
npm install -g @modelcontextprotocol/cli
```

2. Connect to the MCP server:
```bash
mcp connect http://localhost:8080/mcp
```

## Testing the MCP Integration

### Sample Natural Language Commands

1. **Customer Creation:**
   - "Create a customer named Alice Smith with email alice@example.com and address in San Francisco, CA"

2. **Product Management:**
   - "Add a wireless mouse product for $29.99 with 50 units in stock"
   - "Find all products in the Electronics category"

3. **Order Processing:**
   - "Create an order for Alice Smith with 2 wireless mice"
   - "Show me all orders for customer with email alice@example.com"

4. **Complex Scenarios:**
   - "Set up a complete e-commerce scenario: Create customer Bob Johnson in Seattle, add a gaming laptop for $1999, and place an order for 1 laptop"

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/example/ecommerce/
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data transfer objects
│   │   ├── entity/              # JPA entities
│   │   ├── mcp/                 # MCP server implementation
│   │   ├── repository/          # Data repositories
│   │   ├── service/             # Business logic
│   │   └── config/              # Configuration classes
│   └── resources/
│       └── application.yml      # Application configuration
├── test/                        # Test files
└── build.gradle                 # Build configuration
```

### Adding New MCP Tools

1. Create a new method in `EcommerceMcpServer.java`
2. Annotate with `@McpTool`
3. Define parameters with `@McpTool.Parameter`
4. Implement the business logic
5. Return a Map with success status and data

Example:
```java
@McpTool(
    name = "new_tool",
    description = "Description of what this tool does"
)
public Map<String, Object> newTool(
        @McpTool.Parameter(description = "Parameter description") String param) {
    // Implementation
    return Map.of("success", true, "data", result);
}
```

## Monitoring and Logging

The application includes comprehensive logging for:
- MCP tool invocations
- API request/response cycles
- Database operations
- Error handling

Logs are available in the console output and can be configured in `application.yml`.

## Security Considerations

- API keys should be stored securely (environment variables)
- Input validation is implemented for all endpoints
- Database connections use connection pooling
- CORS can be configured for web client access

## Performance

- Connection pooling for database operations
- Lazy loading for entity relationships
- Efficient query patterns with JPA
- Docker containerization for consistent performance

## Troubleshooting

### Common Issues

1. **Database Connection Failed:**
   - Ensure PostgreSQL is running
   - Check connection parameters in `application.yml`
   - Verify database exists

2. **MCP Client Connection Issues:**
   - Check if the application is running on port 8080
   - Verify MCP server configuration
   - Check OpenAI API key validity

3. **Build Failures:**
   - Ensure Java 17+ is installed
   - Check Gradle wrapper permissions
   - Verify all dependencies are available

### Logs and Debugging

Enable debug logging by adding to `application.yml`:
```yaml
logging:
  level:
    com.example.ecommerce: DEBUG
    org.springframework.ai: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions and support:
- Create an issue in the repository
- Check the troubleshooting section
- Review the Spring AI documentation

## Future Enhancements

- [ ] Add more complex business logic scenarios
- [ ] Implement authentication and authorization
- [ ] Add comprehensive test coverage
- [ ] Create web-based MCP client interface
- [ ] Add support for batch operations
- [ ] Implement caching for better performance
- [ ] Add metrics and monitoring
- [ ] Create API documentation with Swagger/OpenAPI
