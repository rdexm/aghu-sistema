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

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.AfaDispMdtoCbSpsRN.AfaDispensacaoMdtosRNExceptionCode;
import br.gov.mec.aghu.farmacia.dispensacao.business.DispensacaoMdtosCodBarrasRN.DispensacaoMdtosCodBarrasRNExceptionCode;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class DispensacaoDePrescricaoNaoEletronicaON extends BaseBusiness implements Serializable{

	private static final long serialVersionUID = 2560331732938314499L;
	
	private static final Log LOG = LogFactory.getLog(DispensacaoDePrescricaoNaoEletronicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	public enum DispensacaoDePrescricaoNaoEletronicaONONExceptionCode implements	BusinessExceptionCode {
		QUANTIDADE_DISPENSADA_INFERIOR_ANTERIOR, FORMATO_ETIQUETA_INVALIDO, MDTO_JA_FOI_DISPENSADO_SEM_CB, MDTO_JA_FOI_DISPENSADO_COM_CB
		, BLOQUEIO_EDICAO_DISPENSACAO_PMM_COM_CB, MEDICAMENTO_VENCIDO_ESTORNO_MEDICAMENTOS, NUMERO_EXTERNO_SERA_GERADO_AUTOMATICAMENTE
		, MEDICAMENTO_JA_DISPENSADO_OUTRA_FARMACIA, VALIDADE_PMM_MAIOR_QUE_24_HORAS
	}
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private TratarOcorrenciasON  tratarOcorrenciasON;
	
	@Inject
	private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;
	
	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;
	
	@Inject
	private EstornaMedicamentoDispensadoRN estornaMedicamentoDispensadoRN;
	
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
			Long seqAfaPrescricaoMedicamento, Integer matCodigo) {
		List<AfaDispensacaoMdtos> listDisp = getAfaDispensacaoMdtosDAO()
				.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
						seqAfaPrescricaoMedicamento, matCodigo, null, null);
		for(AfaDispensacaoMdtos adm : listDisp){
			adm.setPermiteDispensacaoSemEtiqueta(verificarSeDispensacaoPossuiEtiqueta(adm.getSeq()));
			//Sendo reaproveitado atributo transient "permiteDispensacaoSemEtiqueta" para informar se dispensação foi feita por etiqueta
			
			adm.getMedicamento().getTipoApresentacaoMedicamento().getSiglaDescricao();
		}
		return listDisp;
	}
	
	private Boolean verificarSeDispensacaoPossuiEtiqueta(Long dsmSeq){
		Long qtdeEtiquetas = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(dsmSeq, null);
		if(qtdeEtiquetas != 0){
			return Boolean.FALSE;
		}else{
			return Boolean.TRUE;
		}
	}
	/**
	 * Dispensação sem CB chama este método direto
	 * Com CB, código de barra, é aplicado outras validações anteriores(dispensaMdtoComCBPrescricaoNaoEletronica)
	 * @param medMatCodigo
	 * @param qtdeDispensada
	 * @param observacao
	 * @param seqAfaPrescricaoMedicamento
	 * @param atdSeq
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @param unfSeq
	 * @param unfSeqSolicitante
	 * @return
	 * @throws BaseException 
	 * @throws MECBaseException
	 */
	public AfaDispensacaoMdtos persistirAfaDispMdtoComPrescricaoNaoEletronica(
			Integer medMatCodigo, BigDecimal qtdeDispensada,
			String observacao, Long seqAfaPrescricaoMedicamento,
			Integer atdSeq, String nomeMicrocomputador, 
			RapServidores servidorLogado, Short unfSeq, Short unfSeqSolicitante) throws BaseException{
		
		validarSeDispensacaoJaExiste(seqAfaPrescricaoMedicamento, medMatCodigo);
		
		AfaDispensacaoMdtos newDisp = getFarmaciaFacade()
				.processaNovaAfaDispMdto(atdSeq, null, null, null,
						medMatCodigo, null,
						Boolean.FALSE, qtdeDispensada,
						null, null, DominioSituacaoItemPrescritoDispensacaoMdto.DT,
						null, Boolean.TRUE, seqAfaPrescricaoMedicamento); 
		newDisp.setObservacao(observacao);
		newDisp.setQtdeSolicitada(qtdeDispensada);
		newDisp.setIndSituacao(DominioSituacaoDispensacaoMdto.D);
		newDisp.setDthrDispensacao(new Date());
		newDisp.setDthrTriado(new Date());
		
		newDisp.setUnidadeFuncional(getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		newDisp.setUnidadeFuncionalSolicitante(getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeqSolicitante));
		
		newDisp.setServidorTriadoPor(servidorLogado);
		newDisp.setServidor(servidorLogado);
		
		AghMicrocomputador microUserDispensador = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, DominioCaracteristicaMicrocomputador.DISPENSADOR);
		getTratarOcorrenciasON().validaSeMicroComputadorDispensador(null, newDisp, microUserDispensador);
		
		getEstoqueFacade().tratarDispensacaoMedicamentoEstoque(newDisp, null, nomeMicrocomputador);
		getFarmaciaFacade().criaDispMdtoTriagemPrescricao(newDisp, nomeMicrocomputador);
		
		return newDisp;
		//Não é chamad este método porque a prescrição é nova, então ele não possui etiquetas.
		//getAfaDispMdtoCbSpsRN().assinaDispMdtoSemUsoDeEtiqueta(admNew, nomeMicrocomputador, servidorLogado);
	}
	
	private void validarSeDispensacaoJaExiste(Long seqAfaPrescricaoMedicamento, Integer matCodigo) throws ApplicationBusinessException {
		//Verifica se já existe dispensacao COM etiquetas e qtde dispensada
		List<AfaDispensacaoMdtos> dispList = getAfaDispensacaoMdtosDAO()
			.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
					seqAfaPrescricaoMedicamento,
					matCodigo, Boolean.TRUE, Boolean.TRUE);
		
		if(!dispList.isEmpty()){
			throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.MDTO_JA_FOI_DISPENSADO_COM_CB);
		}else{
			//Verifica se já existe dispensacao SEM etiquetas e qtde dispensada
			dispList = getAfaDispensacaoMdtosDAO()
			.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
					seqAfaPrescricaoMedicamento,
					matCodigo, Boolean.FALSE, Boolean.TRUE);
			if(!dispList.isEmpty()){
				throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.MDTO_JA_FOI_DISPENSADO_SEM_CB);
			}
		}
		
	}
	public void dispensaMdtoComCBPrescricaoNaoEletronica(String nroEtiqueta,Integer atdSeq,
			String nomeMicrocomputador,	RapServidores servidorLogado, Short unfSeq, Short unfSeqSolicitante,
			Long seqAfaPrescricaoMedicamento) throws BaseException{
		
		AfaDispMdtoCbSps dispMdtoCbSps = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(nroEtiqueta, DominioIndExcluidoDispMdtoCbSps.I);
		if(dispMdtoCbSps != null) {
			throw new ApplicationBusinessException(DispensacaoMdtosCodBarrasRNExceptionCode.AFA_01697);
		}
		
		SceLoteDocImpressao loteDocImp = getEstoqueFacade().getLoteDocImpressaoByNroEtiqueta(nroEtiqueta);
		if(loteDocImp == null){
			throw new ApplicationBusinessException(
					DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.FORMATO_ETIQUETA_INVALIDO);
		}
		
		if(DateUtil.validaDataTruncadaMaior(new Date(), loteDocImp.getDtValidade())){
			String dtaFormatada = DateUtil.obterDataFormatada(loteDocImp.getDtValidade(), "dd/MM/yyyy"); 
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MEDICAMENTO_VENCIDO, dtaFormatada);
		}
		
		Integer medMatCodigo = loteDocImp.getMaterial().getCodigo();
		
		List<AfaDispensacaoMdtos> dispList = getAfaDispensacaoMdtosDAO()
				.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
						seqAfaPrescricaoMedicamento,
						medMatCodigo, Boolean.TRUE, Boolean.TRUE);
		
		AfaDispensacaoMdtos admUpdate = null;
		if(dispList!= null && !dispList.isEmpty()){//Este mdto já foi dispensado nesta prescricao, incrementar qtde
			if(dispList.size() != 1){
				throw new IllegalArgumentException();
			}
			admUpdate = dispList.get(0);
			getAfaDispensacaoMdtosDAO().desatachar(admUpdate);
			
			BigDecimal qtdeDispensada = admUpdate.getQtdeDispensada().add(BigDecimal.ONE);
			atualizarAfaDispMdtoComPrescricaoNaoEletronica(admUpdate,
					qtdeDispensada, admUpdate.getObservacao(),
					nomeMicrocomputador, servidorLogado
					, Boolean.FALSE);//Considera-se falso porque dispensacaComCB é uma edição "comum"
			
		}else{
			/*//Antes de criar novaDispensacao, verificar se medicamento dispensado sem CB e não estornado
			List<AfaDispensacaoMdtos> dispSemCBNaoEstorn = getAfaDispensacaoMdtosDAO()
			.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
					seqAfaPrescricaoMedicamento,
					medMatCodigo, Boolean.FALSE, Boolean.TRUE);
			if(!dispSemCBNaoEstorn.isEmpty()){
				throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.MDTO_JA_FOI_DISPENSADO_SEM_CB);
			}*/
			
			//nova dispensacao
			admUpdate = persistirAfaDispMdtoComPrescricaoNaoEletronica(medMatCodigo,
					BigDecimal.ONE, null, seqAfaPrescricaoMedicamento,
					atdSeq, nomeMicrocomputador, servidorLogado, unfSeq,
					unfSeqSolicitante);
		}
		
		AfaDispMdtoCbSps dmc = getAfaDispMdtoCbSpsRN().processaNovaAfaDispMdto(nroEtiqueta, admUpdate, loteDocImp);
		getAfaDispMdtoCbSpsRN().persisteDispMdtoCbSps(dmc, nomeMicrocomputador);
		
		//Neste caso nã é necessário alterar a quantidade dispensada porque o estoque só da baixa em 1 quando é enviado etiqueta
		//Removido chamada ao estoque porque as funções anteriores ja chamavam, persistirAfaDispMdtoComPrescricaoNaoEletronica ou atualizarAfaDispMdtoComPrescricaoNaoEletronica
		//getEstoqueFacade().tratarDispensacaoMedicamentoEstoque(admUpdate, nroEtiqueta, nomeMicrocomputador, servidorLogado);
		
		getAfaDispMdtoCbSpsDAO().persistir(dmc);
		
		/*getAfaDispMdtoCbSpsRN().validaAtualizaCodigoDeBarras(nroEtiqueta, adm
				.getPrescricaoMedica().getId().getAtdSeq(), adm
				.getPrescricaoMedica().getId().getSeq(), adm.getSeq(),
				nomeMicrocomputador, servidorLogado);*/
	}

	/**
	 * MÉTODO CRIADO PROPOSICIONALMENTE com as colunas que são atualizaveis na prescrição médica não eletrônica.
	 * @param afaDispOld 
	 * @param seqAfaDisp
	 * @param qtdeDispensada
	 * @param observacao
	 * @param edicaoDeDispensacaoComCB 
	 * @throws BaseException 
	 * @throws MECBaseException 
	 */
	public void atualizarAfaDispMdtoComPrescricaoNaoEletronica(AfaDispensacaoMdtos afaDispOld,
			BigDecimal qtdeDispensada, String observacao,
			String nomeMicrocomputador, RapServidores servidorLogado, Boolean impedeChamadaEstoque) throws BaseException {
		
		validarFarmaciaDispensadoraUsuario(afaDispOld.getUnidadeFuncional(), nomeMicrocomputador);
		
		if(afaDispOld.getQtdeDispensada().intValue() > qtdeDispensada.intValue()){
			throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.QUANTIDADE_DISPENSADA_INFERIOR_ANTERIOR);
		}
		
		AfaDispensacaoMdtos admUpdate = getAfaDispensacaoMdtosDAO().obterPorChavePrimaria(afaDispOld.getSeq());
		
		if(impedeChamadaEstoque //se o estoque não será chamado, só é permitido alterar a observação
				&& (!afaDispOld.getQtdeDispensada().equals(admUpdate.getQtdeDispensada())
				|| CoreUtil.modificados(afaDispOld.getQtdeEstornada(), admUpdate.getQtdeEstornada())
				|| CoreUtil.modificados(afaDispOld.getMedicamento(), admUpdate.getMedicamento()))){
			throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.BLOQUEIO_EDICAO_DISPENSACAO_PMM_COM_CB);
		}
		
		admUpdate.setQtdeDispensada(qtdeDispensada);
		admUpdate.setQtdeSolicitada(qtdeDispensada);
		admUpdate.setObservacao(observacao);
		
		//confirmar new date 
		getFarmaciaFacade().atualizaAfaDispMdto(admUpdate, afaDispOld, nomeMicrocomputador);
		
		/*A edição de dispensação com Código de Barras, é permitido somente edição da observação
		por este motivo não é ncessário chamar rotina do estoque
		caso seja chamado, irá ocorrer problema porque ele irá dispensar ou estornar as qtdes passadas.*/
		if(!impedeChamadaEstoque){
			
			//A quantidade dispensada precisa ser alterada com a nova diferença, porque a rotina do estoque não efetua o cálculo
			admUpdate.setQtdeDispensada(qtdeDispensada.subtract(afaDispOld.getQtdeDispensada()));
			
			//Necessário armazenar qtde de estorno porque o estoque não avalida a diferença
			BigDecimal qtdeEstornada = admUpdate.getQtdeEstornada(); 
			if(qtdeEstornada != null){
				admUpdate.setQtdeEstornada(admUpdate.getQtdeDispensada());
			}
			getEstoqueFacade().tratarDispensacaoMedicamentoEstoque(admUpdate, null, nomeMicrocomputador);
			admUpdate.setQtdeDispensada(qtdeDispensada);
			admUpdate.setQtdeEstornada(qtdeEstornada);
		}
	}
	
	private void validarFarmaciaDispensadoraUsuario(
			AghUnidadesFuncionais unidadeFuncional, String nomeMicrocomputador) throws ApplicationBusinessException {
		AghMicrocomputador micro = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, null);
		if(!unidadeFuncional.equals(micro.getAghUnidadesFuncionais())){
			throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.MEDICAMENTO_JA_DISPENSADO_OUTRA_FARMACIA, unidadeFuncional.getSeqEDescricao());
		}
		
	}

	public void efetuarEstornoDispensacaoMdtoNaoEletronica(AfaDispensacaoMdtos dispensacaoMdtoOld, String nomeComputadorRede, RapServidores usuarioLogado) throws BaseException {
		
		if(dispensacaoMdtoOld.getPermiteDispensacaoSemEtiqueta() == null){
			dispensacaoMdtoOld.setPermiteDispensacaoSemEtiqueta(verificarSeDispensacaoPossuiEtiqueta(dispensacaoMdtoOld.getSeq()));
		}
		
		if(!dispensacaoMdtoOld.getPermiteDispensacaoSemEtiqueta()){//Se FALSE, dispensação COM etiqueta
			List<AfaDispMdtoCbSps> afaDispMdtoCbSps = getAfaDispMdtoCbSpsDAO().pesquisarDispMdtoCbSpsByDispensacaoMdto(dispensacaoMdtoOld.getSeq(), DominioIndExcluidoDispMdtoCbSps.I);
			for(AfaDispMdtoCbSps dispMdtoCbSps: afaDispMdtoCbSps){
				if(DateUtil.validaDataTruncadaMaior(new Date(), dispMdtoCbSps.getLoteDocImpressao().getDtValidade())){
					//String dtaFormatada = DateUtil.obterDataFormatada(dispMdtoCbSps.getLoteDocImpressao().getDtValidade(), "dd/MM/yyyy"); 
					throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.MEDICAMENTO_VENCIDO_ESTORNO_MEDICAMENTOS);
				}
				
				getEstornaMedicamentoDispensadoRN().atualizaAfaDispMdtoCbSpsExcluido(dispMdtoCbSps);
			}
		}
		
		AfaDispensacaoMdtos admNewEstornada = getAfaDispensacaoEstornada(dispensacaoMdtoOld.getSeq());
		
		BigDecimal qtdeEstornada = admNewEstornada.getQtdeEstornada();
		if(dispensacaoMdtoOld.getQtdeEstornada() != null){//envia pro estoque somente a diferença
			admNewEstornada.setQtdeEstornada(qtdeEstornada.subtract(dispensacaoMdtoOld.getQtdeEstornada()));
		}
		// Faz alterações relacionadas ao estorno no item da RM (estória 14504) 
		getEstoqueFacade().estornarMedicamentoRequisicaoMaterial(admNewEstornada, dispensacaoMdtoOld, null, nomeComputadorRede);
		
		admNewEstornada.setQtdeEstornada(qtdeEstornada);
	}
	
	protected AfaDispensacaoMdtos getAfaDispensacaoEstornada(Long dsmSeq) throws ApplicationBusinessException {
		AfaDispensacaoMdtos admNew = getAfaDispensacaoMdtosDAO().obterPorChavePrimaria(dsmSeq);
		
		AghParametros paramMotivoEstorno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_MOTIVO_ESTORNO_MDTO_PMM);
		AfaTipoOcorDispensacao motivoEstorno = getFarmaciaFacade().obterOcorrencia(paramMotivoEstorno.getVlrNumerico().shortValue());
		admNew.setTipoOcorrenciaDispensacaoEstornado(motivoEstorno);
		admNew.setQtdeEstornada(admNew.getQtdeDispensada());
		admNew.setDthrEstorno(new Date());
		
		return admNew;
	}
	
	public void persistirAfaPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) throws ApplicationBusinessException{
		prescricaoMedicamento.validarAfaPrescricaoMedicamento();
		if(DateUtil.validaDataMaior(prescricaoMedicamento.getDthrFim(), DateUtil.adicionaDias(prescricaoMedicamento.getDthrInicio(), 1))){
			throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.VALIDADE_PMM_MAIOR_QUE_24_HORAS);
		}
		String paramNumExterno = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_GERAR_NUMERO_EXTERNO_AUTO);
		Boolean gerarNumeroExternoAutomatico = "S".equals(paramNumExterno.trim());
		if(gerarNumeroExternoAutomatico){
			if(prescricaoMedicamento.getNumeroExterno() != null){
				throw new ApplicationBusinessException(DispensacaoDePrescricaoNaoEletronicaONONExceptionCode.NUMERO_EXTERNO_SERA_GERADO_AUTOMATICAMENTE);
			}
			prescricaoMedicamento.setNumeroExterno(getAfaPrescricaoMedicamentoDAO().gerarNovoNumeroExternoBySequence());
		}
		getAfaPrescricaoMedicamentoDAO().persistirAfaPrescricaoMedicamento(prescricaoMedicamento);
	}

	private EstornaMedicamentoDispensadoRN getEstornaMedicamentoDispensadoRN(){
		return estornaMedicamentoDispensadoRN;
	}

	private IFarmaciaFacade getFarmaciaFacade(){
		return farmaciaFacade;
	}
	
	private IAghuFacade getAghuFacade(){
		return aghuFacade;
	}
	
	private IParametroFacade getParametroFacade(){
		return parametroFacade;
	}
	
	private IEstoqueFacade getEstoqueFacade(){
		return estoqueFacade;
	}
	
	private TratarOcorrenciasON getTratarOcorrenciasON(){
		return tratarOcorrenciasON;
	}
	
	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN(){
		return afaDispMdtoCbSpsRN;
	}
	
	private IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	private AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
		return afaDispMdtoCbSpsDAO;
	}
	
	private AfaPrescricaoMedicamentoDAO getAfaPrescricaoMedicamentoDAO(){
		return afaPrescricaoMedicamentoDAO;
	}

}