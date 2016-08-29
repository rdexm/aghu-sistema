package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.exames.vo.UnitarizacaoVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoMaterial;

/**
 * 
 * @author amalmeida
 *
 */
@Stateless
public class ImprimirEtiquetasUnitarizacaoON extends BaseBusiness {


	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	@Inject
	private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;
	
	
	@EJB
	private SceLoteDocImpressaoRN sceLoteDocImpressaoRN;
	
	private static final Log LOG = LogFactory.getLog(ImprimirEtiquetasUnitarizacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9046719109546539260L;

	public enum ImprimirEtiquetasUnitarizacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_QTDE_ETIQUETAS,MENSAGEM_ERRO_QTDE_DISPONIVEL,ERRO_GERACAO_ARQUIVO, MENSAGEM_ERRO_DIRETORIO_INEXISTENTE, 
		MENSAGEM_ERRO_MEDICAMENTO_SEM_APRESENTACAO;
	}

	/**
	 * ORADB WHEN-BUTTON-PRESSED
	 * @param loteDocumento
	 * @param qtde_etiquetas
	 * @throws BaseException
	 */
	public void gerarInterfaceamentoUnitarizacao(SceLoteDocImpressao loteDocImpressao, String nomeMicrocomputador
			, Integer qtdeEtiquetas) 
				throws BaseException {
		//redução de validade feita antes
		UnitarizacaoVO unitarizacaoVo = new UnitarizacaoVO();
		
		if(qtdeEtiquetas != null){
			this.validarQtdeEtiquetas(loteDocImpressao.getLoteDocumento(), qtdeEtiquetas);
			unitarizacaoVo.setNroNf(this.obterNroNf(loteDocImpressao.getLoteDocumento()));
			
		}else{
			unitarizacaoVo.setNroNf(loteDocImpressao.getInrNrsSeq());
		}
		
		unitarizacaoVo.setDtValidade(loteDocImpressao.getDtValidade());
		
		unitarizacaoVo.setNroInicial(loteDocImpressao.getNroInicial());
		
		buscarMedicamento(loteDocImpressao.getMaterial(), unitarizacaoVo);
		unitarizacaoVo.setLaboratorio(loteDocImpressao.getMarcaComercial().getDescricao());
		unitarizacaoVo.setLotCodigo(loteDocImpressao.getLoteCodigo());
		unitarizacaoVo.setLotSeq(loteDocImpressao.getSeq().intValue());
		
		/**
		 * GERAR INTERFACEAMENTO
		 */
		gerarInterfaceamento(loteDocImpressao.getQtde(), unitarizacaoVo);

	}

	private void buscarMedicamento(ScoMaterial material, UnitarizacaoVO unitarizacaoVo) throws ApplicationBusinessException {

		if (material.getAfaMedicamento() != null) {

			AfaMedicamento medicamento = material.getAfaMedicamento();

			obtemConcentracaoFormatadaEtiqueta(unitarizacaoVo, medicamento);
					
			obtemDescricaoEtiquetaFrascoeSeringa(unitarizacaoVo, medicamento);
			
			populaSiglaApresentacaoMedicamento(unitarizacaoVo, medicamento);

			obtemDescricaoUnidadeMedidaMedica(unitarizacaoVo, medicamento);
			
			concatenaConcentracaoDescricaoUnidadeMedidasMedica(unitarizacaoVo);	
			
		}
	}

	/**
	 * @param unitarizacaoVo
	 * @param medicamento
	 * @throws ApplicationBusinessException
	 */
	public void populaSiglaApresentacaoMedicamento(
			UnitarizacaoVO unitarizacaoVo, AfaMedicamento medicamento)
			throws ApplicationBusinessException {
		if (medicamento.getTipoApresentacaoMedicamento() != null) {
			unitarizacaoVo.setSiglaApresentacaoMedicamento(medicamento.getTipoApresentacaoMedicamento().getSigla());
		} else {			
			throw new ApplicationBusinessException(ImprimirEtiquetasUnitarizacaoONExceptionCode.MENSAGEM_ERRO_MEDICAMENTO_SEM_APRESENTACAO);
		}
	}

