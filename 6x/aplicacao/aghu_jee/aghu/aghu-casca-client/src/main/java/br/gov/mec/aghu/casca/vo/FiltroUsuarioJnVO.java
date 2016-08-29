package br.gov.mec.aghu.casca.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

/**
 * VO utilizado como filtro em pesquisa.
 * 
 * @author lcmoura
 * 
 */
public class FiltroUsuarioJnVO implements Serializable {
	private static final long serialVersionUID = 2163200167139362395L;

	private Integer idUsuario;
	private String login;
	private DominioOperacoesJournal operacao;
	private Date dataInicio;
	private Date dataFim;
	private String alteradoPor;

	public FiltroUsuarioJnVO(Integer idUsuario, String login,
			DominioOperacoesJournal operacao, Date dataInicio, Date dataFim,
			String alteradoPor) {
		super();
		this.idUsuario = idUsuario;
		this.login = login;
		this.operacao = operacao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.alteradoPor = alteradoPor;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
}
