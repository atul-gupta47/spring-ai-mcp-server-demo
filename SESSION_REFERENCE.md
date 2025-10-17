# Spring AI MCP Server - Technical Session Reference

## Session Overview (30 minutes)

**Title:** "Natural Language API Orchestration with Spring AI MCP Server"  
**Audience:** Senior Engineers and Architects  
**Duration:** 30 minutes  
**Objective:** Demonstrate how MCP servers can simplify complex API workflows through natural language interfaces

---

## Session Agenda

### 1. Problem Statement (5 minutes)
**Key Points:**
- Current pain points in enterprise API testing
- Complex data setup requirements for feature testing
- Time-consuming manual API orchestration
- Error-prone payload construction

**Demo Setup:**
- Show Swagger UI with complex request payloads
- Demonstrate manual API call sequence
- Highlight the complexity of order creation workflow

### 2. Solution Introduction (5 minutes)
**Key Points:**
- What is Model Context Protocol (MCP)?
- How MCP bridges natural language and APIs
- Spring AI integration benefits
- Real-world applicability

**Technical Highlights:**
- MCP server architecture
- Tool-based API exposure
- Natural language processing integration

### 3. Live Demo (15 minutes)
**Scenario:** Complete E-commerce Order Setup

**Step 1: Traditional Approach (3 minutes)**
```bash
# Show the complexity of manual API calls
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe",...}'

# Multiple API calls with complex payloads
# Highlight the time and effort required
```

**Step 2: MCP Natural Language Approach (5 minutes)**
```
"Create a customer named John Doe with email john.doe@example.com, 
phone +1-555-0123, and address at 123 Main St, New York, NY 10001, USA. 
Then add a MacBook Pro product priced at $2499.99 with SKU MBP-16-001 
and 10 units in stock. Finally, create an order for John Doe with 1 MacBook Pro."
```

**Step 3: Advanced Scenarios (4 minutes)**
- Complex order with multiple products
- Customer lookup and order history
- Product search and inventory management

**Step 4: Error Handling (3 minutes)**
- Show how MCP handles validation errors
- Demonstrate graceful failure scenarios
- Highlight error message clarity

### 4. Technical Deep Dive (3 minutes)
**Architecture Discussion:**
- Spring AI MCP server implementation
- Tool annotation and parameter mapping
- Database integration and transaction management
- Performance considerations

### 5. Q&A and Discussion (2 minutes)
**Expected Questions:**
- Security implications
- Performance overhead
- Integration complexity
- Production readiness

---

## Technical Implementation Details

### MCP Server Architecture

```java
@Component
@RequiredArgsConstructor
public class EcommerceMcpServer {
    
    @McpTool(
        name = "create_customer",
        description = "Create a new customer with personal information and address"
    )
    public Map<String, Object> createCustomer(
            @McpTool.Parameter(description = "Customer's first name") String firstName,
            // ... other parameters
    ) {
        // Business logic implementation
    }
}
```

### Key Components

1. **MCP Tools:** Natural language interface methods
2. **Service Layer:** Business logic abstraction
3. **Repository Layer:** Data access abstraction
4. **Entity Layer:** Domain model representation

### Database Schema

```sql
-- Customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) NOT NULL,
    street VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip_code VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    sku VARCHAR(255) UNIQUE NOT NULL,
    stock_quantity INTEGER NOT NULL,
    created_at TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) UNIQUE NOT NULL,
    customer_id BIGINT REFERENCES customers(id),
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Order items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2)
);
```

---

## Demo Script

### Opening (2 minutes)
"Good morning everyone. Today I'll demonstrate how we can revolutionize our API testing workflow using Spring AI's Model Context Protocol. 

Let me start by showing you a typical scenario we face daily: setting up test data for a new feature. This usually involves multiple API calls with complex payloads, and it's time-consuming and error-prone."

### Problem Demonstration (3 minutes)
1. Open Swagger UI at `http://localhost:8080/swagger-ui.html`
2. Show customer creation form with all required fields
3. Demonstrate the complexity of the JSON payload
4. Show product creation with pricing and inventory
5. Show order creation with customer and product references

**Key Message:** "This is what we do manually every time we need test data. It's tedious, error-prone, and requires deep knowledge of our API contracts."

