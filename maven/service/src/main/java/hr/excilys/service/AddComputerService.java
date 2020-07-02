package service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dto.DTOComputer;
import dto.mapper.ComputerDTOMapper;
import model.Computer;
import persistence.DAOComputer;

@Service
public class AddComputerService {

	private final DAOComputer daoComputer;
	private final ComputerDTOMapper computerDTOMapper;

	@Autowired
	public AddComputerService(DAOComputer daoComputer, ComputerDTOMapper computerDTOMapper) {

		this.daoComputer = daoComputer;
		this.computerDTOMapper = computerDTOMapper;
	}

	public boolean addComputer(DTOComputer dtoComputer) {

		Optional<Computer> computer = computerDTOMapper.fromDTO(dtoComputer);
		if (computer.isPresent()) {

			return daoComputer.insertComputer(computer.get());
		}
		
		return false;
	}
}
