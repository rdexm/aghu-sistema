package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.CadastroContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.ContasCorrentesFornecedorVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroContasCorrentesFornecedorController extends ActionController {

	private static final long serialVersionUID = -480247529602377287L;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private CadastroContasCorrentesFornecedorVO filtro;
	private List<ContasCorrentesFornecedorVO> data;
	private String agencia;
	private boolean pesquisaFeita;
	private ContasCorrentesFornecedorVO itemExclusao;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		filtro = new CadastroContasCorrentesFornecedorVO();
	
	}
	
	
	public void pesquisar(){
		setData(comprasFacade.buscarDadosContasCorrentesFornecedor(prepararPesquisaBasica()));
		setPesquisaFeita(Boolean.TRUE);
	}
	
	private CadastroContasCorrentesFornecedorVO prepararPesquisaBasica() {			
				CadastroContasCorrentesFornecedorVO filtroBasico = new CadastroContasCorrentesFornecedorVO();			
				filtroBasico.setFornecedor(filtro.getFornecedor());			
				return filtroBasico;			
	}
	
	public void limpar(){
		setFiltro(null);
		setAgencia(null);
		setData(null);
		setPesquisaFeita(Boolean.FALSE);
	}
	
	private void limparFiltrosSecundarios(){
		filtro.setAgenciaBanco(null);
		filtro.setNumeroConta(null);
		filtro.setPreferencial(false);
		setAgencia(null);
	}
	
	public void excluir(){
		try {
			comprasFacade.deletarContaCorrenteFornecedor(getItemExclusao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.apresentarMsgNegocio(Severity.WARN, "CC_FORNECEDOR_M5");
		this.pesquisar();
	}
	
	public void adicionar(){
		List<ContasCorrentesFornecedorVO> validationData = comprasFacade.buscarDadosContasCorrentesFornecedor(filtro);
		try {
			comprasFacade.verificaRegistrosExistentes(validationData);
			comprasFacade.verificaContaPreferencialParaFornecedor(filtro.getFornecedor().getNumero(), montarPreferencia(filtro.isPreferencial()), false);
			comprasFacade.inserirContaCorrenteFornecedor(comprasFacade.montarContaCorrenteFornecedor(filtro));
			this.apresentarMsgNegocio(Severity.WARN, "CC_FORNECEDOR_M6");
			limparFiltrosSecundarios();
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private DominioSimNao montarPreferencia(boolean obterSelectedPref) {
		if(obterSelectedPref){
			return DominioSimNao.S;
		}else{
			return DominioSimNao.N;
		}
	}
	
	public void carregarAgencia(){
		StringBuilder builder = new StringBuilder();
		builder.append(filtro.getAgenciaBanco().getId().getCodigo())
		.append(" - ")
		.append(filtro.getAgenciaBanco().getDescricao());
		agencia = builder.toString();
	}
	
	public void limparAgencia(){
		agencia = null;
	}
	
	public void atualizaPreferencial(ContasCorrentesFornecedorVO item){
		try {
			validaContaPreferencial(item);
			comprasFacade.atualizarContaCorrenteFornecedor(item);
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void validaContaPreferencial(ContasCorrentesFornecedorVO item) throws ApplicationBusinessException {
		if(DominioSimNao.S.equals(montarPreferencia(item.obterSelectedPref()))){
			comprasFacade.verificaContaPreferencialParaFornecedor(item.getFornecedorNumero(), item.getIndPreferencial(), true);		
		}
	}

	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.returnSGWithCount(comprasFacade.listarFornecedoresPorNumeroCnpjRazaoSocial(param),pesquisarFornecedoresCount(param));
	}
	
	public Long pesquisarFornecedoresCount(String param) {
		return comprasFacade.listarFornecedoresPorNumeroCnpjRazaoSocialCount(param);
	}
	
	public List<FcpAgenciaBanco> pesquisarBancos(String param) {
		return this.returnSGWithCount(estoqueFacade.pesquisarAgenciaBanco(param),pesquisarBancosCount(param));
	}
	
	public Long pesquisarBancosCount(String param) {
		return estoqueFacade.pesquisarAgenciaBancoCount(param);
	}

	public void setFiltro(CadastroContasCorrentesFornecedorVO filtro) {
		this.filtro = filtro;
	}

	public CadastroContasCorrentesFornecedorVO getFiltro() {
		return filtro;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setData(List<ContasCorrentesFornecedorVO> data) {
		this.data = data;
	}

	public List<ContasCorrentesFornecedorVO> getData() {
		return data;
	}

	public void setPesquisaFeita(boolean pesquisaFeita) {
		this.pesquisaFeita = pesquisaFeita;
	}

	public boolean isPesquisaFeita() {
		return pesquisaFeita;
	}

	public void setItemExclusao(ContasCorrentesFornecedorVO itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public ContasCorrentesFornecedorVO getItemExclusao() {
		return itemExclusao;
	}
}
