package hr.excilys.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.excilys.mapper.ComputerMapper;
import hr.excilys.model.Computer;

@Repository
public final class DAOComputer {

	private static final int ASC = 1;
	private final static Logger LOGGER = LoggerFactory.getLogger(DAOComputer.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private ComputerMapper computerMapper;

	public List<Computer> getComputers() throws SQLException {

		try {
			return namedParameterJdbcTemplate.query(EnumQuery.ALLCOMPUTER.getQuery(), computerMapper);

		} catch (DataAccessException e) {
			LOGGER.error("Cannot get all Computers, probleme in the Query");

			return new ArrayList<>();
		}
	}

	public List<Computer> getComputersRows(int page, int lines, String search) throws SQLException {

		search = prepareSearch(search);

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("search", search)
					.addValue("limit", lines).addValue("offset", lines * (page - 1));

			return namedParameterJdbcTemplate.query(EnumQuery.PAGECOMPUTER.getQuery(), parameterMap, computerMapper);
		} catch (DataAccessException e) {
			LOGGER.error("Cannot get Computers at {} page and with {} lines, probleme in the Query maybe search -> {}",
					page, lines, search);

			return new ArrayList<>();
		}
	}

	public Optional<Computer> getComputerById(long id) throws SQLException {

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("id_computer", id);
			List<Computer> computer = namedParameterJdbcTemplate.query(EnumQuery.IDCOMPUTER.getQuery(), parameterMap,
					computerMapper);

			if (computer != null) {
				LOGGER.info("Computer with id = {} : Found", id);

				return Optional.of(computer.get(0));
			}
		} catch (DataAccessException e) {
			LOGGER.error("Computer with id = {} : Probleme in Query", id);
		}
		LOGGER.info("Computer with id = {} : NOT Found", id);

		return Optional.empty();
	}

	public int insertComputer(Computer computer) throws SQLException {

		try {
			MapSqlParameterSource parameterMap;
			if (computer.getCompany().getId() == 0) {
				parameterMap = new MapSqlParameterSource().addValue("name", computer.getName())
						.addValue("introduced", computer.getIntroduced())
						.addValue("discontinued", computer.getDiscontinued()).addValue("id_company", null);
			} else {
				parameterMap = new MapSqlParameterSource().addValue("name", computer.getName())
						.addValue("introduced", computer.getIntroduced())
						.addValue("discontinued", computer.getDiscontinued())
						.addValue("id_company", computer.getCompany().getId());
			}

			return namedParameterJdbcTemplate.update(EnumQuery.INSERTCOMPUTER.getQuery(), parameterMap);
		} catch (DataAccessException e) {
			LOGGER.error("Computer NOT added, probleme in query : Check fields");

			return 0;
		}
	}

	public int updateComputer(Computer computer) throws SQLException {

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("name", computer.getName())
					.addValue("introduced", computer.getIntroduced())
					.addValue("discontinued", computer.getDiscontinued())
					.addValue("id_company", computer.getCompany().getId());

			return namedParameterJdbcTemplate.update(EnumQuery.UPDATECOMPUTER.getQuery(), parameterMap);
		} catch (DataAccessException e) {
			LOGGER.error("Computer NOT updated, probleme in query : Check fields");

			return 0;
		}
	}

	public int deleteComputer(long id) throws SQLException {

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("id_computer", id);

			return namedParameterJdbcTemplate.update(EnumQuery.DELETECOMPUTER.getQuery(), parameterMap);
		} catch (DataAccessException e) {
			LOGGER.error("Computer NOT deleted, probleme in query");

			return 0;
		}
	}

	public int countComputer(String search) throws SQLException {

		search = prepareSearch(search);

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("search", search);

			return namedParameterJdbcTemplate.queryForObject(EnumQuery.COUNTCOMPUTER.getQuery(), parameterMap,
					Integer.class);
		} catch (DataAccessException e) {
			LOGGER.info("ResultSet Empty");

			return -1;
		}
	}

	public List<Computer> getComputersSort(int page, int lines, String search, String order, int direct)
			throws SQLException {
		search = prepareSearch(search);

		try {
			MapSqlParameterSource parameterMap = new MapSqlParameterSource().addValue("search", search)
					.addValue("limit", lines).addValue("offset", lines * (page - 1));
			String query = getOrderQuery(order, direct);

			return namedParameterJdbcTemplate.query(query, parameterMap, computerMapper);
		} catch (DataAccessException e) {
			LOGGER.error(
					"Probleme in query, check fields : page = {}, lines = {}, search = {}, order = {}, direct = {}",
					page, lines, search, order, direct);

			return new ArrayList<>();
		}
	}

	private String getOrderQuery(String order, int direct) {

		if (order.equals("computer")) {
			if (direct == ASC) {
				return EnumQuery.SORTPAGECOMPUTERASC.getQuery();
			} else {
				return EnumQuery.SORTPAGECOMPUTERDESC.getQuery();
			}
		} else {
			if (direct == ASC) {
				return EnumQuery.SORTPAGECOMPANYASC.getQuery();
			} else {
				return EnumQuery.SORTPAGECOMPANYDESC.getQuery();
			}
		}
	}

	private String prepareSearch(String search) {

		if (search == null) {
			search = "%";
		} else if (search.contains("%")) {
			search = search.replace("%", "\\%");
		} else {
			search = "%" + search + "%";
		}

		return search;
	}
}