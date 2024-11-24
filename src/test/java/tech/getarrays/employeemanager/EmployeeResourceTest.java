package tech.getarrays.employeemanager;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.getarrays.employeemanager.model.Employee;
import tech.getarrays.employeemanager.service.EmployeeService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class EmployeeResourceTest {

    @InjectMocks
    private EmployeeResource employeeResource;

    @Mock
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_ReturnsListOfEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "John Doe", "john@example.com", "HR", "Developer", "123456789", "image.jpg", "E123"));

        when(employeeService.findAllEmployees()).thenReturn(employees);

        List<Employee> result = employeeResource.getAllEmployees().getBody();

        assertEquals(employees, result);
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsEmployee() {
        Long id = 1L;
        Employee employee = new Employee(id, "John Doe", "john@example.com", "HR", "Developer", "123456789", "image.jpg", "E123");

        when(employeeService.findEmployeeById(id)).thenReturn(employee);

        Employee result = employeeResource.getEmployeeById(id).getBody();

        assertEquals(employee, result);
    }

    @Test
    void getEmployeeById_NonExistingId_ThrowsEntityNotFoundException() {
        Long id = 1L;

        when(employeeService.findEmployeeById(id)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> employeeResource.getEmployeeById(id));
    }

    @Test
    void addEmployee_ValidEmployee_ReturnsCreatedEmployee() {
        Employee employeeToAdd = new Employee(null,"John Doe", "john@example.com", "HR", "Developer", "123456789", "image.jpg", "E123");
        Employee addedEmployee = new Employee(1L, "John Doe", "john@example.com", "HR", "Developer", "123456789", "image.jpg", "E123");

        when(employeeService.addEmployee(employeeToAdd)).thenReturn(addedEmployee);

        Employee result = employeeResource.addEmployee(employeeToAdd).getBody();

        assertEquals(addedEmployee, result);
    }

    @Test
    void deleteEmployee_ExistingId_DeletesEmployee() {
        Long id = 1L;

        ResponseEntity<?> response = employeeResource.deleteEmployee(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(employeeService, times(1)).deleteEmployee(id);
    }

    @Test
    void deleteEmployee_NonExistingId_ThrowsEntityNotFoundException() {
        Long id = 1L;

        doThrow(EntityNotFoundException.class).when(employeeService).deleteEmployee(id);

        assertThrows(EntityNotFoundException.class, () -> employeeResource.deleteEmployee(id));
    }

    @Test
    void deleteEmployee_UnexpectedException_ReturnsInternalServerError() {
        Long id = 1L;

        doThrow(RuntimeException.class).when(employeeService).deleteEmployee(id);

        ResponseEntity<?> response = employeeResource.deleteEmployee(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ResponseEntity<?>) response).getStatusCode());
        assertEquals("Failed to delete employee with ID: " + id, response.getBody());
    }


    @Test
    void updateEmployee_ValidEmployee_ReturnsUpdatedEmployee() {
        Employee employeeToUpdate = new Employee(1L, "John Doe", "john@example.com", "HR", "Developer", "123456789", "image.jpg", "E123");
        Employee updatedEmployee = new Employee(1L, "Updated Name", "updated@example.com", "Updated departament", "Updated Job", "987654321", "updated.jpg", "E456");

        when(employeeService.updateEmployee(employeeToUpdate)).thenReturn(updatedEmployee);

        Employee result = employeeResource.updateEmployee(employeeToUpdate).getBody();

        assertEquals(updatedEmployee, result);
    }

    @Test
    void updateEmployee_NonExistingEmployee_ThrowsEntityNotFoundException() {
        Employee nonExistingEmployee = new Employee(999L, "Non Existing", "nonexisting@example.com", "Non Existing", "Non Existing", "000000000", "nonexisting.jpg", "E999");

        when(employeeService.updateEmployee(nonExistingEmployee)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> employeeResource.updateEmployee(nonExistingEmployee));
    }

}