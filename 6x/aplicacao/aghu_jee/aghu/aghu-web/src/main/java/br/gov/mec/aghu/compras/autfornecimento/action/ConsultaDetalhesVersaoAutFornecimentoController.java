package br.gov.mec.aghu.compras.autfornecimento.action;

import static br.gov.mec.aghu.compras.action.FornecedorStringUtils.formatCnpj;
import static br.gov.mec.aghu.compras.action.FornecedorStringUtils.formatCpf;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.suprimentos.vo.CalculoValorTotalAFVO;
import br.gov.mec.aghu.core.action.ActionController;

public class ConsultaDetalhesVersaoAutFornecimentoController extends ActionController {

	private static final long serialVersionUID = 2747153530121704400L;

	private static final String PESQUISAR_CONDICOES_PAGAMENTO_AF = "pesquisarCondicoesPagamentoAF";
	
	private static final String PESQUISAR_ITEM_AUT_FORNECIMENTO = "compras-pesquisarItemAutFornecimento";
	
	private static final String IMPRIMIR_AUTORIZACAO_FORNECIMENTO = "compras-imprimirAutorizacaoFornecimento";
	
	private static final String LISTAR_RESPONSAVEIS_ETAPAS_VERSAO_AF = "listarResponsaveisEtapasVersaoAF";
	

	
	// Parâmetros
	
	/** Número da AF */
	private Integer numeroAf;
	
	/** Complemento da AF */
	private Short complementoAf;
	
	/** Sequência Alteração */
	private Short sequenciaAlteracao;
	
	/** Tela de Origem */
	private String origem;
	
	// Dependências
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	// Dados
	
	/** Versão da AF */
	private ScoAutorizacaoFornJn versaoAf;

	/** Cálculo Valor Total */
	private CalculoValorTotalAFVO calculo;
	
	// Métodos

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		versaoAf = autFornecimentoFacade.obterScoAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
		calculo = autFornecimentoFacade.getValorTotalAf(versaoAf.getNumero(), sequenciaAlteracao.intValue());
	
	}
	
	
	/**
	 * Obtem número formatado do fornecedor.
	 */
	public String getNumeroFornecedor() {
		ScoFornecedor fornecedor = versaoAf.getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor();
		return fornecedor.getCgc() != null ? formatCnpj(fornecedor.getCgc()) : formatCpf(fornecedor.getCpf());
	}
	
	/**
	 * Obtem flag 'excluída'.
	 * 
	 * @return Flag
	 */
	public String getExcluida() {
		return DominioSimNao.getInstance(versaoAf.getIndExclusao()).getDescricao();
	}
	
	/**
	 * Obtem descrição do usuário gerador.
	 * 
	 * @return Descrição
	 */
	public String getGerador() {
		return getServidorDescricao(versaoAf.getServidor());
	}
	
	/**
	 * Obtem descrição do usuário gestor.
	 * 
	 * @return Descrição
	 */
	public String getGestor() {
		return getServidorDescricao(versaoAf.getServidorGestor());
	}
	
	/**
	 * Obtem descrição da forma de pagamento.
	 * 
	 * @return Descrição
	 */
	public String getCondPgto() {
		ScoCondicaoPagamentoPropos condPgto = versaoAf.getCondicaoPagamentoPropos();
		
		if (condPgto != null) {
			ScoFormaPagamento formaPgto = condPgto.getFormaPagamento();
			
			if (formaPgto != null) {
				return String.format("%d - %s", formaPgto.getCodigo(), formaPgto.getDescricao());
			}
		}
		
		return null;
	}
	
	/**
	 * Obtem descrição de servidor.
	 * 
	 * @param servidor Servidor
	 * @return Descrição
	 */
	private String getServidorDescricao(RapServidores servidor) {
		if (servidor != null) {
			return String.format("%d - %d - %s", servidor.getId()
					.getVinCodigo(), servidor.getId().getMatricula(), servidor
					.getPessoaFisica().getNome());
		} else {
			return null;
		}
	}
	
	public String pesquisarCondicoesPagamentoAF(){
		return PESQUISAR_CONDICOES_PAGAMENTO_AF;
	}
	
	public String imprimirAf(){
		return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;
	}
	
	public String redirecionarItemAutorizacaoFornecimento(){
		return PESQUISAR_ITEM_AUT_FORNECIMENTO;
	}
	
	public String redirecionarResponsaveisEtapasVersaoAF(){
		return LISTAR_RESPONSAVEIS_ETAPAS_VERSAO_AF;
	}
	
	
	
	
	public String voltar(){
		return origem;
	}

	// Getters/Setters

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public ScoAutorizacaoFornJn getVersaoAf() {
		return versaoAf;
	}

	public CalculoValorTotalAFVO getCalculo() {
		return calculo;
	}
}