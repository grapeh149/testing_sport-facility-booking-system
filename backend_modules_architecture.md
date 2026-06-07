# Backend Architecture: Court, User, SportType Modules
**Document Date:** May 20, 2026  
**Purpose:** Comprehensive explanation of module architecture, relationships, database connections, and testing strategy

---

## 📋 TABLE OF CONTENTS
1. [System Overview](#system-overview)
2. [Database Schema & Connections](#database-schema--connections)
3. [Module: USER](#module-user)
4. [Module: SPORT TYPE](#module-sport-type)
5. [Module: COURT](#module-court)
6. [Inter-Module Connections](#inter-module-connections)
7. [Testing Strategy & Patterns](#testing-strategy--patterns)
8. [Data Flow Examples](#data-flow-examples)

---

## SYSTEM OVERVIEW

### Architecture Pattern: 3-Layer Layered Architecture

```
┌─────────────────────────────────────────┐
│      REST CONTROLLER (HTTP Endpoints)   │
│    AuthController, CourtController, etc │
└──────────────────┬──────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│          SERVICE LAYER (Business Logic) │
│   AuthService, CourtService, etc        │
└──────────────────┬──────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│      REPOSITORY LAYER (Data Access)     │
│   UserRepository, CourtRepository, etc  │
│      (JPA/Spring Data)                  │
└──────────────────┬──────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│       DATABASE (MySQL/Relational DB)    │
│   Tables: users, courts, sport_types    │
└─────────────────────────────────────────┘
```

### Key Design Patterns
- **DTO Pattern**: Separate DTOs for requests and responses
- **Mapper Pattern**: Convert between entities and DTOs
- **Repository Pattern**: Abstract data access layer
- **Service Pattern**: Centralized business logic
- **LAZY Loading**: Reduce memory footprint for large datasets
- **FETCH JOIN**: Prevent N+1 query problems
- **Soft Delete**: Use isActive flag instead of hard delete
- **Transaction Management**: @Transactional ensures ACID compliance

---

## DATABASE SCHEMA & CONNECTIONS

### Database Tables Structure

#### **USERS Table**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Key Fields:**
- `id`: Primary Key (Long - BIGINT)
- `email`: UNIQUE constraint, used for login
- `password_hash`: BCrypt-hashed password
- `role`: Enum (CUSTOMER, OWNER, ADMIN) stored as STRING
- `is_active`: Boolean flag for soft delete

---

#### **SPORT_TYPES Table**
```sql
CREATE TABLE sport_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    icon_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Key Fields:**
- `id`: Primary Key (Integer)
- `name`: Sport type name (e.g., Tennis, Badminton, Basketball)
- `is_active`: Boolean flag for filtering in queries

---

#### **COURTS Table**
```sql
CREATE TABLE courts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    facility_id BIGINT NOT NULL,
    sport_type_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    surface_type VARCHAR(50),
    is_indoor BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (facility_id) REFERENCES facilities(id),
    FOREIGN KEY (sport_type_id) REFERENCES sport_types(id)
);
```

**Key Fields:**
- `id`: Primary Key (Long - BIGINT)
- `facility_id`: Foreign Key to FACILITIES table
- `sport_type_id`: Foreign Key to SPORT_TYPES table
- `is_indoor`, `surface_type`: Sport-specific attributes

---

### Relationship Diagram

```
┌──────────────┐
│   USERS      │
│ (CUSTOMER,   │ ◄─── Owns ────────┐
│  OWNER, ADMIN)                     │
└──────────────┘                     │
                                     │
                            ┌────────┴─────────┐
                            │   FACILITIES      │
                            │  (User's Sports  │
                            │   Venues)        │
                            └────────┬─────────┘
                                     │
                                     │ Contains
                                     │
                            ┌────────▼─────────┐
                            │    COURTS        │
                            │ (Individual      │
                            │  playing areas)  │
                            └────────┬─────────┘
                                     │
                                     │ For Sport
                                     │
                            ┌────────▼──────────────┐
                            │   SPORT_TYPES        │
                            │ (Tennis, Badminton,  │
                            │  Basketball)         │
                            └──────────────────────┘
```

---

## MODULE: USER

### 1. Entity Class: User.java

```java
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;  // Never exposed to client
    
    @Column(name = "phone")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;  // CUSTOMER, OWNER, ADMIN
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // Soft delete
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**JPA Annotations Explained:**
- `@Entity`: Marks class as JPA entity
- `@Table`: Maps to "users" table
- `@UniqueConstraint`: Ensures email uniqueness at DB level
- `@Id`: Primary key field
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-increment
- `@Enumerated(EnumType.STRING)`: Stores enum as string in DB
- `@Column(nullable = false, unique = true)`: DB constraints

---

### 2. Repository: UserRepository.java

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);          // Find user by email
    Optional<User> findByUsername(String username);    // Find user by username
    List<User> findByRole(UserRole role);             // Find all users with specific role
    List<User> findByIsActive(Boolean isActive);      // Find active/inactive users
}
```

**How Spring Data Works:**
- Extends `JpaRepository<User, Long>` provides CRUD methods automatically
- Custom methods are implemented by Spring Data based on method names
- `findByEmail()` generates: `SELECT * FROM users WHERE email = ?`
- Method naming conventions: `findBy`, `getBy`, `readBy`, `queryBy`

---

### 3. Service Layer: AuthService.java

**Authentication Flow:**

```
USER LOGIN REQUEST
    ↓
1. Find User by Email
    ↓
2. Verify Password (BCrypt)
    ↓
3. Check isActive Flag
    ↓
4. Generate JWT Token
    ↓
5. Return AuthResponse with Token
```

```java
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public AuthResponse login(UserLoginRequest request) {
        // Step 1: Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        User user = userOpt.get();
        
        // Step 2: Verify password using BCrypt
        // BCrypt.matches(plainPassword, hashedPassword)
        boolean passwordMatch = passwordEncoder.matches(
            request.getPassword(),
            user.getPasswordHash()
        );
        
        if (!passwordMatch) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }
        
        // Step 3: Check if account is active (soft delete check)
        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa");
        }
        
        // Step 4: Generate JWT token
        String token = tokenProvider.generateToken(user.getId(), user.getEmail());
        
        // Step 5: Return response (password NOT included)
        return new AuthResponse(
            token, 
            user.getId(), 
            user.getEmail(), 
            user.getFullName(), 
            user.getRole().name(), 
            user.getAvatarUrl()
        );
    }
}
```

**Key Security Features:**
1. **Password Hashing**: Uses BCryptPasswordEncoder (Industry standard)
   - Original password never stored in DB
   - Passwords hashed with salt (default 10 rounds)
   - Same password → different hashes (salt-based)

2. **JWT Token**: For stateless authentication
   - Token includes userId and email
   - Token validation on subsequent requests
   - Token expiration (configurable)

3. **Active Account Check**: Prevents login if `isActive = false`

---

### 4. Controller: AuthController.java

```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    // Endpoint: POST /api/auth/register
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            AuthResponse result = authService.register(request);
            return new ApiResponse<>(true, result, "Đăng ký thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, "Đăng ký thất bại: " + e.getMessage());
        }
    }
    
    // Endpoint: POST /api/auth/login
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            AuthResponse result = authService.login(request);
            return new ApiResponse<>(true, result, "Đăng nhập thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, "Đăng nhập thất bại: " + e.getMessage());
        }
    }
}
```

**REST Endpoints:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- Response wrapped in `ApiResponse<T>` with status and message

---

## MODULE: SPORT TYPE

### 1. Entity: SportType.java

```java
@Entity
@Table(name = "sport_types")
public class SportType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "icon_url", length = 500)
    private String iconUrl;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

