package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;

public class FatArqEspelhoProcedAmbDAOTest extends AbstractDAOTest<FatArqEspelhoProcedAmbDAO> {

	@Override
	protected FatArqEspelhoProcedAmbDAO doDaoUnderTests() {
		return new FatArqEspelhoProcedAmbDAO() {
			private static final long serialVersionUID = 849383745639858311L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatArqEspelhoProcedAmbDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatArqEspelhoProcedAmbDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {

	}

	@Test
	public void executarCursorConsultaParte2() {
		if (isEntityManagerOk()) {
			FatCompetencia competencia = new FatCompetencia();
			FatCompetenciaId id = new FatCompetenciaId();
			id.setAno(2012);
			id.setMes(11);
			id.setModulo(DominioModuloCompetencia.AMB);
			id.setDtHrInicio(DateUtil.obterData(2013, 6, 31, 23, 59));
			competencia.setId(id);
			
			List<FatArqEspelhoProcedAmbVO> result = this.daoUnderTests.obterRegistrosGeracaoArquivoBPAParte2(competencia, 1L, 97);
			
			logger.info("###############################################");
			if (result == null) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (FatArqEspelhoProcedAmbVO fatArqEspelhoProcedAmbVO : result) {
					logger.info("Nome paciente " + fatArqEspelhoProcedAmbVO.getNomePaciente());
					logger.info("sexo " + fatArqEspelhoProcedAmbVO.getSexo());
					logger.info("origem inf " + fatArqEspelhoProcedAmbVO.getOrigemInf());
					//order by fields
					logger.info("proced hosp " + fatArqEspelhoProcedAmbVO.getProcedimentoHosp());
					logger.info("atv prof " + fatArqEspelhoProcedAmbVO.getAtvProfissional());
					logger.info("tipo atend " + fatArqEspelhoProcedAmbVO.getTipoAtendimento());
					logger.info("grupo atd " + fatArqEspelhoProcedAmbVO.getGrupoAtendimento());
					logger.info("faixa etaria " + fatArqEspelhoProcedAmbVO.getFaixaEtaria());
				}
			}
		}
	}


	@Override
	protected void finalizeMocks() {
		logger.info("finalizando teste");
		
	}
}