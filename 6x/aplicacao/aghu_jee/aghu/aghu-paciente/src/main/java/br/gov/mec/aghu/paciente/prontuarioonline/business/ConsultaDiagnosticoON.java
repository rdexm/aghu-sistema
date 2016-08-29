package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ConsultaDiagnosticoON extends BaseBusiness {

	@EJB
	private IncluiNotasPOLRN incluiNotasPOLRN;

	private static final Log LOG = LogFactory.getLog(ConsultaDiagnosticoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8873612177266517812L;
	private static final Comparator<MamDiagnostico> COMPARATOR_DIAGNOSTICOS = new Comparator<MamDiagnostico>() {
		@Override
		public int compare(MamDiagnostico o1, MamDiagnostico o2) {
			return (o1.getDataFim() == null && o2.getDataFim() == null) ? 0 : ((o1.getDataFim() == null)?-1: ((o2.getDataFim() == null)?1:o1.getData().compareTo(o2.getData())));
		}
	};
	
	public Integer pesquisaDiagnosticosCount(AipPacientes paciente)
			throws ApplicationBusinessException {
		return this.getAmbulatorioFacade().pesquisarDiagnosticosPorPaciente(paciente).size();
	}

	public List<MamDiagnostico> pesquisaDiagnosticos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente) throws ApplicationBusinessException {
		List<MamDiagnostico> diagnosticos = this.getAmbulatorioFacade().pesquisarDiagnosticosPorPaciente(paciente);
		
		if(diagnosticos.size() > 0){
			//Trata ordenacao dos registros pois nao foi possivel implementar o seguinte order by em criteria: DECODE(data_fim,NULL,'31/12/2060') ASC
			Collections.sort(diagnosticos, COMPARATOR_DIAGNOSTICOS);
		}

		if(firstResult != null && maxResult != null) {
			// A paginação é feita de forma manual, eliminando elementos que não
			// devem aparecer no resultado da pesquisa. 10 é o tamanho máximo de
			// itens por página.
			if (diagnosticos.size() > 10 && firstResult < diagnosticos.size()) {
				int toIndex = firstResult + maxResult;
	
				if (toIndex > diagnosticos.size()) {
					toIndex = diagnosticos.size();
				}
	
				diagnosticos = diagnosticos.subList(firstResult, toIndex);
			}
		}
		return diagnosticos;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	protected IncluiNotasPOLRN getIncluiNotasPOLRN() {
		return incluiNotasPOLRN;
	}
	
	public Boolean habilitarBotaoDiagnostico() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
				
		String login=null;
		if(servidorLogado.getId().getVinCodigo() != null && servidorLogado.getId().getMatricula() != null) {
			RapServidores servidor = getRegistroColaboradorFacade()
					.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
							servidorLogado.getId().getMatricula(),
							servidorLogado.getId().getVinCodigo());
			if (servidor.getUsuario()!=null) {
			login = servidor.getUsuario().toUpperCase();
		}
		}
		if (login ==null && servidorLogado != null){			
			login = servidorLogado.getUsuario();
		}
		
		String prefixoPerfil = "ENF";
		if(getCascaFacade().validarPermissaoPorServidorESeqProcessoEPrefixoPerfil(login, getIncluiNotasPOLRN().mamcGetProcDiag(), prefixoPerfil)){
			return true;
		}
		
		return false;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