**Simplest Module:** No relationships with other entities (referenced by Court, but no join)

---

### 2. Repository: SportTypeRepository.java

```java
@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Integer> {
    List<SportType> findByIsActive(Boolean isActive);  // Find active only
}
```

**Common Query:**
```
SELECT * FROM sport_types WHERE is_active = true
```

---

### 3. Service: SportTypeService.java

```java
@Service
public class SportTypeService {
    
    @Autowired
    private SportTypeRepository sportTypeRepository;
    
    // Get all active sport types (for user UI dropdown)
    public List<SportTypeDTO> getAllActiveSportTypes() {
        List<SportType> sportTypes = sportTypeRepository.findByIsActive(true);
        return sportTypes.stream()
            .map(sportTypeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    // Get all sport types including inactive (for admin)
    public List<SportTypeDTO> getAllSportTypes() {
        List<SportType> sportTypes = sportTypeRepository.findAll();
        return sportTypes.stream()
            .map(sportTypeMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    // Create new sport type
    public SportTypeDTO createSportType(SportTypeDTO sportTypeDTO) {
        // Validation: name cannot be empty
        if (sportTypeDTO.getName() == null || sportTypeDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên loại thể thao không được để trống");
        }
        
        SportType sportType = new SportType();
        sportType.setName(sportTypeDTO.getName());
        sportType.setDescription(sportTypeDTO.getDescription());
        sportType.setIconUrl(sportTypeDTO.getIconUrl());
        sportType.setIsActive(true);
        
        SportType savedSportType = sportTypeRepository.save(sportType);
        return sportTypeMapper.toDTO(savedSportType);
    }
}
```

