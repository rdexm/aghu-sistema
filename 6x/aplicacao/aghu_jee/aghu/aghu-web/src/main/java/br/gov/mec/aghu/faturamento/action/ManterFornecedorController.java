package br.gov.mec.aghu.faturamento.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterFornecedorController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 11318573035074301L;

	@EJB
	protected IComprasFacade comprasFacade;

	private Integer cthSeq;

	private Short seq;

	private ScoFornecedor scoFornecedor;

	private Integer nrFornecedor;

	private String propriedade;

	private String origem;

	private boolean indSituacao;

	private boolean indNacional;

	public void inicio() {
	 

		if (getOrigem() == null || getPropriedade() == null) {
			// mensagem de parâmetro inválidos
			this.apresentarMsgNegocio(Severity.FATAL, "MSG_PARAMETROS_INVALIDOS_MANTER_NOTA_FISCAL");
		}
		iniciarFornecedor();
	
	}

	private void iniciarFornecedor() {
		setScoFornecedor(new ScoFornecedor());
		getScoFornecedor().setDtCadastramento(new Date());
		getScoFornecedor().setDtAlteracao(new Date());
		setIndSituacao(false);
		setIndNacional(false);
	}

	public String gravar() {
		try {
			getScoFornecedor().setSituacao(isIndSituacao() ? DominioSituacao.A : DominioSituacao.I);
			getScoFornecedor().setTipoFornecedor(isIndNacional() ? DominioTipoFornecedor.FNA : DominioTipoFornecedor.FNE);
			setScoFornecedor(comprasFacade.inserirScoFornecedor(getScoFornecedor(), true));
			return getOrigem();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String limpar() {
		iniciarFornecedor();
		return "";
	}

	public String cancelar() {
		return getOrigem();
	}

	public void setCthSeq(final Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setSeq(final Short seq) {
		this.seq = seq;
	}

	public Short getSeq() {
		return seq;
	}

	public void setNrFornecedor(final Integer nrFornecedor) {
		this.nrFornecedor = nrFornecedor;
	}

	public Integer getNrFornecedor() {
		return nrFornecedor;
	}

	public void setPropriedade(final String propriedade) {
		this.propriedade = propriedade;
	}

	public String getPropriedade() {
		return propriedade;
	}

	public void setOrigem(final String origem) {
		this.origem = origem;
	}

	public String getOrigem() {
		return origem;
	}

	public void setIndSituacao(final boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndNacional(final boolean indNacional) {
		this.indNacional = indNacional;
	}

	public boolean isIndNacional() {
		return indNacional;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(final ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}
}
