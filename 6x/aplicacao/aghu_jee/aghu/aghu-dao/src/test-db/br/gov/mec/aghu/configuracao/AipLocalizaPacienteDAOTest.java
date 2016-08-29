package br.gov.mec.aghu.configuracao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.AipLocalizaPaciente;
import br.gov.mec.aghu.paciente.dao.AipLocalizaPacienteDAO;

public class AipLocalizaPacienteDAOTest extends AbstractDAOTest<AipLocalizaPacienteDAO> {
	
	@Override
	protected AipLocalizaPacienteDAO doDaoUnderTests() {
		return new AipLocalizaPacienteDAO() {
			private static final long serialVersionUID = -4408840644270807605L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AipLocalizaPacienteDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	@Test
	public void listarTriagemRealizadaEmergenciaTestaConsultaPostgreSQL() {
		List<AipLocalizaPaciente> lista = null;
		
		if (isEntityManagerOk()) {
			//assert
			try {
				//retornar sem dados
				lista = getDaoUnderTests().listarPacientesPorAtendimento(null, null);
				Assert.assertTrue(lista.isEmpty());
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}			
		}
	}
}