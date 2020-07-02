package service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dto.DTOPagination;
import dto.mapper.PaginationDTOMapper;
import model.Pagination;
import model.Computer;
import persistence.DAOComputer;
import validator.PageValidator;

@Service
public class DashboardService {

	private final static int ASC = 1;
	private final static int DESC = 0;

	private final DAOComputer daoComputer;
	private final PaginationDTOMapper paginationDTOMapper;
	private final PageValidator pageValidator;

	@Autowired
	public DashboardService(DAOComputer daoComputer, PaginationDTOMapper paginationDTOMapper,
			PageValidator pageValidator) {

		this.daoComputer = daoComputer;
		this.paginationDTOMapper = paginationDTOMapper;
		this.pageValidator = pageValidator;
	}

	public List<Computer> getComputersRows(Pagination page) {
		if (StringUtils.isNotEmpty(page.getOrder())) {
			if (page.getDirection() == 1) {

					return daoComputer.getComputersSort(page.getPage(), page.getLines(), page.getSearch(),
							page.getOrder(), ASC);
			} else {

					return daoComputer.getComputersSort(page.getPage(), page.getLines(), page.getSearch(),
							page.getOrder(), DESC);
			}
		} else {

				return daoComputer.getComputersRows(page.getPage(), page.getLines(), page.getSearch());
		}
	}

	public int getCountComputer(String search) {

			return daoComputer.countComputer(search);
	}

	public boolean deleteComputer(List<String> selected) {

		if (selected.isEmpty()) {

			return false;
		}

		selected.stream().forEach(e -> {
			daoComputer.deleteComputer(Long.valueOf(e));
		});

		return true;
	}

	public Pagination paginate(DTOPagination dtoPagination) {

		Pagination page = paginationDTOMapper.getPage(dtoPagination, getCountComputer(dtoPagination.getSearch()));
		pageValidator.checkPageFields(page);

		return page;
	}
}