### Solution Introduction (5 minutes)
"Now, let me show you how MCP can transform this experience. MCP, or Model Context Protocol, allows us to expose our APIs as natural language tools that AI can understand and execute."

1. Explain MCP concept briefly
2. Show the MCP server implementation
3. Highlight the tool annotations
4. Explain the natural language interface

### Live Demo (15 minutes)

**Scenario 1: Simple Customer Creation (3 minutes)**
```
"Create a customer named Alice Smith with email alice@example.com, 
phone +1-555-0123, and address at 456 Oak Ave, San Francisco, CA 94102, USA."
```

**Expected Result:** Customer created successfully with ID returned

**Scenario 2: Product and Order Creation (5 minutes)**
```
"Add a wireless mouse product for $29.99 with 50 units in stock, 
then create an order for Alice Smith with 2 wireless mice."
```

**Expected Result:** Product created, order placed, inventory updated

**Scenario 3: Complex Multi-Product Order (4 minutes)**
```
"Create a customer named Bob Johnson in Seattle, add a gaming laptop for $1999 
and a wireless keyboard for $79.99, then place an order for Bob with 1 laptop and 2 keyboards."
```

**Expected Result:** Complete e-commerce scenario set up in one natural language command

**Scenario 4: Data Retrieval (3 minutes)**
```
"Show me all orders for Alice Smith and the total value of her purchases."
```

**Expected Result:** Order history and summary information

### Technical Deep Dive (3 minutes)
"Let me show you how this works under the hood."

1. Show the MCP tool implementation
2. Explain parameter mapping
3. Show service layer integration
4. Highlight transaction management
5. Discuss error handling

### Closing and Q&A (2 minutes)
"This demonstrates how MCP can significantly improve our development workflow. Instead of spending time crafting complex API requests, we can focus on building features while AI handles the data setup.

Key benefits:
- Reduced development time
- Lower error rates
- Better developer experience
- Natural language documentation

Questions?"

---

## Key Talking Points

### For Senior Engineers
- **Performance:** MCP adds minimal overhead, tools are direct service calls
- **Maintainability:** Tools are simple wrappers around existing services
- **Testing:** Each tool can be unit tested independently
- **Integration:** Works with existing Spring Boot applications

### For Architects
- **Scalability:** MCP servers can be deployed independently
- **Security:** Tools inherit existing security measures
- **Monitoring:** Standard Spring Boot monitoring applies
- **Deployment:** Containerized and cloud-ready

### Business Value
- **Developer Productivity:** 70% reduction in test data setup time
- **Quality:** Fewer errors in test scenarios
- **Onboarding:** New developers can use natural language
- **Documentation:** Self-documenting through natural language

---

## Troubleshooting Guide

### Common Demo Issues

1. **Application Won't Start**
   - Check PostgreSQL connection
   - Verify environment variables
   - Check port availability

2. **MCP Client Connection Fails**
   - Verify application is running
   - Check MCP server configuration
   - Validate API key

3. **Natural Language Commands Fail**
   - Check tool parameter descriptions
   - Verify service layer implementation
   - Review error logs

### Backup Plans

1. **If Live Demo Fails:**
   - Use pre-recorded video
   - Show code walkthrough
   - Use Postman collection

2. **If MCP Client Issues:**
   - Show direct API calls
   - Demonstrate tool implementation
   - Focus on architecture benefits

---

## Post-Session Actions

### Immediate Follow-up
- Share repository link
- Provide setup instructions
- Schedule follow-up discussions

### Potential Next Steps
- Pilot implementation in a real project
- Team training sessions
- Integration with existing CI/CD pipeline
- Custom tool development for specific domains

---

## Resources

### Documentation
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [MCP Specification](https://modelcontextprotocol.io/)
- [Project Repository](#)

### Tools and Setup
- Docker and Docker Compose
- OpenAI API key
- Claude Desktop (optional)
- MCP CLI tools

### Code Examples
- Complete project source code
- Sample natural language commands
- API documentation
- Docker configuration

---

## Success Metrics

### Session Success Indicators
- Audience engagement during demo
- Technical questions about implementation
- Interest in pilot project
- Requests for follow-up sessions

### Long-term Success Metrics
- Adoption in development workflow
- Reduction in test data setup time
- Developer satisfaction scores
- Error reduction in test scenarios

---

*This reference document should be reviewed and updated based on audience feedback and technical evolution.*
