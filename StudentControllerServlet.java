package com.luv2code.web.jdc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String command = request.getParameter("command");
			
			if (command == null) {
				command = "LIST";
			}
			
			switch(command) {
			case "LIST":
				listStudents(request, response);
				break;
			case "ADD": 
				addStudent(request, response);
				break;
			case "LOAD":
				loadStudent(request, response);
				break;
			case "UPDATE":
				updateStudent(request, response);
				break;
			case "DELETE":
				deleteStudent(request, response);
				break;
			default:
				listStudents(request, response);
			}
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void deleteStudent(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String studentId = request.getParameter("studentId");
		int theStudentId = Integer.valueOf(studentId);
		studentDbUtil.deleteStudent(theStudentId);
		listStudents(request, response);
	}

	private void updateStudent(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String studentId = request.getParameter("studentId");
		int theStudentId = Integer.valueOf(studentId);
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		Student student = new Student(theStudentId, firstName, lastName, email);
		studentDbUtil.updateStudent(student);
		listStudents(request, response);
	}

	private void loadStudent(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		//read student id from form data
		String studentId = request.getParameter("studentId");
		//get student from database (db util)
		Student theStudent = studentDbUtil.getStudent(studentId);
		//place student in the request attribute
		request.setAttribute("STUDENT", theStudent);
		//send to jsp page: update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}

	private void addStudent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// read student info from form data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// create a new student object
		Student theStudent = new Student(firstName, lastName, email);
		
		// add the student to the database
		studentDbUtil.addStudent(theStudent);
		
		// send back to main page
		listStudents(request, response);
	}

	private void listStudents(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<Student> students = studentDbUtil.getStudents();
		
		request.setAttribute("STUDENTS", students);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("list_student.jsp");
		dispatcher.forward(request, response);
	}

}
