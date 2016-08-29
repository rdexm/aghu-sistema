package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class ManterAnamneseEvolucaoAbaEvolucoesAnterioresController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MpmEvolucoes> dataModel = new DynamicDataModel<MpmEvolucoes>(this);

	private static final long serialVersionUID = 1345476586786L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Date dataInicial;
	private Date dataFinal;
	private String descricaoEvolucao;
	private MpmEvolucoes evolucaoSelecionada;
	private Long seqAnamnese;

	public void pesquisar() {
		
		this.descricaoEvolucao = null;
		
		if (validarCampos()) {
			this.dataModel.reiniciarPaginator();
		}
	}

	private Boolean validarCampos() {
		if (dataInicial != null || dataFinal != null) {
			if (dataInicial == null) {
				apresentarMsgNegocio("dataInicial", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data Inicial");
				return Boolean.FALSE;
			}
			if (dataFinal == null) {
				apresentarMsgNegocio("dataFinal", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data Final");
				return Boolean.FALSE;
			}
			
			if(DateUtil.validaDataTruncadaMaior(dataInicial, dataFinal)){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_VISUALIZAR_EVOLUCOES_ORDEM_DATA_MENOR");
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	public void limparPesquisa() {
		this.dataInicial = null;
		this.dataFinal = null;
		this.descricaoEvolucao = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public boolean habilitarAbaEvolucoesAnteriores() {
		return this.prescricaoMedicaFacade.verificarAnamneseValida(this.seqAnamnese) && 
		   this.prescricaoMedicaFacade.verificarEvolucoesValidadas(this.seqAnamnese);
	}

	public void setDescricaoEvolucao() {
		StringBuffer descricao = new StringBuffer(300);
		
		if(evolucaoSelecionada != null){
			descricao.append("Data: ")
			.append(DateUtil.obterDataFormatada(evolucaoSelecionada.getDthrCriacao(), "dd/MM/yyyy HH:mm"))
			.append(" - ");
			popularDadosAtendimento(descricao);
			descricao.append(" Nome do Responsável: ")
			.append(evolucaoSelecionada.getServidor().getPessoaFisica().getNome())
			.append(System.getProperty("line.separator"))
			.append("Data Referência: ")
			.append(DateUtil.obterDataFormatada(evolucaoSelecionada.getDthrReferencia(), "dd/MM/yyyy"))
			.append(" Data Fim: ")
			.append(DateUtil.obterDataFormatada(evolucaoSelecionada.getDthrFim(), "dd/MM/yyyy HH:mm"))
			.append(System.getProperty("line.separator"))
			.append(System.getProperty("line.separator"))
			.append("Evolução: ")
			.append(evolucaoSelecionada.getDescricao());
		}
		
		this.descricaoEvolucao = descricao.toString();
	}

	private void popularDadosAtendimento(StringBuffer descricao) {
		descricao.append("Atendimento: ");
		
		Integer seqAtendimento = null;
		MpmAnamneses anamnese = this.prescricaoMedicaFacade.obterAnamneseValidadaPorAnamneses(this.seqAnamnese);
		if(anamnese != null) {
			AghAtendimentos atendimento = anamnese.getAtendimento();			
			seqAtendimento = atendimento != null ? anamnese.getAtendimento().getSeq() : null;
		}		
		descricao.append(seqAtendimento);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MpmEvolucoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.prescricaoMedicaFacade.pesquisarEvolucoesAnteriores(firstResult, maxResult, orderProperty, asc, seqAnamnese, dataInicial, dataFinal);
	}

	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade.pesquisarEvolucoesAnterioresCount(seqAnamnese, dataInicial, dataFinal);
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getDescricaoEvolucao() {
		return descricaoEvolucao;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}
 


	public DynamicDataModel<MpmEvolucoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmEvolucoes> dataModel) {
	 this.dataModel = dataModel;
	}

	public MpmEvolucoes getEvolucaoSelecionada() {
		return evolucaoSelecionada;
	}

	public void setEvolucaoSelecionada(MpmEvolucoes evolucaoSelecionada) {
		this.evolucaoSelecionada = evolucaoSelecionada;
	}
	
}
