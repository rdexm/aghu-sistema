package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmListaServSumrAlta;
import br.gov.mec.aghu.model.MpmListaServSumrAltaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServSumrAltaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ListarSumarioAltaReimpressaoON extends BaseBusiness {
	
	@EJB
	private DocumentoON documentoON;
	
	private static final Log LOG = LogFactory.getLog(ListarSumarioAltaReimpressaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmListaServSumrAltaDAO mpmListaServSumrAltaDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7429239603465796292L;

	private enum ListarSumarioAltaReimpressaoONExceptionCode implements
	BusinessExceptionCode {
		RAP_00175;
	}

	public Long countPesquisaListaSumAltaReimp(Integer prontuario, Integer codigo) {
		return getMpmListaServSumrAltaDAO().countPesquisaListaSumAltaReimp(prontuario, codigo);
	}
	
	public List<MpmListaServSumrAlta> listaPesquisaListaSumAltaReimp(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer prontuario, Integer codigo) {
		return getMpmListaServSumrAltaDAO().listaPesquisaListaSumAltaReimp(firstResult, maxResults,
				orderProperty, asc, prontuario, codigo);
	}

	public void refazer(Integer atdSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		AghAtendimentos atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
		atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
		atendimento.setCtrlSumrAltaPendente(DominioControleSumarioAltaPendente.N);
		getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);

		for(MpmAltaSumario sumario : atendimento.getAltasSumario()) {
			sumario.setConcluido(DominioIndConcluido.R);
			getPrescricaoMedicaFacade().atualizarAltaSumario(sumario.getId(), (sumario.getDiasPermanencia()!= null)?sumario.getDiasPermanencia().shortValue():null, sumario.getIdadeDias(), sumario.getIdadeMeses(), sumario.getIdadeAnos(), sumario.getDthrAlta(), nomeMicrocomputador);
		}
		
		if(atendimento.getServidor() != null) {
			List<RapServidores> servidores = getPrescricaoMedicaFacade().listaProfissionaisEquipAtendimento(atendimento.getServidor());
			for(RapServidores servidor : servidores) {
				MpmListaServSumrAlta listaServSumrAlta = new MpmListaServSumrAlta();
				listaServSumrAlta.setId(new MpmListaServSumrAltaId(servidor.getId().getMatricula(), servidor.getId().getVinCodigo(), atendimento.getSeq()));
				listaServSumrAlta.setServidor(servidor);
				listaServSumrAlta.setAtendimento(atendimento);
				this.persisitirListaServSumralta(listaServSumrAlta);
			}
		}
		
		getMpmListaServSumrAltaDAO().flush();
		
		List<AghDocumento> documentos = getDocumentoON().buscarDocumentosPeloAtendimento(atdSeq);
		for(AghDocumento documento : documentos) {
			if(documento.getTipo().equals(DominioTipoDocumento.SA) || documento.getTipo().equals(DominioTipoDocumento.SO)){
				for(AghVersaoDocumento versao : documento.getAghVersaoDocumentoes()) {
					versao.setSituacao(DominioSituacaoVersaoDocumento.I);
					getDocumentoON().atualizarVersaoDocumento(versao);
				}
			}
		}
	}
	
	

	public void persisitirListaServSumralta(MpmListaServSumrAlta listaServSumrAlta) 
	throws ApplicationBusinessException {
		this.preInserir(listaServSumrAlta);
		getMpmListaServSumrAltaDAO().persistir(listaServSumrAlta);
	}
	
	/**
	 * ORADB MPMT_LSA_BRI
	 * 
	 * Implementação da trigger de before INSERT da tabela MPM_LISTA_SERV_SUMR_ALTAS
	 * 
	 * @param listaServSumrAlta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserir(MpmListaServSumrAlta listaServSumrAlta)
	throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(listaServSumrAlta.getServidor() == null) {
			if(servidorLogado == null) {
				throw new ApplicationBusinessException(ListarSumarioAltaReimpressaoONExceptionCode.RAP_00175);
			}

			listaServSumrAlta.setServidor(servidorLogado);
		}
		
		listaServSumrAlta.setCriadoEm(new Date());
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected MpmListaServSumrAltaDAO getMpmListaServSumrAltaDAO() {
		return mpmListaServSumrAltaDAO;
	}
	
	private DocumentoON getDocumentoON() {
		return documentoON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
