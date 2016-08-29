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

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.RealizarTriagemMedicamentosPrescricaoON.RealizarTriagemMedicamentosPrescricaoONExceptionCode;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class EstornaMedicamentoDispensadoON extends BaseBusiness implements Serializable {


@EJB
private EstornaMedicamentoDispensadoRN estornaMedicamentoDispensadoRN;

private static final Log LOG = LogFactory.getLog(EstornaMedicamentoDispensadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7054278039685913153L;

	public enum EstornarMedicamentoDispensadoONExceptionCode implements	BusinessExceptionCode {
		MESSAGE_QTDE_EST_MAIOR_DISP,MESSAGE_QTDE_EST_INVALIDA,MESSAGE_TIPO_OCOR_ESTORNO_OBRIGATORIO,
		MESSAGE_DATA_FIM_NAO_PREENCHIDA, MESSAGE_DATA_INICIO_NAO_PREENCHIDA, MESSAGE_DATA_INICIO_MAIOR_QUE_DATA_FIM,
		MESSAGE_PACIENTE_SELECIONADO_SEM_ATENDIMENTO,MENSAGEM_LIMITE_DIAS_CONSULTA,
		PRONTUARIO_INVALIDO_ESTORNAR_MDTOS, CODIGO_INVALIDO_ESTORNAR_MDTOS, MESSAGE_DATA_INICIO_MENOR_QUE_DATA_LIMITE
		,MEDICAMENTO_VENCIDO_ESTORNO_MEDICAMENTOS, MESSAGE_QTDE_EST_MAIOR_ANTERIOR
	}
	
	//Pesquisas
	
	public List<AfaDispensacaoMdtos> recuperarListaDispensacaoMedicamentos(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AfaMedicamento medicamento, AipPacientes paciente,
			boolean objectAtachado, Long seqAfaDispMdto)
			throws ApplicationBusinessException {
		Date dthrLimiteEstorno = recuperarDataLimite();
		
		//Tratamento para campos do tipo Date
		Date dataDeReferenciaInicioPesq = DateUtil.truncaData(dataDeReferenciaInicio);
		Date dataDeReferenciaFimPesq = DateUtil.truncaData(DateUtil.adicionaDias(dataDeReferenciaFim, 1));
		
		List<AghAtendimentos> atendimentos = getAghuFacade()
				.obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(
						/*DominioPacAtendimento.S*/null, 
						paciente.getCodigo(), DominioOrigemAtendimento.getOrigensPermitemPrescricaoMedica());
		// Integer seqAtendimento = (atendimento != null ? atendimento.getSeq() : null);
		
		List<AfaDispensacaoMdtos> listaDispensacaoMedicamentos = getAfaDispensacaoMdtosDAO()
				.pesquisarDispensacaoMdtosPorProntuarioDataMedicamento(
						dataDeReferenciaInicioPesq, dataDeReferenciaFimPesq,
						medicamento, paciente, dthrLimiteEstorno,
						atendimentos,//seqAtendimento,
						seqAfaDispMdto,
						DominioSituacaoDispensacaoMdto.D,
						DominioSituacaoDispensacaoMdto.C,
						DominioSituacaoDispensacaoMdto.E);
		
		for(int i =0; i<listaDispensacaoMedicamentos.size();i++){
			AfaDispensacaoMdtos dispMdto = listaDispensacaoMedicamentos.get(i);
			dispMdto.setQtdeDispensada(new BigDecimal(getAfaDispensacaoMdtosDAO().pesquisarQtdeDispensadaMdto(dispMdto.getSeq())));
			if(dispMdto.getTipoOcorrenciaDispensacaoEstornado()!=null) {
				dispMdto.setSeqAfaTipoOcorSelecionada(dispMdto.getTipoOcorrenciaDispensacaoEstornado().getSeq().toString());
			}
		}
		
		return listaDispensacaoMedicamentos;
	}
	
	public Date recuperarDataLimite() throws ApplicationBusinessException {
		AghParametros diasEstorno = getEstornaMedicamentoDispensadoRN().buscarDiasEstorno();
		
		Date dthrLimiteEstorno = getEstornaMedicamentoDispensadoRN().obterDataLimiteEstorno(diasEstorno);
		return dthrLimiteEstorno;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}	
	
	private IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	//Estornos
	
	/**
	 * Verifica e persiste as alterações para estorno de medicamentos
	 * Utiliza Trigger AGH.AFAT_DSM_BRU
	 * 
	 * @param listaDispensacaoModificada
	 * @param listaDispensacaoOriginal
	 * @throws ApplicationBusinessException
	 */
	public void realizaEstornoMedicamentoDispensadoDaLista(
			List<AfaDispensacaoMdtos> listaDispensacaoModificada,
			List<AfaDispensacaoMdtos> listaDispensacaoOriginal, String nomeMicrocomputador) throws BaseException {

		for(int i = 0; i< listaDispensacaoModificada.size(); i++){
			AfaDispensacaoMdtos admOld = listaDispensacaoOriginal.get(i);
			AfaDispensacaoMdtos admNew = listaDispensacaoModificada.get(i);
			if (verificaSeSofreuAlteracao(admOld, admNew)) {
				validaPreenchimentoCampos(admNew,admOld);
				
				//Implementacao regra referente a estoria #14504
				//Necessário armazenar qtde de estorno porque o estoque não avalida a diferença
				BigDecimal qtdeEstornada = admNew.getQtdeEstornada();
				if(admOld.getQtdeEstornada() != null){//envia pro estoque somente a diferença
					admNew.setQtdeEstornada(qtdeEstornada.subtract(admOld.getQtdeEstornada()));
				}
				// Faz alterações relacionadas ao estorno no item da RM (estória 14504) 
				getEstoqueFacade().estornarMedicamentoRequisicaoMaterial(admNew, admOld, null, nomeMicrocomputador);
				
				admNew.setQtdeEstornada(qtdeEstornada);
			}	
		}
	}

	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	/**
	 * Estorna medicamento dispensado com etiqueta
	 * 
	 * @param pEtiqueta
	 * @throws ApplicationBusinessException
	 */
	public void estornarMedicamentoDispensadoByEtiquetaComCb(String pEtiqueta, String nomeMicrocomputador) throws BaseException {
		getEstornaMedicamentoDispensadoRN().estornarDispensacaoMdtoComEtiqueta(pEtiqueta, nomeMicrocomputador);
	}
	
	//Util
	
	/**
	 * Verifica se para a dispensacao de medicamento, os campos obrigatórios para estorno foram preenchidos
	 * Campos: QTDE_ESTORNADA e TIPO_OCORRENCIA_ESTORNO
	 */
	private void validaPreenchimentoCampos(AfaDispensacaoMdtos admNew, AfaDispensacaoMdtos admOld) throws ApplicationBusinessException {
		admNew.setIndexItemSendoAtualizado(Boolean.TRUE);
		
		/*if (admNew.getQtdeEstornada() == null || admNew.getQtdeEstornada().intValue() == 0) { 
			if (admOld.getQtdeEstornada() != null && admOld.getQtdeEstornada().intValue() > 0) {
				throw new AGHUNegocioExceptionSemRollback(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_QTDE_EST_INVALIDA);
			}	
		}*/
		if (admNew.getQtdeEstornada() != null) {
			if (admNew.getTipoOcorrenciaDispensacaoEstornado() == null) {
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_TIPO_OCOR_ESTORNO_OBRIGATORIO);
			}
			if (admOld.getQtdeEstornada() != null && admOld.getQtdeEstornada().compareTo(admNew.getQtdeEstornada()) == 1) {
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_QTDE_EST_MAIOR_ANTERIOR);
			}
			if (admNew.getQtdeEstornada().compareTo(admNew.getQtdeDispensada()) == 1) {
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_QTDE_EST_MAIOR_DISP);
			}
		}else{
			if (admOld.getQtdeEstornada() != null) {
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_QTDE_EST_MAIOR_ANTERIOR);
			}
		}
		admNew.setIndexItemSendoAtualizado(Boolean.FALSE);
	}
	
	/**
	 * Valida o preenchimento de datas utilizadas para 
	 * pesquisa de dispensacao de medicamentos para estorno
	 * 
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @throws ApplicationBusinessException
	 */
	public void validarDatas(Date dataDeReferenciaInicio, Date dataDeReferenciaFim) throws ApplicationBusinessException {
		if ((dataDeReferenciaInicio != null) && (dataDeReferenciaFim == null)){
			// Data de fim deve estar preenchida
			throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_DATA_FIM_NAO_PREENCHIDA);
		}else {
			if ((dataDeReferenciaInicio == null) && (dataDeReferenciaFim != null)){
				// Data de inicio deve estar preenchida
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_DATA_INICIO_NAO_PREENCHIDA);
			}else {
				if ((dataDeReferenciaInicio != null) && (dataDeReferenciaFim != null)){
					if (dataDeReferenciaInicio.compareTo(dataDeReferenciaFim) > 0){
						// Data de Incio deve ser menor que data de Fim
						throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_DATA_INICIO_MAIOR_QUE_DATA_FIM);
					}
					if(DateUtil.truncaData(recuperarDataLimite()).compareTo(dataDeReferenciaInicio) > 0){
						throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_DATA_INICIO_MENOR_QUE_DATA_LIMITE, DateUtil.dataToString(recuperarDataLimite(), "dd/MM/yyyy"));
					}
				}
			}
		}
		
		Integer difDiasInformado = DateUtil.calcularDiasEntreDatas(dataDeReferenciaInicio, dataDeReferenciaFim);
		if (difDiasInformado > 90) {
			throw new ApplicationBusinessException(
					EstornarMedicamentoDispensadoONExceptionCode.MENSAGEM_LIMITE_DIAS_CONSULTA,
					90,
					DateUtil.dataToString(dataDeReferenciaInicio, "dd/MM/yyyy"),
					DateUtil.dataToString(dataDeReferenciaFim, "dd/MM/yyyy"),
					difDiasInformado);
		}
	}
	
	/**
	 * Verifica se houve alteração na tela
	 * @param admOld
	 * @param admNew
	 * @return
	 */
	private boolean verificaSeSofreuAlteracao(
			AfaDispensacaoMdtos admOld, AfaDispensacaoMdtos admNew) {
		if (!CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada())
				|| !CoreUtil.igual(admOld.getQtdeEstornada(), admNew.getQtdeEstornada())
				|| !CoreUtil.igual(admOld.getTipoOcorrenciaDispensacaoEstornado(), admNew.getTipoOcorrenciaDispensacaoEstornado())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void processaAfaTipoOcorBySeqInAfaDispMdtoEstorno(AfaDispensacaoMdtos adm, Short seqMotivoEstornoPrescricaNaoEletronica) throws ApplicationBusinessException {
		adm.setTipoOcorrenciaDispensacaoEstornado(null);
		if(adm.getSeqAfaTipoOcorSelecionada() != null && !"".equals(adm.getSeqAfaTipoOcorSelecionada().trim())){
			List<AfaTipoOcorDispensacao> tiposOcorrenciaDispensacaoEstornado = getFarmaciaFacade().pesquisarTipoOcorrenciasAtivasEstornadas(seqMotivoEstornoPrescricaNaoEletronica);
			for(AfaTipoOcorDispensacao tod:tiposOcorrenciaDispensacaoEstornado){
				if(tod.getSeq().toString().equals(adm.getSeqAfaTipoOcorSelecionada())){
					adm.setTipoOcorrenciaDispensacaoEstornado(tod);
					return;
				}
			}
			adm.setTipoOcorrenciaDispensacao(null);
			throw new ApplicationBusinessException(RealizarTriagemMedicamentosPrescricaoONExceptionCode.NENHUM_VALOR_ENCONTRADO_MOTIVO, adm.getSeqAfaTipoOcorSelecionada());
		}
	}
	
	//Getters
	
	private EstornaMedicamentoDispensadoRN getEstornaMedicamentoDispensadoRN(){
		return estornaMedicamentoDispensadoRN;
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}

	public AipPacientes obterPacienteComAtendimentoPorProntuarioOUCodigo(
			Integer numeroProntuario, Integer codigoPaciente) throws ApplicationBusinessException  {
		AipPacientes paci = null;

		List<AipPacientes> listaPacientes = getPacienteFacade().obterPacienteComAtendimentoPorProntuarioOUCodigo
		(numeroProntuario, codigoPaciente, DominioOrigemAtendimento.getOrigensPermitemPrescricaoMedica());
		
		if (listaPacientes != null && listaPacientes.size() > 0) {
			paci = listaPacientes.get(0);
		}
		
		if(paci == null ){
			if(codigoPaciente == null && numeroProntuario != null && getPacienteFacade().obterPacientePorProntuario(numeroProntuario) == null){
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.PRONTUARIO_INVALIDO_ESTORNAR_MDTOS);
			}else if(codigoPaciente != null && getPacienteFacade().obterPacientePorCodigo(codigoPaciente) == null){
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.CODIGO_INVALIDO_ESTORNAR_MDTOS);
			}else{
				throw new ApplicationBusinessException(EstornarMedicamentoDispensadoONExceptionCode.MESSAGE_PACIENTE_SELECIONADO_SEM_ATENDIMENTO);
			}
		} 
		return paci;
	}

	public String validaSeMedicamentoVencidoByEtiqueta(String etiqueta) throws ApplicationBusinessException {
		SceLoteDocImpressao loteDocImp = getEstoqueFacade().getLoteDocImpressaoByNroEtiqueta(etiqueta);
		if(DateValidator.validaDataMenorQueAtual(loteDocImp.getDtValidade())){
			return getResourceBundleValue("MEDICAMENTO_VENCIDO_ESTORNO_MEDICAMENTOS");
		}
		return null;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
}