**REST Endpoints:**
- `GET /api/sport-types` - Get all active sport types
- `POST /api/sport-types` - Create new sport type

---

## MODULE: COURT

### 1. Entity: Court.java with Relationships

```java
@Entity
@Table(name = "courts")
public class Court {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relationship 1: Many courts belong to one facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
    
    // Relationship 2: Many courts for one sport type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_type_id", nullable = false)
    private SportType sportType;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "surface_type", length = 50)
    private String surfaceType;  // e.g., "Grass", "Concrete", "Clay"
    
    @Column(name = "is_indoor", nullable = false)
    private Boolean isIndoor = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // One court has many time slots (for booking)
    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots;
}
```

**JPA Relationship Annotations:**

| Annotation | Meaning | Example |
|------------|---------|---------|
| `@ManyToOne` | Many courts → One facility | `@ManyToOne private Facility facility;` |
| `@OneToMany` | One court → Many timeSlots | `@OneToMany private List<TimeSlot> timeSlots;` |
| `@JoinColumn` | FK column in current table | `@JoinColumn(name = "facility_id")` |
| `fetch = FetchType.LAZY` | Don't load immediately | Saves memory |
| `cascade = CascadeType.ALL` | Cascade delete operations | Delete court → delete timeSlots |
| `orphanRemoval = true` | Remove orphaned children | Removed items auto-deleted from DB |

---

### 2. Repository: CourtRepository.java

```java
@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    
    // Simple queries - Spring Data generates SQL automatically
    List<Court> findByFacilityId(Long facilityId);
    List<Court> findByFacilityIdAndIsActive(Long facilityId, Boolean isActive);
    List<Court> findByIsActive(Boolean isActive);
    
    // Complex query: Prevent N+1 queries using FETCH JOIN
    @Query("SELECT DISTINCT c FROM Court c " +
           "JOIN FETCH c.facility f " +           // Eagerly load facility
           "JOIN FETCH c.sportType st " +         // Eagerly load sportType
           "WHERE c.isActive = true " +
           "AND f.status = 'APPROVED' " +
           "AND (:sportTypeId IS NULL OR st.id = :sportTypeId)")
    List<Court> searchActiveApprovedCourts(@Param("sportTypeId") Long sportTypeId);
    
    // Native SQL query: Direct database access
    @Query(nativeQuery = true, 
           value = "SELECT f.owner_id FROM courts c " +
                   "INNER JOIN facilities f ON c.facility_id = f.id " +
                   "WHERE c.id = :courtId")
    Optional<Long> findOwnerIdByCourtId(@Param("courtId") Long courtId);
}
```

**Query Types:**

1. **Spring Data Query Methods** (Auto-generated)
   ```
   findByFacilityId(1L)
   ↓ Generates SQL:
   SELECT * FROM courts WHERE facility_id = 1
   ```

2. **JPQL Query** (Object-oriented SQL)
   ```
   JOIN FETCH c.facility    ← Prevent N+1 problem
   No separate query for facility, loaded with court
   ```

3. **Native SQL** (Raw SQL)
   ```
   SELECT f.owner_id FROM courts c INNER JOIN facilities f ...
   Direct database access, bypasses ORM
   ```

---

### 3. Service: CourtService.java

