package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaLeitosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class PesquisaLeitosController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5261279863645189246L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Responsável pela pesquisa de FatConvenioSaude
	 */
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	/**
	 * Injeção da PesquisaInternacaoFacade
	 */
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	/**
	 * LOV Status
	 */
	private AinTiposMovimentoLeito tiposMovimentoLeito = null;

	/**
	 * LOV Acomodação
	 */
	private AinAcomodacoes acomodacoes = null;

	/**
	 * LOV Clínica
	 */
	private AghClinicas clinicas = null;

	/**
	 * LOV Convênio
	 */
	private FatConvenioSaude conveniosSaude = null;

	/**
	 * LOV Unidade
	 */
	private AghUnidadesFuncionais unidadesFuncionais = null;

	/**
	 * LOV Leito
	 */
	private AinLeitos leitos = null;

	/**
	 * Código do leito.
	 */
	private String leito;

	/**
	 * Lista de AinTiposMovimentoLeito
	 */
	private List<AinTiposMovimentoLeito> listaTiposMovimentoLeito = new ArrayList<AinTiposMovimentoLeito>();

	/**
	 * Lista de AinAcomodacoes
	 */
	private List<AinAcomodacoes> listaAcomodacoes = new ArrayList<AinAcomodacoes>();

	/**
	 * Lista de AghClinicas
	 */
	private List<AghClinicas> listaClinicas = new ArrayList<AghClinicas>();

	/**
	 * Lista de FatConvenioSaude
	 */
	private List<FatConvenioSaude> listaConveniosSaude = new ArrayList<FatConvenioSaude>();

	/**
	 * Lista de AghUnidadesFuncionais
	 */
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();

	/**
	 * Lista de AinLeitos
	 */
	private List<AinLeitos> listaLeitos = new ArrayList<AinLeitos>();

	/**
	 * Atendimento Urgência para visualização.
	 */
	private AinAtendimentosUrgencia ainAtendimentosUrgencia = new AinAtendimentosUrgencia();

	/**
	 * Código do Atend. de Urgência.
	 */
	private Integer seqAtendimentoUrgencia;

	/**
	 * Codigo AinTiposMovimentoLeito
	 */
	private Integer codigoStatus;

	/**
	 * Codigo AinAcomodacoes
	 */
	private Integer codigoAcomodacao;

	/**
	 * Codigo AghClinicas
	 */
	private Integer codigoClinica;

	/**
	 * Codigo FatConvenioSaude
	 */
	private Integer codigoConveniosSaude;

	/**
	 * Codigo AghUnidadesFuncionais
	 */
	private Integer codigoUnidadesFuncionais;

	/**
	 * Codigo AinLeitos
	 */
	private String codigoLeitos;

	/**
	 * Descricao AinTiposMovimentoLeito
	 */
	private String descricaoStatus;

	/**
	 * Descricao AinAcomodacoes
	 */
	private String descricaoAcomodacao;

	/**
	 * Descricao AghClinicas
	 */
	private String descricaoClinica;

	/**
	 * Descricao FatConvenioSaude
	 */
	private String descricaoConveniosSaude;

	/**
	 * Descricao AghUnidadesFuncionais
	 */
	private String descricaoUnidadesFuncionais;

	/**
	 * Grupo Convênio
	 */
	private DominioGrupoConvenioPesquisaLeitos grupoConvenio;

	/**
	 * Ala
	 */
	private AghAla ala;

	/**
	 * Andar
	 */
	private Integer andar;

	/**
	 * Exclusivo infecção
	 */
	private DominioSimNao infeccao;

	/**
	 * Movimento Leito
	 */
	private DominioMovimentoLeito mvtoLeito = null;

	/**
	 * Data block VPL
	 */
	private boolean vpl = false;

	/**
	 * Data block VPL1
	 */
	private boolean vpl1 = false;

	/**
	 * Data block STP
	 */
	private boolean stp = false;

	/**
	 * Botão Detalhar Internação
	 */
	private boolean exibirBotaoDetalharInternacao;

	private boolean exibirBotaoDetalharAtUrgencia;

	/**
	 * Botão Liberar
	 */
	private boolean exibirBotaoLiberar;

	/**
	 * Botão Bloquear
	 */
	private boolean exibirBotaoBloquear;

	/**
	 * Botão Reservar
	 */
	private boolean exibirBotaoReservar;

	/**
	 * Botão Pesquisar Reserva
	 */
	private boolean exibirBotaoPesquisarReserva;

	private boolean atualizarLista;
	
	/**
	 * Lista de Solicitações
	 */
	private List<PesquisaLeitosVO> listaSolicitacoes = new ArrayList<PesquisaLeitosVO>();
	
	@Inject @Paginator
	private DynamicDataModel<AinLeitos> dataModel;	
	
	@Inject @Paginator
	private DynamicDataModel<AinLeitos> dataModel2;	

	/**
	 * Controle de data block
	 */
	private int dataBlock;
	
	private final String PAGE_DETALHAR_LEITO = "detalharLeitos";
	private final String PAGE_DETALHAR_INTERNACAO = "pesquisaDetalheInternacao";
	private final String PAGE_LIBERAR_LEITO = "internacao-liberaLeito";
	private final String PAGE_BLOQUEAR_LEITO = "internacao-bloqueiaLeito";
	private final String PAGE_RESERVAR_LEITO = "internacao-reservaLeito";
	private final String PAGE_TRANSFERIR_LEITO = "pesquisarDisponibilidadeLeito";
	private final String PAGE_DAR_ALTA = "internacao-dadosDaAltaPaciente";
    
	private String leitoId;
	
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciaForm(){
	 

		if (this.isAtualizarLista()) {
			this.dataModel.reiniciarPaginator();
			this.dataModel2.reiniciarPaginator();
			this.setAtualizarLista(false);
		}		
	
	}
	
	public String detalharLeito(){
		return PAGE_DETALHAR_LEITO;
	}
	
	public String detalharInternacao(){
		return PAGE_DETALHAR_INTERNACAO;
	}
	
	public String liberarLeito(){
		return PAGE_LIBERAR_LEITO;
	}
	
	public String bloquearLeito(){
		return PAGE_BLOQUEAR_LEITO;
	}
	
	public String reservarLeito(){
		return PAGE_RESERVAR_LEITO;
	}
	
	public String transferirLeito(){
		return PAGE_TRANSFERIR_LEITO;
	}
	
	public String darAlta(){
		return PAGE_DAR_ALTA;
	}
	
	public AghAla[] getAlaItens() {
		List<AghAla> alas = aghuFacade.buscarTodasAlas();
		
		return alas.toArray(new AghAla[alas.size()]);
	}
	
	/**
	 * Pesquisa Status para lista de valores.
	 */
	public List<AinTiposMovimentoLeito> pesquisarStatus(String param){
		this.listaTiposMovimentoLeito = this.cadastrosBasicosInternacaoFacade.pesquisarTipoSituacaoLeitoPorDescricao(param);
		return this.listaTiposMovimentoLeito;
	}
	
	/**
	 * Pesquisa Acomodacoes para lista de valores.
	 * 
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacao(String param){
		this.listaAcomodacoes = this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(param);
		return this.listaAcomodacoes;
	}

	/**
	 * Pesquisa Clinicas para lista de valores.
	 * 
	 * @return
	 */
	public List<AghClinicas> pesquisarClinica(String param){
		this.listaClinicas = this.aghuFacade.pesquisarClinicas(param);
		return this.listaClinicas;
	}

	/**
	 * Pesquisa FatConvenioSaude para lista de valores.
	 * 
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaude(String param){
		this.listaConveniosSaude = this.faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricao(param);
		return this.listaConveniosSaude;
	}

	/**
	 * Pesquisa AghUnidadesFuncionais para lista de valores.
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String param){
		this.listaUnidadesFuncionais = this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalOrdenado(param);
		return this.listaUnidadesFuncionais;
	}

	/**
	 * Pesquisa AinLeitos para lista de valores.
	 */
	public List<AinLeitos> pesquisarLeitos(String param){
		this.listaLeitos = this.cadastrosBasicosInternacaoFacade.pesquisarLeitosOrdenado(param);
		return this.listaLeitos; 
	}

	/**
	 * Método responsável por recuperar dados que serão usados no modal de
	 * Atend. Urgência.
	 * 
	 * cidsilva 04/06/2010
	 */
	public void detalharAtendimentoUrgencia() {
		this.ainAtendimentosUrgencia = (pesquisaInternacaoFacade
				.obterDetalheAtendimentoUrgencia2(this.leitos.getLeitoID()));
	}

	/**
	 * Ação do botão pesquisar.
	 * 
	 * @return String
	 */
	public String pesquisar() {

		try {
			this.pesquisaInternacaoFacade.validaDados(tiposMovimentoLeito, acomodacoes, clinicas, conveniosSaude,
														unidadesFuncionais, leitos, grupoConvenio, ala,	andar, infeccao);
			
			
	
			if (this.tiposMovimentoLeito != null) {
				this.mvtoLeito = tiposMovimentoLeito.getGrupoMvtoLeito();
			}
	
			dataBlock = pesquisaInternacaoFacade.verificarDataBlock(conveniosSaude, grupoConvenio, mvtoLeito);
	
			switch (dataBlock) {
			case 0:
				// Data block VPL
				vpl1 = false;
				vpl = true;
				stp = false;
				break;
			case 1:
				// Data block VPL1
				vpl1 = true;
				vpl = false;
				stp = false;
				break;
			case 2:
				// Data block STP
				Long countSolicitacoes = this.pesquisaInternacaoFacade
						.pesquisarSolicitacoesTransferenciaPacientesCount();
				if (countSolicitacoes > 0) {
					vpl1 = false;
					vpl = false;
					stp = true;
					this.listaSolicitacoes = pesquisaInternacaoFacade
							.pesquisarSolicitacoesTransferenciaPacientes();
				} else {
					// Data block VPL
					vpl1 = false;
					vpl = true;
					stp = false;
					this.dataBlock = 0;
				}
				break;
			}
	
			this.dataModel.reiniciarPaginator();
			this.dataModel2.reiniciarPaginator();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			dataModel.limparPesquisa();
			dataModel2.limparPesquisa();
			return null;
		}
		if (stp)  {
			super.openDialog("modalSTPWG");
		}
		return null;
	}

	/**
	 * Ação do botão continuar
	 * 
	 * @return
	 */
	public String continuar() {

		this.dataBlock = 0;
		this.vpl1 = false;
		this.vpl = true;
		this.stp = false;
		this.dataModel.reiniciarPaginator();
		this.dataModel2.reiniciarPaginator();

		return null;
	}

	/**
	 * Ação do botão limpar pesquisa
	 */
	public void limparPesquisa() {
		this.codigoAcomodacao = null;
		this.codigoClinica = null;
		this.codigoConveniosSaude = null;
		this.codigoLeitos = null;
		this.codigoStatus = null;
		this.codigoUnidadesFuncionais = null;
		this.andar = null;
		this.grupoConvenio = null;
		this.ala = null;
		this.infeccao = null;
		this.tiposMovimentoLeito = null;
		this.acomodacoes = null;
		this.clinicas = null;
		this.conveniosSaude = null;
		this.unidadesFuncionais = null;
		this.leitos = null;
		this.dataModel.limparPesquisa();
		this.dataModel2.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		Long count = this.pesquisaInternacaoFacade.pesquisarLeitosCount(tiposMovimentoLeito, acomodacoes, clinicas, conveniosSaude, unidadesFuncionais, leitos, grupoConvenio, ala, andar, infeccao, mvtoLeito, dataBlock); 
		return count;
	}

	@Override
	public List<PesquisaLeitosVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<PesquisaLeitosVO> result = pesquisaInternacaoFacade.pesquisarLeitos(tiposMovimentoLeito, acomodacoes, clinicas,
				conveniosSaude, unidadesFuncionais, leitos,	grupoConvenio, ala, andar, infeccao, this.mvtoLeito, dataBlock,
				firstResult, maxResult, orderProperty, asc);

		if (result == null) {
			result = new ArrayList<PesquisaLeitosVO>();
		}

		return result;
	}


	// GETs AND SETs

	public AinTiposMovimentoLeito getTiposMovimentoLeito() {
		return tiposMovimentoLeito;
	}

	public void setTiposMovimentoLeito(
			AinTiposMovimentoLeito tiposMovimentoLeito) {
		this.tiposMovimentoLeito = tiposMovimentoLeito;
	}

	public Integer getCodigoStatus() {
		return codigoStatus;
	}

	public void setCodigoStatus(Integer codigoStatus) {
		this.codigoStatus = codigoStatus;
	}

	public String getDescricaoStatus() {
		return descricaoStatus;
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
	}

	public List<AinTiposMovimentoLeito> getListaTiposMovimentoLeito() {
		return listaTiposMovimentoLeito;
	}

	public void setListaTiposMovimentoLeito(
			List<AinTiposMovimentoLeito> listaTiposMovimentoLeito) {
		this.listaTiposMovimentoLeito = listaTiposMovimentoLeito;
	}

	public AinAcomodacoes getAcomodacoes() {
		return acomodacoes;
	}

	public void setAcomodacoes(AinAcomodacoes acomodacoes) {
		this.acomodacoes = acomodacoes;
	}

	public List<AinAcomodacoes> getListaAcomodacoes() {
		return listaAcomodacoes;
	}

	public void setListaAcomodacoes(List<AinAcomodacoes> listaAcomodacoes) {
		this.listaAcomodacoes = listaAcomodacoes;
	}

	public Integer getCodigoAcomodacao() {
		return codigoAcomodacao;
	}

	public void setCodigoAcomodacao(Integer codigoAcomodacao) {
		this.codigoAcomodacao = codigoAcomodacao;
	}

	public String getDescricaoAcomodacao() {
		return descricaoAcomodacao;
	}

	public void setDescricaoAcomodacao(String descricaoAcomodacao) {
		this.descricaoAcomodacao = descricaoAcomodacao;
	}

	public AghClinicas getClinicas() {
		return clinicas;
	}

	public void setClinicas(AghClinicas clinicas) {
		this.clinicas = clinicas;
	}

	public List<AghClinicas> getListaClinicas() {
		return listaClinicas;
	}

	public void setListaClinicas(List<AghClinicas> listaClinicas) {
		this.listaClinicas = listaClinicas;
	}

	public Integer getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public String getDescricaoClinica() {
		return descricaoClinica;
	}

	public void setDescricaoClinica(String descricaoClinica) {
		this.descricaoClinica = descricaoClinica;
	}

	public FatConvenioSaude getConveniosSaude() {
		return conveniosSaude;
	}

	public void setConveniosSaude(FatConvenioSaude conveniosSaude) {
		this.conveniosSaude = conveniosSaude;
	}

	public List<FatConvenioSaude> getListaConveniosSaude() {
		return listaConveniosSaude;
	}

	public void setListaConveniosSaude(
			List<FatConvenioSaude> listaConveniosSaude) {
		this.listaConveniosSaude = listaConveniosSaude;
	}

	public Integer getCodigoConveniosSaude() {
		return codigoConveniosSaude;
	}

	public void setCodigoConveniosSaude(Integer codigoConveniosSaude) {
		this.codigoConveniosSaude = codigoConveniosSaude;
	}

	public String getDescricaoConveniosSaude() {
		return descricaoConveniosSaude;
	}

	public void setDescricaoConveniosSaude(String descricaoConveniosSaude) {
		this.descricaoConveniosSaude = descricaoConveniosSaude;
	}

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(
			List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public Integer getCodigoUnidadesFuncionais() {
		return codigoUnidadesFuncionais;
	}

	public void setCodigoUnidadesFuncionais(Integer codigoUnidadesFuncionais) {
		this.codigoUnidadesFuncionais = codigoUnidadesFuncionais;
	}

	public String getDescricaoUnidadesFuncionais() {
		return descricaoUnidadesFuncionais;
	}

	public void setDescricaoUnidadesFuncionais(
			String descricaoUnidadesFuncionais) {
		this.descricaoUnidadesFuncionais = descricaoUnidadesFuncionais;
	}

	public DominioGrupoConvenioPesquisaLeitos getGrupoConvenio() {
		return grupoConvenio;
	}

	public void setGrupoConvenio(
			DominioGrupoConvenioPesquisaLeitos grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}

	public Integer getAndar() {
		return andar;
	}

	public void setAndar(Integer andar) {
		this.andar = andar;
	}

	public AghAla getAla() {
		return ala;
	}

	public void setAla(AghAla a) {
		this.ala = a;
	}

	public DominioSimNao getInfeccao() {
		return infeccao;
	}

	public void setInfeccao(DominioSimNao infeccao) {
		this.infeccao = infeccao;
	}

	public AinLeitos getLeitos() {
		return leitos;
	}

	public void setLeitos(AinLeitos leitos) {
		this.leitos = leitos;
	}

	public List<AinLeitos> getListaLeitos() {
		return listaLeitos;
	}

	public void setListaLeitos(List<AinLeitos> listaLeitos) {
		this.listaLeitos = listaLeitos;
	}

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}

	public DominioMovimentoLeito getMvtoLeito() {
		return mvtoLeito;
	}

	public void setMvtoLeito(DominioMovimentoLeito mvtoLeito) {
		this.mvtoLeito = mvtoLeito;
	}

	
	public boolean isVpl() {
		return vpl;
	}

	public void setVpl(boolean vpl) {
		this.vpl = vpl;
	}

	public boolean isVpl1() {
		return vpl1;
	}

	public void setVpl1(boolean vpl1) {
		this.vpl1 = vpl1;
	}

	public boolean isStp() {
		return stp;
	}

	public void setStp(boolean stp) {
		this.stp = stp;
	}

	public int getDataBlock() {
		return dataBlock;
	}

	public void setDataBlock(int dataBlock) {
		this.dataBlock = dataBlock;
	}

	public List<PesquisaLeitosVO> getListaSolicitacoes() {
		return listaSolicitacoes;
	}

	public void setListaSolicitacoes(List<PesquisaLeitosVO> listaSolicitacoes) {
		this.listaSolicitacoes = listaSolicitacoes;
	}

	public boolean isExibirBotaoDetalharInternacao() {
		return exibirBotaoDetalharInternacao;
	}

	public void setExibirBotaoDetalharInternacao(
			boolean exibirBotaoDetalharInternacao) {
		this.exibirBotaoDetalharInternacao = exibirBotaoDetalharInternacao;
	}

	public boolean isExibirBotaoLiberar() {
		return exibirBotaoLiberar;
	}

	public void setExibirBotaoLiberar(boolean exibirBotaoLiberar) {
		this.exibirBotaoLiberar = exibirBotaoLiberar;
	}

	public boolean isExibirBotaoBloquear() {
		return exibirBotaoBloquear;
	}

	public void setExibirBotaoBloquear(boolean exibirBotaoBloquear) {
		this.exibirBotaoBloquear = exibirBotaoBloquear;
	}

	public boolean isExibirBotaoReservar() {
		return exibirBotaoReservar;
	}

	public void setExibirBotaoReservar(boolean exibirBotaoReservar) {
		this.exibirBotaoReservar = exibirBotaoReservar;
	}

	public boolean isExibirBotaoPesquisarReserva() {
		return exibirBotaoPesquisarReserva;
	}

	public void setExibirBotaoPesquisarReserva(
			boolean exibirBotaoPesquisarReserva) {
		this.exibirBotaoPesquisarReserva = exibirBotaoPesquisarReserva;
	}

	public boolean isExibirBotaoDetalharAtUrgencia() {
		return exibirBotaoDetalharAtUrgencia;
	}

	public void setExibirBotaoDetalharAtUrgencia(
			boolean exibirBotaoDetalharAtUrgencia) {
		this.exibirBotaoDetalharAtUrgencia = exibirBotaoDetalharAtUrgencia;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public AinAtendimentosUrgencia getAinAtendimentosUrgencia() {
		return ainAtendimentosUrgencia;
	}

	public void setAinAtendimentosUrgencia(
			AinAtendimentosUrgencia ainAtendimentosUrgencia) {
		this.ainAtendimentosUrgencia = ainAtendimentosUrgencia;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public void setAtualizarLista(boolean atualizarLista) {
		this.atualizarLista = atualizarLista;
	}

	public boolean isAtualizarLista() {
		return atualizarLista;
	}

	public DynamicDataModel<AinLeitos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinLeitos> dataModel) {
		this.dataModel = dataModel;
	}

	public DynamicDataModel<AinLeitos> getDataModel2() {
		return dataModel2;
	}

	public void setDataModel2(DynamicDataModel<AinLeitos> dataModel2) {
		this.dataModel2 = dataModel2;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
	
	
	
}