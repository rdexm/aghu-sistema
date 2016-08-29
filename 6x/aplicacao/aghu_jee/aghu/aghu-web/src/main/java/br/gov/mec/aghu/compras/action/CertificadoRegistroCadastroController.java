package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class CertificadoRegistroCadastroController extends ActionController{

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 4238965678690693998L;

	@EJB
	private IComprasFacade comprasFacade;

	private Integer numeroFrn;
	private ScoFornecedor fornecedor;
	private static final String PAGE_CERTIFICADO_REGISTRO_CADASTRAL = "relatorioCertificadoRegistroCadastral";
	private String voltarPara;

	public void inicio() {
	 

	 

		if (this.getNumeroFrn() != null) {
			List<ScoFornecedor> listaFornecedor = this
					.pesquisarFornecedoresPorCgcCpfRazaoSocial(getNumeroFrn().toString());
			if (listaFornecedor != null && listaFornecedor.size() > 0) {
				this.fornecedor = listaFornecedor.get(0);
			}
			this.contarFornecedoresPorCgcCpfRazaoSocial(getNumeroFrn().toString());
		}
		this.validarCrc();
	
	}
	

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(
			String parametro) {
		return this.comprasFacade.listarFornecedoresAtivos(parametro, null,
				100, null, true);
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
		if (fornecedor != null && fornecedor.getCrc() == null) {
			this.apresentarMsgNegocio(Severity.WARN, "MSG_FORNECEDOR_SEM_CRC");
		}
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void validarCrc() {
		if (fornecedor != null && fornecedor.getCrc() == null) {
			this.apresentarMsgNegocio(Severity.WARN, "MSG_FORNECEDOR_SEM_CRC");
		}
	}

	public String visualizarCrc() {
			numeroFrn = fornecedor.getNumero();
			return PAGE_CERTIFICADO_REGISTRO_CADASTRAL;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

}