```java
@Service
@Transactional
public class CourtService {
    
    @Autowired
    private CourtRepository courtRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private SportTypeRepository sportTypeRepository;
    
    @Autowired
    private CourtMapper courtMapper;
    
    // Get all courts for a specific facility
    public List<CourtDTO> getCourtsByFacility(Long facilityId) {
        // Validate facility exists
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        // Query database
        return courtRepository.findByFacilityId(facilityId)
            .stream()
            .map(courtMapper::toDTO)  // Convert entity → DTO
            .collect(Collectors.toList());
    }
    
    // Create new court with validation
    public CourtDTO createCourt(CourtCreateRequest request) {
        // Step 1: Validate facility exists
        Facility facility = facilityRepository.findById(request.getFacilityId())
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        // Step 2: Validate sport type exists
        SportType sportType = sportTypeRepository.findById(request.getSportTypeId())
            .orElseThrow(() -> new RuntimeException("SportType not found"));
        
        // Step 3: Map request DTO to entity
        Court court = courtMapper.toEntity(request);
        
        // Step 4: Set relationships (foreign keys)
        court.setFacility(facility);
        court.setSportType(sportType);
        
        // Step 5: Set defaults
        court.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        court.setCreatedAt(LocalDateTime.now());
        
        // Step 6: Save to database (INSERT)
        courtRepository.save(court);
        
        // Step 7: Convert entity back to DTO and return
        return courtMapper.toDTO(court);
    }
    
    // Update existing court (partial update)
    public CourtDTO updateCourt(Long id, CourtCreateRequest request) {
        // Fetch court
        Court court = courtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Court not found"));
        
        // Update only provided fields (null-safe update)
        if (request.getName() != null && !request.getName().isEmpty()) {
            court.setName(request.getName());
        }
        
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            court.setDescription(request.getDescription());
        }
        
        if (request.getSurfaceType() != null) {
            court.setSurfaceType(request.getSurfaceType());
        }
        
        if (request.getIsIndoor() != null) {
            court.setIsIndoor(request.getIsIndoor());
        }
        
        // Save changes
        courtRepository.save(court);
        
        return courtMapper.toDTO(court);
    }
}
```

**Key Business Logic:**
1. Validate related entities exist (Facility, SportType)
2. Map DTOs to entities using Mapper
3. Set relationships (FK references)
4. Apply business rules and defaults
5. Persist to database

---

### 4. Controller: CourtController.java

```java
@RestController
@RequestMapping("/api/courts")
@CrossOrigin(origins = "*")
public class CourtController {
    
    @Autowired
    private CourtService courtService;
    
    // Endpoint: GET /api/courts/facility/{facilityId}
    @GetMapping("/facility/{facilityId}")
    public ApiResponse<List<CourtDTO>> getCourtsByFacility(@PathVariable Long facilityId) {
        try {
            List<CourtDTO> result = courtService.getCourtsByFacility(facilityId);
            return new ApiResponse<>(true, result, "Lấy danh sách sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    // Endpoint: GET /api/courts/{id}
    @GetMapping("/{id}")
    public ApiResponse<CourtDTO> getCourtDetails(@PathVariable Long id) {
        try {
            CourtDTO result = courtService.getCourtDetails(id);
            return new ApiResponse<>(true, result, "Lấy thông tin sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    // Endpoint: POST /api/courts
    @PostMapping
    public ApiResponse<CourtDTO> createCourt(@Valid @RequestBody CourtCreateRequest request) {
        try {
            CourtDTO result = courtService.createCourt(request);
            return new ApiResponse<>(true, result, "Tạo sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    // Endpoint: PUT /api/courts/{id}
    @PutMapping("/{id}")
    public ApiResponse<CourtDTO> updateCourt(@PathVariable Long id, @RequestBody CourtCreateRequest request) {
        try {
            CourtDTO result = courtService.updateCourt(id, request);
            return new ApiResponse<>(true, result, "Cập nhật sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
```

**REST API Endpoints:**
- `GET /api/courts/facility/{facilityId}` - Get courts by facility
- `GET /api/courts/{id}` - Get court details
- `POST /api/courts` - Create court
- `PUT /api/courts/{id}` - Update court

---

## INTER-MODULE CONNECTIONS

### Connection Flow: Creating a Court

