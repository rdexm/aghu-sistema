package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasRetornoHqlVO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioEscalaDeSalasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaDeSalasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 66307710373139735L;

	/**
	 * ORADB FUNCTION MBCC_EQUIPE_SALA
	 * 
	 * Retorna as equipes de uma determinada sala cirurgica
	 * 
	 * #22393
	 */
	public String obterEquipeSala(Short unidade, Short sala, MbcTurnos turno, DominioDiaSemana diaSemana) {
		
		List<RelatorioEscalaDeSalasRetornoHqlVO> listMbcSalaCirurgica = getMbcSalaCirurgicaDAO().pesquisarEscalaDeSalasRelatorio(unidade, sala, turno, diaSemana);
		
		String retorno = "";
		if(listMbcSalaCirurgica == null || listMbcSalaCirurgica.isEmpty()){
			return retorno;
		}else{
			for (RelatorioEscalaDeSalasRetornoHqlVO relatorioEscalaDeSalasRetornoHqlVO : listMbcSalaCirurgica) {
				String registro = "";
				if(relatorioEscalaDeSalasRetornoHqlVO.getSigla() != null){
					registro = relatorioEscalaDeSalasRetornoHqlVO.getSigla().concat(" ");
				}
				registro = registro.concat(verificarServidor(relatorioEscalaDeSalasRetornoHqlVO.getNome()));
				registro = registro.concat(verificarUrgencia(relatorioEscalaDeSalasRetornoHqlVO.getIndUrgencia()));
				registro = registro.concat(verificarParticular(relatorioEscalaDeSalasRetornoHqlVO.getCirurgiaParticular()));
				registro = registro.concat("\n");
				
				retorno = retorno.concat(registro);
			}
		}
		return retorno;
	}

	private String verificarServidor(String nomeUsual) {
		String retorno = "";
			if(nomeUsual!=null ){
				if(nomeUsual.length()<14){
					retorno = getAmbulatorioFacade().mpmcMinusculo(nomeUsual.substring(0, nomeUsual.length()), 2);
				}else{
					retorno = getAmbulatorioFacade().mpmcMinusculo(nomeUsual.substring(0, 14), 2);
				}
			}

		return retorno;
	}
	
	private String verificarUrgencia(Boolean urgencia){
		if(urgencia){
			return "Urgências";
		}else{
			return "";
		}
	}
	
	private String verificarParticular(Boolean particular){
		if(particular){
			return "Particular";
		}else{
			return "";
		}
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return iPesquisaInternacaoFacade;
	}
}