package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

@Stateless
public class HorariosRotinaColetaFERM extends HorariosRotinaColeta {

	private static final Log LOG = LogFactory.getLog(HorariosRotinaColetaFERM.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelHorarioRotinaColetasDAO aelHorarioRotinaColetasDAO;
	
	@Override
	public List<DataProgramadaVO> getHorariosRotinaColeta(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHoraAtual, Date dataHoraLimite) {
		List<AelHorarioRotinaColetas> horariosRotina = getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetasFeriadoManha(unidadeSolicitante, unfSeqAvisa, dataHoraAtual);
		return getListaDataProgramada(dataHoraAtual, dataHoraLimite, horariosRotina);
	}

	protected AelHorarioRotinaColetasDAO getAelHorarioRotinaColetasDAO() {
		return aelHorarioRotinaColetasDAO;
	}
	
}