```
FRONTEND REQUEST:
{
    "name": "Court A",
    "facilityId": 1,
    "sportTypeId": 2,
    "surfaceType": "Grass",
    "isIndoor": false
}
    ↓
CourtController.createCourt(CourtCreateRequest)
    ↓
CourtService.createCourt(CourtCreateRequest)
    ├─ FacilityRepository.findById(1)  ← Validates Facility exists
    │  Returns: Facility object or error
    │
    ├─ SportTypeRepository.findById(2)  ← Validates SportType exists
    │  Returns: SportType object or error
    │
    └─ CourtRepository.save(court)  ← Insert into DB
       Sets:
       - court.facility = Facility(id=1)
       - court.sportType = SportType(id=2)
       - court.createdAt = NOW()
    ↓
DATABASE:
INSERT INTO courts (facility_id, sport_type_id, name, surface_type, is_indoor, created_at)
VALUES (1, 2, 'Court A', 'Grass', false, 2026-05-20 10:30:00)
    ↓
CourtMapper converts Court entity → CourtDTO
    ↓
ApiResponse<CourtDTO> returned to frontend
{
    "success": true,
    "data": {
        "id": 5,
        "name": "Court A",
        "facilityId": 1,
        "sportTypeId": 2,
        "surfaceType": "Grass",
        "isIndoor": false
    },
    "message": "Tạo sân thành công"
}
```

### Cross-Module Validations

**When creating a Court:**
- ✓ User (owner) must have created the Facility
- ✓ Facility must exist
- ✓ SportType must exist and isActive = true
- ✓ Court name must be unique within facility (business rule)

---

## TESTING STRATEGY & PATTERNS

### Test Architecture: Unit Testing with MockMvc

```
┌─────────────────────────────────────┐
│   Test Class (@ExtendWith)          │
├─────────────────────────────────────┤
│   Setup:                            │
│   - @InjectMocks: Controller        │
│   - @Mock: Service                  │
│   - MockMvc: HTTP simulation        │
├─────────────────────────────────────┤
│   Test Method:                      │
│   1. Arrange: Mock setup            │
│   2. Act: Call endpoint             │
│   3. Assert: Verify response        │
└─────────────────────────────────────┘
```

### Test Example: AuthControllerTest.java

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {
    
    @InjectMocks
    private AuthController authController;
    
    @Mock
    private AuthService authService;  // Mocked - not real implementation
    
    private MockMvc mockMvc;  // Simulates HTTP requests
    private ObjectMapper objectMapper;  // JSON serialization
    
    @BeforeEach
    void setUp() {
        // Create MockMvc using standalone controller setup
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setValidator(new org.springframework.validation.Validator() {
                public boolean supports(Class<?> c) { return true; }
                public void validate(Object o, org.springframework.validation.Errors e) { }
            })
            .build();
    }
    
    @Test
    @DisplayName("Should register successfully")
    void testRegisterSuccess() throws Exception {
        // ARRANGE: Set up test data
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        
        AuthResponse mockResponse = new AuthResponse(
            "jwt_token_here", 
            1L, 
            "test@example.com", 
            "Test User", 
            "CUSTOMER", 
            null
        );
        
        // Mock the service method
        when(authService.register(any(UserRegistrationRequest.class)))
            .thenReturn(mockResponse);
        
        // ACT: Make HTTP request
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        // ASSERT: Verify response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Đăng ký thành công"));
    }
    
    @Test
    @DisplayName("Should handle register failure - email exists")
    void testRegisterFailure() throws Exception {
        // ARRANGE
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");
        
        // Mock service to throw exception
        when(authService.register(any(UserRegistrationRequest.class)))
            .thenThrow(new RuntimeException("Email đã được đăng ký"));
        
        // ACT & ASSERT
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").containsString("Email đã được đăng ký"));
    }
    
    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() throws Exception {
        // ARRANGE
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        AuthResponse mockResponse = new AuthResponse(
            "jwt_token", 1L, "test@example.com", "Test User", "CUSTOMER", null
        );
        
        when(authService.login(any(UserLoginRequest.class)))
            .thenReturn(mockResponse);
        
        // ACT & ASSERT
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.token").value("jwt_token"))
        .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }
    
    @Test
    @DisplayName("Should handle login failure - wrong password")
    void testLoginFailure() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");
        
        when(authService.login(any(UserLoginRequest.class)))
            .thenThrow(new RuntimeException("Email hoặc mật khẩu không đúng"));
        
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false));
    }
}
```

### Test Example: CourtControllerTest.java

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("CourtController Unit Tests")
class CourtControllerTest {
    
    @InjectMocks
    private CourtController courtController;
    
    @Mock
    private CourtService courtService;
    
    private MockMvc mockMvc;
    private CourtDTO courtDTO;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courtController).build();
        
        // Prepare test data
        courtDTO = new CourtDTO();
        courtDTO.setId(1L);
        courtDTO.setName("Court 1");
        courtDTO.setFacilityId(1L);
        courtDTO.setSportTypeId(2);
    }
    
    @Test
    @DisplayName("Should get courts by facility")
    void testGetCourtsByFacility() throws Exception {
        // ARRANGE
        when(courtService.getCourtsByFacility(1L))
            .thenReturn(List.of(courtDTO));
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/courts/facility/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("Court 1"));
    }
    
    @Test
    @DisplayName("Should create court successfully")
    void testCreateCourt() throws Exception {
        // ARRANGE
        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("New Court");
        request.setFacilityId(1L);
        request.setSportTypeId(2);
        request.setSurfaceType("Grass");
        request.setIsIndoor(false);
        
        CourtDTO responseDTO = new CourtDTO();
        responseDTO.setId(2L);
        responseDTO.setName("New Court");
        
        when(courtService.createCourt(any(CourtCreateRequest.class)))
            .thenReturn(responseDTO);
        
        // ACT & ASSERT
        mockMvc.perform(
            post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.name").value("New Court"));
    }
    
    @Test
    @DisplayName("Should handle court not found exception")
    void testGetCourtDetails_NotFound() throws Exception {
        // ARRANGE
        when(courtService.getCourtDetails(999L))
            .thenThrow(new RuntimeException("Court not found"));
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/courts/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false));
    }
}
```

