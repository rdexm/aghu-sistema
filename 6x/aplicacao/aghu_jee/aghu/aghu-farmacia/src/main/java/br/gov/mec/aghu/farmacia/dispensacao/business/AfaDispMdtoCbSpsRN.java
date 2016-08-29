package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.dominio.DominioCoresSinaleiro;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class AfaDispMdtoCbSpsRN extends BaseBusiness
		implements Serializable {

	private static final Log LOG = LogFactory.getLog(AfaDispMdtoCbSpsRN.class);
	
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
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -900540034124097259L;

	public enum AfaDispensacaoMdtosRNExceptionCode implements BusinessExceptionCode {
		RAP_00175, AFA_01495, AFA_01504, AFA_01489, AFA_01477, AFA_01488, MEDICAMENTO_VENCIDO, CANCELAR_DISPENSACAO_SEM_ETIQUETA_SOMENTE_POR_ESTORNO
	}
	
	/**
	 * @ORADB AFAT_SDC_BRI
	 * @param dmc
	 * @throws ApplicationBusinessException  
	 */
	public void persisteDispMdtoCbSps(AfaDispMdtoCbSps dmc, String nomeMicrocomputador) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		verificaSeEtiquetaJaExiste(dmc.getNroEtiqueta());
		dmc.setCriadoEm(new Date());
//		if(dmc.getSeq() == null){
//			//Id é atribuído automaticamente, passo escrito somente para deixar transcrição da Trigger explicita
//			//:new.seq := afac_get_afa_sdc_sq1_nextval;
//		}
		
		if (dmc.getServidor() == null || dmc.getServidor().getId() == null
				|| dmc.getServidor().getId().getMatricula() == null
				|| dmc.getServidor().getId().getVinCodigo() == null) {
			dmc.setServidor(servidorLogado);
		}
		
		if(dmc == null || dmc.getServidor() == null || dmc.getServidor().getId().getMatricula() == null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.RAP_00175);
		}		

		dmc.setMicroComputador(getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(nomeMicrocomputador));

		if(dmc.getMicroComputador() == null) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.MICROCOMPUTADOR_NAO_CADASTRADO_IDENTIFICADO);
		}
		
		dmc.setAlteradoEm(new Date());
		dmc.setServidorAlterado(servidorLogado);
		
		//getAfaDispMdtoCbSpsDAO().persistir(dmc);
		//NÃO ESQUECER DE FAZER CHAMADA getAfaDispMdtoCbSpsDAO().persistir quando necessário inclusao 
	}
	
	/**
	 * @ORADB afak_sdc_rn.rn_sdcp_ver_etiqueta
	 * @param nroEtiqueta
	 * @throws ApplicationBusinessException 
	 */
	public void verificaSeEtiquetaJaExiste(String nroEtiqueta) throws ApplicationBusinessException {
		AfaDispMdtoCbSps mcs = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(nroEtiqueta, DominioIndExcluidoDispMdtoCbSps.I);
		if(mcs!=null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01495);
		}
	}
	
	/**
	 * @ORADB PROCEDURE AFAP_PREENCHE_SINALEIRA
	 */
	public DominioCoresSinaleiro preencheSinaleira(Long seqAfaDispMdto, BigDecimal qtdeDispensada){

		DominioCoresSinaleiro corSinaleira = null;
		Long v_qtdeEtiq = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps.I);
		DominioSituacaoDispensacaoMdto v_sitDSM = getAfaDispensacaoMdtosDAO().pesquisarSituacaoDispensacaoMdto(seqAfaDispMdto);

		if (DominioSituacaoDispensacaoMdto.D.equals(v_sitDSM) && v_qtdeEtiq.intValue() == 0){
			corSinaleira = DominioCoresSinaleiro.VERDE;			

		}else{
			if(qtdeDispensada.intValue() == v_qtdeEtiq){
				corSinaleira = DominioCoresSinaleiro.VERDE;
			}
			else if(qtdeDispensada.intValue() > v_qtdeEtiq && v_qtdeEtiq.intValue() > 0){
				corSinaleira = DominioCoresSinaleiro.AMARELO;
			}else{
				corSinaleira = DominioCoresSinaleiro.VERMELHO;
			}
		}

		return corSinaleira;
	}
	
	/**
	 * @ORADB AFAP_VALIDA_CBN
	 * @param etiqueta
	 * @param atdSeq
	 * @param seq
	 * @param seq2
	 * @throws ApplicationBusinessException 
	 */
	public void validaAtualizaCodigoDeBarras(String etiqueta, Integer pmeAtdSeq,
			Integer pmeSeq, Long dispMdtoSeq, String nomeMicrocomputador) throws BaseException {
		IEstoqueFacade estoqueFacade = this.getEstoqueFacade();
		AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO = this.getAfaDispensacaoMdtosDAO();
		
		//AfaDispMdtoCbSps dispMdtoCbSps = afaDispMdtoCbSpsDAO.getDispMdtoCbSpsByEtiqueta(etiqueta, DominioIndExcluidoDispMdtoCbSps.I);
		
		SceLoteDocImpressao loteDocImp = estoqueFacade.getLoteDocImpressaoByNroEtiqueta(etiqueta);
		if(loteDocImp == null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01504);
		}
		
		AfaDispensacaoMdtos dispMdtoNew = afaDispensacaoMdtosDAO.obterPorChavePrimaria(dispMdtoSeq);
		
		AfaDispensacaoMdtos dispMdtoOld =null;
		try{
			dispMdtoOld = (AfaDispensacaoMdtos) BeanUtils.cloneBean(dispMdtoNew);/* afaDispensacaoMdtosDAO.obterPorChavePrimaria(admNew.getSeq());*/
			afaDispensacaoMdtosDAO.desatachar(dispMdtoOld);
		}catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		Boolean encontrou = Boolean.FALSE;
		if(dispMdtoNew.getMedicamento().getMatCodigo().equals(loteDocImp.getMaterial().getCodigo())){
			if(DominioSituacaoDispensacaoMdto.T.equals(dispMdtoNew.getIndSituacao())){
				//-- Verifica se o medicimento está dentro da validade
				if(DateValidator.validaDataTruncadaMaior(new Date(), loteDocImp.getDtValidade())){
					String dtaFormatada = DateUtil.obterDataFormatada(loteDocImp.getDtValidade(), "dd/MM/yyyy"); 
					throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MEDICAMENTO_VENCIDO, dtaFormatada);
				}
				
				AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO = this.getAfaDispMdtoCbSpsDAO();
				
				Long qtdeEtiquetasLidas = afaDispMdtoCbSpsDAO.getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(dispMdtoSeq, DominioIndExcluidoDispMdtoCbSps.I);
				qtdeEtiquetasLidas = qtdeEtiquetasLidas +1;
				encontrou = Boolean.TRUE;
				if(dispMdtoNew.getQtdeDispensada().intValue() == qtdeEtiquetasLidas){
					//-- Troca a situação do mdto para dispensado
					dispMdtoNew.setIndSituacao(DominioSituacaoDispensacaoMdto.D);
					this.getFarmaciaFacade().atualizaAfaDispMdto(dispMdtoNew, dispMdtoOld, nomeMicrocomputador);
				}
				
				AfaDispMdtoCbSps dmc = processaNovaAfaDispMdto(etiqueta, dispMdtoNew, loteDocImp);
				this.getFarmaciaDispensacaoFacade().persisteDispMdtoCbSps(dmc, nomeMicrocomputador);
				estoqueFacade.tratarDispensacaoMedicamentoEstoque(dispMdtoNew, etiqueta, nomeMicrocomputador);
				
				afaDispMdtoCbSpsDAO.persistir(dmc);
			}
		}
		
		if(!encontrou) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01489);
		}
	}
	
	/**
	 * @ORADB AFAP_PREENCHE_CHECK_DISP
	 * @param adm
	 * @throws ApplicationBusinessException 
	 */
	public void assinaDispMdtoSemUsoDeEtiqueta(AfaDispensacaoMdtos admNew, String nomeMicrocomputador) throws BaseException {
		AfaDispensacaoMdtos admOld =null;
		try{
			admOld = (AfaDispensacaoMdtos) BeanUtils.cloneBean(admNew);/* getAfaDispensacaoMdtosDAO().obterPorChavePrimaria(admNew.getSeq());*/
			getAfaDispensacaoMdtosDAO().desatachar(admOld);
		}catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		Long qtdeEtiquetas = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(admNew.getSeq(), DominioIndExcluidoDispMdtoCbSps.I);
		
		if(qtdeEtiquetas > 0){
			admNew.setItemDispensadoSemEtiqueta(Boolean.FALSE);
			if(!DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01477);
			}else{
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01488);
			}
		}else{
			if(admNew.getItemDispensadoSemEtiqueta()){				
				admNew.setIndSituacao(DominioSituacaoDispensacaoMdto.D);
				admNew.setCorSinaleiro(DominioCoresSinaleiro.VERDE);
				atualizaSituacaoAfaDispMdto(admNew, admOld, nomeMicrocomputador);
			}else{
				/*admNew.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
				admNew.setCorSinaleiro(DominioCoresSinaleiro.VERMELHO);
				atualizaSituacaoAfaDispMdto(admNew, admOld);*/
				//Melhoria #18708
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.CANCELAR_DISPENSACAO_SEM_ETIQUETA_SOMENTE_POR_ESTORNO);
			}
		}
	}
	
	//Util
	
	private void atualizaSituacaoAfaDispMdto(AfaDispensacaoMdtos admNew,
			AfaDispensacaoMdtos admOld, String nomeMicrocomputador) throws BaseException {
		try {
			getFarmaciaFacade().atualizaAfaDispMdto(admNew, admOld, nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			admNew.setIndSituacao(admOld.getIndSituacao());
			admNew.setCorSinaleiro(admOld.getCorSinaleiro());
			admNew.setItemDispensadoSemEtiqueta(!admOld.getItemDispensadoSemEtiqueta());
			throw e;
		}
	}
	
	public AfaDispMdtoCbSps processaNovaAfaDispMdto(String etiqueta, AfaDispensacaoMdtos dispMdto, SceLoteDocImpressao loteDocImpressao) {
		//etiqueta = etiqueta.substring(0,10);
		AfaDispMdtoCbSps dmc = new AfaDispMdtoCbSps();
		dmc.setNroEtiqueta(etiqueta);
		dmc.setDispensacaoMdto(dispMdto);
		dmc.setIndExcluido(DominioIndExcluidoDispMdtoCbSps.I);
		dmc.setLoteDocImpressao(loteDocImpressao);
		return dmc;
	}
	
	public boolean assinalaMedicamentoDispensado(Long seqAfaDispMdto){
		
		boolean medicamentoDispensando = false;
		
		Long v_qtdeEtiq = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps.I);
		DominioSituacaoDispensacaoMdto v_sitDSM = getAfaDispensacaoMdtosDAO().pesquisarSituacaoDispensacaoMdto(seqAfaDispMdto);

		if (DominioSituacaoDispensacaoMdto.D.equals(v_sitDSM) && v_qtdeEtiq.intValue() == 0){
			medicamentoDispensando = true;
		}
		
		return medicamentoDispensando;
	}
	
	//Getters
	
	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}
	
	protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
		return afaDispMdtoCbSpsDAO;
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}