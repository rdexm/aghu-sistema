package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.compras.action.FornecedorStringUtils;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PesquisaVersoesAutFornecimentoController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4082536560802524561L;

	private static final String CONSULTAR_DETALHES_VERSAO_AUT_FORNECIMENTO = "compras-consultarDetalhesVersaoAutFornecimento";
	private static final String PESQUISAR_ITENS_DE_VERSOES_AUT_FORNECIMENTO = "compras-pesquisarItensDeVersoesAutFornecimento";
	private static final String LISTAR_RESPONSAVEIS_ETAPAS_VERSAO_AF = "compras-listarResponsaveisEtapasVersaoAF";
	private static final String IMPRIMIR_AUTORIZACAO_FORNECIMENTO = "compras-imprimirAutorizacaoFornecimento";
	private static final String PESQUISAR_VERSOES_AUT_FORNECIMENTO = "compras-pesquisarVersoesAutFornecimento";

	
	// Parâmetros
	
	/** Número da AF */
	private Integer numeroAf;
	
	/** Complemento da AF */
	private Short complementoAf;
	
	/** Tela de Origem */
	private String origem;
	
	// Dependências
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	/** AF */
	private ScoAutorizacaoForn af;

	@Inject @Paginator
	private DynamicDataModel<ScoAutorizacaoFornJn> dataModel;
	
	@Inject
	private RelatorioAutorizacaoFornecimentoController relatorioAutorizacaoFornecimentoController;

	// Métodos
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
		af = autFornecimentoFacade.buscarAutFornPorNumPac(numeroAf, complementoAf);
		dataModel.reiniciarPaginator();
	
	}
	
	
	/**
	 * Obtem fornecedor da AF.
	 * 
	 * @return Fornecedor
	 */
	public String obterFornecedor() {
		return FornecedorStringUtils.format(af.getPropostaFornecedor().getFornecedor());
	}

	@Override
	public Long recuperarCount() {
		return autFornecimentoFacade.contarAutFornJNPorNumPacNumCompl(numeroAf, complementoAf);
	}

	@Override
	public List<ScoAutorizacaoFornJn> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return autFornecimentoFacade.buscarAutFornJNPorNumPacNumCompl(numeroAf, complementoAf, first, max);
	}
	
	/**
	 * Obtem servidor de versão da AF.
	 */
	public String obterServidor(ScoAutorizacaoFornJn jn) {
		if (jn != null){
			RapServidores ser = jn.getServidorControlado();
	
			if (ser != null) {
				RapPessoasFisicas pes = ser.getPessoaFisica();
				if (pes != null) {
				   return String.format("%d - %d - %s", ser.getId().getVinCodigo(), ser.getId().getMatricula(), pes.getNome());
				}
				return null;
			} else {
				return null;
			}
		}
		return null;
	}
	
	public String consultarDetalhesVersaoAutFornecimento(){
		return CONSULTAR_DETALHES_VERSAO_AUT_FORNECIMENTO;
	}

	public String pesquisarItensDeVersoesAutFornecimento(){
		return PESQUISAR_ITENS_DE_VERSOES_AUT_FORNECIMENTO;
	}
	
	public String listarResponsaveisEtapasVersaoAF(){
		return LISTAR_RESPONSAVEIS_ETAPAS_VERSAO_AF;
	}
	
	public String imprimirAutorizacaoFornecimento(Short seqAlteracao) throws BaseException, JRException, SystemException, IOException, DocumentException{
		relatorioAutorizacaoFornecimentoController.setNumPac(this.numeroAf);
		relatorioAutorizacaoFornecimentoController.setNroComplemento(this.complementoAf);
		relatorioAutorizacaoFornecimentoController.setSequenciaAlteracao(seqAlteracao);
		relatorioAutorizacaoFornecimentoController.setVoltarParaUrl(PESQUISAR_VERSOES_AUT_FORNECIMENTO);
		relatorioAutorizacaoFornecimentoController.iniciar();
		return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;
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

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public DynamicDataModel<ScoAutorizacaoFornJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAutorizacaoFornJn> dataModel) {
		this.dataModel = dataModel;
	}
}