### Test Patterns Used

| Pattern | Usage | Example |
|---------|-------|---------|
| **AAA Pattern** | Arrange-Act-Assert | Setup mocks → Call endpoint → Verify |
| **Mocking** | Replace real services | `@Mock AuthService` |
| **Mockito.when()** | Define mock behavior | `when(service.method()).thenReturn(value)` |
| **Exception Testing** | Test error scenarios | `when(...).thenThrow(Exception)` |
| **JsonPath** | Verify JSON response | `jsonPath("$.success").value(true)` |
| **DisplayName** | Readable test names | `@DisplayName("Should...")` |

---

## DATA FLOW EXAMPLES

### Example 1: User Registration Flow

```
┌─────────────────────────────────────────────────────────────┐
│ FRONTEND: User fills registration form                      │
│ {email: "john@example.com", password: "secure123", ...}    │
└──────────────────┬──────────────────────────────────────────┘
                   │ HTTP POST
                   ↓
┌─────────────────────────────────────────────────────────────┐
│ AuthController.register(UserRegistrationRequest)            │
│ - Receives JSON request                                     │
│ - Validates @Valid annotations                             │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────────────┐
│ AuthService.register()                                      │
│ 1. Check if email exists: userRepository.findByEmail()     │
│ 2. If exists: throw RuntimeException                        │
│ 3. If not: create User entity                              │
│ 4. Hash password: passwordEncoder.encode(plainPassword)    │
│    plainPassword: "secure123"                              │
│    → BCrypt hashed: "$2a$10$..."                           │
│ 5. Save to DB: userRepository.save(user)                  │
│ 6. Generate JWT: tokenProvider.generateToken()            │
│ 7. Return AuthResponse (token, userId, email, etc)       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────────────┐
│ DATABASE OPERATION (JPA)                                    │
│ EntityManager generates SQL:                                │
│ INSERT INTO users                                           │
│   (email, password_hash, full_name, role, is_active, ...)  │
│ VALUES                                                      │
│   ('john@example.com', '$2a$10$...', 'John Doe', 'CUSTOMER', true, ...)
│                                                             │
│ Database creates new row with auto-incremented id = 5     │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────────────┐
│ RESPONSE: ApiResponse<AuthResponse>                         │
│ {                                                           │
│   "success": true,                                          │
│   "message": "Đăng ký thành công",                         │
│   "data": {                                                 │
│     "token": "eyJhbGciOiJIUzI1NiIs...",                    │
│     "userId": 5,                                            │
│     "email": "john@example.com",                            │
│     "role": "CUSTOMER"                                      │
│   }                                                         │
│ }                                                           │
└─────────────────────────────────────────────────────────────┘
```

