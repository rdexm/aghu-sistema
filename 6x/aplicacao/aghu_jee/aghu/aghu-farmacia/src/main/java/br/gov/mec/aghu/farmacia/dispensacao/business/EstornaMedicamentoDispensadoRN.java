package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EstornaMedicamentoDispensadoRN extends BaseBusiness
		implements Serializable {

	private static final Log LOG = LogFactory.getLog(EstornaMedicamentoDispensadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5324312001158008647L;

	public enum EstornarMedicamentoDispensadoRNExceptionCode implements	BusinessExceptionCode {
		AFA_01232,AFA_01491,ERRO_TIPO_OCORR_ESTORNO
	}
	
	/**
	 * @ORADB TRIGGER "AGH".AFAT_SDC_BRU
	 * 
	 * Atualiza o nome do usuário e a data em que foi realizada a operação de atualização.
	 * @param entidade
	 * @return entidade
	 * @throws ApplicationBusinessException 
	 */
	public AfaDispMdtoCbSps bruPreAtualizacaoAfaDispMdtoCbSps(AfaDispMdtoCbSps entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setServidor(servidorLogado);
		entidade.setAlteradoEm(new Date());
		
		return entidade;
	}
	
	/**
	 * @ORADB PROCEDURE AFAP_EXCLUI_ETIQUETA
	 * 
	 * Atualiza status de dispensacao com etiqueta para [E]Estornado
	 * 
	 * @param pEtiqueta
	 * @throws ApplicationBusinessException
	 */
	public void estornarDispensacaoMdtoComEtiqueta(String pEtiqueta, String nomeMicrocomputador)
			throws BaseException {

		/* Busca o número do código de barras a ser dispensado */
		List<AfaDispMdtoCbSps> afaDispMdtoCbSpsList = getAfaDispMdtoCbSpsDAO().obterListaDispMdtoCbSpsPorEtiqueta(pEtiqueta,DominioIndExcluidoDispMdtoCbSps.I);

		//Existem etiquetas com CB ?
		if (afaDispMdtoCbSpsList != null && afaDispMdtoCbSpsList.size() > 0) {

			/* Busca a seq da tabela afa_dispensacao_mdtos */
			AfaDispMdtoCbSps afaDispMdtoCbSps = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(pEtiqueta,DominioIndExcluidoDispMdtoCbSps.I);
			AfaDispensacaoMdtos dispensacaoMdtos = afaDispMdtoCbSps.getDispensacaoMdto();
			AfaDispensacaoMdtos dispensacaoMdtosOld = getFarmaciaFacade().getAfaDispOldDesatachado(dispensacaoMdtos);

			/* Busca a quantidade de medicamentos estornados */
			Long qtdeEstornada = getAfaDispensacaoMdtosDAO().obterQtdeDispMdtosEstornadosBySeq(dispensacaoMdtos.getSeq());
			
			qtdeEstornada = qtdeEstornada + 1;
			for (AfaDispMdtoCbSps itemEtiquetaEstorno : afaDispMdtoCbSpsList) {
				atualizaAfaDispMdtoCbSpsExcluido(itemEtiquetaEstorno);
			}
			//Setando como TRIADO 
			//dispensacaoMdtos.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
			if (dispensacaoMdtos.getTipoOcorrenciaDispensacaoEstornado() == null) {
				// Tipo Ocorrencia SOBRA MEDICAÇÃO
				Short seqSobraMedicacao = buscarCodigoPadraoTOD()
						.getVlrNumerico().shortValue();
				AfaTipoOcorDispensacao tipoOcorEstornado = getAfaTipoOcorDispensacaoDAO().obterPorChavePrimaria(seqSobraMedicacao);
				dispensacaoMdtos.setTipoOcorrenciaDispensacaoEstornado(tipoOcorEstornado);
				dispensacaoMdtos.setSeqAfaTipoOcorSelecionada(seqSobraMedicacao.toString());
			}
			dispensacaoMdtos.setQtdeEstornada(new BigDecimal(qtdeEstornada));
						
			// Faz alterações relacionadas ao estorno no item da RM (estória 14504) 
			getEstoqueFacade().estornarMedicamentoRequisicaoMaterial(dispensacaoMdtos, dispensacaoMdtosOld, pEtiqueta, nomeMicrocomputador);
			 
		} else {
			throw new ApplicationBusinessException(EstornarMedicamentoDispensadoRNExceptionCode.AFA_01491);
		}
	}
	
	/**
	 * Atualiza instancia de dispensacao com codigo de barras
	 * @param entidade
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizaAfaDispMdtoCbSpsExcluido(AfaDispMdtoCbSps entidade) throws ApplicationBusinessException {
		entidade.setIndExcluido(DominioIndExcluidoDispMdtoCbSps.E);
		entidade = bruPreAtualizacaoAfaDispMdtoCbSps(entidade);
		getAfaDispMdtoCbSpsDAO().persistir(entidade);
	}
	
	//UTIL 
	
	public AghParametros buscarDiasEstorno() throws ApplicationBusinessException{
		
		AghParametros diasEstorno = null;
		diasEstorno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_ESTORNO);
		return diasEstorno;
	}
	
	public AghParametros buscarCodigoPadraoTOD() throws ApplicationBusinessException{
		
		AghParametros codigoPadraoTOD = null;
		codigoPadraoTOD = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_TOD_CB);
		return codigoPadraoTOD;
	}
	
	public Date obterDataLimiteEstorno(AghParametros diasEstorno){

		@SuppressWarnings("deprecation")
		Date dataLimiteEstorno = br.gov.mec.aghu.core.utils.DateUtil.adicionaDias(new Date(), diasEstorno.getVlrNumerico().negate().intValue());
				
		return dataLimiteEstorno;
	}
	
	//Getters 
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO() {
		return afaDispMdtoCbSpsDAO;
	}
	
	protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO() {
		return afaTipoOcorDispensacaoDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
