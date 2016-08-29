package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCoresSituacaoItemPrescrito;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class RealizarTriagemMedPrescrPaginatorController extends ActionController/* implements ActionPaginator */{
	
	private static final long serialVersionUID = 1015787014955026334L;

	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private MpmPrescricaoMedica prescricaoMedica;
	private FatConvenioSaude convenioSaude;
	
	private Integer atdSeqPrescricao;
	private Integer seqPrescricao;	
	
	//listaTriagemModificada não consiste que todos os objetos desta lista foram modificados
	private List<AfaDispensacaoMdtos> listaTriagemModificada;
	private AfaDispensacaoMdtos mdtoSelecionado;
	private List<AfaDispensacaoMdtos> listaTriagemOriginal;
	
	//private AfaDispensacaoMdtos triagemSelecionada;
	
	private List<AfaTipoOcorDispensacao> tiposOcorDispensacao;
	private List<AghUnidadesFuncionais> farmacias;
	
	private Boolean tipoOcorAlterado;
	private Boolean controleBtnGravar;
	
	private Boolean flagRegistroAnterior;
	private Boolean flagRegistroProximo;
	
	//A tela permite que o usuário selecione próximo registro ou anterior
	//estas váriaveis representam o número da prescrição próxima e anterior
	private Integer seqPrescricaoProxima;
	private Integer seqPrescricaoAnterior;
	
	
	private String urlBtVoltar;
	private Boolean encerraConversacaoBtVoltar;
	
	/*
	 * Parametros utilizados em editarTriagemDispensacao
	 */
	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private Integer medMatCodigo;
	private Short seqp;
	
	private String nomeComputadorRede;
	private AghMicrocomputador computador;
	
	private Short unfSeq;
	
	//Melhoria 15787, Defeito 15939
	private Boolean permiteClicarEmOcorrencias;
	
	/**/
	private Boolean ativo;
	
	@Inject
	private DispMdtosCbPaginatorController dispMdtosCbPaginatorController;
	
	@Inject
	private MedicamentosDispensacaoPaginatorController medicamentosDispensacaoPaginatorController;
	
	@Inject
	private MovimentacaoTriagemDispensacaoMdtosController movimentacaoTriagemDispensacaoMdtosController;
	
	@Inject
	private TratarOcorrenciasPaginatorController tratarOcorrenciasPaginatorController;
	
	
	public void iniciarPagina() throws ApplicationBusinessException{
		try {
			nomeComputadorRede = getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
		}	

		//Obtém a prescrição médica pelo id
		this.prescricaoMedica = this.prescricaoMedicaFacade.obterPrescricaoComFatConvenioSaude(atdSeqPrescricao, seqPrescricao);
		
		FatConvenioSaudePlano convenioSaudePlano = pacienteFacade.obterConvenioSaudePlanoAtendimento(atdSeqPrescricao);
		
		Short seqFatConvenioSaude = convenioSaudePlano.getId().getCnvCodigo();
		this.convenioSaude = faturamentoFacade.obterConvenioSaude(seqFatConvenioSaude);
		
		ativo = Boolean.TRUE;
		listaTriagemOriginal = this.farmaciaDispensacaoFacade.recuperarListaTriagemMedicamentosPrescricao(prescricaoMedica);
		listaTriagemModificada = this.farmaciaDispensacaoFacade.recuperarListaTriagemMedicamentosPrescricao(prescricaoMedica);
		farmacias = farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa("");
		tiposOcorDispensacao = farmaciaDispensacaoFacade.pesquisarTipoOcorrenciasAtivasENaoEstornada();
		//triagemSelecionada = new AfaDispensacaoMdtos();
		//Ao carregar a tela, verificar se existe triagem do tipo geladeira, caso sim a TipoOCorDisp é alterado e valorAlterado tem que iniciar com TRUE
		tipoOcorAlterado = this.farmaciaDispensacaoFacade.processaTipoOcorrenciaParaListaMedicamentosPrescricao(listaTriagemModificada);
		controleBtnGravar = tipoOcorAlterado;
		
		seqPrescricaoAnterior= farmaciaDispensacaoFacade.processaProximaPrescricaoTriagemMedicamentoByProntuario(atdSeqPrescricao, seqPrescricao, Boolean.FALSE);
		seqPrescricaoProxima = farmaciaDispensacaoFacade.processaProximaPrescricaoTriagemMedicamentoByProntuario(atdSeqPrescricao, seqPrescricao, Boolean.TRUE);
		
		flagRegistroAnterior = seqPrescricaoAnterior !=null ? Boolean.TRUE:Boolean.FALSE;
		flagRegistroProximo  = seqPrescricaoProxima !=null ? Boolean.TRUE:Boolean.FALSE;

		permiteClicarEmOcorrencias  = farmaciaDispensacaoFacade.verificarAcessoOcorrenciaDeTriagem(nomeComputadorRede);//MENSAGEM_UNIDADE_FUNCIONAL_MICRO_NAO_FARMACIA; 
	}

/*	@Override
	protected Integer recuperarCount() {
		return listaTriagemModificada.size();
	}

	@Override
	protected List<AfaDispensacaoMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return listaTriagemModificada;
	}*/
	
	public String gravar(){
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
			}
			this.farmaciaDispensacaoFacade.realizaTriagemMedicamentoPrescricao(listaTriagemModificada, listaTriagemOriginal, nomeMicrocomputador);
			/**///this.farmaciaFacade.flush();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TRIAGEM_ALTERADA_SUCESSO");
			controleBtnGravar = Boolean.FALSE;
			tipoOcorAlterado  = Boolean.FALSE;
			iniciarPagina();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			controleBtnGravar = Boolean.FALSE;
			tipoOcorAlterado  = Boolean.TRUE;
		}
		return null;
	}
	
	/*public void selecionar(AfaDispensacaoMdtos selecao){
		triagemSelecionada = selecao;
	}*/
	
	public void triarTodosItens(){
		for(AfaDispensacaoMdtos adm:listaTriagemModificada) {
			if(adm.getIndSituacao().equals(DominioSituacaoDispensacaoMdto.S)) {
				processaSelecaoIndSituacao(adm, DominioSituacaoDispensacaoMdto.T);
				//adm.setIndSituacao(DominioSituacaoDispensacaoMdto.T);
			}
		}
		gravar();
	}
	
	//Funcionalidades a serem desenvolvidas em outras estórias
	
	public String ocorrencias(){
		if(permiteClicarEmOcorrencias){
			tratarOcorrenciasPaginatorController.setProntuario(getPrescricaoMedica().getAtendimento().getPaciente().getProntuario());
			tratarOcorrenciasPaginatorController.setUnfSeq(getUnfSeq());
			tratarOcorrenciasPaginatorController.setUrlBtVoltar("realizarTriagemMedicamentosPrescricaoList");
			tratarOcorrenciasPaginatorController.setOrigemTelaRealizarTriagem(Boolean.TRUE);
			//tratarOcorrenciasPaginatorController.iniciarPagina();
			return RetornoAcaoStrEnum.TRATAR_OCORRENCIAS.toString();
		}else{
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_UNIDADE_FUNCIONAL_MICRO_NAO_FARMACIA");
			controleBtnGravar = Boolean.FALSE;
			tipoOcorAlterado  = Boolean.TRUE;
			return null;
		}
		//this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_AFAF_TRATAR_OCORRE_DESENV");
	}
	
	public String editarTriagem(){
		movimentacaoTriagemDispensacaoMdtosController.setPmdAtdSeq(getPmdAtdSeq());
		movimentacaoTriagemDispensacaoMdtosController.setPmdSeq(getPmdSeq());
		movimentacaoTriagemDispensacaoMdtosController.setMedMatCodigo(getMedMatCodigo());
		movimentacaoTriagemDispensacaoMdtosController.setSeqp(getSeqp());
		movimentacaoTriagemDispensacaoMdtosController.setSeqPrescMed(getPrescricaoMedica().getId().getSeq());
		movimentacaoTriagemDispensacaoMdtosController.setUrlBtVoltar("farmacia-realizar-triagem-mdtos");
		return RetornoAcaoStrEnum.EDITAR_TRIAGEM.toString();
		//this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_AFAF_TRATAR_OCORRE_DESENV");
	}
	
	public String movimento(){
		return RetornoAcaoStrEnum.MOVIMENTO_TRIAGEM_DISPENSACAO.toString();
		//this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_AFAF_MVTO_DISP_MDTO_DESENV");
	}
	
	public String dispensar() throws ApplicationBusinessException{
		try{
			computador = administracaoFacade.obterAghMicroComputadorPorNomeOuIPException(nomeComputadorRede);
			if(computador != null) {
				administracaoFacade.desatacharAghMicroComputador(computador);
				setaParametrosDispensacaoMdtoCodBarras();
				return RetornoAcaoStrEnum.DISPENSAR_COD_BARRAS.toString();
			}	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;		
	}
	
	private void setaParametrosDispensacaoMdtoCodBarras() throws ApplicationBusinessException {
		dispMdtosCbPaginatorController.setAtdSeqPrescricao(getAtdSeqPrescricao());
		dispMdtosCbPaginatorController.setSeqPrescricao(getSeqPrescricao());
		dispMdtosCbPaginatorController.setUrlBtVoltar("realizarTriagemMedicamentosPrescricaoList");
		dispMdtosCbPaginatorController.iniciarPagina();
	}

	public String cancelar(){
		listaTriagemModificada = null;
		listaTriagemOriginal = null;
		return urlBtVoltar;
		//return RetornoAcaoStrEnum.CANCELAR_TRIAGEM.toString();
	}
	
	public String situacao(){
		medicamentosDispensacaoPaginatorController.setAtdSeqPrescricao(getPrescricaoMedica().getId().getAtdSeq());
		medicamentosDispensacaoPaginatorController.setSeqPrescricao(getPrescricaoMedica().getId().getSeq());
		medicamentosDispensacaoPaginatorController.setPrescricaoMedica(getPrescricaoMedica());
		medicamentosDispensacaoPaginatorController.setUnfSeq(getUnfSeq());
		medicamentosDispensacaoPaginatorController.setUrlBtVoltar("farmacia-realizar-triagem-mdtos");
		medicamentosDispensacaoPaginatorController.getDataModel().reiniciarPaginator();
		return RetornoAcaoStrEnum.SITUACAO_DISPENSACAO.toString();
		//this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_AFAF_CONS_SIT_DISP_DESENV");
	}
	
	public void controleBtnGravarTrue(){
		this.controleBtnGravar = Boolean.TRUE;
	}
	
	public void processaAfaOcorDispSelecao(AfaDispensacaoMdtos adm){
		try{
			farmaciaDispensacaoFacade.processaAfaTipoOcorBySeqInAfaDispMdto(adm);
			controleBtnGravarTrue();
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void processaAghUnfSelecao(AfaDispensacaoMdtos adm){
		try{
			farmaciaDispensacaoFacade.processaAghUnfSeqInAfaDispMdto(adm);
			controleBtnGravarTrue();
			processarQtdeDisponivelEstoque(adm);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void processarQtdeDisponivelEstoque(AfaDispensacaoMdtos adm) {
		farmaciaDispensacaoFacade.processarQtdeMaterialDisponivelEstoque(adm);
		
	}

	public void processaSelecaoIndSituacao(AfaDispensacaoMdtos adm, DominioSituacaoDispensacaoMdto indSitAssoc){
		adm.setIndSituacaoNova(indSitAssoc);
		farmaciaDispensacaoFacade.processaImagensSituacoesDispensacao(adm);
		controleBtnGravarTrue();
	}
	
	/**
	 * Efetua operação da seta na tela
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String processaRegistroProximo() throws ApplicationBusinessException{
		seqPrescricao = seqPrescricaoProxima;
		iniciarPagina();
		return null;
	}
	
	public String processaCorLinhaTriagem(AfaDispensacaoMdtos adm){
		if(adm != null && adm.getCorSituacaoItemPrescrito() != null) {
			return adm.getCorSituacaoItemPrescrito().getDescricao();
		} else {
			return "";
		}
	}
	
	/**
	 * Efetua operação da seta na tela
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String processaRegistroAnterior() throws ApplicationBusinessException{
		seqPrescricao = seqPrescricaoAnterior;
		iniciarPagina();
		return null;
	}
	
	public DominioCoresSituacaoItemPrescrito[] getDominioCoresSituacaoItemPrescrito(){
		return DominioCoresSituacaoItemPrescrito.values();
	}
	
	// Getters and Setters
	// ===================

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Integer getAtdSeqPrescricao() {
		return atdSeqPrescricao;
	}

	public void setAtdSeqPrescricao(Integer atdSeqPrescricao) {
		this.atdSeqPrescricao = atdSeqPrescricao;
	}

	public Integer getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(Integer seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public List<AfaDispensacaoMdtos> getListaTriagemModificada() {
		return listaTriagemModificada;
	}

	public void setListaTriagemModificada(
			List<AfaDispensacaoMdtos> listaTriagemModificada) {
		this.listaTriagemModificada = listaTriagemModificada;
	}

	public List<AfaDispensacaoMdtos> getListaTriagemOriginal() {
		return listaTriagemOriginal;
	}

	public void setListaTriagemOriginal(
			List<AfaDispensacaoMdtos> listaTriagemOriginal) {
		this.listaTriagemOriginal = listaTriagemOriginal;
	}

	/*public AfaDispensacaoMdtos getTriagemSelecionada() {
		return triagemSelecionada;
	}

	public void setTriagemSelecionada(AfaDispensacaoMdtos triagemSelecionada) {
		this.triagemSelecionada = triagemSelecionada;
	}*/

	public Boolean getTipoOcorAlterado() {
		return tipoOcorAlterado;
	}

	public void setTipoOcorAlterado(Boolean tipoOcorAlterado) {
		this.tipoOcorAlterado = tipoOcorAlterado;
	}

	public Boolean getControleBtnGravar() {
		return controleBtnGravar;
	}

	public void setControleBtnGravar(Boolean controleBtnGravar) {
		this.controleBtnGravar = controleBtnGravar;
	}
	
	public static enum RetornoAcaoStrEnum {
		
		TRATAR_OCORRENCIAS("tratarOcorrenciasList"),
		CANCELAR_TRIAGEM("cancelarTriagem"),
		MOVIMENTO_TRIAGEM_DISPENSACAO("movimentoTriagemDispensacao"),
		DISPENSAR_COD_BARRAS("dispensacaoMdtosCodBarrasList"),//migrado
		SITUACAO_DISPENSACAO("farmacia-medicamento-situacao-dispensacao"),
		EDITAR_TRIAGEM("farmacia-editar-triagem-dispensacao-mdtos");		
		private final String str;
		
		RetornoAcaoStrEnum(String str) {
			
			this.str = str;
		}
		
		@Override
		public String toString() {
			
			return this.str;
		}
	}

	public List<AfaTipoOcorDispensacao> getTiposOcorDispensacao() {
		return tiposOcorDispensacao;
	}

	public void setTiposOcorDispensacao(
			List<AfaTipoOcorDispensacao> tiposOcorDispensacao) {
		this.tiposOcorDispensacao = tiposOcorDispensacao;
	}

	public List<AghUnidadesFuncionais> getFarmacias() {
		return farmacias;
	}

	public void setFarmacias(List<AghUnidadesFuncionais> farmacias) {
		this.farmacias = farmacias;
	}

	public Boolean getFlagRegistroAnterior() {
		return flagRegistroAnterior;
	}

	public void setFlagRegistroAnterior(Boolean flagRegistroAnterior) {
		this.flagRegistroAnterior = flagRegistroAnterior;
	}

	public Boolean getFlagRegistroProximo() {
		return flagRegistroProximo;
	}

	public void setFlagRegistroProximo(Boolean flagRegistroProximo) {
		this.flagRegistroProximo = flagRegistroProximo;
	}

	public Integer getSeqPrescricaoProxima() {
		return seqPrescricaoProxima;
	}

	public void setSeqPrescricaoProxima(Integer seqPrescricaoProxima) {
		this.seqPrescricaoProxima = seqPrescricaoProxima;
	}

	public Integer getSeqPrescricaoAnterior() {
		return seqPrescricaoAnterior;
	}

	public void setSeqPrescricaoAnterior(Integer seqPrescricaoAnterior) {
		this.seqPrescricaoAnterior = seqPrescricaoAnterior;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public Boolean getEncerraConversacaoBtVoltar() {
		return encerraConversacaoBtVoltar;
	}

	public void setEncerraConversacaoBtVoltar(Boolean encerraConversacaoBtVoltar) {
		this.encerraConversacaoBtVoltar = encerraConversacaoBtVoltar;
	}

	public AghMicrocomputador getComputador() {
		return computador;
	}

	public void setComputador(AghMicrocomputador computador) {
		this.computador = computador;
	}

	public Boolean getPermiteClicarEmOcorrencias() {
		return permiteClicarEmOcorrencias;
	}

	public void setPermiteClicarEmOcorrencias(Boolean permiteClicarEmOcorrencias) {
		this.permiteClicarEmOcorrencias = permiteClicarEmOcorrencias;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	public Long getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public FatConvenioSaude getConvenioSaude() {
		return convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude convenioSaude) {
		this.convenioSaude = convenioSaude;
	}

	public AfaDispensacaoMdtos getMdtoSelecionado() {
		return mdtoSelecionado;
	}

	public void setMdtoSelecionado(AfaDispensacaoMdtos mdtoSelecionado) {
		this.mdtoSelecionado = mdtoSelecionado;
	}

}