### Example 2: Creating a Court Flow

```
┌──────────────────────────────────────────────────────────────┐
│ FRONTEND: Admin fills court creation form                    │
│ {                                                            │
│   "name": "Court 1",                                         │
│   "facilityId": 3,                                           │
│   "sportTypeId": 2,                                          │
│   "surfaceType": "Grass",                                    │
│   "isIndoor": false                                          │
│ }                                                            │
└────────────────────┬─────────────────────────────────────────┘
                     │ HTTP POST /api/courts
                     ↓
┌──────────────────────────────────────────────────────────────┐
│ CourtController.createCourt(CourtCreateRequest)              │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────────┐
│ CourtService.createCourt()                                   │
│                                                              │
│ Step 1: Validate Facility                                    │
│ facilityRepository.findById(3)                               │
│ SELECT * FROM facilities WHERE id = 3                        │
│ ✓ Found: Facility(id=3, name="My Sports Center")            │
│                                                              │
│ Step 2: Validate SportType                                   │
│ sportTypeRepository.findById(2)                              │
│ SELECT * FROM sport_types WHERE id = 2                       │
│ ✓ Found: SportType(id=2, name="Badminton")                  │
│                                                              │
│ Step 3: Map DTO to Entity                                    │
│ court = courtMapper.toEntity(request)                        │
│                                                              │
│ Step 4: Set Relationships                                    │
│ court.setFacility(facility)       → facility_id = 3        │
│ court.setSportType(sportType)     → sport_type_id = 2      │
│ court.setCreatedAt(LocalDateTime.now())                     │
│                                                              │
│ Step 5: Save to Database                                     │
│ courtRepository.save(court)                                  │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────────┐
│ DATABASE OPERATION                                           │
│ INSERT INTO courts                                           │
│   (facility_id, sport_type_id, name, surface_type, ...)     │
│ VALUES                                                       │
│   (3, 2, 'Court 1', 'Grass', ...)                           │
│                                                              │
│ Database auto-generates: id = 15                             │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────────┐
│ Convert back to DTO and return                               │
│ CourtDTO = courtMapper.toDTO(court)                          │
│                                                              │
│ RESPONSE:                                                    │
│ {                                                            │
│   "success": true,                                           │
│   "data": {                                                  │
│     "id": 15,                                                │
│     "name": "Court 1",                                       │
│     "facilityId": 3,                                         │
│     "sportTypeId": 2,                                        │
│     "surfaceType": "Grass",                                  │
│     "isIndoor": false                                        │
│   },                                                         │
│   "message": "Tạo sân thành công"                           │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
```

---

## KEY TAKEAWAYS

### Architecture Summary
✓ **3-Layer Architecture**: Controller → Service → Repository → DB
✓ **DTO Pattern**: Isolate API contracts from internal entities
✓ **Lazy Loading**: Reduce memory via @ManyToOne(fetch = FetchType.LAZY)
✓ **FETCH JOIN**: Prevent N+1 queries with JOIN FETCH
✓ **Soft Delete**: Use isActive flag instead of hard delete
✓ **Transaction Management**: @Transactional ensures ACID
✓ **Exception Handling**: @ControllerAdvice for centralized error handling

### Module Relationships
```
User ← owns → Facility ← contains → Court ← has → SportType
                        ↓
                    TimeSlot ← for booking
```

### Testing Approach
✓ **Unit Tests**: Mock services, test controllers with MockMvc
✓ **AAA Pattern**: Arrange mocks → Act on endpoint → Assert response
✓ **Standalone Setup**: Test controller without full Spring context
✓ **DisplayName**: Readable test names
✓ **JsonPath**: Verify JSON structure and values

### Security Features
✓ **BCrypt Hashing**: Password never stored plain
✓ **JWT Tokens**: Stateless authentication
✓ **Active Account Check**: Soft delete protection
✓ **DTO Exclusions**: Password never exposed in responses
✓ **Validation Annotations**: @Valid on DTOs
