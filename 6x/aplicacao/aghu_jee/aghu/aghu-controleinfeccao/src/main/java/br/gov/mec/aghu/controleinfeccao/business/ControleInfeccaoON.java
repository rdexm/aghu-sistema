package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoFatorPredisponentesDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ControleInfeccaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ControleInfeccaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private MciMvtoFatorPredisponentesDAO mciMvtoFatorPredisponentesDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;

	private static final long serialVersionUID = -3059459667833751522L;


	/**
	 * Método para verificar se para o leito do paciente é necessário fazer
	 * solicitação de medida preventiva de infecção a enfermagem.
	 * 
	 */	
	public boolean verificarNecessidadeMedidaPreventivaInfeccao(
			Integer codigoPaciente) {

		if (verificarExistenciaMedidaPreventivaQuartoPrivativo(codigoPaciente)
				|| verificarExistenciaMedidaPreventivaIsolamento(codigoPaciente)
				|| verificarExistenciaFatorPredisponentes(codigoPaciente)) {
				// Se algum registro foi encontrado, paciente necessita medida preventiva
			return true;
		} else {
			// Se nada foi encontrado, paciente não precisa medida preventiva
			return false;
		}
	}

	public boolean verificaLeitoExclusivoControleInfeccao(AinLeitos leito) {
		if(leito.getQuarto().getIndExclusivInfeccao().equals(DominioSimNao.S)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private MciMvtoMedidaPreventivasDAO getMciMvtoMedidaPreventivasDAO(){
		return mciMvtoMedidaPreventivasDAO;
	}
	
	private boolean verificarExistenciaMedidaPreventivaQuartoPrivativo(
			Integer codigoPaciente) {

		List<MciMvtoMedidaPreventivas> movimentoMedidaPreventivaList = getMciMvtoMedidaPreventivasDAO()
				.pesquisarMedidaPreventiva(codigoPaciente);

		if (movimentoMedidaPreventivaList == null
				|| movimentoMedidaPreventivaList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	private MciMvtoFatorPredisponentesDAO getMciMvtoFatorPredisponentesDAO(){
		return mciMvtoFatorPredisponentesDAO;
	}
	
	private boolean verificarExistenciaMedidaPreventivaIsolamento(
			Integer codigoPaciente) {
		
		List<MciMvtoMedidaPreventivas> movimentoMedidaPreventivaList = getMciMvtoFatorPredisponentesDAO().listaMedidasPreventivaIsolamento(codigoPaciente);

		if (movimentoMedidaPreventivaList == null
				|| movimentoMedidaPreventivaList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

		
	private boolean verificarExistenciaFatorPredisponentes(
			Integer codigoPaciente) {

		List<MciMvtoMedidaPreventivas> movimentoMedidaPreventivaList = getMciMvtoMedidaPreventivasDAO()
				.listarFatoresPredisponentes(codigoPaciente);

		if (movimentoMedidaPreventivaList == null
				|| movimentoMedidaPreventivaList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
}