	private void concatenaConcentracaoDescricaoUnidadeMedidasMedica(UnitarizacaoVO unitarizacaoVo) {
		if(unitarizacaoVo.getConc()!=null && unitarizacaoVo.getUmmDesc()!=null){
			unitarizacaoVo.setConcentracao(unitarizacaoVo.getConc().concat(" ").concat(unitarizacaoVo.getUmmDesc()));//ex: 1 G/ML
		}else{
			unitarizacaoVo.setConcentracao(StringUtils.EMPTY);//ex: 1 G/ML
		}
	}

	private void obtemDescricaoUnidadeMedidaMedica(UnitarizacaoVO unitarizacaoVo, AfaMedicamento medicamento) {
		if(medicamento.getDescricaoUnidadeMedidaMedica() !=null){
			unitarizacaoVo.setUmmDesc(medicamento.getDescricaoUnidadeMedidaMedica());// ex: G/ML
		}
	}

	private void obtemDescricaoEtiquetaFrascoeSeringa(UnitarizacaoVO unitarizacaoVo, AfaMedicamento medicamento) {
		if(medicamento.getDescricaoEtiquetaFrasco()!=null && !medicamento.getDescricaoEtiquetaFrasco().isEmpty()){
			unitarizacaoVo.setNomeMed(medicamento.getDescricaoEtiquetaFrasco());
		}else{
			if (medicamento.getDescricaoEtiquetaSeringa()!=null && !medicamento.getDescricaoEtiquetaSeringa().isEmpty()) {
				unitarizacaoVo.setNomeMed(medicamento.getDescricaoEtiquetaSeringa());
			} else {
				unitarizacaoVo.setNomeMed(medicamento.getDescricao());
			}
		}
	}

	private void obtemConcentracaoFormatadaEtiqueta(UnitarizacaoVO unitarizacaoVo, AfaMedicamento medicamento) {
		unitarizacaoVo.setConc(medicamento.getConcentracaoFormatada());		
	}

	public Integer obterNroNf(SceLoteDocumento loteDocumento) {
		Integer nroNf = null;
		if(loteDocumento.getEntradaSaidaSemLicitacao()==null){
			SceNotaRecebimento notaRecebimento = sceNotaRecebimentoDAO.obterPorChavePrimaria(loteDocumento.getItemNotaRecebimento().getId().getNrsSeq());
			if(loteDocumento.getItemNotaRecebimento()!= null && notaRecebimento!=null && notaRecebimento.getDocumentoFiscalEntrada()!=null){
				nroNf = notaRecebimento.getDocumentoFiscalEntrada().getSeq();
			}

		}
		return nroNf;
	}


	private void validarQtdeEtiquetas(SceLoteDocumento loteDocumento,	Integer qtde_etiquetas) throws ApplicationBusinessException {

		if(qtde_etiquetas == null){

			throw new ApplicationBusinessException(ImprimirEtiquetasUnitarizacaoONExceptionCode.MENSAGEM_ERRO_QTDE_ETIQUETAS);
		}

		if(qtde_etiquetas > loteDocumento.getQuantidade()){
			throw new ApplicationBusinessException(ImprimirEtiquetasUnitarizacaoONExceptionCode.MENSAGEM_ERRO_QTDE_DISPONIVEL);
		}
	}

	private void gerarInterfaceamento(Integer qtde_etiquetas, UnitarizacaoVO unitarizacaoVo) throws ApplicationBusinessException {

		AghParametros paramUnitarizacao = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_IMP_UNITARIZADORA_MEDICAMENTO);	

		try{

			if(paramUnitarizacao.getVlrNumerico().intValue() == 1){
				/**
				 * HOSPITAL JUIZ DE FORA
				 */
				this.getFarmaciaFacade().gerarCSVInterfaceamentoImpressoraHujf(unitarizacaoVo, qtde_etiquetas.longValue());

			}else{
				/**
				 * HCPA e outros Hospitais
				 */
				this.getFarmaciaFacade().gerarCSVInterfaceamentoImpressoraGriffols(unitarizacaoVo, qtde_etiquetas.longValue());
			}

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		}

	}

	protected SceLoteDocImpressaoRN getSceLoteDocImpressaoRN() {
		return sceLoteDocImpressaoRN;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}


	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO() {
		return sceLoteDocImpressaoDAO;
	}

}
