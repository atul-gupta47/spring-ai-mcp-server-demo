# MCP Client Setup Guide

This guide explains how to set up and use MCP clients to interact with the E-commerce MCP Server demo.

## Prerequisites

- OpenAI API key
- Java 17+ installed
- Docker and Docker Compose (for running the demo)
- Node.js (for MCP CLI tools)

## Option 1: Claude Desktop Integration

### Step 1: Install Claude Desktop

1. Download Claude Desktop from [Anthropic's website](https://claude.ai/download)
2. Install and launch the application

### Step 2: Configure MCP Server

1. Create or edit the Claude Desktop configuration file:

**macOS:** `~/Library/Application Support/Claude/claude_desktop_config.json`
**Windows:** `%APPDATA%\Claude\claude_desktop_config.json`

2. Add the following configuration:

```json
{
  "mcpServers": {
    "ecommerce-demo": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/your/ecommerce-mcp-demo.jar"
      ],
      "env": {
        "OPENAI_API_KEY": "your-openai-api-key-here",
        "SPRING_DATASOURCE_URL": "jdbc:postgresql://localhost:5432/ecommerce_demo",
        "SPRING_DATASOURCE_USERNAME": "postgres",
        "SPRING_DATASOURCE_PASSWORD": "postgres"
      }
    }
  }
}
```

### Step 3: Restart Claude Desktop

1. Close Claude Desktop completely
2. Restart the application
3. The MCP server should now be available in Claude

### Step 4: Test the Integration

In Claude Desktop, try these commands:

```
"Create a customer named John Doe with email john.doe@example.com and address in New York"
```

```
"Add a laptop product for $999 with 10 units in stock"
```

```
"Create an order for John Doe with 1 laptop"
```

## Option 2: MCP CLI Tools

### Step 1: Install MCP CLI

```bash
npm install -g @modelcontextprotocol/cli
```

### Step 2: Start the Demo Application

```bash
# Start the application and database
docker-compose up --build

# Or run locally
./gradlew bootRun
```

### Step 3: Connect to MCP Server

```bash
# Connect to the MCP server
mcp connect http://localhost:8080/mcp

# Or use the MCP client directly
mcp client --server http://localhost:8080/mcp
```

### Step 4: Use MCP Tools

```bash
# List available tools
mcp list-tools

# Call a specific tool
mcp call-tool create_customer --params '{
  "firstName": "Alice",
  "lastName": "Smith",
  "email": "alice@example.com",
  "phone": "+1-555-0123",
  "street": "123 Main St",
  "city": "San Francisco",
  "state": "CA",
  "zipCode": "94102",
  "country": "USA"
}'
```

## Option 3: Custom MCP Client

### Step 1: Create a Simple MCP Client

```python
import requests
import json

class EcommerceMcpClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.mcp_endpoint = f"{base_url}/mcp"
    
    def call_tool(self, tool_name, params):
        payload = {
            "tool": tool_name,
            "parameters": params
        }
        
        response = requests.post(
            f"{self.mcp_endpoint}/call",
            json=payload,
            headers={"Content-Type": "application/json"}
        )
        
        return response.json()
    
    def create_customer(self, **kwargs):
        return self.call_tool("create_customer", kwargs)
    
    def create_product(self, **kwargs):
        return self.call_tool("create_product", kwargs)
    
    def create_order(self, customer_id, order_items_json):
        return self.call_tool("create_order", {
            "customerId": customer_id,
            "orderItemsJson": order_items_json
        })

# Usage example
client = EcommerceMcpClient()

# Create a customer
customer = client.create_customer(
    firstName="John",
    lastName="Doe",
    email="john.doe@example.com",
    phone="+1-555-0123",
    street="123 Main St",
    city="New York",
    state="NY",
    zipCode="10001",
    country="USA"
)

print(f"Customer created: {customer}")
```

## Option 4: Postman/Insomnia Integration

### Step 1: Import MCP Endpoints

Create a new collection with the following endpoints:

**Base URL:** `http://localhost:8080`

**Endpoints:**
- `POST /mcp/call` - Call MCP tools
- `GET /mcp/tools` - List available tools
- `GET /mcp/health` - Check MCP server health

### Step 2: Create Tool Call Requests

**Create Customer Tool Call:**
```json
{
  "tool": "create_customer",
  "parameters": {
    "firstName": "Alice",
    "lastName": "Smith",
    "email": "alice@example.com",
    "phone": "+1-555-0123",
    "street": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102",
    "country": "USA"
  }
}
```

**Create Product Tool Call:**
```json
{
  "tool": "create_product",
  "parameters": {
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "category": "Electronics",
    "sku": "WM-001",
    "stockQuantity": 50
  }
}
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the application is running on port 8080
   - Check if PostgreSQL is accessible
   - Verify Docker containers are running

2. **Authentication Errors**
   - Verify OpenAI API key is correct
   - Check environment variables
   - Ensure API key has proper permissions

3. **Tool Not Found**
   - Check if MCP server is properly configured
   - Verify tool names match exactly
   - Review server logs for errors

4. **Parameter Validation Errors**
   - Check parameter names and types
   - Verify required parameters are provided
   - Review tool descriptions for correct format

### Debug Mode

Enable debug logging by setting the following environment variable:

```bash
export SPRING_AI_LOG_LEVEL=DEBUG
```

Or add to your application configuration:

```yaml
logging:
  level:
    com.example.ecommerce: DEBUG
    org.springframework.ai: DEBUG
```

## Sample Natural Language Commands

### Customer Management
```
"Create a customer named Bob Johnson with email bob@example.com and address in Seattle, WA"
```

```
"Find the customer with email alice@example.com"
```

### Product Management
```
"Add a gaming laptop product for $1999 with 5 units in stock"
```

```
"Find all products in the Electronics category"
```

```
"Search for products with 'mouse' in the name"
```

### Order Management
```
"Create an order for Bob Johnson with 1 gaming laptop"
```

```
"Show me all orders for customer with email bob@example.com"
```

### Complex Scenarios
```
"Set up a complete e-commerce scenario: Create customer Sarah Wilson in Chicago, add a wireless keyboard for $79.99, and place an order for 2 keyboards"
```

## Advanced Usage

### Batch Operations
```
"Create 5 customers with different names and addresses, then add 3 different products, and create orders for each customer"
```

### Data Analysis
```
"Show me the total value of all orders and the most popular product"
```

### Inventory Management
```
"Update the stock quantity for all products to 100 units"
```

## Security Considerations

1. **API Key Protection**
   - Never commit API keys to version control
   - Use environment variables or secure key management
   - Rotate keys regularly

2. **Network Security**
   - Use HTTPS in production
   - Implement proper authentication
   - Restrict access to MCP endpoints

3. **Input Validation**
   - All inputs are validated by the service layer
   - SQL injection protection through JPA
   - XSS protection through proper encoding

## Performance Tips

1. **Connection Pooling**
   - Database connections are pooled automatically
   - Adjust pool size based on load

2. **Caching**
   - Consider adding Redis for frequently accessed data
   - Implement query result caching

3. **Monitoring**
   - Use Spring Boot Actuator for health checks
   - Monitor MCP tool execution times
   - Set up alerts for failures

## Next Steps

1. **Custom Tools**
   - Add domain-specific tools for your use case
   - Implement batch operations
   - Add reporting and analytics tools

2. **Integration**
   - Connect to existing systems
   - Implement webhook notifications
   - Add real-time updates

3. **Scaling**
   - Deploy to cloud platforms
   - Implement load balancing
   - Add horizontal scaling support
