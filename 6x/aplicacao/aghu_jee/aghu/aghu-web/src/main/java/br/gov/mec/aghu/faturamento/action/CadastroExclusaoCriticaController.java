package br.gov.mec.aghu.faturamento.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroExclusaoCriticaController extends ActionController {

	private static final long serialVersionUID = -1414751715542294484L;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private FatExclusaoCritica fatExclusaoCriticaCadastro;
	
	private static final String PAGE_PESQUISA_EXCLUSAO_CRITICA = "cadastroExclusaoCriticaList";
	
	private Boolean indSituacao = Boolean.FALSE;
	private Boolean cns = Boolean.FALSE;
	private Boolean telefone = Boolean.FALSE;
	private Boolean cbo = Boolean.FALSE;
	private Boolean qtd = Boolean.FALSE;
	private Boolean idMaior = Boolean.FALSE;
	private Boolean idMenor = Boolean.FALSE;
	private Boolean permMenor = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;
		
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		if (fatExclusaoCriticaCadastro != null && this.editando) {
			this.indSituacao = fatExclusaoCriticaCadastro.getIndSituacao()
					.isAtivo();
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.cns = fatExclusaoCriticaCadastro.getCns().isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.telefone = fatExclusaoCriticaCadastro.getTelefone()
						.isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {

				this.cbo = fatExclusaoCriticaCadastro.getCbo().isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.qtd = fatExclusaoCriticaCadastro.getQtd().isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.idMaior = fatExclusaoCriticaCadastro.getIdMaior()
						.isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.idMenor = fatExclusaoCriticaCadastro.getIdMenor()
						.isAtivo();
			}
			if (fatExclusaoCriticaCadastro.getCns() != null) {
				this.permMenor = fatExclusaoCriticaCadastro.getPermMenor()
						.isAtivo();
			}

			this.editando = Boolean.TRUE;
		} else {
			fatExclusaoCriticaCadastro = new FatExclusaoCritica();
			this.editando = Boolean.FALSE;
		}
	}
	
	public void limparInclusao(){
		fatExclusaoCriticaCadastro = new FatExclusaoCritica();
		this.indSituacao = Boolean.FALSE; 
		this.cns = Boolean.FALSE;
		this.telefone = Boolean.FALSE;
		this.cbo = Boolean.FALSE;
		this.qtd = Boolean.FALSE;
		this.idMaior = Boolean.FALSE;
		this.idMenor = Boolean.FALSE;
		this.permMenor = Boolean.FALSE;
		this.editando = Boolean.FALSE;
	}
	 
	public String persistir() {
		this.fatExclusaoCriticaCadastro.setIndSituacao(DominioSituacao.getInstance(this.indSituacao));
		this.fatExclusaoCriticaCadastro.setCns(DominioSituacao.getInstance(this.cns));
		this.fatExclusaoCriticaCadastro.setCbo(DominioSituacao.getInstance(this.cbo));
		this.fatExclusaoCriticaCadastro.setIdMaior(DominioSituacao.getInstance(this.idMaior));
		this.fatExclusaoCriticaCadastro.setIdMenor(DominioSituacao.getInstance(this.idMenor));
		this.fatExclusaoCriticaCadastro.setPermMenor(DominioSituacao.getInstance(this.permMenor));
		this.fatExclusaoCriticaCadastro.setQtd(DominioSituacao.getInstance(this.qtd));
		this.fatExclusaoCriticaCadastro.setTelefone(DominioSituacao.getInstance(this.telefone));
		
		try {
			if(!this.editando){
				this.faturamentoApoioFacade.persistirExclusaoCritica(fatExclusaoCriticaCadastro);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_EXCLUSAO_CRITICA", fatExclusaoCriticaCadastro.getCodigo());
				limparInclusao();
			} else {
				this.faturamentoApoioFacade.alterarExclusaoCritica(fatExclusaoCriticaCadastro);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_EXCLUSAO_CRITICA", fatExclusaoCriticaCadastro.getCodigo());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar() {
		limparInclusao();
		this.editando = Boolean.FALSE;
		return PAGE_PESQUISA_EXCLUSAO_CRITICA;
	}

	public Boolean getEditando() {
		return editando;
	}
	public void setEditando(Boolean editando) {
		this.editando = editando;
	}
	public FatExclusaoCritica getFatExclusaoCriticaCadastro() {
		return fatExclusaoCriticaCadastro;
	}
	public void setFatExclusaoCriticaCadastro(
			FatExclusaoCritica fatExclusaoCriticaCadastro) {
		this.fatExclusaoCriticaCadastro = fatExclusaoCriticaCadastro;
	}
	public Boolean getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Boolean getCns() {
		return cns;
	}
	public void setCns(Boolean cns) {
		this.cns = cns;
	}
	public Boolean getTelefone() {
		return telefone;
	}
	public void setTelefone(Boolean telefone) {
		this.telefone = telefone;
	}
	public Boolean getCbo() {
		return cbo;
	}
	public void setCbo(Boolean cbo) {
		this.cbo = cbo;
	}
	public Boolean getQtd() {
		return qtd;
	}
	public void setQtd(Boolean quantidade) {
		this.qtd = quantidade;
	}
	public Boolean getIdMaior() {
		return idMaior;
	}
	public void setIdMaior(Boolean idMaior) {
		this.idMaior = idMaior;
	}
	public Boolean getIdMenor() {
		return idMenor;
	}
	public void setIdMenor(Boolean idMenor) {
		this.idMenor = idMenor;
	}
	public Boolean getPermMenor() {
		return permMenor;
	}
	public void setPermMenor(Boolean permMenor) {
		this.permMenor = permMenor;
	}
}