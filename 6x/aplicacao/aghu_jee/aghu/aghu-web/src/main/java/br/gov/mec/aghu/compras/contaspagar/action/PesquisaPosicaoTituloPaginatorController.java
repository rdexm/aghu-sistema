package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroPesquisaPosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.FcpPagamento;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * #37797 – Consultar posição do Título
 * 
 * @author aghu
 *
 */
public class PesquisaPosicaoTituloPaginatorController extends ActionController implements ActionPaginator {

	private static final String _SEPARADOR_ = " | ";

	private static final long serialVersionUID = 3800683905605563982L;

	// Quebra de linha para tool tips
	private static final String BREAK_LINE = "<br/>";

	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}

	@EJB
	private IContasPagarFacade contasPagarFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// Lista com os resultados da pesquisa
	@Inject @Paginator
	private DynamicDataModel<PosicaoTituloVO> dataModel;

	// Filtro
	private FiltroPesquisaPosicaoTituloVO filtro = new FiltroPesquisaPosicaoTituloVO();

	// Filtro principal da pesquisa
	private FcpTitulo filtroTitulo = new FcpTitulo();
	private FcpPagamento filtroPagamento = new FcpPagamento();

	// Controla slider
	private boolean sliderAberto = true;

	/*
	 * Pesquisa principal
	 */

	@Override
	public List<PosicaoTituloVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.contasPagarFacade.pesquisarPosicaoTitulo(firstResult, maxResult, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.contasPagarFacade.pesquisarPosicaoTituloCount(this.filtro);
	}

	public void pesquisar() {
		try {

			if (this.filtro.getComplemento() != null && this.filtro.getNumeroAF() == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PESQUISAR_COMPLEMENTO_SEM_AF");
				this.dataModel.limparPesquisa();
			} else{
				// Chamada da RN02
				this.contasPagarFacade.validarIntervaloDataGeracaoPesquisaTitulo(this.filtro.getDataInicio(), this.filtro.getDataFim());
				this.dataModel.reiniciarPaginator();
				this.sliderAberto = false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.dataModel.limparPesquisa();
			this.sliderAberto = true;
		}
	}

	public void limpar() {
		this.filtroTitulo = new FcpTitulo();
		this.filtroPagamento = new FcpPagamento();
		this.dataModel.limparPesquisa();
		this.filtro = new FiltroPesquisaPosicaoTituloVO();
		this.sliderAberto = true;
	}

	/*
	 * Descrição do filtro
	 */

	public String getDescricaoFiltro() {
		StringBuilder sb = new StringBuilder("Filtros");
		if (this.dataModel.getPesquisaAtiva()) {
			popularDescricaoFiltroParte1(sb); // Evita violação NPath complexity
			popularDescricaoFiltroParte2(sb); // Evita violação NPath complexity
		}
		return sb.toString();
	}

	private void popularDescricaoFiltroParte1(StringBuilder sb) {
		if (this.filtro.getNumero() != null) {
			sb.append(_SEPARADOR_).append("Número: ").append(this.filtro.getNumero());
		}

		if (this.filtro.getParcela() != null) {
			sb.append(_SEPARADOR_).append("Parcela: ").append(this.filtro.getParcela());
		}

		if (this.filtro.getSituacao() != null) {
			sb.append(_SEPARADOR_).append("Situação: ").append(this.filtro.getSituacao().getDescricao());
		}

		if (this.filtro.getNotaRecebimento() != null) {
			sb.append(_SEPARADOR_).append("NR: ").append(this.filtro.getNotaRecebimento());
		}

		if (this.filtro.getNumeroAF() != null) {
			sb.append(_SEPARADOR_).append("Número AF: ").append(this.filtro.getNumeroAF());
		}

		if (this.filtro.getComplemento() != null) {
			sb.append(_SEPARADOR_).append("Complemento: ").append(this.filtro.getComplemento());
		}

		if (this.filtro.getBo() != null) {
			sb.append(_SEPARADOR_).append("BO: ").append(this.filtro.getBo());
		}

		if (this.filtro.getNumeroContrato() != null) {
			sb.append(_SEPARADOR_).append("Contrato: ").append(this.filtro.getNumeroContrato());
		}
	}

	private void popularDescricaoFiltroParte2(StringBuilder sb) {

		String labelPeriodoGeracao = "Período de Geração: ";

		if (filtro.getDataInicio() != null && filtro.getDataFim() != null) {
			sb.append(_SEPARADOR_).append(labelPeriodoGeracao).append(DateUtil.obterDataFormatada(this.filtro.getDataInicio(), DateConstants.DATE_PATTERN_DDMMYYYY)).append(" até ").append(DateUtil.obterDataFormatada(this.filtro.getDataFim(), DateConstants.DATE_PATTERN_DDMMYYYY));
		} else if (filtro.getDataInicio() != null && filtro.getDataFim() == null) {
			sb.append(_SEPARADOR_).append(labelPeriodoGeracao).append("Data início ").append(DateUtil.obterDataFormatada(this.filtro.getDataInicio(), DateConstants.DATE_PATTERN_DDMMYYYY));
		} else if (filtro.getDataFim() != null && filtro.getDataInicio() == null) {
			sb.append(_SEPARADOR_).append(labelPeriodoGeracao).append("Data fim ").append(DateUtil.obterDataFormatada(this.filtro.getDataFim(), DateConstants.DATE_PATTERN_DDMMYYYY));
		}

		if (this.filtro.getGeradoPor() != null) {
			sb.append(_SEPARADOR_).append("Gerado por: ").append(this.filtro.getGeradoPor().getPessoaFisica().getNome());
		}

		if (this.filtro.getFornecedor() != null) {
			sb.append(_SEPARADOR_).append("Fornecedor: ").append(this.filtro.getFornecedor().getRazaoSocial());
		}

		if (this.filtro.getEstornadoPor() != null) {
			sb.append(_SEPARADOR_).append("Estornado por: ").append(this.filtro.getEstornadoPor().getPessoaFisica().getNome());
		}

		if (this.filtro.getDocumento() != null) {
			sb.append(_SEPARADOR_).append("Número Pagamento: ").append(this.filtro.getDocumento());
		}

		if (this.filtro.getPagoPor() != null) {
			sb.append(_SEPARADOR_).append("Pago por: ").append(this.filtro.getPagoPor().getPessoaFisica().getNome());
		}

		if (StringUtils.isNotBlank(this.filtro.getObservacao())) {
			sb.append(_SEPARADOR_).append("Observação: ").append(this.filtro.getObservacao());
		}
	}

	/*
	 * Métodos para SuggestionBox
	 */

	public List<RapServidores> pesquisarProfissionais(String parametro) {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidorPorMatriculaNome(parametro),pesquisarProfissionaisCount(parametro));
	}

	public Long pesquisarProfissionaisCount(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(parametro);
	}

	public List<ScoFornecedor> pesquisarFornecedores(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, 0, 100, null, false),pesquisarFornecedoresCount(parametro));
	}

	public Long pesquisarFornecedoresCount(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
	}

	/*
	 * Hints
	 */

	public String getHintNumero(PosicaoTituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append("Número AF: ").append(transformarNuloVazio(item.getNumeroAF()))
		.append(" / ").append(transformarNuloVazio(item.getComplemento())).append(BREAK_LINE)
		.append("BO: ").append(transformarNuloVazio(item.getBo())).append(BREAK_LINE)
		.append("Número Contrato: ").append(transformarNuloVazio(item.getNumeroContrato())).append(BREAK_LINE);
		return buffer.toString();
	}

	public String getHintEstorno(PosicaoTituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append("Motivo: ").append(transformarNuloVazio(item.getMotivoEstorno())).append(BREAK_LINE)
		.append("Data: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getDataEstorno(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Estornado por: ").append(transformarNuloVazio(item.getResponsavelEstornado())).append(BREAK_LINE);
		return buffer.toString();
	}

	public String getHintPagamento(PosicaoTituloVO item) {
		StringBuilder buffer = new StringBuilder(150);
		buffer.append("Tipo de Documento: ").append(transformarNuloVazio(item.getTipoDocPagamentoDescricao())).append(BREAK_LINE)
		.append("Número: ").append(transformarNuloVazio(item.getDocumento())).append(BREAK_LINE)
		.append("Banco: ").append(transformarNuloVazio(item.getBancoNome())).append(BREAK_LINE)
		.append("Agência: ").append(transformarNuloVazio(item.getAgencia())).append(BREAK_LINE)
		.append("Conta: ").append(transformarNuloVazio(item.getConta())).append(BREAK_LINE)
		.append("Pago em: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getPagoEm(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Pago por: ").append(transformarNuloVazio(item.getResponsavelPagamento())).append(BREAK_LINE)
		.append("Valor: ").append(item.getValorPagamento() != null ? AghuNumberFormat.formatarNumeroMoeda(item.getValorPagamento()) : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Acréscimo: ").append(item.getAcrescimo() != null ? AghuNumberFormat.formatarNumeroMoeda(item.getAcrescimo()) : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Desconto: ").append(item.getDesconto() != null ? AghuNumberFormat.formatarNumeroMoeda(item.getDesconto()) : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Observação: ").append(transformarNuloVazio(item.getObservacao())).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Transforma nulo em vazio
	 * 
	 * @param o
	 * @return
	 */
	private String transformarNuloVazio(Object o) {
		return o == null ? StringUtils.EMPTY : o.toString().trim();
	}

	/*
	 * Métodos para preencher a cor de fundo
	 */
	public String colorirCampoSituacao(DominioSituacaoTitulo situacao) {
		// Chamada da RN01
		return this.contasPagarFacade.colorirCampoSituacao(situacao);
	}

	/*
	 * Getters/Setters
	 */

	public FiltroPesquisaPosicaoTituloVO getFiltro() {
		return filtro;
	}

	public DynamicDataModel<PosicaoTituloVO> getDataModel() {
		return dataModel;
	}

	public FcpTitulo getFiltroTitulo() {
		return filtroTitulo;
	}

	public void setFiltroTitulo(FcpTitulo filtroTitulo) {
		this.filtroTitulo = filtroTitulo;
	}

	public FcpPagamento getFiltroPagamento() {
		return filtroPagamento;
	}

	public void setFiltroPagamento(FcpPagamento filtroPagamento) {
		this.filtroPagamento = filtroPagamento;
	}

	public boolean isSliderAberto() {
		return sliderAberto;
	}

	public void setSliderAberto(boolean sliderAberto) {
		this.sliderAberto = sliderAberto;
	}

}