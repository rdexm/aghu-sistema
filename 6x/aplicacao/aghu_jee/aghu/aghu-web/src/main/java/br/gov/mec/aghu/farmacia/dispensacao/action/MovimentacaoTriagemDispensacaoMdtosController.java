package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MovimentacaoTriagemDispensacaoMdtosController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -7311058456544463024L;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private PesquisaDispensacaoMdtosPaginatorController pesquisaDispensacaoMdtosPaginatorController;
	
	@Inject
	private TratarOcorrenciasPaginatorController tratarOcorrenciasPaginatorController;
	
	@Inject
	private RealizarTriagemMedPrescrPaginatorController realizarTriagemMedPrescrPaginatorController;

	// Identificadores de itemPrescrito
	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private Integer medMatCodigo;
	private Short seqp;
	private MpmItemPrescricaoMdto itemPrescrito;
	private Integer seqPrescMed;
	private MpmPrescricaoMedica prescricaoMedica;
	
	// Campos de transição
	private Integer matCodigo;
	private Integer atdSeq;
	private Integer seqPresc;
	private AfaMedicamento medicamento;
	private AfaTipoOcorDispensacao ocorrencia;
	private AghUnidadesFuncionais farmacia;
	private Integer qtdForn;
	private DominioSituacaoDispensacaoMdto situacao;
	private DominioSituacaoItemPrescritoDispensacaoMdto sitItemPrescrito;
	
	//Listas para manipulacao
	private List<AfaDispensacaoMdtos> listaMedicamentosPrescritosModificados;
	private List<AfaDispensacaoMdtos> listaMedicamentosPrescritosOriginal;
	
	private AfaDispensacaoMdtos triagemSelecionada;
	
	//Atributos para compor cabecalho
	private String descricaoMdtoPrescrito;
	private DominioSituacaoItemPrescritoDispensacaoMdto situacaoItemPrescritoDispensacao;
	//Combos
	private List<AfaTipoOcorDispensacao> tiposOcorDispensacao;
	private List<AghUnidadesFuncionais> farmacias;
	private List<AfaMedicamento> medicamentos;
	//Armazena index do último registro selecionado
	//A última linha selecionada, mantinha-se com style selecionado, por este motivo força-se o style antigo
	private String indexRegistroUltimoClick;
	private String indexRegistroProblema;

	private String urlBtVoltar;
	private Boolean exibirBotaoLimpar;
	private Boolean controleBtnGravar;
	@Inject @Paginator
	private DynamicDataModel<AfaDispensacaoMdtos> dataModel;

	@Inject
	private DispMdtosCbPaginatorController dispMdtosCbPaginatorController;
	
	public void iniciarPagina() {
	 

		// Obtém a prescrição médica pelo id.
		this.prescricaoMedica = this.prescricaoMedicaFacade.obterPrescricaoPorId(pmdAtdSeq, seqPrescMed);
		MpmItemPrescricaoMdtoId idItemPrescrito = new MpmItemPrescricaoMdtoId(pmdAtdSeq, pmdSeq, medMatCodigo, seqp);
		this.itemPrescrito = this.prescricaoMedicaFacade.obterMpmItemPrescricaoMdtoPorChavePrimaria(idItemPrescrito);
		triagemSelecionada = new AfaDispensacaoMdtos();
		this.listaMedicamentosPrescritosOriginal = this.farmaciaDispensacaoFacade.pesquisarDispensacaoMdtoPorItemPrescrito(itemPrescrito,prescricaoMedica, false);
		this.listaMedicamentosPrescritosModificados = this.farmaciaDispensacaoFacade.pesquisarDispensacaoMdtoPorItemPrescrito(itemPrescrito, prescricaoMedica, true);
		if (listaMedicamentosPrescritosModificados == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MOVIMENTACAO_DISP_MDTOS_NENHUM_ITEM_DISPENSACAO");
			this.listaMedicamentosPrescritosModificados = this.farmaciaDispensacaoFacade.criarListaNovaMovimentacao();
		}
		// Combos
		this.farmacias = this.farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa("");
		this.tiposOcorDispensacao = farmaciaDispensacaoFacade.pesquisarTipoOcorrenciasAtivasENaoEstornada();
		this.medicamentos = farmaciaFacade.pesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica);
		
		this.descricaoMdtoPrescrito = this.farmaciaDispensacaoFacade.recuperaDescricaoMdtoPrescrito(prescricaoMedica,itemPrescrito);
		this.situacaoItemPrescritoDispensacao = listaMedicamentosPrescritosOriginal.get(0).getIndSitItemPrescrito();
		
		dataModel.reiniciarPaginator(); // exibir resultado na entrada da tela.
		this.indexRegistroProblema = "-1";
		this.indexRegistroUltimoClick = "-1";
		
		sitItemPrescrito = DominioSituacaoItemPrescritoDispensacaoMdto.IF;
		
		if(exibirBotaoLimpar==null){
			
			exibirBotaoLimpar=false;
		}
	
	}
	
	public String voltar() {
		return definirRetorno();
	}

	@Override
	public List<AfaDispensacaoMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.listaMedicamentosPrescritosModificados;
	}

	@Override
	public Long recuperarCount() {
		return (long) this.listaMedicamentosPrescritosModificados.size();
	}

	public String historico() {
		pesquisaDispensacaoMdtosPaginatorController.setAtdSeq(prescricaoMedica.getAtendimento().getSeq());
		pesquisaDispensacaoMdtosPaginatorController.setProntuario(prescricaoMedica.getAtendimento().getPaciente().getProntuario());
		pesquisaDispensacaoMdtosPaginatorController.setNomePaciente(prescricaoMedica.getAtendimento().getPaciente().getNome());
		pesquisaDispensacaoMdtosPaginatorController.setMatCodigo(matCodigo);
		pesquisaDispensacaoMdtosPaginatorController.getDataModel().reiniciarPaginator();
		pesquisaDispensacaoMdtosPaginatorController.setUrlBtVoltar("farmacia-editarTriagemDispensacaoMedicamentos");
		return "pesquisarDispensacaoMdtosList";
	}
	
	public String ocorrencias() {
		tratarOcorrenciasPaginatorController.setProntuario(prescricaoMedica.getAtendimento().getPaciente().getProntuario());
		tratarOcorrenciasPaginatorController.setCodigoPaciente(prescricaoMedica.getAtendimento().getPaciente().getCodigo());
		tratarOcorrenciasPaginatorController.setPaciente(prescricaoMedica.getAtendimento().getPaciente());
		tratarOcorrenciasPaginatorController.setUrlBtVoltar("farmacia-editarTriagemDispensacaoMedicamentos");
		tratarOcorrenciasPaginatorController.iniciarPagina();
		return "farmacia-tratarOcorrenciasList";
	}
	
	public String cancelar() {
		return definirRetorno();
	}
	
	public void selecionar(AfaDispensacaoMdtos selecao){
		triagemSelecionada = selecao;
	}

	public void exibirSitItemPrescrito(AfaDispensacaoMdtos selecao) {
		
		if (selecao.getIndSitItemPrescrito() != null) {
			this.situacaoItemPrescritoDispensacao = selecao.getIndSitItemPrescrito();
		} else {
			this.situacaoItemPrescritoDispensacao = null;
		}
	}
	
	public void selecionarSituacao(AfaDispensacaoMdtos selecao) {
		DominioSituacaoItemPrescritoDispensacaoMdto domSitItemPresc = 
			this.farmaciaDispensacaoFacade.verificarSituacaoItemPrescritoIncluidoFarmacia(selecao);
		selecao.setIndSitItemPrescrito(domSitItemPresc);
		this.situacaoItemPrescritoDispensacao = domSitItemPresc;
	}
	
	public String gravar() {
		try {
			String nomeMicrocomputador = null;
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			
			this.farmaciaDispensacaoFacade.persistirMovimentoDispensacaoMdtos(prescricaoMedica, 
					itemPrescrito, listaMedicamentosPrescritosModificados, 
					listaMedicamentosPrescritosOriginal, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_MOV_DISPMDTOS_SUCESSO");
			indexRegistroUltimoClick = "-1";
			
			return definirRetorno();
		} catch (ApplicationBusinessException e) {
			indexRegistroProblema = getIndexComProblemaByListaTriagem(listaMedicamentosPrescritosModificados);
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
		}
		return null;
	}
	
	private String definirRetorno(){
		try {
			if (urlBtVoltar.equals("farmacia-realizar-triagem-mdtos")) {
				realizarTriagemMedPrescrPaginatorController.iniciarPagina();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return urlBtVoltar;
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
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void processaMdtoSelecao(AfaDispensacaoMdtos adm){

		try{
			farmaciaDispensacaoFacade.processaMdtoSelecaoInMovTriagemDisp(adm,medicamentos);
			controleBtnGravarTrue();
		}catch (ApplicationBusinessException e) {
			adm.setMedicamento(new AfaMedicamento());
			apresentarExcecaoNegocio(e);
		}
		
	}
	public void processaSelecaoIndSituacao(AfaDispensacaoMdtos adm, DominioSituacaoDispensacaoMdto indSitAssoc){
		adm.setIndSituacaoNova(indSitAssoc);
		farmaciaDispensacaoFacade.processaImagensSituacoesDispensacao(adm);
		controleBtnGravarTrue();
	}
	
	private String getIndexComProblemaByListaTriagem(
			List<AfaDispensacaoMdtos> lista) {
		for(AfaDispensacaoMdtos adm: lista){
			if(adm.getIndexItemSendoAtualizado() !=null && adm.getIndexItemSendoAtualizado()){
				return String.valueOf(lista.indexOf(adm));
			}
		}
		return null;
	}

	
	public void controleBtnGravarTrue(){
		this.controleBtnGravar = Boolean.TRUE;
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(String str) {
		
		return this.returnSGWithCount(farmaciaFacade.pesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica, str),pesquisarMedicamentosCount(str));
	}
	
	
	public Long pesquisarMedicamentosCount(String str) {
		return farmaciaFacade.pesquisarMdtosParaDispensacaoPorItemPrescritoCount(itemPrescrito, prescricaoMedica, str);
	}
	
	public String setaParametrosDispensacaoMdtoCodBarras() throws ApplicationBusinessException{
		dispMdtosCbPaginatorController.setAtdSeqPrescricao(getPmdAtdSeq());
		dispMdtosCbPaginatorController.setSeqPrescricao(getSeqPrescMed());
		dispMdtosCbPaginatorController.setUrlBtVoltar("farmacia-editar-triagem-dispensacao-mdtos");
		dispMdtosCbPaginatorController.iniciarPagina();
		return "farmacia-dispensa-mdtos-codigo-barras";
	}
	/**
	 * Pesquisa Motivo: Tipo Ocorrência Dispensação
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaTipoOcorDispensacao> pesquisarTodosMotivosOcorrenciaDispensacao(String strPesquisa){
		return this.returnSGWithCount(farmaciaDispensacaoFacade.pesquisarTodosMotivosOcorrenciaDispensacao(strPesquisa),pesquisarTodosMotivosOcorrenciaDispensacaoCount(strPesquisa));
	}
	
	public Long pesquisarTodosMotivosOcorrenciaDispensacaoCount(String strPesquisa){
		return farmaciaDispensacaoFacade.pesquisarTodosMotivosOcorrenciaDispensacaoCount(strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFarmacia(String strParam) {
		List<AghUnidadesFuncionais> result = farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa(strParam);
		
		return this.returnSGWithCount(result,pesquisarUnidadesFarmaciaCount(strParam));
	}
	
	public Long pesquisarUnidadesFarmaciaCount(String strParam) {
		return farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisaCount(strParam);
	}
	
	/**
	 * Retorna lista contendo os valores de dominio
	 * @return
	 */
	public List<DominioSituacaoDispensacaoMdto> obterValoresDominioSituacaoDispensacaoMdto() {
		
		return Arrays.asList(DominioSituacaoDispensacaoMdto.S, DominioSituacaoDispensacaoMdto.T);
	}
	
	public void adicionarDispensacao(){
			AfaDispensacaoMdtos afaDispensacaoMdtos = new AfaDispensacaoMdtos();
			//Campos transient
			afaDispensacaoMdtos.setMatCodigoMdtoSelecionado(medicamento.getMatCodigo().toString());
			if (ocorrencia != null){
				afaDispensacaoMdtos.setSeqAfaTipoOcorSelecionada(ocorrencia.getSeq().toString());
			}
			afaDispensacaoMdtos.setSeqUnidadeFuncionalSelecionada(farmacia.getSeq().toString());
			
			afaDispensacaoMdtos.setMedicamento(medicamento);
			afaDispensacaoMdtos.setTipoOcorrenciaDispensacao(ocorrencia);
			afaDispensacaoMdtos.setUnidadeFuncional(farmacia);
			afaDispensacaoMdtos.setQtdeDispensada(new BigDecimal(qtdForn)); 
			afaDispensacaoMdtos.setIndSituacao(situacao);
			afaDispensacaoMdtos.setIndSitItemPrescrito(sitItemPrescrito);
			
			afaDispensacaoMdtos.setIndSituacaoNova(afaDispensacaoMdtos.getIndSituacao());
			farmaciaDispensacaoFacade.processaImagensSituacoesDispensacao(afaDispensacaoMdtos);
			
			listaMedicamentosPrescritosModificados.add(afaDispensacaoMdtos);
			
			medicamento = null;
			ocorrencia = null;
			farmacia = null;
			qtdForn = null;
			situacao = null;
	}

	// Getters e setters

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public List<AfaDispensacaoMdtos> getListaMedicamentosPrescritosModificados() {
		return listaMedicamentosPrescritosModificados;
	}

	public void setListaMedicamentosPrescritosModificados(
			List<AfaDispensacaoMdtos> listaMedicamentosPrescritosModificados) {
		this.listaMedicamentosPrescritosModificados = listaMedicamentosPrescritosModificados;
	}

	public MpmItemPrescricaoMdto getItemPrescrito() {
		return itemPrescrito;
	}

	public void setItemPrescrito(MpmItemPrescricaoMdto itemPrescrito) {
		this.itemPrescrito = itemPrescrito;
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

	public Integer getSeqPrescMed() {
		return seqPrescMed;
	}

	public void setSeqPrescMed(Integer seqPrescMed) {
		this.seqPrescMed = seqPrescMed;
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

	public List<AfaMedicamento> getMedicamentos() {
		return medicamentos;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public AfaTipoOcorDispensacao getOcorrencia() {
		return ocorrencia;
	}

	public AghUnidadesFuncionais getFarmacia() {
		return farmacia;
	}
	
	public DominioSituacaoDispensacaoMdto getSituacao() {
		return situacao;
	}

	public DominioSituacaoItemPrescritoDispensacaoMdto getSitItemPrescrito() {
		return sitItemPrescrito;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public void setOcorrencia(AfaTipoOcorDispensacao ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public void setFarmacia(AghUnidadesFuncionais farmacia) {
		this.farmacia = farmacia;
	}

	public void setSituacao(DominioSituacaoDispensacaoMdto situacao) {
		this.situacao = situacao;
	}

	public void setSitItemPrescrito(
			DominioSituacaoItemPrescritoDispensacaoMdto sitItemPrescrito) {
		this.sitItemPrescrito = sitItemPrescrito;
	}

	public void setMedicamentos(List<AfaMedicamento> medicamentos) {
		this.medicamentos = medicamentos;
	}

	public String getIndexRegistroProblema() {
		return indexRegistroProblema;
	}

	public void setIndexRegistroProblema(String indexRegistroProblema) {
		this.indexRegistroProblema = indexRegistroProblema;
	}

	public String getIndexRegistroUltimoClick() {
		return indexRegistroUltimoClick;
	}

	public void setIndexRegistroUltimoClick(String indexRegistroUltimoClick) {
		this.indexRegistroUltimoClick = indexRegistroUltimoClick;
	}

	public String getDescricaoMdtoPrescrito() {
		return descricaoMdtoPrescrito;
	}

	public void setDescricaoMdtoPrescrito(String descricaoMdtoPrescrito) {
		this.descricaoMdtoPrescrito = descricaoMdtoPrescrito;
	}

	public DominioSituacaoItemPrescritoDispensacaoMdto getSituacaoItemPrescritoDispensacao() {
		return situacaoItemPrescritoDispensacao;
	}

	public void setSituacaoItemPrescritoDispensacao(
			DominioSituacaoItemPrescritoDispensacaoMdto situacaoItemPrescritoDispensacao) {
		this.situacaoItemPrescritoDispensacao = situacaoItemPrescritoDispensacao;
	}

	public List<AfaDispensacaoMdtos> getListaMedicamentosPrescritosOriginal() {
		return listaMedicamentosPrescritosOriginal;
	}

	public void setListaMedicamentosPrescritosOriginal(
			List<AfaDispensacaoMdtos> listaMedicamentosPrescritosOriginal) {
		this.listaMedicamentosPrescritosOriginal = listaMedicamentosPrescritosOriginal;
	}
	
	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public AfaDispensacaoMdtos getTriagemSelecionada() {
		return triagemSelecionada;
	}

	public void setTriagemSelecionada(AfaDispensacaoMdtos triagemSelecionada) {
		this.triagemSelecionada = triagemSelecionada;
	}

	public Boolean getControleBtnGravar() {
		return controleBtnGravar;
	}

	public void setControleBtnGravar(Boolean controleBtnGravar) {
		this.controleBtnGravar = controleBtnGravar;
	}

	public Integer getQtdForn() {
		return qtdForn;
	}

	public void setQtdForn(Integer qtdForn) {
		this.qtdForn = qtdForn;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeqPresc() {
		return seqPresc;
	}

	public void setSeqPresc(Integer seqPresc) {
		this.seqPresc = seqPresc;
	}

	public Boolean getExibirBotaoLimpar() {
		return exibirBotaoLimpar;
	}

	public void setExibirBotaoLimpar(Boolean exibirBotaoLimpar) {
		this.exibirBotaoLimpar = exibirBotaoLimpar;
	}
}