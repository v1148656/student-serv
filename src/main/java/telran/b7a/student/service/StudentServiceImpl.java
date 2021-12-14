package telran.b7a.student.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.b7a.student.dao.StudentMongoRepository;
import telran.b7a.student.dto.ScoreDto;
import telran.b7a.student.dto.StudentCredentialsDto;
import telran.b7a.student.dto.StudentDto;
import telran.b7a.student.dto.UpdateStudentDto;
import telran.b7a.student.dto.exception.StudentNotFoundException;
import telran.b7a.student.model.Student;

@Service
public class StudentServiceImpl implements StudentService {

	StudentMongoRepository studentRepository;
//	StudentRepository studentRepository;
	ModelMapper modelMapper;
	
	@Autowired
	public StudentServiceImpl(StudentMongoRepository studentRepository, ModelMapper modelMapper) {
		this.studentRepository = studentRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public boolean addStudent(StudentCredentialsDto studentCredentialsDto) {
		if (studentRepository.findById(studentCredentialsDto.getId()).isPresent()) {
			return false;
		}
//		Student student = new Student(studentCredentialsDto.getId(), studentCredentialsDto.getName(), studentCredentialsDto.getPassword());
		Student student = modelMapper.map(studentCredentialsDto, Student.class);
		studentRepository.save(student);
		return true;
	}

	@Override
	public StudentDto findStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		return modelMapper.map(student, StudentDto.class);
//		return StudentDto.builder()
//							.id(student.getId())
//							.name(student.getName())
//							.scores(student.getScores())
//							.build();
	}

	@Override
	public StudentDto deleteStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		studentRepository.deleteById(id);
		return modelMapper.map(student, StudentDto.class);
//		return StudentDto.builder()
//				.id(student.getId())
//				.name(student.getName())
//				.scores(student.getScores())
//				.build();
	}

	@Override
	public StudentCredentialsDto updateStudent(Integer id, UpdateStudentDto updateStudentDto) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		student.setName(updateStudentDto.getName());
		student.setPassword(updateStudentDto.getPassword());
		return modelMapper.map(student, StudentCredentialsDto.class);
//		return StudentCredentialsDto.builder()
//				.id(id)
//				.name(student.getName())
//				.password(student.getPassword())
//				.build();
	}

	@Override
	public boolean addScore(Integer id, ScoreDto scoreDto) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
		studentRepository.save(student);
		return res;
	}

	@Override
	public List<StudentDto> findStudentsByName(String name) {
		return studentRepository.findByNameIgnoreCase(name)
								.map(s -> modelMapper.map(s, StudentDto.class))
								.collect(Collectors.toList());
	}

	@Override
	public long getStudentsNamesQuantity(List<String> names) {
		return studentRepository.countByNameInIgnoreCase(names);
	}

	@Override
	public List<StudentDto> getStudentsByExamScore(String exam, int score) {
		return studentRepository.findByExamAndScoreGreaterEqualsThan(exam, score)
				.map(s -> modelMapper.map(s, StudentDto.class))
				.collect(Collectors.toList());
	}

}